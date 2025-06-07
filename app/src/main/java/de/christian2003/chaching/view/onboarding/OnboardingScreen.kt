package de.christian2003.chaching.view.onboarding

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.database.entities.Type
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onNavigateUp: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Scaffold { innerPadding ->
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> OnboardingPageStatic(
                        painter = painterResource(R.drawable.onboarding_transfers),
                        title = stringResource(R.string.onboarding_page1_title),
                        text = stringResource(R.string.onboarding_page1_text),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        foregroundColor = MaterialTheme.colorScheme.onPrimary
                    )
                    1 -> OnboardingPageStatic(
                        painter = painterResource(R.drawable.onboarding_analysis),
                        title = stringResource(R.string.onboarding_page2_title),
                        text = stringResource(R.string.onboarding_page2_text),
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        foregroundColor = MaterialTheme.colorScheme.onSecondary
                    )
                    2 -> OnboardingPageDynamic(
                        defaultTypes = viewModel.defaultTypes.toMap(),
                        onTypeClick = { type, selected ->
                            viewModel.changeTypeSelected(type, selected)
                        }
                    )
                }
            }
            BottomRow(
                page = pagerState.currentPage,
                pageCount = pagerState.pageCount,
                onNextClick = {
                    if (pagerState.currentPage == pagerState.pageCount - 1) {
                        viewModel.save()
                        onNavigateUp()
                    }
                    else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                onPreviousClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                nextButtonVisible = viewModel.typesSelected || pagerState.currentPage != pagerState.pageCount - 1
            )
        }
    }
}


@Composable
fun BottomRow(
    page: Int,
    pageCount: Int,
    onNextClick: () -> Unit,
    onPreviousClick: ()  -> Unit,
    modifier: Modifier = Modifier,
    nextButtonVisible: Boolean = true
) {
    var color: Color = when (page) {
        0 -> MaterialTheme.colorScheme.onPrimary
        1 -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalDivider(color = color)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.margin_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            if (page != 0) {
                TextButton(
                    onClick = onPreviousClick,
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = color
                    ),
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(stringResource(R.string.button_previous))
                }
            }
            Row(
                modifier = Modifier.align(Alignment.Center)
            ) {
                repeat(pageCount) { i ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(if (i == page) { color } else { color.copy(0.6f) })
                            .size(8.dp)
                    )
                }
            }
            if (nextButtonVisible) {
                TextButton(
                    onClick = onNextClick,
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = color
                    ),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(if (page != pageCount - 1) { stringResource(R.string.button_next) } else { stringResource(R.string.button_finish) })
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OnboardingPageStatic(
    painter: Painter,
    title: String,
    text: String,
    backgroundColor: Color,
    foregroundColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Image(
            painter = painter,
            contentDescription = "",
            modifier = Modifier
                .size(dimensionResource(R.dimen.image_onboarding))
                .padding(
                    horizontal = dimensionResource(R.dimen.margin_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        )
        Column(
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
        ) {
            Text(
                text = title,
                color = foregroundColor,
                style = MaterialTheme.typography.headlineLargeEmphasized,
                modifier = Modifier.padding(
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
            )
            Text(
                text = text,
                color = foregroundColor,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(
                    bottom = dimensionResource(R.dimen.padding_vertical)
                )
            )
        }
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OnboardingPageDynamic(
    defaultTypes: Map<Type, Boolean>,
    onTypeClick: (Type, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Text(
            text = stringResource(R.string.onboarding_page3_title),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineLargeEmphasized,
            modifier = Modifier.padding(
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
        )
        Text(
            text = stringResource(R.string.onboarding_page3_text),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
        )
        FlowRow(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            defaultTypes.forEach { (type, selected) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                ) {
                    IconToggleButton(
                        checked = selected,
                        onCheckedChange = {
                            onTypeClick(type, it)
                        },
                        colors = IconButtonDefaults.iconToggleButtonColors().copy(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            checkedContainerColor = MaterialTheme.colorScheme.primary,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .padding(4.dp)
                            .size(56.dp)
                    ) {
                        Icon(
                            painter = painterResource(type.icon.drawableResourceId),
                            contentDescription = "",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = type.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
