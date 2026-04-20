package com.mindforge.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.mindforge.app.domain.model.GameHistory
import com.mindforge.app.domain.model.GameType
import com.mindforge.app.domain.repository.MindForgeRepository
import com.mindforge.app.ui.theme.BackgroundLight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: MindForgeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val gameId: String = savedStateHandle.get<String>("gameId").orEmpty()
    private val difficulty: String = savedStateHandle.get<String>("difficulty").orEmpty()

    val topScores: StateFlow<List<GameHistory>> = repository.observeFilteredTopScores(gameId, difficulty)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

@Composable
fun ResultRoute(
    gameId: String,
    difficulty: String,
    score: Int,
    accuracy: Int,
    streak: Int,
    dailyBonus: Boolean,
    rank: Int,
    onDone: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val topScores by viewModel.topScores.collectAsStateWithLifecycle()
    
    ResultScreen(
        gameId = gameId,
        score = score,
        accuracy = accuracy,
        streak = streak,
        dailyBonus = dailyBonus,
        rank = rank,
        topScores = topScores,
        onDone = onDone
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultScreen(
    gameId: String,
    score: Int,
    accuracy: Int,
    streak: Int,
    dailyBonus: Boolean,
    rank: Int,
    topScores: List<GameHistory>,
    onDone: () -> Unit
) {
    val gameTitle = remember(gameId) { GameType.fromId(gameId).title }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Game Over", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. MINI SCORE CARD
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatPillMini(label = "Score", value = score.toString(), icon = Icons.Rounded.Stars, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                StatPillMini(label = "Rank", value = "#$rank", icon = Icons.Rounded.EmojiEvents, color = Color(0xFFDAA520), modifier = Modifier.weight(1f))
            }

            // 2. SCOREBOARD (MAIN FOCUS)
            Text(
                text = "Scoreboard - $gameTitle",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
            )
            
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 2.dp
            ) {
                if (topScores.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val displayedScores = topScores.take(10)
                        itemsIndexed(displayedScores) { index, entry ->
                            val entryRank = index + 1
                            ScoreboardRow(
                                rank = entryRank,
                                score = entry.score,
                                isCurrent = (entryRank == rank && entry.score == score),
                                difficulty = entry.difficulty
                            )
                        }
                        
                        if (rank > 10) {
                            item {
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Text("...", color = Color.Gray, fontWeight = FontWeight.Bold)
                                }
                            }
                            item {
                                ScoreboardRow(
                                    rank = rank,
                                    score = score,
                                    isCurrent = true,
                                    difficulty = "current"
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. DONE BUTTON
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Back to Games", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ScoreboardRow(rank: Int, score: Int, isCurrent: Boolean, difficulty: String) {
    Surface(
        color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        border = if (isCurrent) null else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when (rank) {
                            1 -> Color(0xFFFFD700)
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> if (isCurrent) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.05f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = if (rank <= 3 || isCurrent) Color.White else Color.Black.copy(alpha = 0.5f)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isCurrent) "YOU" else "Session #$rank",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else Color.Black
                )
                if (!isCurrent) {
                    Text(
                        text = difficulty.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
            
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun StatPillMini(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
                Text(text = label, fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        }
    }
}
