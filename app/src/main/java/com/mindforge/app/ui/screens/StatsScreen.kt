package com.mindforge.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.mindforge.app.domain.model.*
import com.mindforge.app.domain.repository.MindForgeRepository
import com.mindforge.app.ui.components.GradientCard
import com.mindforge.app.ui.components.InfoLine
import com.mindforge.app.ui.components.SectionHeader
import com.mindforge.app.ui.theme.categoryColor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StatsUiState(
    val stats: UserStats = UserStats(),
    val achievements: List<Achievement> = emptyList(),
    val categoryProgress: List<CategoryProgress> = emptyList(),
    val filteredTopScores: List<GameHistory> = emptyList()
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: MindForgeRepository
) : ViewModel() {
    private val _selectedCategory = MutableStateFlow<Category>(Category.MEMORY)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedGameId = MutableStateFlow<String>(GameType.forCategory(Category.MEMORY).first().id)
    val selectedGameId = _selectedGameId.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<String>("easy")
    val selectedDifficulty = _selectedDifficulty.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StatsUiState> = combine(
        repository.observeUserStats(),
        repository.observeAchievements(),
        repository.observeCategoryProgress(),
        combine(_selectedGameId, _selectedDifficulty) { gameId, diff ->
            gameId to diff
        }.flatMapLatest { (gameId, diff) ->
            repository.observeFilteredTopScores(gameId, diff)
        }
    ) { stats, achievements, categoryProgress, filteredScores ->
        StatsUiState(
            stats = stats,
            achievements = achievements,
            categoryProgress = categoryProgress,
            filteredTopScores = filteredScores
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatsUiState()
    )

    init {
        viewModelScope.launch { repository.ensureSeedData() }
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
        // Auto-select first game of the category
        _selectedGameId.value = GameType.forCategory(category).first().id
    }

    fun selectGame(gameId: String) {
        _selectedGameId.value = gameId
    }

    fun selectDifficulty(difficulty: String) {
        _selectedDifficulty.value = difficulty
    }
}

@Composable
fun StatsRoute(
    viewModel: StatsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedGameId by viewModel.selectedGameId.collectAsStateWithLifecycle()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()

    StatsScreen(
        state = state,
        selectedCategory = selectedCategory,
        selectedGameId = selectedGameId,
        selectedDifficulty = selectedDifficulty,
        onCategorySelect = viewModel::selectCategory,
        onGameSelect = viewModel::selectGame,
        onDifficultySelect = viewModel::selectDifficulty,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsScreen(
    state: StatsUiState,
    selectedCategory: Category,
    selectedGameId: String,
    selectedDifficulty: String,
    onCategorySelect: (Category) -> Unit,
    onGameSelect: (String) -> Unit,
    onDifficultySelect: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Leaderboard")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Progress & Scoreboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        item {
                            GradientCard(
                                colors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    SectionHeader("Daily Momentum", "Keep your brain active every day.")
                                    InfoLine("Global Streak", "${state.stats.streak} Days")
                                }
                            }
                        }
                        
                        item {
                            Text("Category Streaks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }

                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(state.categoryProgress) { progress ->
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(progress.category.title, style = MaterialTheme.typography.labelLarge)
                                            Text("${progress.streak}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                            Text("Day Streak", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text("Achievements", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        
                        items(state.achievements) { achievement ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Rounded.EmojiEvents,
                                        contentDescription = null,
                                        tint = if (achievement.unlocked) Color(0xFFFFD700) else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(achievement.title, fontWeight = FontWeight.Bold)
                                        Text(
                                            if (achievement.unlocked) "Unlocked" else "In Progress",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        item {
                            LeaderboardFilters(
                                selectedCategory = selectedCategory,
                                selectedGameId = selectedGameId,
                                selectedDifficulty = selectedDifficulty,
                                onCategorySelect = onCategorySelect,
                                onGameSelect = onGameSelect,
                                onDifficultySelect = onDifficultySelect
                            )
                        }

                        if (state.filteredTopScores.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No scores match these filters.", color = Color.Gray)
                                }
                            }
                        } else {
                            items(state.filteredTopScores.size) { index ->
                                val score = state.filteredTopScores[index]
                                val gameType = GameType.fromId(score.gameType)
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (index == 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(if (index == 0) Color(0xFFFFD700) else MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp,
                                                color = if (index == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(gameType.title, fontWeight = FontWeight.Bold)
                                            Text(score.difficulty.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall)
                                        }
                                        Text(
                                            text = score.score.toString(),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun LeaderboardFilters(
    selectedCategory: Category,
    selectedGameId: String,
    selectedDifficulty: String,
    onCategorySelect: (Category) -> Unit,
    onGameSelect: (String) -> Unit,
    onDifficultySelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Rounded.FilterList, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Text("Scoreboard Filters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        // 1. Categories Icons Row
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Category", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Category.entries.forEach { category ->
                    CategoryFilterIcon(
                        icon = getCategoryIcon(category),
                        label = category.title,
                        isSelected = selectedCategory == category,
                        onClick = { onCategorySelect(category) },
                        accentColor = categoryColor(category)
                    )
                }
            }
        }

        // 2. Games Row (Filtered by Category)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Game", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(GameType.forCategory(selectedCategory)) { game ->
                    FilterPill(
                        text = game.title,
                        isSelected = selectedGameId == game.id,
                        onClick = { onGameSelect(game.id) }
                    )
                }
            }
        }

        // 3. Difficulty Row
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Difficulty", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("easy" to "Easy", "medium" to "Medium", "hard" to "Hard").forEach { (id, label) ->
                    FilterPill(
                        text = label,
                        isSelected = selectedDifficulty == id,
                        onClick = { onDifficultySelect(id) }
                    )
                }
            }
        }
        
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.Black.copy(alpha = 0.05f))
    }
}

@Composable
private fun CategoryFilterIcon(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = if (isSelected) accentColor else Color.Black.copy(alpha = 0.05f),
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) Color.White else Color.Gray,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) accentColor else Color.Gray
        )
    }
}

private fun getCategoryIcon(category: Category): ImageVector = when (category) {
    Category.MEMORY -> Icons.Rounded.Psychology
    Category.MATH -> Icons.Rounded.Calculate
    Category.ATTENTION -> Icons.Rounded.CenterFocusStrong
    Category.VOCABULARY -> Icons.AutoMirrored.Rounded.MenuBook
}

@Composable
private fun FilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
