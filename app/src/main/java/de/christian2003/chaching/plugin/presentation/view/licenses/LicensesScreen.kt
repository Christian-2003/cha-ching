package de.christian2003.chaching.plugin.presentation.view.licenses

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import de.christian2003.chaching.R
import de.christian2003.chaching.plugin.presentation.ui.composables.ListItemContainer
import de.christian2003.chaching.plugin.presentation.ui.composables.Shape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/**
 * View displays a list of all licenses used by the app.
 *
 * @param viewModel     View model for the view.
 * @param onNavigateUp  Callback invoked to navigate up on the navigation stack.
 */
@Composable
fun LicensesScreen(
    viewModel: LicensesViewModel,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.licenses_title),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onNavigateUp()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator()
            }
            else {
                LicensesList(
                    licenses = viewModel.licenses,
                    onLicenseClicked = { license ->
                        viewModel.loadLicenseText(license)
                    }
                )
            }

            if (viewModel.displayedSoftwareName != null && viewModel.displayedLicenseText != null) {
                LicenseDialog(
                    softwareName = viewModel.displayedSoftwareName!!,
                    licenseText = viewModel.displayedLicenseText!!,
                    onDismiss = {
                        viewModel.displayedSoftwareName = null
                        viewModel.displayedLicenseText = null
                    }
                )
            }
        }
    }
}


/**
 * Composable displays a list of all licenses used.
 *
 * @param licenses          List of licenses to display.
 * @param onLicenseClicked  Callback invoked once a license is clicked.
 */
@Composable
fun LicensesList(
    licenses: List<License>,
    onLicenseClicked: (License) -> Unit
) {
    LazyColumn {
        itemsIndexed(licenses) { index, license ->
            LicensesListRow(
                license = license,
                isFirst = index == 0,
                isLast = index == licenses.size - 1,
                onLicenseClicked = onLicenseClicked
            )
        }
    }
}


/**
 * Composable displays a single license that is used by the app.
 *
 * @param license           License to display.
 * @param isFirst           Whether this is the first item in the list.
 * @param isLast            Whether this is the last item in the list.
 * @param onLicenseClicked  Callback invoked once the license is clicked.
 */
@Composable
fun LicensesListRow(
    license: License,
    isFirst: Boolean,
    isLast: Boolean,
    onLicenseClicked: (License) -> Unit
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
                    onLicenseClicked(license)
                }
                .padding(
                    vertical = dimensionResource(R.dimen.padding_vertical),
                    horizontal = dimensionResource(R.dimen.padding_horizontal)
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
                    painter = painterResource(R.drawable.ic_license),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = "",
                    modifier = Modifier.size(dimensionResource(R.dimen.image_xs))
                )
            }
            Text(
                text = license.softwareName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


/**
 * Composable displays a dialog through which a license can be displayed.
 *
 * @param softwareName      Name of the software whose license is being displayed..
 * @param licenseText       Text of the license to display.
 * @param onDismiss         Callback to close the dialog without deleting the petrol entry.
 */
@Composable
fun LicenseDialog(
    softwareName: String,
    licenseText: String,
    onDismiss: () -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        sheetGesturesEnabled = false
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = softwareName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                onDismiss()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cancel),
                            contentDescription = ""
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )

            HorizontalDivider()

            Text(
                text = licenseText,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = dimensionResource(R.dimen.margin_horizontal),
                        end = dimensionResource(R.dimen.margin_horizontal),
                        bottom = dimensionResource(R.dimen.padding_vertical)
                    )
            )
        }
    }
}
