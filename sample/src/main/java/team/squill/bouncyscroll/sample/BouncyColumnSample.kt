package team.squill.bouncyscroll.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import team.squill.bouncyscroll.BouncyColumn

@Composable
fun BouncyColumnSample(modifier: Modifier = Modifier) {
    BouncyColumn(modifier = modifier) {
        repeat(30) { index ->
            SampleItem(index = index, total = 30)
        }
    }
}
