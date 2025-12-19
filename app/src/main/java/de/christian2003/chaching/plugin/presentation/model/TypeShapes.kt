package de.christian2003.chaching.plugin.presentation.model

import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.graphics.shapes.RoundedPolygon
import de.christian2003.chaching.domain.type.TypeIcon


enum class TypeShapes(
    val shape: RoundedPolygon
) {

    Circle(MaterialShapes.Circle),
    VerySunny(MaterialShapes.VerySunny),
    Sunny(MaterialShapes.Sunny),
    Cookie6Sided(MaterialShapes.Cookie6Sided),
    Cookie12Sided(MaterialShapes.Cookie12Sided),
    Clover8Leaf(MaterialShapes.Clover8Leaf),
    SoftBurst(MaterialShapes.SoftBurst),
    Flower(MaterialShapes.Flower);

    companion object {

        fun getShapeForTypeIcon(icon: TypeIcon): TypeShapes {
            return entries[icon.ordinal % entries.size]
        }


        @Composable
        fun getShapeColor(icon: TypeIcon): Color {
            return when (icon.ordinal % 3) {
                0 -> MaterialTheme.colorScheme.primaryContainer
                1 -> MaterialTheme.colorScheme.secondaryContainer
                2 -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceContainerHigh
            }
        }


        @Composable
        fun getOnShapeColor(icon: TypeIcon): Color {
            return when (icon.ordinal % 3) {
                0 -> MaterialTheme.colorScheme.onPrimaryContainer
                1 -> MaterialTheme.colorScheme.onSecondaryContainer
                2 -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.onSurface
            }
        }

    }

}
