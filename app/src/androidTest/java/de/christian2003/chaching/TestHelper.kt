package de.christian2003.chaching

import android.content.Context
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import de.christian2003.chaching.plugin.infrastructure.update.UpdateManager
import de.christian2003.chaching.plugin.presentation.ChaChing

fun ComposeContentTestRule.launchChaChing(context: Context) {
    val updateManager = UpdateManager()

    setContent {
        ChaChing(
            updateManager = updateManager
        )
    }
}
