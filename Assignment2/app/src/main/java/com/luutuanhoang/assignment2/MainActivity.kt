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

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var borrowButton: Button
    private lateinit var rentalService: RentalService

    private val bookingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedAttributes = result.data?.getStringArrayListExtra(BookingActivity.EXTRA_SELECTED_ATTRIBUTES)
            val updatedRating = result.data?.getFloatExtra(BookingActivity.EXTRA_UPDATED_RATING, rentalService.getRentalItems()[viewPager.currentItem].rating)
            val months = result.data?.getIntExtra(BookingActivity.EXTRA_BORROW_MONTHS, 1) ?: 1
            val currentItem = rentalService.getRentalItems()[viewPager.currentItem]

            rentalService.deductBalance(currentItem.pricePerMonth * months)
            updateActionBarTitle()

            selectedAttributes?.let {
                rentalService.updateRentalItem(viewPager.currentItem, it, updatedRating ?: rentalService.getRentalItems()[viewPager.currentItem].rating)
            }
            viewPager.adapter?.notifyItemChanged(viewPager.currentItem)
            (supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}") as? RentalItemFragment)?.updateItem(rentalService.getRentalItems()[viewPager.currentItem])
            Snackbar.make(
                findViewById(android.R.id.content),
                "Booked ${currentItem.name} for $months month(s) with ${currentItem.multiChoiceAttribute.size} features and rating ${currentItem.rating}",
                Snackbar.LENGTH_LONG
            ).show()
        } else if (result.resultCode == RESULT_CANCELED) {
            Snackbar.make(
                findViewById(android.R.id.content),
                "Booking canceled",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rentalService = RentalService()

        viewPager = findViewById(R.id.viewPager)
        previousButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)
        borrowButton = findViewById(R.id.borrowButton)

        viewPager.adapter = RentalPagerAdapter(rentalService.getRentalItems(), this)

        updateActionBarTitle()

        previousButton.setOnClickListener {
            if (viewPager.currentItem > 0) viewPager.currentItem -= 1
        }

        nextButton.setOnClickListener {
            if (viewPager.currentItem < rentalService.getRentalItems().size - 1) viewPager.currentItem += 1
        }

        borrowButton.setOnClickListener {
            val currentItem = rentalService.getRentalItems()[viewPager.currentItem]
            val intent = Intent(this, BookingActivity::class.java).apply {
                putExtra(BookingActivity.EXTRA_RENTAL_ITEM, currentItem)
                putExtra(BookingActivity.EXTRA_BALANCE, rentalService.balance)
                putExtra(BookingActivity.EXTRA_IMAGE, currentItem.imageResId)
            }
            bookingLauncher.launch(intent)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                previousButton.isEnabled = position > 0
                nextButton.isEnabled = position < rentalService.getRentalItems().size - 1
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_balance -> {
                showAddBalanceDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddBalanceDialog() {
        val editText = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Balance")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
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


    private fun updateActionBarTitle() {
        supportActionBar?.title = "Balance: ${rentalService.balance}"
    }
}