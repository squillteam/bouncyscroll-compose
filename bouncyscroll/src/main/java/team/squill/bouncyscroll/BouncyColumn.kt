package team.squill.bouncyscroll

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch

@Composable
fun BouncyColumn(
    modifier: Modifier = Modifier,
    overscrollLimit: Float = BouncyDefaults.OverscrollLimit,
    bounceDamping: Float = BouncyDefaults.Damping,
    stiffness: Float = BouncyDefaults.Stiffness,
    dampingRatio: Float = BouncyDefaults.DampingRatio,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val currentOffset = offsetY.value
                if (currentOffset == 0f) return Offset.Zero

                val newOffset = (currentOffset + available.y)
                    .coerceIn(minOf(0f, currentOffset), maxOf(0f, currentOffset))
                val consumed = newOffset - currentOffset
                scope.launch { offsetY.snapTo(newOffset) }
                return Offset(0f, consumed)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y != 0f) {
                    val newOffset = (offsetY.value + available.y * bounceDamping)
                        .coerceIn(-overscrollLimit, overscrollLimit)
                    scope.launch { offsetY.snapTo(newOffset) }
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = dampingRatio,
                        stiffness = stiffness
                    )
                )
                return super.onPostFling(consumed, available)
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