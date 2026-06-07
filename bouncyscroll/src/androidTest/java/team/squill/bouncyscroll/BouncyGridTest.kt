package team.squill.bouncyscroll

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
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

class BouncyGridTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setUpContent(itemCount: Int = 50, columns: Int = 3) {
        composeTestRule.setContent {
            BouncyGrid(
                itemCount = itemCount,
                columns = columns,
                modifier = Modifier.fillMaxSize(),
            ) { i ->
                Text(
                    text = "Item $i",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("item_$i")
                )
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
    fun overscrollAtTop_thenSwipeUp_gridScrolls() {
        setUpContent()
        composeTestRule.onNodeWithTag("item_0").assertIsDisplayed()

        composeTestRule.onRoot().performTouchInput { swipeDown() }
        composeTestRule.mainClock.advanceTimeBy(100)

        composeTestRule.onRoot().performTouchInput { swipeUp() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("item_0").assertIsNotDisplayed()
    }

    @Test
    fun afterSpringSettles_gridRemainsScrollable() {
        setUpContent()

        composeTestRule.onRoot().performTouchInput { swipeDown() }
        composeTestRule.mainClock.advanceTimeBy(2000)
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().performTouchInput { swipeUp() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("item_0").assertIsNotDisplayed()
    }

    @Test
    fun lastRow_isFilledWithSpacersWhenItemCountNotDivisibleByColumns() {
        // 10 items, 3 columns → last row has 1 item + 2 spacers
        // item_9 must be visible initially (it's in the last row, row 4)
        setUpContent(itemCount = 10, columns = 3)
        composeTestRule.onNodeWithTag("item_9").assertIsDisplayed()
    }
}
