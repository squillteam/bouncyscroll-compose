package team.squill.bouncyscroll.sample

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import team.squill.bouncyscroll.BouncyLazyGrid

@Composable
fun BouncyLazyGridSample(modifier: Modifier = Modifier) {
    val items = remember { List(30) { it } }

    BouncyLazyGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
    ) {
        items(items) { index ->
            SampleItem(index = index, total = items.size)
        }
    }
}
