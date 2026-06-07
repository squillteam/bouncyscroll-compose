package team.squill.bouncyscroll.sample

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import team.squill.bouncyscroll.BouncyColumn

@Composable
fun SampleScreen(modifier: Modifier = Modifier) {
    val times = 30
    BouncyColumn(modifier = modifier) {
        repeat(times) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(200,200,150,(((times - index) / times.toFloat()) * 100f + 155).toInt())),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Item $index", color = Color.Black)
            }
        }
    }
}
