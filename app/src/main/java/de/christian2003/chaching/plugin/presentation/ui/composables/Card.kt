package de.christian2003.chaching.plugin.presentation.ui.composables


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import de.christian2003.chaching.R


/**
 * Composable displays a card to the user.
 *
 * @param text              Text for the card.
 * @param modifier          Modifier.
 * @param backgroundColor   Background color for the card.
 * @param foregroundColor   Foreground color for the card.
 * @param icon              Icon for the card.
 * @param content           Content to display below the card text.
 */
@Composable
fun Card(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    foregroundColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    icon: Painter = painterResource(R.drawable.ic_info),
    content: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(backgroundColor)
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                tint = foregroundColor,
                contentDescription = "",
                modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_horizontal))
            )
            Text(
                text = text,
                color = foregroundColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (content != null) {
            content()
        }
    }
}
