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

class BouncyColumnTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setUpContent(itemCount: Int = 30) {
        composeTestRule.setContent {
            BouncyColumn(modifier = Modifier.fillMaxSize()) {
                repeat(itemCount) { i ->
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

    // Regression test for the freeze bug:
    // Before the fix, onPreScroll was consuming all scroll delta when offsetY crossed zero,
    // causing the list to stop responding after an overscroll gesture.
    @Test
    fun overscrollAtTop_thenSwipeUp_listScrolls() {
        setUpContent()
        composeTestRule.onNodeWithTag("item_0").assertIsDisplayed()

        // Pull down to trigger overscroll at the top
        composeTestRule.onRoot().performTouchInput { swipeDown() }
        // Advance time partway through the spring animation (offsetY is non-zero)
        composeTestRule.mainClock.advanceTimeBy(100)

        // Scroll down while animation is in progress
        composeTestRule.onRoot().performTouchInput { swipeUp() }
        composeTestRule.waitForIdle()

        // The list must have scrolled — item_0 is no longer visible
        composeTestRule.onNodeWithTag("item_0").assertIsNotDisplayed()
    }

    @Test
    fun afterSpringSettles_listRemainsScrollable() {
        setUpContent()

        // Trigger overscroll and let the spring fully settle
        composeTestRule.onRoot().performTouchInput { swipeDown() }
        composeTestRule.mainClock.advanceTimeBy(2000)
        composeTestRule.waitForIdle()

        // Scroll down — list must still respond
        composeTestRule.onRoot().performTouchInput { swipeUp() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("item_0").assertIsNotDisplayed()
    }
}
