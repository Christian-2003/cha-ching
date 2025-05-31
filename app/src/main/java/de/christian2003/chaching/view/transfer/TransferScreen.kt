package de.christian2003.chaching.view.transfer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import de.christian2003.chaching.R
import de.christian2003.chaching.ui.composables.TextInput
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    viewModel: TransferViewModel,
    onNavigateUp: () -> Unit
) {
    val numberFormat: NumberFormat = NumberFormat.getInstance(Locale.getDefault())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (viewModel.isCreating) { stringResource(R.string.transfer_titleCreate) } else { stringResource(R.string.transfer_titleEdit) })
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
            TextInput(
                value = viewModel.value,
                onValueChange = {
                    viewModel.updateValue(it)
                },
                label = stringResource(R.string.transfer_valueLabel),
                errorMessage = viewModel.valueErrorMessage,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}
