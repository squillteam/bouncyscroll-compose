package team.squill.bouncyscroll

import androidx.compose.animation.core.Spring

object BouncyDefaults {
    const val OverscrollLimit = 300f
    const val Damping = 0.4f
    const val Stiffness = Spring.StiffnessLow
    const val DampingRatio = Spring.DampingRatioMediumBouncy
}