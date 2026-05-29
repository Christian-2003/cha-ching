package de.christian2003.chaching.plugin.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class SuccessColorScheme(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color
)


val successLightScheme = SuccessColorScheme(
    success = successLight,
    onSuccess = onSuccessLight,
    successContainer = successContainerLight,
    onSuccessContainer = onSuccessContainerLight
)

val successDarkScheme = SuccessColorScheme(
    success = successDark,
    onSuccess = onSuccessDark,
    successContainer = successContainerDark,
    onSuccessContainer = onSuccessContainerDark
)

val successMediumContrastLightColorScheme = SuccessColorScheme(
    success = successLightMediumContrast,
    onSuccess = onSuccessLightMediumContrast,
    successContainer = successContainerLightMediumContrast,
    onSuccessContainer = onSuccessContainerLightMediumContrast
)

val successMediumContrastDarkColorScheme = SuccessColorScheme(
    success = successDarkMediumContrast,
    onSuccess = onSuccessDarkMediumContrast,
    successContainer = successContainerDarkMediumContrast,
    onSuccessContainer = onSuccessContainerDarkMediumContrast
)

val successHighContrastLightColorScheme = SuccessColorScheme(
    success = successLightHighContrast,
    onSuccess = onSuccessLightHighContrast,
    successContainer = successContainerLightHighContrast,
    onSuccessContainer = onSuccessContainerLightHighContrast
)

val successHighContrastDarkColorScheme = SuccessColorScheme(
    success = successDarkHighContrast,
    onSuccess = onSuccessDarkHighContrast,
    successContainer = successContainerDarkHighContrast,
    onSuccessContainer = onSuccessContainerDarkHighContrast
)


val LocalSuccessColors = staticCompositionLocalOf {
    SuccessColorScheme(
        success = Color.Unspecified,
        onSuccess = Color.Unspecified,
        successContainer = Color.Unspecified,
        onSuccessContainer = Color.Unspecified
    )
}

val MaterialTheme.successColors: SuccessColorScheme
    @Composable get() = LocalSuccessColors.current
