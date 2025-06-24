package de.christian2003.chaching.plugin.presentation.view.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.rememberAsyncImagePainter
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import java.time.LocalDate
import androidx.core.net.toUri
import java.time.format.DateTimeFormatter


/**
 * Screen displays settings of the app.
 *
 * @param viewModel                 View model.
 * @param onNavigateUp              Callback invoked to navigate up on the navigation stack.
 * @param onNavigateToTypes         Callback invoked to navigate to the screen displaying the list
 *                                  of types.
 * @param onNavigateToLicenses      Callback invoked to navigate to the screen displaying licenses.
 * @param onNavigateToHelpMessages  Callback invoked to navigate to the screen displaying the list
 *                                  of help messages.
 * @param onNavigateToOnboarding    Callback invoked to navigate to the app onboarding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToTypes: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onNavigateToHelpMessages: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val context: Context = LocalContext.current
    val exportSuccessMessage = stringResource(R.string.settings_data_exportSuccess)
    val exportErrorMessage = stringResource(R.string.settings_data_exportError)
    val importSuccessMessage = stringResource(R.string.settings_data_importSuccess)
    val importErrorMessage = stringResource(R.string.settings_data_importError)
    val exportFilename = stringResource(R.string.settings_data_exportFilename, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data != null && result.data!!.data != null) {
            viewModel.exportDataToJsonFile(
                uri = result.data!!.data!!,
                onFinished = { success ->
                    if (success) {
                        Toast.makeText(context, exportSuccessMessage, Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(context, exportErrorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            )
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.data != null && result.data!!.data != null) {
            viewModel.importUri = result.data!!.data!!
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings_title))
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
                .verticalScroll(rememberScrollState())
        ) {
            //General
            GeneralSection()
            HorizontalDivider()

            //Data
            Headline(
                title = stringResource(R.string.settings_data),
                indentToPrefixIcon = true
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_data_typesTitle),
                info = stringResource(R.string.settings_data_typesInfo),
                onClick = onNavigateToTypes,
                endIcon = painterResource(R.drawable.ic_next),
                prefixIcon = painterResource(R.drawable.ic_types)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_data_exportTitle),
                info = stringResource(R.string.settings_data_exportInfo),
                onClick = {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("application/json")
                    intent.putExtra(Intent.EXTRA_TITLE, exportFilename)
                    exportLauncher.launch(intent)
                },
                prefixIcon = painterResource(R.drawable.ic_export)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_data_importTitle),
                info = stringResource(R.string.settings_data_importInfo),
                onClick = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("application/json")
                    importLauncher.launch(intent)
                },
                prefixIcon = painterResource(R.drawable.ic_import)
            )
            HorizontalDivider()


            //Help
            Headline(
                title = stringResource(R.string.settings_help),
                indentToPrefixIcon = true
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_help_helpMessagesTitle),
                info = stringResource(R.string.settings_help_helpMessagesInfo),
                onClick = onNavigateToHelpMessages,
                endIcon = painterResource(R.drawable.ic_next),
                prefixIcon = painterResource(R.drawable.ic_help)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_help_onboardingTitle),
                info = stringResource(R.string.settings_help_onboardingInfo),
                onClick = onNavigateToOnboarding,
                endIcon = painterResource(R.drawable.ic_next),
                prefixIcon = painterResource(R.drawable.ic_welcome)
            )
            HorizontalDivider()


            //About
            Headline(
                title = stringResource(R.string.settings_about),
                indentToPrefixIcon = true
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_licensesTitle),
                info = stringResource(R.string.settings_about_licensesInfo),
                onClick = onNavigateToLicenses,
                endIcon = painterResource(R.drawable.ic_next),
                prefixIcon = painterResource(R.drawable.ic_license)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_repoTitle),
                info = stringResource(R.string.settings_about_repoInfo),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://github.com/Christian-2003/cha-ching".toUri())
                    context.startActivity(intent)
                },
                endIcon = painterResource(R.drawable.ic_external),
                prefixIcon = painterResource(R.drawable.ic_github)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_issuesTitle),
                info = stringResource(R.string.settings_about_issuesInfo),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, "https://github.com/Christian-2003/cha-ching/issues".toUri())
                    context.startActivity(intent)
                },
                endIcon = painterResource(R.drawable.ic_external),
                prefixIcon = painterResource(R.drawable.ic_bug)
            )
            SettingsItemButton(
                setting = stringResource(R.string.settings_about_moreTitle),
                info = stringResource(R.string.settings_about_moreInfo),
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.setData(uri)
                    context.startActivity(intent)
                },
                endIcon = painterResource(R.drawable.ic_external),
                prefixIcon = painterResource(R.drawable.ic_android)
            )
        }

        if (viewModel.importUri != null) {
            ImportDialog(
                onDismiss = {
                    viewModel.importUri = null
                },
                onConfirm = { importStrategy ->
                    viewModel.importDataFromJsonFile(
                        viewModel.importUri!!,
                        importStrategy = importStrategy,
                        onFinished = { success ->
                            if (success) {
                                Toast.makeText(context, importSuccessMessage, Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Toast.makeText(context, importErrorMessage, Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                    viewModel.importUri = null
                }
            )
        }
    }
}


/**
 * Composable displays an item button.
 *
 * @param setting   Title for the setting.
 * @param info      Info for the setting.
 * @param onClick   Callback to invoke when the item button is clicked.
 */
@Composable
private fun SettingsItemButton(
    setting: String,
    info: String,
    onClick: () -> Unit,
    endIcon: Painter? = null,
    prefixIcon: Painter? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable() {
                onClick()
            }
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prefixIcon != null) {
            Icon(
                painter = prefixIcon,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                contentDescription = "",
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_horizontal))
                    .size(dimensionResource(R.dimen.image_xs))
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = setting,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (endIcon != null) {
                    Icon(
                        painter = endIcon,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = dimensionResource(R.dimen.padding_horizontal) / 2)
                            .size(dimensionResource(R.dimen.image_xxs))
                    )
                }
            }
            Text(
                text = info,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


/**
 * Displays the general information which contains info about the app.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GeneralSection() {
    val context: Context = LocalContext.current
    val version: String? = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(R.dimen.margin_horizontal),
                vertical = dimensionResource(R.dimen.padding_vertical)
            )
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.mipmap.ic_launcher),
            contentDescription = "",
            modifier = Modifier.size(dimensionResource(R.dimen.image_l))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = dimensionResource(R.dimen.padding_horizontal))
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLargeEmphasized
            )
            if (version != null) {
                Text(
                    text = version,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = stringResource(R.string.settings_about_copyright, LocalDate.now().year.toString()),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_vertical))
            )
        }
    }
}
