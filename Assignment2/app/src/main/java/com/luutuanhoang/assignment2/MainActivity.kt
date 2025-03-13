package com.luutuanhoang.assignment2

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar

/**
 * Main activity of the Instrument Rental application.
 * Displays a scrollable list of rental instruments and provides navigation controls.
 * Users can browse instruments, view details, and initiate the booking process.
 */
class MainActivity : AppCompatActivity() {
    // UI component references
    private lateinit var viewPager: ViewPager2
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var borrowButton: Button

    // Data service for managing rental items and user balance
    private lateinit var rentalService: RentalService

    /**
     * Activity result launcher for handling the booking flow.
     * Processes results from BookingActivity including:
     * - Selected instrument attributes
     * - Updated rating
     * - Rental duration
     * - Balance deduction
     */
    private val bookingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Extract data from result intent
            val selectedAttributes = result.data?.getStringArrayListExtra(BookingActivity.EXTRA_SELECTED_ATTRIBUTES)
            val updatedRating = result.data?.getFloatExtra(BookingActivity.EXTRA_UPDATED_RATING, rentalService.getRentalItems()[viewPager.currentItem].rating)
            val months = result.data?.getIntExtra(BookingActivity.EXTRA_BORROW_MONTHS, 1) ?: 1
            val currentItem = rentalService.getRentalItems()[viewPager.currentItem]

            // Update user balance based on rental cost
            rentalService.deductBalance(currentItem.pricePerMonth * months)
            updateActionBarTitle()

            // Update rental item with new attributes and rating
            selectedAttributes?.let {
                rentalService.updateRentalItem(viewPager.currentItem, it, updatedRating ?: rentalService.getRentalItems()[viewPager.currentItem].rating)
            }

            // Update UI to reflect changes
            viewPager.adapter?.notifyItemChanged(viewPager.currentItem)
            (supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}") as? RentalItemFragment)?.updateItem(rentalService.getRentalItems()[viewPager.currentItem])

            // Show confirmation message
            Snackbar.make(
                findViewById(android.R.id.content),
                "Booked ${currentItem.name} for $months month(s) with ${currentItem.multiChoiceAttribute.size} features and rating ${currentItem.rating}",
                Snackbar.LENGTH_LONG
            ).show()
        } else if (result.resultCode == RESULT_CANCELED) {
            // Handle canceled booking
            Snackbar.make(
                findViewById(android.R.id.content),
                "Booking canceled",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Initialize activity, set up UI components and event handlers
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the rental service
        rentalService = RentalService()

        // Get references to UI components
        viewPager = findViewById(R.id.viewPager)
        previousButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)
        borrowButton = findViewById(R.id.borrowButton)

        // Set up ViewPager with rental items
        viewPager.adapter = RentalPagerAdapter(rentalService.getRentalItems(), this)

        // Display current balance in action bar
        updateActionBarTitle()

        // Set up navigation button click listeners
        previousButton.setOnClickListener {
            if (viewPager.currentItem > 0) viewPager.currentItem -= 1
        }

        nextButton.setOnClickListener {
            if (viewPager.currentItem < rentalService.getRentalItems().size - 1) viewPager.currentItem += 1
        }

        // Set up borrow button to launch booking activity
        borrowButton.setOnClickListener {
            val currentItem = rentalService.getRentalItems()[viewPager.currentItem]
            val intent = Intent(this, BookingActivity::class.java).apply {
                putExtra(BookingActivity.EXTRA_RENTAL_ITEM, currentItem)
                putExtra(BookingActivity.EXTRA_BALANCE, rentalService.balance)
                putExtra(BookingActivity.EXTRA_IMAGE, currentItem.imageResId)
            }
            bookingLauncher.launch(intent)
        }

        // Update navigation button states when page changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Disable Previous button at first item, Next button at last item
                previousButton.isEnabled = position > 0
                nextButton.isEnabled = position < rentalService.getRentalItems().size - 1
            }
        })
    }

    /**
     * Initialize the options menu in the action bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * Handle options menu item selection
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_balance -> {
                showAddBalanceDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Display dialog for adding balance to the user's account
     */
    private fun showAddBalanceDialog() {
        val editText = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Balance")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                // Parse input and update balance
                val amount = editText.text.toString().toIntOrNull() ?: 0
                rentalService.addBalance(amount)
                updateActionBarTitle()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    /**
     * Update the action bar title to display current balance
     */
    private fun updateActionBarTitle() {
        supportActionBar?.title = "Balance: ${rentalService.balance}"
    }
}