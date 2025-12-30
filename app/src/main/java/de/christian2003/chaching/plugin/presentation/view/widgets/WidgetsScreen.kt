package de.christian2003.chaching.plugin.presentation.view.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.ui.composables.HelpCard
import kotlin.math.roundToInt


/**
 * Screen through which to edit widget settings.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback invoked to navigate up the navigation stack.
 */
@Composable
fun WidgetsScreen(
    viewModel: WidgetsViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.widgets_title))
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
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            //Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(viewModel.isHelpCardVisible) {
                    HelpCard(
                        text = stringResource(R.string.widgets_help),
                        onDismiss = {
                            viewModel.dismissHelpCard()
                        },
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.margin_horizontal),
                            end = dimensionResource(R.dimen.margin_horizontal),
                            bottom = dimensionResource(R.dimen.padding_vertical)
                        )
                    )
                }

                InstancesCountInformation(
                    instancesCount = viewModel.numberOfWidgetInstances,
                    onPinWidget = {
                        viewModel.requestPinWidget()
                    },
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_vertical))
                )

                WidgetPreview(
                    wallpaperBitmap = viewModel.wallpaperBitmap,
                    widgetOpacity = viewModel.widgetOpacity,
                    isObfuscated = viewModel.widgetIsObfuscated
                )

                OpacitySlider(
                    opacity = viewModel.widgetOpacity,
                    onOpacityChange = {
                        viewModel.widgetOpacity = it
                    },
                    modifier = Modifier.padding(
                        vertical = dimensionResource(R.dimen.padding_vertical)
                    )
                )

                RadioButtonSelector(
                    selectedOption = if (viewModel.widgetIsObfuscated) { 1 } else { 0 },
                    onOptionSelected = {
                        viewModel.widgetIsObfuscated = (it == 1)
                    },
                    optionLabels = listOf(
                        stringResource(R.string.widgets_valueOption_cleartextLabel),
                        stringResource(R.string.widgets_valueOption_obfuscateLabel)
                    ),
                    title = stringResource(R.string.widgets_valueOption),
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_vertical))
                )

                RadioButtonSelector(
                    selectedOption = viewModel.widgetClickAction,
                    onOptionSelected = {
                        viewModel.widgetClickAction = it
                    },
                    optionLabels = listOf(
                        stringResource(R.string.widgets_clickAction_openApp),
                        stringResource(R.string.widgets_clickAction_openAnalysis),
                        stringResource(R.string.widgets_clickAction_openSettings)
                    ),
                    title = stringResource(R.string.widgets_clickAction),
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_vertical))
                )
            }

            //Bottom row:
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(
                            horizontal = dimensionResource(R.dimen.margin_horizontal),
                            vertical = dimensionResource(R.dimen.padding_vertical)
                        )
                ) {
                    Button(
                        onClick = {
                            viewModel.save()
                            onNavigateUp()
                        }
                    ) {
                        Text(stringResource(R.string.button_save))
                    }
                }
            }
        }
    }
}


/**
 * Box at the top of the page to inform the user if multiple instances are created
 * or to allow the user to pin the widget on the launcher.
 *
 * @param instancesCount    Number of active widget instances in the launcher.
 * @param onPinWidget       Callback invoked to pin the widget in the launcher.
 * @param modifier          Modifier.
 */
@Composable
private fun InstancesCountInformation(
    instancesCount: Int?,
    onPinWidget: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (instancesCount != null && (instancesCount > 1 || instancesCount == 0)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.padding_horizontal))
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            if (instancesCount > 1) {
                Text(
                    text = stringResource(R.string.widgets_globalSettingsInfo),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else {
                Text(
                    text = stringResource(R.string.widgets_pinInLauncherInfo),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = dimensionResource(R.dimen.padding_horizontal))
                )
                TextButton(
                    onClick = onPinWidget
                ) {
                    Text(stringResource(R.string.widgets_pinInLauncherButton))
                }
            }
        }
    }
}


/**
 * Preview of the widget. This uses the preview for the widget picker internally.
 *
 * @param wallpaperBitmap   Bitmap of the launcher wallpaper to display behind the widget
 *                          preview.
 * @param widgetOpacity     Widget background opacity.
 * @param isObfuscated      Whether values are obfuscated in the widget.
 * @param modifier          Modifier.
 */
@Composable
private fun WidgetPreview(
    wallpaperBitmap: ImageBitmap?,
    widgetOpacity: Float,
    isObfuscated: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
    ) {
        if (wallpaperBitmap != null) {
            Image(
                bitmap = wallpaperBitmap,
                contentDescription = "",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.extraLargeIncreased)
            )
        }
        else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.extraLargeIncreased)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            )
        }
        AndroidView(
            factory = { context -> OverviewWidgetView(context) },
            update = { view ->
                view.setOpacity(widgetOpacity)
                view.setObfuscated(isObfuscated)
            },
            modifier = Modifier
                .width(192.dp)
                .height(76.dp)
                .clip(MaterialTheme.shapes.large)
        )
    }
}


/**
 * Slider to change the widget background opacity.
 *
 * @param opacity           Opacity.
 * @param onOpacityChange   Callback invoked once opacity changes.
 * @param modifier          Modifier.
 */
@Composable
private fun OpacitySlider(
    opacity: Float,
    onOpacityChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal)
            )
    ) {
        Text(
            text = stringResource(R.string.widgets_opacityLabel),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                value = opacity,
                onValueChange = onOpacityChange,
                steps = 9,
                valueRange = 0f..1f,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
            )
            Box(
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "${(opacity * 100f).roundToInt()} %",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "100 %",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.alpha(0f)
                )
            }
        }
    }
}


/**
 * Selector using radio buttons to select an option.
 *
 * @param selectedOption    Index of the selected option.
 * @param onOptionSelected  Callback invoked once an option is selected.
 * @param optionLabels      Labels for the options.
 * @param title             Title for the selection.
 * @param modifier          Modifier.
 */
@Composable
private fun RadioButtonSelector(
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
    optionLabels: List<String>,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.margin_horizontal))
        )
        optionLabels.forEachIndexed { index, label ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onOptionSelected(index)
                    }
                    .padding(
                        horizontal = dimensionResource(R.dimen.margin_horizontal),
                        vertical = dimensionResource(R.dimen.padding_vertical)
                    )
            ) {
                RadioButton(
                    selected = index == selectedOption,
                    onClick = {
                        onOptionSelected(index)
                    },
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.padding_horizontal))
                        .size(24.dp)
                )
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
