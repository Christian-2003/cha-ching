package de.christian2003.chaching.plugin.presentation.view.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.model.HelpCards
import de.christian2003.chaching.plugin.presentation.ui.composables.HelpCard
import de.christian2003.chaching.plugin.presentation.ui.composables.ListItemContainer
import de.christian2003.chaching.plugin.presentation.ui.composables.Shape


/**
 * Screen displays a list of all help messages that help the user understand functionalities all over
 * the app. Through this screen, a user can reactivate a help message if they have dismissed it
 * previously.
 *
 * @param viewModel     View model.
 * @param onNavigateUp  Callback invoked to navigate up on the navigation stack.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    viewModel: HelpViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.help_title))
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
            LazyColumn {
                item {
                    AnimatedVisibility(viewModel.helpCards[HelpCards.HELP_LIST] == true) {
                        HelpCard(
                            text = stringResource(R.string.help_help),
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
                }
                val helpCards = viewModel.helpCards.toList()
                itemsIndexed(helpCards) { index, (helpCard, visible) ->
                    HelpListItem(
                        helpCard = helpCard,
                        visible = visible,
                        isFirst = index == 0,
                        isLast = index == helpCards.size - 1,
                        onClick = { helpCard ->
                            viewModel.toggleHelpCardVisibility(helpCard)
                        }
                    )
                }
            }
        }
    }
}


/**
 * Shows an item in the list which displays the state of all help messages.
 *
 * @param helpCard  Help card for which to display an item.
 * @param visible   Whether the help message is visible.
 * @param isFirst   Whether this is the first list item.
 * @param isLast    Whether this is the last list item.
 * @param onClick   List item was clicked (i.e. the help message should be toggled to be visible).
 */
@Composable
private fun HelpListItem(
    helpCard: HelpCards,
    visible: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: (HelpCards) -> Unit
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick(helpCard)
                }
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
                    .size(dimensionResource(R.dimen.image_m))
            ) {
                Shape(
                    shape = MaterialShapes.Cookie12Sided,
                    color = MaterialTheme.colorScheme.surface
                )
                Icon(
                    painter = if (visible) {
                        painterResource(R.drawable.ic_visible)
                    } else {
                        painterResource(R.drawable.ic_invisible)
                    },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(helpCard.shortNameStringRes),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (visible) { stringResource(R.string.help_visible) } else { stringResource(R.string.help_invisible) },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Checkbox(
                checked = visible,
                onCheckedChange = {
                    onClick(helpCard)
                },
                modifier = Modifier
                    .padding(start = dimensionResource(R.dimen.padding_horizontal))
                    .size(24.dp)
            )
        }
    }
}
