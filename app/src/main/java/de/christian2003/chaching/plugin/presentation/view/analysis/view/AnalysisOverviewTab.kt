package de.christian2003.chaching.plugin.presentation.view.analysis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import de.christian2003.chaching.plugin.presentation.view.analysis.AnalysisViewModel


@Composable
fun AnalysisOverviewTab(
    viewModel: AnalysisViewModel,
    bottomPadding: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

    }
}
