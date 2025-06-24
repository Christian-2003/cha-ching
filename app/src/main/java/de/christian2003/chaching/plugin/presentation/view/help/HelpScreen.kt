package de.christian2003.chaching.plugin.presentation.view.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.ui.composables.HelpCard


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
                viewModel.helpCards.forEach { (helpCard, visible) ->
                    item {
                        HelpListItem(
                            helpCard = helpCard,
                            visible = visible,
                            onClick = { helpCard ->
                                viewModel.toggleHelpCardVisibility(helpCard)
                            }
                        )
                    }
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
 * @param onClick   List item was clicked (i.e. the help message should be toggled to be visible).
 */
@Composable
private fun HelpListItem(
    helpCard: HelpCards,
    visible: Boolean,
    onClick: (HelpCards) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(!visible) {
                onClick(helpCard)
            }
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(helpCard.shortNameStringRes),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            AnimatedVisibility(!visible) {
                Text(
                    text = stringResource(R.string.help_clickToActivate),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Text(
            text = if (visible) { stringResource(R.string.help_visible) } else { stringResource(R.string.help_invisible) },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
        )
    }
}
