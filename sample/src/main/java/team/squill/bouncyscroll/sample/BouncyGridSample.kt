package team.squill.bouncyscroll.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import team.squill.bouncyscroll.BouncyGrid

@Composable
fun BouncyGridSample(modifier: Modifier = Modifier) {
    BouncyGrid(
        itemCount = 30,
        columns = 3,
        modifier = modifier,
    ) { index ->
        SampleItem(index = index, total = 30)
    }
}
