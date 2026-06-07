package team.squill.bouncyscroll

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class BouncyLazyColumnTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setUpContent(itemCount: Int = 30) {
        composeTestRule.setContent {
            val items = remember { List(itemCount) { it } }
            BouncyLazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items) { i ->
                    Text(
                        text = "Item $i",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .testTag("item_$i")
                    )
                }
            }
        }
    }

    @Test
    fun initialState_firstItemIsVisible() {
        setUpContent()
        composeTestRule.onNodeWithTag("item_0").assertIsDisplayed()
    }

    @Test
    fun swipeUp_scrollsContentDown() {
        setUpContent()
        composeTestRule.onNodeWithTag("item_0").assertIsDisplayed()

        composeTestRule.onRoot().performTouchInput { swipeUp() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("item_0").assertIsNotDisplayed()
    }

    @Test
    fun overscrollAtTop_thenSwipeUp_listScrolls() {
        setUpContent()
        composeTestRule.onNodeWithTag("item_0").assertIsDisplayed()

        composeTestRule.onRoot().performTouchInput { swipeDown() }
        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule.onRoot().performTouchInput { swipeUp() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("item_0").assertIsNotDisplayed()
    }

    @Test
    fun afterSpringSettles_listRemainsScrollable() {
        setUpContent()

        composeTestRule.onRoot().performTouchInput { swipeDown() }
        composeTestRule.mainClock.advanceTimeBy(2000)
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().performTouchInput { swipeUp() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("item_0").assertIsNotDisplayed()
    }
}
