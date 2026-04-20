package com.mindforge.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.mindforge.app.domain.model.*
import com.mindforge.app.domain.repository.MindForgeRepository
import com.mindforge.app.domain.usecase.ObserveDashboardUseCase
import com.mindforge.app.ui.components.*
import com.mindforge.app.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeDashboardUseCase: ObserveDashboardUseCase,
    private val repository: MindForgeRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardState> = observeDashboardUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardState()
    )

    init {
        viewModelScope.launch { repository.ensureSeedData() }
    }
}

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    onCategoryClick: (String) -> Unit,
    onDailyChallengeClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onOpenCategory = onCategoryClick,
        onOpenGame = { _, _ -> onDailyChallengeClick() },
        onOpenStats = onStatsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: DashboardState,
    onOpenCategory: (String) -> Unit,
    onOpenGame: (String, String) -> Unit,
    onOpenStats: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("MindForge", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    IconButton(onClick = onOpenStats) {
                        Icon(imageVector = Icons.Rounded.Insights, contentDescription = "Stats")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OverallStreakCard(
                    streak = state.stats.streak,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                DailyChallengeCard(
                    onClick = { onOpenGame(GameType.DAILY_CHALLENGE.id, "medium") }
                )
            }

            item {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            val chunks = state.categories.chunked(2)
            items(chunks.size) { index ->
                val rowCategories = chunks[index]
                Row(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowCategories.forEach { overview ->
                        CategoryCardGrid(
                            title = overview.category.title,
                            streak = overview.progress.streak,
                            icon = when (overview.category) {
                                Category.MEMORY -> Icons.Rounded.Psychology
                                Category.MATH -> Icons.Rounded.Calculate
                                Category.ATTENTION -> Icons.Rounded.CenterFocusStrong
                                Category.VOCABULARY -> Icons.AutoMirrored.Rounded.MenuBook
                            },
                            accentColor = categoryColor(overview.category),
                            backgroundBrush = Brush.verticalGradient(categoryGradient(overview.category)),
                            activeDays = overview.progress.weeklyActivity,
                            onClick = { onOpenCategory(overview.category.id) },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                    if (rowCategories.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
