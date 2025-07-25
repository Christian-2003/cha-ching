package de.christian2003.chaching.plugin.presentation.view.type

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.domain.type.TypeIcon
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import de.christian2003.chaching.plugin.presentation.ui.composables.HelpCard
import de.christian2003.chaching.plugin.presentation.ui.composables.TextInput


/**
 * Displays the screen through which to edit / delete a type.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback invoked to navigate up on the navigation stack.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeScreen(
    viewModel: TypeViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (viewModel.name.isNotEmpty()) {
                            viewModel.name
                        } else {
                            viewModel.namePlaceholder
                        }
                    )
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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(viewModel.isHelpCardVisible) {
                HelpCard(
                    text = stringResource(R.string.type_help_create),
                    onDismiss = {
                        viewModel.dismissHelpCard()
                    },
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.margin_horizontal),
                        end = dimensionResource(R.dimen.margin_horizontal),
                        bottom = dimensionResource(R.dimen.padding_vertical),
                    )
                )
            }
            TextInput(
                value = viewModel.name,
                onValueChange = {
                    viewModel.name = it
                },
                label = stringResource(R.string.type_nameLabel),
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.margin_horizontal),
                    end = dimensionResource(R.dimen.margin_horizontal),
                    bottom = dimensionResource(R.dimen.padding_vertical),
                )
            )
            Checkbox(
                checked = viewModel.isHoursWorkedEditable,
                onCheckedChange = {
                    viewModel.isHoursWorkedEditable = it
                },
                title = stringResource(R.string.type_hoursWorkedEditableTitle),
                text = stringResource(R.string.type_hoursWorkedEditableText)
            )
            Headline(stringResource(R.string.type_chooseIcon))
            TypeIconSelection(
                selected = viewModel.icon,
                onSelectedChange = { icon ->
                    viewModel.icon = icon
                }
            )
            Button(
                onClick = {
                    viewModel.save()
                    onNavigateUp()
                },
                enabled = viewModel.name.isNotEmpty(),
                modifier = Modifier.padding(
                        vertical = dimensionResource(R.dimen.padding_vertical),
                        horizontal = dimensionResource(R.dimen.margin_horizontal)
                    )
                    .align(Alignment.End)
            ) {
                Text(
                    if (viewModel.isCreating) {
                        stringResource(R.string.button_createType)
                    } else {
                        stringResource(R.string.button_save)
                    }
                )
            }
        }
    }
}


/**
 * Checkbox component for the app.
 *
 * @param checked           Whether the checkbox is checked.
 * @param onCheckedChange   Callback invoked once the checked-state changes.
 * @param title             Title for the checkbox.
 * @param modifier          Modifier.
 * @param text              Optional subtext describing the checkbox in greater detail.
 */
@Composable
private fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    text: String? = null
) {
    var checkboxStartPadding = dimensionResource(R.dimen.margin_horizontal) - 12.dp
    if (checkboxStartPadding < 0.dp) {
        checkboxStartPadding = 0.dp
    }
    var checkboxEndPadding = dimensionResource(R.dimen.padding_horizontal) - 12.dp
    if (checkboxEndPadding < 0.dp) {
        checkboxEndPadding = 0.dp
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onCheckedChange(!checked)
            }
            .padding(
                start = checkboxStartPadding,
                top = dimensionResource(R.dimen.padding_vertical),
                end = dimensionResource(R.dimen.margin_horizontal),
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = checkboxEndPadding)
        )
        Column {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge
            )
            if (text != null) {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}


/**
 * Displays (multiple) rows of icon buttons from which the user can select one icon for the type.
 *
 * @param selected          Icon selected currently.
 * @param onSelectedChange  Callback invoked once the selection changes.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TypeIconSelection(
    selected: TypeIcon,
    onSelectedChange: (TypeIcon) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.margin_horizontal),
                end = dimensionResource(R.dimen.margin_horizontal),
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        TypeIcon.entries.forEach { typeIcon ->
            IconToggleButton(
                checked = typeIcon == selected,
                onCheckedChange = {
                    if (it) {
                        onSelectedChange(typeIcon)
                    }
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
                    painter = painterResource(typeIcon.drawableResourceId),
                    contentDescription = "",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
