package team.squill.bouncyscroll

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.launch

@Composable
fun BouncyColumn(
    modifier: Modifier = Modifier,
    overscrollLimit: Float = 300f,
    bounceDamping: Float = 0.4f,
    stiffness: Float = Spring.StiffnessLow,
    dampingRatio: Float = Spring.DampingRatioMediumBouncy,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return if (offsetY.value != 0f) {
                    val newOffset = (offsetY.value + available.y)
                        .coerceIn(-overscrollLimit, overscrollLimit)
                    scope.launch {
                        offsetY.snapTo(newOffset)
                    }
                    available
                } else {
                    Offset.Zero
                }
            }
        }
    }

    Column(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
            .verticalScroll(rememberScrollState())
            .graphicsLayer {
                translationY = offsetY.value
            },
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}