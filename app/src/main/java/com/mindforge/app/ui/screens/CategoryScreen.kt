package com.mindforge.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.mindforge.app.domain.model.*
import com.mindforge.app.domain.repository.MindForgeRepository
import com.mindforge.app.ui.components.CategoryStreakCard
import com.mindforge.app.ui.components.GameCardPremium
import com.mindforge.app.ui.theme.BackgroundLight
import com.mindforge.app.ui.theme.categoryColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CategoryUiState(
    val category: Category? = null,
    val games: List<GameType> = emptyList(),
    val streak: Int = 0,
    val activeDays: List<Boolean> = List(7) { false },
    val isLoading: Boolean = true
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: MindForgeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val categoryId = savedStateHandle.get<String>("categoryId") ?: Category.MEMORY.id
    
    val uiState: StateFlow<CategoryUiState> = combine(
        repository.observeCategoryProgress(),
        flowOf(Category.fromId(categoryId))
    ) { progressList, category ->
        val progress = progressList.find { it.category.id == category.id }
        CategoryUiState(
            category = category,
            games = GameType.forCategory(category),
            streak = progress?.streak ?: 0,
            activeDays = progress?.weeklyActivity ?: List(7) { false },
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryUiState())
}

@Composable
fun CategoryRoute(
    viewModel: CategoryViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onOpenGame: (String, String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CategoryScreen(state = state, onBack = onBack, onOpenGame = onOpenGame)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryScreen(
    state: CategoryUiState,
    onBack: () -> Unit,
    onOpenGame: (String, String) -> Unit
) {
    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text(state.category?.title ?: "Category", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        state.category?.let { category ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                // 1. TOP STREAK CARD
                CategoryStreakCard(
                    title = "${category.title} Streak",
                    streak = state.streak,
                    icon = when (category) {
                        Category.MEMORY -> Icons.Rounded.Psychology
                        Category.MATH -> Icons.Rounded.Calculate
                        Category.ATTENTION -> Icons.Rounded.CenterFocusStrong
                        Category.VOCABULARY -> Icons.AutoMirrored.Rounded.MenuBook
                    },
                    accentColor = categoryColor(category),
                    activeDays = state.activeDays,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // 2. DIFFICULTY SELECTOR
                Surface(
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.03f),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Difficulty.entries.forEach { diff ->
                            val isSelected = selectedDifficulty == diff
                            Button(
                                onClick = { selectedDifficulty = diff },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color.White else Color.Transparent,
                                    contentColor = if (isSelected) categoryColor(category) else Color.Gray
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = if (isSelected) 4.dp else 0.dp
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(diff.label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. GAMES LIST
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.games) { game ->
                        GameCardPremium(
                            title = game.title,
                            subtitle = game.description,
                            icon = when (category) {
                                Category.MEMORY -> Icons.Rounded.Psychology
                                Category.MATH -> Icons.Rounded.Calculate
                                Category.ATTENTION -> Icons.Rounded.CenterFocusStrong
                                Category.VOCABULARY -> Icons.AutoMirrored.Rounded.MenuBook
                            },
                            accentColor = categoryColor(category),
                            difficulty = selectedDifficulty.label,
                            onClick = { onOpenGame(game.id, selectedDifficulty.id) }
                        )
                    }
                }

                // 4. PLAY RANDOM BUTTON
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Short on time? Try this",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(
                        onClick = { 
                            val randomGame = state.games.randomOrNull()
                            randomGame?.let { onOpenGame(it.id, selectedDifficulty.id) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = categoryColor(category)),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = categoryColor(category))
                    ) {
                        Icon(Icons.Rounded.Shuffle, null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Play Random", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
