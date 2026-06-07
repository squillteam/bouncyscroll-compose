package team.squill.bouncyscroll

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch

@Composable
internal fun rememberBouncyScrollConnection(
    offsetY: Animatable<Float, androidx.compose.animation.core.AnimationVector1D>,
    overscrollLimit: Float,
    bounceDamping: Float,
    stiffness: Float,
    dampingRatio: Float,
): NestedScrollConnection {
    val scope = rememberCoroutineScope()
    return remember {
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

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(dampingRatio = dampingRatio, stiffness = stiffness)
                )
                return super.onPostFling(consumed, available)
            }
        }
    }
}
