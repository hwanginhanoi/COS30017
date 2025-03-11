package com.luutuanhoang.assignment2

class RentalService {
    var balance: Int = 5000
        private set

    private val rentalItems: List<RentalItem> = createSampleRentalItems()

    fun getRentalItems(): List<RentalItem> = rentalItems

    fun updateRentalItem(position: Int, selectedAttributes: List<String>, updatedRating: Float) {
        val currentItem = rentalItems[position]
        currentItem.multiChoiceAttribute.clear()
        currentItem.multiChoiceAttribute.addAll(selectedAttributes)
        currentItem.rating = updatedRating
    }

    fun addBalance(amount: Int) {
        balance += amount
    }

    fun deductBalance(amount: Int) {
        if (balance >= amount) {
            balance -= amount
        } else {
            throw IllegalArgumentException("Insufficient balance")
        }
    }

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