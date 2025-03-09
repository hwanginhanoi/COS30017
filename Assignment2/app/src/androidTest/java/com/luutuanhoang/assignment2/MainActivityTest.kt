package com.luutuanhoang.assignment2

import android.view.View
import android.widget.Button
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testInitialUIElementsExist() {
        // Check that all main UI elements are visible
        onView(withId(R.id.viewPager)).check(matches(isDisplayed()))
        onView(withId(R.id.previousButton)).check(matches(isDisplayed()))
        onView(withId(R.id.nextButton)).check(matches(isDisplayed()))
        onView(withId(R.id.borrowButton)).check(matches(isDisplayed()))
    }

    @Test
    fun testInitialNavigationButtonStates() {
        // In the initial state (first item), previous button should be disabled
        activityRule.scenario.onActivity { activity ->
            val previousButton = activity.findViewById<Button>(R.id.previousButton)
            val nextButton = activity.findViewById<Button>(R.id.nextButton)
            assertEquals(false, previousButton.isEnabled)
            assertEquals(true, nextButton.isEnabled)
        }
    }

    @Test
    fun testNavigationButtons() {
        // Click next button
        onView(withId(R.id.nextButton)).perform(click())

        // Now both buttons should be enabled if there are at least 3 items
        activityRule.scenario.onActivity { activity ->
            val previousButton = activity.findViewById<Button>(R.id.previousButton)
            assertEquals(true, previousButton.isEnabled)
        }

        // Click previous button to go back to first item
        onView(withId(R.id.previousButton)).perform(click())

        // Check that previous button is disabled again
        activityRule.scenario.onActivity { activity ->
            val previousButton = activity.findViewById<Button>(R.id.previousButton)
            assertEquals(false, previousButton.isEnabled)
        }
    }

    @Test
    fun testNavigationToLastItem() {
        activityRule.scenario.onActivity { activity ->
            val viewPager = activity.findViewById<ViewPager2>(R.id.viewPager)
            val rentalService = RentalService()
            val lastItemPosition = rentalService.getRentalItems().size - 1

            // Navigate to last item
            viewPager.setCurrentItem(lastItemPosition, false)

            // Check navigation button states at last item
            val previousButton = activity.findViewById<Button>(R.id.previousButton)
            val nextButton = activity.findViewById<Button>(R.id.nextButton)
            assertEquals(true, previousButton.isEnabled)
            assertEquals(false, nextButton.isEnabled)
        }
    }

    @Test
    fun testAddBalanceDialog() {
        // Initial balance should be displayed in title
        activityRule.scenario.onActivity { activity ->
            val initialBalance = RentalService().balance
            assertEquals("Balance: $initialBalance", activity.supportActionBar?.title)
        }

        // Open the menu and click "Add Balance"
        onView(withId(R.id.action_add_balance)).perform(click())

        // Verify dialog appears
        onView(withText("Add Balance"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        // Enter amount and click Add
        onView(withClassName(endsWith("EditText")))
            .perform(typeText("100"))
        onView(withText("Add"))
            .perform(click())

        // Verify balance was updated in title
        activityRule.scenario.onActivity { activity ->
            val expectedBalance = RentalService().balance + 100
            assertEquals("Balance: $expectedBalance", activity.supportActionBar?.title)
        }
    }

    @Test
    fun testBorrowButtonOpensBookingActivity() {
        // Click on borrow button
        onView(withId(R.id.borrowButton)).perform(click())

        // Verify booking activity is launched by checking for elements unique to that activity
        // Instead of checking for title "Booking Details", check for unique elements
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()))
        onView(withText("Save Booking")).check(matches(isDisplayed()))
        onView(withId(R.id.cancelButton)).check(matches(isDisplayed()))

        // Alternative approach: check for the item details section
        onView(withId(R.id.itemImageBooking)).check(matches(isDisplayed()))
        onView(withId(R.id.itemDetails)).check(matches(isDisplayed()))
    }

    private fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            private var currentIndex = 0
            private var viewObjHash = 0

            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View): Boolean {
                if (matcher.matches(view) && currentIndex == index) {
                    viewObjHash = view.hashCode()
                    return true
                }
                if (matcher.matches(view)) {
                    currentIndex++
                }
                return false
            }
        }
    }
}