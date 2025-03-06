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

class BookingActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_RENTAL_ITEM = "rental_item"
        const val EXTRA_SELECTED_ATTRIBUTES = "selected_attributes"
        const val EXTRA_UPDATED_RATING = "updated_rating"
        const val EXTRA_BORROW_MONTHS = "borrow_months"
        const val EXTRA_BALANCE = "balance"
        const val EXTRA_IMAGE = "extra_image"
    }

    private lateinit var itemDetails: TextView
    private lateinit var selectedAttributes: ChipGroup
    private lateinit var ratingBar: RatingBar
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var rentalItem: RentalItem
    private lateinit var monthsTextView: TextView
    private lateinit var incrementButton: Button
    private lateinit var decrementButton: Button
    private lateinit var totalMoneyTextView: TextView
    private var balance: Int = 0
    private var months: Int = 1
    private val selectedAttributesList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        rentalItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_RENTAL_ITEM, RentalItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_RENTAL_ITEM)
        } ?: throw IllegalStateException("No rental item provided")

        balance = intent.getIntExtra(EXTRA_BALANCE, 0)

        itemDetails = findViewById(R.id.itemDetails)
        selectedAttributes = findViewById(R.id.selectedAttributes)
        ratingBar = findViewById(R.id.ratingBarBooking)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        monthsTextView = findViewById(R.id.monthsTextView)
        incrementButton = findViewById(R.id.incrementButton)
        decrementButton = findViewById(R.id.decrementButton)
        totalMoneyTextView = findViewById(R.id.totalMoney)

        itemDetails.text = getString(R.string.item_details_format,
            rentalItem.name,
            rentalItem.rating,
            rentalItem.pricePerMonth,
            rentalItem.description)

        ratingBar.rating = rentalItem.rating

        rentalItem.multiChoiceAttribute.forEach { attribute ->
            addChip(attribute)
        }

        addAddChip()

        decrementButton.isEnabled = months > 1
        updateButtonColors()

        incrementButton.setOnClickListener {
            months++
            monthsTextView.text = String.format(Locale.getDefault(), "%d", months)
            decrementButton.isEnabled = months > 1
            updateTotalMoney()
            checkBalance()
            updateButtonColors()
        }

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

        saveButton.setOnClickListener {
            val neededBalance = rentalItem.pricePerMonth * months
            if (balance >= neededBalance) {
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
                // Handle insufficient balance
            }
        }

        cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        val imageResourceId = intent.getIntExtra(EXTRA_IMAGE, 0)
        val itemImageView: ImageView = findViewById(R.id.itemImageBooking)
        itemImageView.setImageResource(imageResourceId)

        updateTotalMoney()
        checkBalance()
    }

    private fun updateTotalMoney() {
        val totalMoney = rentalItem.pricePerMonth * months
        val formattedTotal = String.format(Locale.US, "%.2f", totalMoney.toDouble())
        val formattedBalance = String.format(Locale.US, "%.2f", balance.toDouble())

        // Create base text that's always shown
        val baseText = "Total: $$formattedTotal (Balance: $$formattedBalance)"

        if (totalMoney > balance) {
            totalMoneyTextView.setTextColor(resources.getColor(R.color.red, theme))
            totalMoneyTextView.text = getString(R.string.total_with_warning, baseText)
        } else {
            totalMoneyTextView.setTextColor(resources.getColor(android.R.color.black, theme))
            totalMoneyTextView.text = getString(R.string.total_normal, baseText)
        }
    }

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

    private fun addChip(attribute: String) {
        val chip = Chip(this).apply {
            text = attribute
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                selectedAttributes.removeView(this)
                selectedAttributesList.remove(attribute)
            }
        }
        selectedAttributes.addView(chip, selectedAttributes.childCount - 1)
        selectedAttributesList.add(attribute)
    }

    private fun addAddChip() {
        val inflater = LayoutInflater.from(this)
        val addChip = inflater.inflate(R.layout.chip_input, selectedAttributes, false) as Chip

        addChip.setOnClickListener {
            showAddAttributeDialog()
        }

        selectedAttributes.addView(addChip)
    }

    private fun showAddAttributeDialog() {
        val editText = EditText(this).apply {
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add new attribute")
            .setView(editText)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.setOnShowListener {
            val addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            addButton.isEnabled = false

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val newAttribute = s.toString()
                    val isValid = newAttribute.isNotBlank() && newAttribute.matches(Regex("^[a-zA-Z0-9 ]+$"))
                    addButton.isEnabled = isValid
                    if (!isValid) {
                        editText.error = "Invalid input. Only letters and numbers are allowed."
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            addButton.setOnClickListener {
                val newAttribute = editText.text.toString()
                addChip(newAttribute)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun updateButtonColors() {
        if (decrementButton.isEnabled) {
            decrementButton.setBackgroundColor(resources.getColor(R.color.purple_500, theme))
        } else {
            decrementButton.setBackgroundColor(resources.getColor(R.color.gray, theme))
        }
    }
}