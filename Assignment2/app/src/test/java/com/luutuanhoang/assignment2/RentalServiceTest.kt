package com.luutuanhoang.assignment2

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RentalServiceTest {

    private lateinit var rentalService: RentalService

    @Before
    fun setup() {
        rentalService = RentalService()
    }

    @Test
    fun testInitialBalance() {
        assertEquals(5000, rentalService.balance)
    }

    @Test
    fun testAddBalance() {
        rentalService.addBalance(1000)
        assertEquals(6000, rentalService.balance)
    }

    @Test
    fun testDeductBalance() {
        // Initial balance is 5000
        rentalService.deductBalance(1000)
        assertEquals(4000, rentalService.balance)
    }

    @Test
    fun testDeductBalanceInsufficientFunds() {
        // Initial balance is 5000
        val initialBalance = rentalService.balance
        try {
            rentalService.deductBalance(6000)
            fail("Expected an IllegalArgumentException to be thrown")
        } catch (e: IllegalArgumentException) {
            // Exception is expected
            assertEquals("Insufficient balance", e.message)
            // Verify balance didn't change
            assertEquals(initialBalance, rentalService.balance)
        }
    }

    @Test
    fun testGetRentalItems() {
        val items = rentalService.getRentalItems()
        assertNotNull(items)
        assertTrue(items.isNotEmpty())
    }

    @Test
    fun testUpdateRentalItem() {
        val initialItem = rentalService.getRentalItems()[0]
        val initialRating = initialItem.rating
        val newAttributes = arrayListOf("New Feature 1", "New Feature 2")
        val newRating = 4.5f

        rentalService.updateRentalItem(0, newAttributes, newRating)

        val updatedItem = rentalService.getRentalItems()[0]
        assertEquals(newRating, updatedItem.rating, 0.01f)
        assertEquals(newAttributes, updatedItem.multiChoiceAttribute)
    }

    @Test
    fun testAddAndDeductBalanceMultipleTimes() {
        // Initial balance: 5000
        rentalService.addBalance(500)  // Now 5500
        rentalService.deductBalance(200)  // Now 5300
        rentalService.addBalance(700)  // Now 6000
        rentalService.deductBalance(1500)  // Now 4500

        assertEquals(4500, rentalService.balance)
    }

    @Test
    fun testDeductZeroAmount() {
        val initialBalance = rentalService.balance
        rentalService.deductBalance(0)
        assertEquals(initialBalance, rentalService.balance)
    }

    @Test
    fun testAddZeroAmount() {
        val initialBalance = rentalService.balance
        rentalService.addBalance(0)
        assertEquals(initialBalance, rentalService.balance)
    }
}