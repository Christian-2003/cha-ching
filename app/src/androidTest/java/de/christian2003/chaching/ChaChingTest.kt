package de.christian2003.chaching

import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToString
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChaChingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        composeTestRule.launchChaChing(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun onboardingTest() {
        //Navigate through onboarding:
        composeTestRule.onNodeWithText("Document your earnings").assertExists()
        composeTestRule.onNodeWithText("Next").assertExists().performClick()
        composeTestRule.onNodeWithText("Analyze your earnings").assertExists()
        composeTestRule.onNodeWithText("Next").assertExists().performClick()
        composeTestRule.onNodeWithText("Choose earning types to track").assertExists()
        composeTestRule.onNodeWithText("Finish").assertDoesNotExist()
        composeTestRule.onNodeWithText("Salary").assertExists().onParent().onChildren().filter(
            hasClickAction() and isToggleable() and hasAnySibling(hasText("Salary"))
        ).onFirst().performClick()
        composeTestRule.onNodeWithText("Finish").assertExists().performClick()

        //On main screen:
        composeTestRule.onNodeWithText("Cha Ching").assertExists()
        composeTestRule.onNodeWithText("No data to show").assertExists()
    }

    @Test
    fun appLaunches() {
        println(composeTestRule.onRoot().printToString())
    }

}
