package team.squill.bouncyscroll

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlin.math.ceil

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BouncyGrid(
    itemCount: Int,
    columns: Int,
    modifier: Modifier = Modifier,
    overscrollLimit: Float = BouncyDefaults.OverscrollLimit,
    bounceDamping: Float = BouncyDefaults.Damping,
    stiffness: Float = BouncyDefaults.Stiffness,
    dampingRatio: Float = BouncyDefaults.DampingRatio,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemContent: @Composable (index: Int) -> Unit
) {
    val offsetY = remember { Animatable(0f) }
    val nestedScrollConnection = rememberBouncyScrollConnection(
        offsetY = offsetY,
        overscrollLimit = overscrollLimit,
        bounceDamping = bounceDamping,
        stiffness = stiffness,
        dampingRatio = dampingRatio,
    )

    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        androidx.compose.foundation.layout.Column(
            modifier = modifier
                .nestedScroll(nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .graphicsLayer { translationY = offsetY.value },
            verticalArrangement = verticalArrangement,
        ) {
            val rowCount = ceil(itemCount / columns.toFloat()).toInt()
            for (row in 0 until rowCount) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = horizontalArrangement,
                ) {
                    for (col in 0 until columns) {
                        val index = row * columns + col
                        if (index < itemCount) {
                            Box(modifier = Modifier.weight(1f)) {
                                itemContent(index)
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
