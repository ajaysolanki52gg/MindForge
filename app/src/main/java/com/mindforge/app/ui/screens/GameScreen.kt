package com.mindforge.app.ui.screens

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Whatshot
import com.mindforge.app.domain.model.*
import com.mindforge.app.domain.repository.MindForgeRepository
import com.mindforge.app.domain.usecase.GenerateDailyChallengeUseCase
import com.mindforge.app.domain.usecase.SubmitGameResultUseCase
import com.mindforge.app.games.*
import com.mindforge.app.ui.components.*
import com.mindforge.app.ui.theme.FlameColor
import com.mindforge.app.ui.theme.categoryColor
import com.mindforge.app.utils.DateUtils
import com.mindforge.app.utils.SeedUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class GameUiState(
    val isLoading: Boolean = true,
    val isShowingInstructions: Boolean = true,
    val session: GameSession? = null,
    val currentRoundIndex: Int = 0,
    val score: Int = 0,
    val highScore: Int = 0,
    val correctAnswers: Int = 0,
    val isFinishing: Boolean = false,
    val pendingResult: RewardSummary? = null,
    val currentGameId: String = GameType.PATTERN_RECALL.id,
    val difficultyId: String = Difficulty.EASY.id,
    val categoryStreak: Int = 0,
    val weeklyActivity: List<Boolean> = List(7) { false }
) {
    val currentRound: GameRound? get() = session?.rounds?.getOrNull(currentRoundIndex)
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: MindForgeRepository,
    private val sessionFactory: GameSessionFactory,
    private val submitGameResultUseCase: SubmitGameResultUseCase,
    private val generateDailyChallengeUseCase: GenerateDailyChallengeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val gameIdParam = savedStateHandle.get<String>("gameId") ?: GameType.PATTERN_RECALL.id
    private val gameType = GameType.fromId(gameIdParam)
    private val difficulty = Difficulty.fromId(savedStateHandle.get<String>("difficulty") ?: Difficulty.EASY.id)
    private val _uiState = MutableStateFlow(GameUiState(currentGameId = gameType.id, difficultyId = difficulty.id))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadSession()
        loadHighScore()
        loadStats()
    }

    private fun loadSession() {
        viewModelScope.launch {
            repository.ensureSeedData()
            val session = if (gameType == GameType.DAILY_CHALLENGE) {
                val tasks = generateDailyChallengeUseCase(DateUtils.todayKey())
                sessionFactory.createDailySession(
                    tasks = tasks,
                    seed = SeedUtils.seedFor("daily-session", DateUtils.todayKey())
                )
            } else {
                sessionFactory.createStandardSession(gameType, difficulty)
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    session = session,
                    currentGameId = gameType.id
                )
            }
        }
    }

    private fun loadHighScore() {
        viewModelScope.launch {
            val highScore = repository.getHighScoreForGame(gameIdParam)
            _uiState.update { it.copy(highScore = highScore) }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            if (gameType == GameType.DAILY_CHALLENGE) {
                val stats = repository.observeUserStats().first()
                _uiState.update { it.copy(categoryStreak = stats.streak, weeklyActivity = stats.weeklyActivity) }
            } else {
                gameType.category?.let { category ->
                    val progress = repository.observeCategoryProgress().first().find { it.category == category }
                    if (progress != null) {
                        _uiState.update { it.copy(categoryStreak = progress.streak, weeklyActivity = progress.weeklyActivity) }
                    }
                }
            }
        }
    }

    fun dismissInstructions() {
        _uiState.update { it.copy(isShowingInstructions = false) }
    }

    fun submitChoice(index: Int, timeRatio: Float) {
        val round = _uiState.value.currentRound ?: return
        when (round) {
            is MultipleChoiceRound -> resolveRound(index == round.correctIndex, timeRatio)
            is StroopRound -> resolveRound(round.options.getOrNull(index) == round.correctColorName, timeRatio)
            else -> Unit
        }
    }

    fun submitTextAnswer(input: String, timeRatio: Float) {
        val round = _uiState.value.currentRound as? TextEntryRound ?: return
        resolveRound(input.trim().equals(round.answer.trim(), ignoreCase = true), timeRatio)
    }

    fun submitPattern(selected: Set<Int>, timeRatio: Float) {
        val round = _uiState.value.currentRound as? PatternRecallRound ?: return
        resolveRound(selected == round.highlightedCells, timeRatio)
    }

    fun submitTarget(index: Int, timeRatio: Float) {
        val round = _uiState.value.currentRound as? TapTargetRound ?: return
        resolveRound(index == round.targetIndex, timeRatio)
    }

    fun submitReaction(elapsedMillis: Long) {
        val round = _uiState.value.currentRound as? ReactionRound ?: return
        val windowScore = ((round.idealMillis * 2 - elapsedMillis).coerceAtLeast(150L) / 6L).toInt()
        val isCorrect = elapsedMillis <= round.idealMillis * 2
        advance(correct = isCorrect, points = if (isCorrect) windowScore else 0)
    }

    fun submitTooSoon() {
        advance(correct = false, points = 0)
    }

    fun onTimeout() {
        advance(correct = false, points = 0)
    }

    fun markResultHandled() {
        _uiState.update { it.copy(pendingResult = null) }
    }

    private fun resolveRound(isCorrect: Boolean, timeRatio: Float) {
        val base = (100 * difficulty.timeBonusMultiplier * (0.65f + (timeRatio.coerceIn(0f, 1f) * 0.35f))).roundToInt()
        advance(correct = isCorrect, points = if (isCorrect) base else 0)
    }

    private fun advance(correct: Boolean, points: Int) {
        val current = _uiState.value
        if (current.isFinishing) return
        val session = current.session ?: return
        val nextIndex = current.currentRoundIndex + 1
        val nextScore = current.score + points
        val nextCorrect = current.correctAnswers + if (correct) 1 else 0
        if (nextIndex >= session.rounds.size) {
            finishSession(session, nextScore, nextCorrect)
        } else {
            _uiState.update {
                it.copy(
                    currentRoundIndex = nextIndex,
                    score = nextScore,
                    correctAnswers = nextCorrect
                )
            }
        }
    }

    private fun finishSession(session: GameSession, score: Int, correctAnswers: Int) {
        _uiState.update {
            it.copy(
                score = score,
                correctAnswers = correctAnswers,
                isFinishing = true
            )
        }
        viewModelScope.launch {
            val accuracy = ((correctAnswers / session.rounds.size.toFloat()) * 100f).roundToInt()
            val result = submitGameResultUseCase(
                gameType = if (session.isDailyChallenge) GameType.DAILY_CHALLENGE else gameType,
                difficulty = session.difficulty,
                score = score,
                accuracyPercent = accuracy,
                isDailyChallenge = session.isDailyChallenge
            )
            _uiState.update {
                it.copy(
                    score = score,
                    correctAnswers = correctAnswers,
                    isFinishing = false,
                    pendingResult = result
                )
            }
        }
    }
}

@Composable
fun GameRoute(
    viewModel: GameViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onFinished: (String, String, RewardSummary) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.pendingResult) {
        state.pendingResult?.let { result ->
            onFinished(state.currentGameId, state.difficultyId, result)
            viewModel.markResultHandled()
        }
    }
    GameScreen(state = state, onBack = onBack, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameScreen(
    state: GameUiState,
    onBack: () -> Unit,
    viewModel: GameViewModel
) {
    if (state.isShowingInstructions && !state.isLoading && state.session != null) {
        val firstRound = state.session.rounds.firstOrNull()
        InstructionDialog(
            title = state.session.title,
            description = if (state.session.isDailyChallenge) 
                "Complete 5 different games today to maintain your streak and earn bonus XP!"
                else firstRound?.gameType?.let { getInstructionText(it) } ?: "Ready to train your brain?",
            onStart = { viewModel.dismissInstructions() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.session?.title ?: "MindForge") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        val round = state.currentRound
        if (state.isLoading || round == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (!state.isShowingInstructions) {
            var timeLeft by remember(round.id) { mutableStateOf(round.timeLimitSeconds) }
            LaunchedEffect(round.id) {
                timeLeft = round.timeLimitSeconds
                while (timeLeft > 0) {
                    delay(1_000)
                    timeLeft -= 1
                }
                viewModel.onTimeout()
            }
            val timeRatio = timeLeft / round.timeLimitSeconds.toFloat()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Round ${state.currentRoundIndex + 1} / ${state.session?.rounds?.size ?: 1}",
                                style = MaterialTheme.typography.labelLarge
                            )
                            if (state.highScore > 0) {
                                Text(
                                    text = "Best: ${state.highScore}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = { ((state.currentRoundIndex + 1) / (state.session?.rounds?.size ?: 1).toFloat()) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                item {
                    QuestionCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            SectionHeader(
                                title = round.gameType.title,
                                subtitle = round.prompt,
                                modifier = Modifier.weight(1f)
                            )
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Whatshot, null, tint = FlameColor, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = state.categoryStreak.toString(),
                                        fontWeight = FontWeight.Black,
                                        color = FlameColor,
                                        fontSize = 14.sp
                                    )
                                }
                                Text("Streak", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        StreakIndicator(
                            activeDays = state.weeklyActivity,
                            accentColor = round.gameType.category?.let { categoryColor(it) } ?: MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Black.copy(alpha = 0.05f))
                        
                        InfoLine(label = "Difficulty", value = state.session?.difficulty?.label ?: "")
                        InfoLine(label = "Time Left", value = "${timeLeft}s")
                        InfoLine(label = "Score", value = state.score.toString())
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        when (round) {
                            is MultipleChoiceRound -> MultipleChoiceContent(round, timeRatio, viewModel)
                            is TextEntryRound -> TextEntryContent(round, timeRatio, viewModel)
                            is PatternRecallRound -> PatternRecallContent(round, timeRatio, viewModel)
                            is TapTargetRound -> TapTargetContent(round, timeRatio, viewModel)
                            is ReactionRound -> ReactionContent(round, viewModel)
                            is StroopRound -> StroopContent(round, timeRatio, viewModel)
                        }
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize().padding(padding))
        }
    }
}

private fun getInstructionText(gameType: GameType): String = when (gameType) {
    GameType.PATTERN_RECALL -> "Memorize the pattern of highlighted cells and tap them after they disappear. Accuracy is key!"
    GameType.NUMBER_RECALL -> "A sequence of numbers will appear briefly. Remember the exact sequence and type it back."
    GameType.OBJECT_RECALL -> "Study the group of objects shown. You will be asked to identify which ones were in the original set."
    GameType.QUICK_MATH -> "Solve the arithmetic problems as fast as you can. Don't let the timer run out!"
    GameType.NUMBER_SERIES -> "Identify the pattern in the number sequence and find the next logical number."
    GameType.LOGICAL_REASONING -> "Analyze the options and pick the one that logically follows the pattern or is the 'odd one out'."
    GameType.TIMED_MENTAL_MATH -> "A series of calculations will be presented. Focus on both speed and precision."
    GameType.TARGET_TAP -> "Find and tap the specific target among many distractors. Be as quick as possible!"
    GameType.REACTION_TEST -> "Wait for the signal (color change) and tap the button immediately. Every millisecond counts!"
    GameType.STROOP_TEST -> "Identify the color of the ink, not the word itself. This requires extreme focus!"
    GameType.SYNONYM_ANTONYM -> "Identify the correct synonym or antonym for the given word from the options provided."
    GameType.WORD_MEANING -> "Choose the most accurate definition for the displayed vocabulary word."
    GameType.FILL_IN_THE_BLANK -> "Select the word that best completes the sentence to make it grammatically and logically correct."
    GameType.SPELLING_CORRECTION -> "Identify the correctly spelled version of the word. Look out for common typos!"
    else -> gameType.description
}

@Composable
private fun InstructionDialog(
    title: String,
    description: String,
    onStart: () -> Unit
) {
    Dialog(onDismissRequest = { }) {
        QuestionCard(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "HOW TO PLAY",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color.Black.copy(alpha = 0.4f)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = parseMarkdown(description),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Black.copy(alpha = 0.8f),
                    lineHeight = 26.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                DailyAnswerButton(
                    text = "Start Game",
                    onClick = onStart
                )
            }
        }
    }
}

@Composable
private fun MultipleChoiceContent(round: MultipleChoiceRound, timeRatio: Float, viewModel: GameViewModel) {
    var isRevealPhase by remember(round.id) { mutableStateOf(round.revealText != null) }
    LaunchedEffect(round.id) {
        if (round.revealText != null) {
            delay(round.revealMillis)
            isRevealPhase = false
        }
    }
    
    if (isRevealPhase) {
        Text(
            text = parseMarkdown(round.revealText ?: ""),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
        )
    } else {
        round.supportingText?.let { Text(text = parseMarkdown(it), modifier = Modifier.padding(bottom = 8.dp)) }
        round.options.forEachIndexed { index, option ->
            DailyAnswerButton(
                text = option,
                onClick = { viewModel.submitChoice(index, timeRatio) }
            )
        }
    }
}

@Composable
private fun TextEntryContent(round: TextEntryRound, timeRatio: Float, viewModel: GameViewModel) {
    var value by remember(round.id) { mutableStateOf("") }
    var isRevealPhase by remember(round.id) { mutableStateOf(round.revealText != null) }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    
    val isNumberGame = round.gameType == GameType.NUMBER_RECALL || 
                       round.gameType == GameType.QUICK_MATH || 
                       round.gameType == GameType.NUMBER_SERIES ||
                       round.gameType == GameType.TIMED_MENTAL_MATH

    LaunchedEffect(value) {
        if (value.isNotEmpty() && value.length == round.answer.length) {
            if (isNumberGame && round.gameType == GameType.NUMBER_RECALL) {
                keyboardController?.hide()
                focusManager.clearFocus()
                delay(300)
                viewModel.submitTextAnswer(value, timeRatio)
            }
        }
    }

    LaunchedEffect(round.id) {
        if (round.revealText != null) {
            delay(round.revealMillis)
            isRevealPhase = false
        }
    }
    
    if (isRevealPhase) {
        Text(
            text = parseMarkdown(round.revealText ?: ""),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
        )
    } else {
        round.supportingText?.let { Text(text = parseMarkdown(it), modifier = Modifier.padding(bottom = 8.dp)) }
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Type answer") },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isNumberGame) KeyboardType.Number else KeyboardType.Text
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(4.dp))
        DailyAnswerButton(
            text = "Submit",
            onClick = { 
                keyboardController?.hide()
                focusManager.clearFocus()
                viewModel.submitTextAnswer(value, timeRatio) 
            }
        )
    }
}

@Composable
private fun PatternRecallContent(round: PatternRecallRound, timeRatio: Float, viewModel: GameViewModel) {
    var isRevealPhase by remember(round.id) { mutableStateOf(true) }
    val selectedCells = remember(round.id) { mutableStateListOf<Int>() }
    LaunchedEffect(round.id) {
        delay(round.revealMillis)
        isRevealPhase = false
        selectedCells.clear()
    }
    
    Text(
        text = if (isRevealPhase) "Memorize the pattern" else "Tap the remembered cells",
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(round.gridSize) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                repeat(round.gridSize) { column ->
                    val cellIndex = (row * round.gridSize) + column
                    val highlighted = if (isRevealPhase) {
                        cellIndex in round.highlightedCells
                    } else {
                        cellIndex in selectedCells
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable(enabled = !isRevealPhase) {
                                if (cellIndex in selectedCells) {
                                    selectedCells.remove(cellIndex)
                                } else {
                                    selectedCells.add(cellIndex)
                                }
                            }
                    )
                }
            }
        }
    }
    if (!isRevealPhase) {
        Spacer(modifier = Modifier.height(8.dp))
        DailyAnswerButton(
            text = "Check Pattern",
            onClick = { viewModel.submitPattern(selectedCells.toSet(), timeRatio) }
        )
    }
}

@Composable
private fun TapTargetContent(round: TapTargetRound, timeRatio: Float, viewModel: GameViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(round.items.chunked(round.columns).size) { rowIndex ->
            val rowItems = round.items.chunked(round.columns)[rowIndex]
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                rowItems.forEachIndexed { columnIndex, label ->
                    val absoluteIndex = (rowIndex * round.columns) + columnIndex
                    DailyAnswerButton(
                        text = label,
                        onClick = { viewModel.submitTarget(absoluteIndex, timeRatio) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private enum class ReactionPhase { WAITING, GO }

@Composable
private fun ReactionContent(round: ReactionRound, viewModel: GameViewModel) {
    var phase by remember(round.id) { mutableStateOf(ReactionPhase.WAITING) }
    var startTime by remember(round.id) { mutableLongStateOf(0L) }
    LaunchedEffect(round.id) {
        delay(round.waitMillis)
        phase = ReactionPhase.GO
        startTime = SystemClock.elapsedRealtime()
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
    ) {
        Text(
            text = if (phase == ReactionPhase.GO) "Tap now!" else "Wait for green...",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        DailyAnswerButton(
            text = "Tap",
            onClick = {
                if (phase == ReactionPhase.GO) {
                    viewModel.submitReaction(SystemClock.elapsedRealtime() - startTime)
                } else {
                    viewModel.submitTooSoon()
                }
            },
            backgroundColor = if (phase == ReactionPhase.GO) Color(0xFF4CAF50) else Color.Gray
        )
    }
}

@Composable
private fun StroopContent(round: StroopRound, timeRatio: Float, viewModel: GameViewModel) {
    Text(
        text = round.word,
        color = round.inkColor,
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
    )
    round.options.forEachIndexed { index, option ->
        DailyAnswerButton(
            text = option,
            onClick = { viewModel.submitChoice(index, timeRatio) }
        )
    }
}
