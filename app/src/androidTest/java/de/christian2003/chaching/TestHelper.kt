package de.christian2003.chaching

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import de.christian2003.chaching.plugin.presentation.ChaChing


fun ComposeContentTestRule.launchChaChing(context: Context) {
    setContent {
        ChaChing(
            ""
        )
    }
}
