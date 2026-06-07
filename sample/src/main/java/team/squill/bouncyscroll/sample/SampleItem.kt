package team.squill.bouncyscroll.sample

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

@Composable
fun SampleItem(index: Int, total: Int) {
    val alpha = ((total - index) / total.toFloat() * 100f + 155).toInt()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(200, 200, 150, alpha)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Item $index", color = Color.Black)
    }
}
