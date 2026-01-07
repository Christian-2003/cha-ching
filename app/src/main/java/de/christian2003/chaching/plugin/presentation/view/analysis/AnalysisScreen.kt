package de.christian2003.chaching.plugin.presentation.view.analysis

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.ui.composables.NavigationBarProtection
import de.christian2003.chaching.plugin.presentation.view.analysis.model.DataTabOptions
import de.christian2003.chaching.plugin.presentation.view.analysis.view.AnalysisDataTab
import de.christian2003.chaching.plugin.presentation.view.analysis.view.AnalysisFilterSheet
import de.christian2003.chaching.plugin.presentation.view.analysis.view.AnalysisOverviewTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * Screen displays the analysis of data to the user.
 *
 * @param viewModel     View model from which to source data.
 * @param onNavigateUp  Callback invoked to navigate up on the navigation stack.
 */
@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.analysis_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.isFilterSheetVisible = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_filter),
                            contentDescription = ""
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val bottomPadding: Dp = remember { innerPadding.calculateBottomPadding() }

        if (viewModel.analysisResult == null) {
            //Loading indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LoadingIndicator()
            }
        }
        else {
            //Pager and tabs
            val tabNames: Array<String> = stringArrayResource(R.array.analysis_tabs)
            val pagerState: PagerState = rememberPagerState(pageCount = { tabNames.size })
            val coroutineScope: CoroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabNames.forEachIndexed { index, tabName ->
                        Tab(
                            selected = index == pagerState.currentPage,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = tabName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> AnalysisOverviewTab(
                            viewModel = viewModel,
                            bottomPadding = bottomPadding
                        )
                        1 -> AnalysisDataTab(
                            viewModel = viewModel,
                            bottomPadding = bottomPadding,
                            options = DataTabOptions.Incomes
                        )
                        2 -> AnalysisDataTab(
                            viewModel = viewModel,
                            bottomPadding = bottomPadding,
                            options = DataTabOptions.Expenses
                        )
                    }
                }
            }
        }

        NavigationBarProtection(height = bottomPadding)
    }

    if (viewModel.isFilterSheetVisible) {
        AnalysisFilterSheet(
            filter = viewModel.analysisFilter,
            onDismiss = {
                viewModel.isFilterSheetVisible = false
            },
            onApply = {
                viewModel.analysisFilter = it
                viewModel.isFilterSheetVisible = false
                viewModel.startAnalysis(true)
            },
            onFormatDate = {
                viewModel.formatDate(it)
            }
        )
    }
}
