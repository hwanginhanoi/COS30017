package com.luutuanhoang.assignment2

/**
 * Service class that manages rental items and user balance.
 * Acts as a data repository for the application and handles business logic
 * related to rental operations and financial transactions.
 */
class RentalService {
    /**
     * The user's current balance in dollars.
     * - Initial value is 5000
     * - Private setter to prevent direct modification from outside the class
     * - Can only be modified through addBalance and deductBalance methods
     */
    var balance: Int = 5000
        private set

    /**
     * The collection of available rental items.
     * Initialized with sample data through createSampleRentalItems() method.
     */
    private val rentalItems: List<RentalItem> = createSampleRentalItems()

    /**
     * Returns the list of available rental items.
     * @return An unmodifiable list of rental items
     */
    fun getRentalItems(): List<RentalItem> = rentalItems

    /**
     * Updates a rental item's attributes and rating.
     * Used after a user modifies an item during the booking process.
     *
     * @param position The index position of the item to update
     * @param selectedAttributes New list of attributes to replace existing ones
     * @param updatedRating New rating value for the item
     */
    fun updateRentalItem(position: Int, selectedAttributes: List<String>, updatedRating: Float) {
        val currentItem = rentalItems[position]
        currentItem.multiChoiceAttribute.clear()
        currentItem.multiChoiceAttribute.addAll(selectedAttributes)
        currentItem.rating = updatedRating
    }

    /**
     * Increases the user's balance by the specified amount.
     * Used when the user adds funds to their account.
     *
     * @param amount Amount to add to the balance (in dollars)
     */
    fun addBalance(amount: Int) {
        balance += amount
    }

    /**
     * Decreases the user's balance by the specified amount.
     * Used when a user rents an instrument.
     *
     * @param amount Amount to deduct from the balance (in dollars)
     * @throws IllegalArgumentException if the user has insufficient balance
     */
    fun deductBalance(amount: Int) {
        if (balance >= amount) {
            balance -= amount
        } else {
            throw IllegalArgumentException("Insufficient balance")
        }
    }

    /**
     * Creates a list of sample rental items for demonstration.
     * In a real application, this data would likely come from a database.
     *
     * @return List of sample RentalItem objects with predefined data
     */
    private fun createSampleRentalItems(): List<RentalItem> {
        return listOf(
            RentalItem(
                "Electric Guitar",
                4.5f,
                mutableListOf("6 strings", "Solid body", "Maple neck"),
                150,
                "Professional electric guitar with excellent tone",
                R.drawable.electric_guitar
            ),
            RentalItem(
                "Digital Piano",
                5.0f,
                mutableListOf("88 keys", "Weighted", "Built-in speakers"),
                200,
                "Full-size digital piano with realistic feel",
                R.drawable.digital_piano
            ),
            RentalItem(
                "Drum Kit",
                4.0f,
                mutableListOf("5 pieces", "Cymbals included", "Hardware included"),
                250,
                "Complete acoustic drum set for beginners",
                R.drawable.drum_kit
            )
        )
    }
}