package com.luutuanhoang.assignment2

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookingActivityTest {

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testBookingActivity() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val rentalItem = RentalItem(
            "Electric Guitar",
            4.5f,
            mutableListOf("6 strings", "Solid body", "Maple neck"),
            150,
            "Professional electric guitar with excellent tone",
            R.drawable.electric_guitar
        )

        val intent = Intent(context, BookingActivity::class.java).apply {
            putExtra(BookingActivity.EXTRA_RENTAL_ITEM, rentalItem)
            putExtra(BookingActivity.EXTRA_BALANCE, 1000)
            putExtra(BookingActivity.EXTRA_IMAGE, R.drawable.electric_guitar)
        }

        ActivityScenario.launch<BookingActivity>(intent).use {
            // Verify initial state
            onView(withId(R.id.itemDetails)).check(matches(withText(containsString("Electric Guitar"))))
            onView(withId(R.id.totalMoney)).check(matches(withText(containsString("Total: $150.00"))))

            // Increment months
            onView(withId(R.id.incrementButton)).perform(click())
            onView(withId(R.id.monthsTextView)).check(matches(withText("2")))
            onView(withId(R.id.totalMoney)).check(matches(withText(containsString("Total: $300.00"))))

            // Decrement months
            onView(withId(R.id.decrementButton)).perform(click())
            onView(withId(R.id.monthsTextView)).check(matches(withText("1")))
            onView(withId(R.id.totalMoney)).check(matches(withText(containsString("Total: $150.00"))))

            // Save booking
            onView(withId(R.id.saveButton)).perform(click())
        }
    }
}