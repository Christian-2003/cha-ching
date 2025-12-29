package de.christian2003.chaching.plugin.presentation.view.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.ui.composables.Headline
import java.time.LocalDate
import androidx.core.net.toUri
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import de.christian2003.chaching.domain.apps.AppItem
import de.christian2003.chaching.plugin.presentation.ui.composables.ListItemContainer
import de.christian2003.chaching.plugin.presentation.ui.composables.NavigationBarProtection
import de.christian2003.chaching.plugin.presentation.ui.theme.ThemeContrast
import okhttp3.OkHttpClient
import java.time.format.DateTimeFormatter


/**
 * Screen displays settings of the app.
 *
 * @param viewModel                 View model.
 * @param onNavigateUp              Callback invoked to navigate up on the navigation stack.
 * @param onNavigateToTypes         Callback invoked to navigate to the screen displaying the list
 *                                  of types.
 * @param onNavigateToTrash         Callback invoked to navigate to the screen displaying the trash bin.
 * @param onNavigateToLicenses      Callback invoked to navigate to the screen displaying licenses.
 * @param onNavigateToHelpMessages  Callback invoked to navigate to the screen displaying the list
 *                                  of help messages.
 * @param onNavigateToOnboarding    Callback invoked to navigate to the app onboarding.
 * @param onUseGlobalThemeChange    Callback invoked once the user changes whether to use global theme.
 * @param onThemeContrastChange     Callback invoked once the theme contrast changes.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateUp: () -> Unit,
    onNavigateToTypes: () -> Unit,
    onNavigateToTrash: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onNavigateToHelpMessages: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onUseGlobalThemeChange: (Boolean) -> Unit,
    onThemeContrastChange: (ThemeContrast) -> Unit
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
        val bottomPadding: Dp = innerPadding.calculateBottomPadding()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                )
        ) {
            //General
            item {
                GeneralSection()
            }


            //Customization:
            item {
                Headline(
                    title = stringResource(R.string.settings_customization),
                )
                SettingsItemSwitch(
                    title = stringResource(R.string.settings_customization_globalThemeTitle),
                    info = stringResource(R.string.settings_customization_globalThemeInfo),
                    prefixIcon = painterResource(R.drawable.ic_theme),
                    checked = viewModel.useGlobalTheme,
                    onCheckedChange = {
                        viewModel.updateUseGlobalTheme(it)
                        onUseGlobalThemeChange(it)
                    },
                    isFirst = true,
                    isLast = viewModel.useGlobalTheme
                )
                AnimatedVisibility(
                    visible = !viewModel.useGlobalTheme,
                    enter = expandVertically(spring(Spring.DampingRatioMediumBouncy)) + fadeIn(spring(Spring.DampingRatioMediumBouncy)),
                    exit = shrinkVertically(spring(Spring.DampingRatioMediumBouncy)) + fadeOut(spring(Spring.DampingRatioMediumBouncy))
                ) {
                    SettingsItemButton(
                        setting = stringResource(R.string.settings_customization_contrastTitle),
                        info = stringResource(R.string.settings_customization_contrastInfo),
                        onClick = {
                            viewModel.dialog = SettingsScreenDialog.Contrast
                        },
                        prefixIcon = painterResource(R.drawable.ic_contrast),
                        isLast = true
                    )
                }
            }


            //Data
            item {
                Headline(
                    title = stringResource(R.string.settings_data),
                )
                SettingsItemButton(
                    setting = stringResource(R.string.settings_data_typesTitle),
                    info = stringResource(R.string.settings_data_typesInfo),
                    onClick = onNavigateToTypes,
                    endIcon = painterResource(R.drawable.ic_next),
                    prefixIcon = painterResource(R.drawable.ic_types),
                    isFirst = true
                )
                SettingsItemButton(
                    setting = stringResource(R.string.settings_data_trashTitle),
                    info = stringResource(R.string.settings_data_trashInfo),
                    onClick = onNavigateToTrash,
                    endIcon = painterResource(R.drawable.ic_next),
                    prefixIcon = painterResource(R.drawable.ic_delete),
                    badgeCount = viewModel.numberOfTypesInTrash
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
                    prefixIcon = painterResource(R.drawable.ic_import),
                    isLast = true
                )
            }


            //Help
            item {
                Headline(
                    title = stringResource(R.string.settings_help),
                )
                SettingsItemButton(
                    setting = stringResource(R.string.settings_help_helpMessagesTitle),
                    info = stringResource(R.string.settings_help_helpMessagesInfo),
                    onClick = onNavigateToHelpMessages,
                    endIcon = painterResource(R.drawable.ic_next),
                    prefixIcon = painterResource(R.drawable.ic_help_outlined),
                    isFirst = true
                )
                SettingsItemButton(
                    setting = stringResource(R.string.settings_help_onboardingTitle),
                    info = stringResource(R.string.settings_help_onboardingInfo),
                    onClick = onNavigateToOnboarding,
                    endIcon = painterResource(R.drawable.ic_next),
                    prefixIcon = painterResource(R.drawable.ic_welcome),
                    isLast = true
                )
            }


            //About
            item {
                Headline(
                    title = stringResource(R.string.settings_about),
                )
                SettingsItemButton(
                    setting = stringResource(R.string.settings_about_licensesTitle),
                    info = stringResource(R.string.settings_about_licensesInfo),
                    onClick = onNavigateToLicenses,
                    endIcon = painterResource(R.drawable.ic_next),
                    prefixIcon = painterResource(R.drawable.ic_license),
                    isFirst = true
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
                    prefixIcon = painterResource(R.drawable.ic_android),
                    isLast = true
                )
            }


            //Apps:
            item {
                AppsSection(
                    apps = viewModel.apps,
                    client = viewModel.client,
                    onAppClick = { app ->
                        val intent = Intent(Intent.ACTION_VIEW, app.url)
                        context.startActivity(intent)
                    }
                )
            }


            //Spacer at the bottom to scroll beneath the navigation bar:
            item {
                Box(
                    modifier = Modifier.height(bottomPadding)
                )
            }
        }

        NavigationBarProtection(
            height = bottomPadding
        )

        when (viewModel.dialog) {
            SettingsScreenDialog.Contrast -> {
                ContrastDialog(
                    contrast = viewModel.themeContrast,
                    onDismiss = {
                        viewModel.dialog = SettingsScreenDialog.None
                    },
                    onSave = { themeContrast ->
                        viewModel.dialog = SettingsScreenDialog.None
                        viewModel.updateThemeContrast(themeContrast)
                        onThemeContrastChange(themeContrast)
                    },
                    onFormatValue = {
                        viewModel.formatValue(it)
                    },
                    onFormatDate = {
                        viewModel.formatDate(it)
                    }
                )
            }
            else -> { }
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
 * @param setting       Title for the setting.
 * @param info          Info for the setting.
 * @param onClick       Callback to invoke when the item button is clicked.
 * @param endIcon       End icon.
 * @param isFirst       Whether this is the first item in the list.
 * @param isLast        Whether this is the last item in the list.
 * @param prefixIcon    Optional prefix icon.
 * @param badgeCount    Count to show in the badge. If this is 0, the badge is hidden.
 */
@Composable
private fun SettingsItemButton(
    setting: String,
    info: String,
    onClick: () -> Unit,
    endIcon: Painter? = null,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    prefixIcon: Painter? = null,
    badgeCount: Int = 0
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable() {
                    onClick()
                }
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_horizontal),
                    vertical = dimensionResource(R.dimen.padding_vertical)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (prefixIcon != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.padding_horizontal))
                        .size(dimensionResource(R.dimen.image_xs))
                ) {
                    Icon(
                        painter = prefixIcon,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize()
                    )
                    if (badgeCount > 0) {
                        Badge(
                            modifier = Modifier.offset(
                                x = dimensionResource(R.dimen.image_xs) / 3,
                                y = dimensionResource(R.dimen.image_xs) / -3
                            )
                        ) {
                            Text(badgeCount.toString())
                        }
                    }
                }
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
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
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
}


/**
 * Composable displays an item switch.
 *
 * @param title             Title for the setting.
 * @param info              Info for the setting.
 * @param checked           Whether the switch is checked.
 * @param onCheckedChange   Callback invoked once the switch is (un)checked.
 * @param isFirst           Whether this is the first item in the list.
 * @param isLast            Whether this is the last item in the list.
 * @param prefixIcon        Optional prefix icon.
 */
@Composable
fun SettingsItemSwitch(
    title: String,
    info: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    prefixIcon: Painter? = null
) {
    ListItemContainer(
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCheckedChange(!checked)
                }
                .padding(
                    vertical = dimensionResource(R.dimen.padding_vertical),
                    horizontal = dimensionResource(R.dimen.padding_horizontal)
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
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = info,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_horizontal))
            )
        }
    }
}


/**
 * Displays the general information which contains info about the app.
 */
@Composable
private fun GeneralSection() {
    val context: Context = LocalContext.current
    val version: String? = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = dimensionResource(R.dimen.margin_horizontal),
                end = dimensionResource(R.dimen.margin_horizontal),
                bottom = dimensionResource(R.dimen.padding_vertical)
            )
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(
                horizontal = dimensionResource(R.dimen.padding_horizontal),
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


/**
 * Displays the list of other apps that the user might check out.
 *
 * @param apps          List of apps to display.
 * @param client        OkHttpClient to use for loading the SVG images.
 * @param onAppClick    Callback invoked once an app is clicked.
 */
@Composable
private fun AppsSection(
    apps: List<AppItem>,
    client: OkHttpClient,
    onAppClick: (AppItem) -> Unit
) {
    val imageLoader: ImageLoader = ImageLoader.Builder(LocalContext.current.applicationContext)
        .okHttpClient(client)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()
    AnimatedVisibility(apps.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Headline(
                title = stringResource(R.string.settings_apps),
            )
            apps.forEachIndexed { index, app ->
                ListItemContainer(
                    isFirst = index == 0,
                    isLast = index == apps.size - 1
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAppClick(app)
                            }
                            .padding(
                                horizontal = dimensionResource(R.dimen.margin_horizontal),
                                vertical = dimensionResource(R.dimen.padding_vertical)
                            )
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(app.iconUrl)
                                .build(),
                            imageLoader = imageLoader,
                            contentDescription = "",
                            modifier = Modifier
                                .padding(end = dimensionResource(R.dimen.padding_horizontal))
                                .size(dimensionResource(R.dimen.image_xs))
                        )
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = app.displayName,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    painter = painterResource(R.drawable.ic_external),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = "",
                                    modifier = Modifier
                                        .padding(start = dimensionResource(R.dimen.padding_horizontal) / 2)
                                        .size(dimensionResource(R.dimen.image_xxs))
                                )
                            }
                            val scheme: String? = app.url.scheme
                            val host: String? = app.url.host
                            if (scheme != null && host != null) {
                                Text(
                                    text = "$scheme://$host",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
