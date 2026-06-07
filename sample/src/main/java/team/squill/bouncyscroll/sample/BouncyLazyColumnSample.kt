package team.squill.bouncyscroll.sample

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import team.squill.bouncyscroll.BouncyLazyColumn

@Composable
fun BouncyLazyColumnSample(modifier: Modifier = Modifier) {
    val items = remember { List(30) { it } }

    BouncyLazyColumn(modifier = modifier) {
        items(items) { index ->
            SampleItem(index = index, total = items.size)
        }
    }
}
