package com.luutuanhoang.assignment2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.Locale

/**
 * Activity for handling instrument booking functionality.
 * Allows users to:
 * - View instrument details
 * - Select rental duration in months
 * - Add/remove instrument attributes
 * - Rate the instrument
 * - Complete or cancel booking
 */
class BookingActivity : AppCompatActivity() {
    companion object {
        // Intent extra keys for data passing between activities
        const val EXTRA_RENTAL_ITEM = "rental_item"
        const val EXTRA_SELECTED_ATTRIBUTES = "selected_attributes"
        const val EXTRA_UPDATED_RATING = "updated_rating"
        const val EXTRA_BORROW_MONTHS = "borrow_months"
        const val EXTRA_BALANCE = "balance"
        const val EXTRA_IMAGE = "extra_image"
    }

    // UI component references
    private lateinit var itemDetails: TextView
    private lateinit var selectedAttributes: ChipGroup
    private lateinit var ratingBar: RatingBar
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var monthsTextView: TextView
    private lateinit var incrementButton: Button
    private lateinit var decrementButton: Button
    private lateinit var totalMoneyTextView: TextView

    // Data properties
    private lateinit var rentalItem: RentalItem
    private var balance: Int = 0
    private var months: Int = 1
    private val selectedAttributesList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        // Retrieve rental item from intent with version-compatible approach
        rentalItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_RENTAL_ITEM, RentalItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_RENTAL_ITEM)
        } ?: throw IllegalStateException("No rental item provided")

        // Get user's current balance
        balance = intent.getIntExtra(EXTRA_BALANCE, 0)

        // Initialize UI components
        initializeUIComponents()

        // Display item details
        itemDetails.text = getString(R.string.item_details_format,
            rentalItem.name,
            rentalItem.rating,
            rentalItem.pricePerMonth,
            rentalItem.description)

        // Set initial rating value
        ratingBar.rating = rentalItem.rating

        // Initialize attribute chips
        rentalItem.multiChoiceAttribute.forEach { attribute ->
            addChip(attribute)
        }

        // Add the special "+" chip for adding new attributes
        addAddChip()

        // Disable decrement button initially (since months start at 1)
        decrementButton.isEnabled = months > 1
        updateButtonColors()

        // Setup click listeners
        setupButtonListeners()

        // Display instrument image
        val imageResourceId = intent.getIntExtra(EXTRA_IMAGE, 0)
        val itemImageView: ImageView = findViewById(R.id.itemImageBooking)
        itemImageView.setImageResource(imageResourceId)

        // Initialize price calculations
        updateTotalMoney()
        checkBalance()
    }

    /**
     * Initialize references to all UI components
     */
    private fun initializeUIComponents() {
        itemDetails = findViewById(R.id.itemDetails)
        selectedAttributes = findViewById(R.id.selectedAttributes)
        ratingBar = findViewById(R.id.ratingBarBooking)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        monthsTextView = findViewById(R.id.monthsTextView)
        incrementButton = findViewById(R.id.incrementButton)
        decrementButton = findViewById(R.id.decrementButton)
        totalMoneyTextView = findViewById(R.id.totalMoney)
    }

    /**
     * Set up click listeners for all interactive buttons
     */
    private fun setupButtonListeners() {
        // Increment months button
        incrementButton.setOnClickListener {
            months++
            monthsTextView.text = String.format(Locale.getDefault(), "%d", months)
            decrementButton.isEnabled = months > 1
            updateTotalMoney()
            checkBalance()
            updateButtonColors()
        }

        // Decrement months button
        decrementButton.setOnClickListener {
            if (months > 1) {
                months--
                monthsTextView.text = String.format(Locale.getDefault(), "%d", months)
                decrementButton.isEnabled = months > 1
                updateTotalMoney()
                checkBalance()
                updateButtonColors()
            }
        }

        // Save booking button
        saveButton.setOnClickListener {
            val neededBalance = rentalItem.pricePerMonth * months
            if (balance >= neededBalance) {
                // Deduct balance and prepare result data
                balance -= neededBalance
                rentalItem.rating = ratingBar.rating

                val resultIntent = Intent().apply {
                    putStringArrayListExtra(EXTRA_SELECTED_ATTRIBUTES, ArrayList(selectedAttributesList))
                    putExtra(EXTRA_UPDATED_RATING, rentalItem.rating)
                    putExtra(EXTRA_BORROW_MONTHS, months)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                // Handle insufficient balance - UI already shows warning
            }
        }

        // Cancel booking button
        cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    /**
     * Update the total cost display based on rental price and duration
     * Also shows warning if balance is insufficient
     */
    private fun updateTotalMoney() {
        val totalMoney = rentalItem.pricePerMonth * months
        val formattedTotal = String.format(Locale.US, "%.2f", totalMoney.toDouble())
        val formattedBalance = String.format(Locale.US, "%.2f", balance.toDouble())

        // Create base text that's always shown
        val baseText = "Total: $$formattedTotal (Balance: $$formattedBalance)"

        // Show warning if balance is insufficient
        if (totalMoney > balance) {
            totalMoneyTextView.setTextColor(resources.getColor(R.color.red, theme))
            totalMoneyTextView.text = getString(R.string.total_with_warning, baseText)
        } else {
            totalMoneyTextView.setTextColor(resources.getColor(android.R.color.black, theme))
            totalMoneyTextView.text = getString(R.string.total_normal, baseText)
        }
    }

    /**
     * Check if user has sufficient balance and update save button state
     */
    private fun checkBalance() {
        val neededBalance = rentalItem.pricePerMonth * months
        if (balance >= neededBalance) {
            saveButton.isEnabled = true
            saveButton.setBackgroundColor(resources.getColor(R.color.purple_500, theme))
        } else {
            saveButton.isEnabled = false
            saveButton.setBackgroundColor(resources.getColor(R.color.gray, theme))
        }
    }

    /**
     * Add an attribute chip to the ChipGroup
     * @param attribute The text to display on the chip
     */
    private fun addChip(attribute: String) {
        val chip = Chip(this).apply {
            text = attribute
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                selectedAttributes.removeView(this)
                selectedAttributesList.remove(attribute)
            }
        }
        // Add new chip before the "+" chip (which is the last item)
        selectedAttributes.addView(chip, selectedAttributes.childCount - 1)
        selectedAttributesList.add(attribute)
    }

    /**
     * Add the special "+" chip that allows adding new attributes
     */
    private fun addAddChip() {
        val inflater = LayoutInflater.from(this)
        val addChip = inflater.inflate(R.layout.chip_input, selectedAttributes, false) as Chip

        addChip.setOnClickListener {
            showAddAttributeDialog()
        }

        selectedAttributes.addView(addChip)
    }

    /**
     * Show dialog for adding a new attribute
     */
    private fun showAddAttributeDialog() {
        val editText = EditText(this).apply {
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add new attribute")
            .setView(editText)
            .setPositiveButton("Add", null) // Will set listener later
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Use OnShowListener to customize button behavior
        dialog.setOnShowListener {
            val addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            addButton.isEnabled = false // Initially disabled until valid input

            // Add text validation listener
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val newAttribute = s.toString()
                    // Validate input - only allow alphanumeric and spaces
                    val isValid = newAttribute.isNotBlank() && newAttribute.matches(Regex("^[a-zA-Z0-9 ]+$"))
                    addButton.isEnabled = isValid
                    if (!isValid) {
                        editText.error = "Invalid input. Only letters and numbers are allowed."
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // Set click listener for the Add button
            addButton.setOnClickListener {
                val newAttribute = editText.text.toString()
                addChip(newAttribute)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    /**
     * Update button colors based on enabled/disabled state
     */
    private fun updateButtonColors() {
        if (decrementButton.isEnabled) {
            decrementButton.setBackgroundColor(resources.getColor(R.color.purple_500, theme))
        } else {
            decrementButton.setBackgroundColor(resources.getColor(R.color.gray, theme))
        }
    }
}