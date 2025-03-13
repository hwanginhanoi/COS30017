package com.luutuanhoang.assignment2

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * Fragment responsible for displaying a single rental item's details.
 * This fragment is used within the ViewPager to show instrument information.
 */
class RentalItemFragment : Fragment() {
    // The rental item to be displayed
    private var item: RentalItem? = null

    /**
     * Initialize fragment with arguments passed from parent activity
     * Retrieves the RentalItem object in a way that's compatible with different Android versions
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Handle Parcelable retrieval with version compatibility
            item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Use the new type-safe method on Android 13+
                it.getParcelable(ARG_ITEM, RentalItem::class.java)
            } else {
                // Use the deprecated method for older Android versions
                @Suppress("DEPRECATION")
                it.getParcelable(ARG_ITEM)
            }
        }
    }

    /**
     * Inflate the fragment layout
     * @return The View for this fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_rental, container, false)
    }

    /**
     * Called immediately after onCreateView() when the view is created.
     * Updates UI elements with the rental item data.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateViews(view)
    }

    /**
     * Populates the view with data from the rental item
     * @param view The fragment's root view
     */
    private fun updateViews(view: View) {
        item?.let { rentalItem ->
            // Set basic text fields
            view.findViewById<TextView>(R.id.itemName).text = rentalItem.name
            view.findViewById<RatingBar>(R.id.ratingBar).rating = rentalItem.rating
            view.findViewById<TextView>(R.id.priceText).text =
                getString(R.string.price_format, rentalItem.pricePerMonth)
            view.findViewById<TextView>(R.id.descriptionText).text = rentalItem.description

            // Create chips for each attribute
            val chipGroup = view.findViewById<ChipGroup>(R.id.attributeChips)
            chipGroup.removeAllViews() // Clear existing chips before adding new ones
            rentalItem.multiChoiceAttribute.forEach { attribute ->
                val chip = Chip(requireContext())
                chip.text = attribute
                chip.isCheckable = false // Display only, not selectable
                chipGroup.addView(chip)
            }
        }
    }

    /**
     * Public method to update the fragment with a new rental item
     * Called when the item data changes (e.g., after booking)
     * @param newItem The updated rental item to display
     */
    fun updateItem(newItem: RentalItem) {
        item = newItem
        view?.let { updateViews(it) } // Update UI if view is available
    }

    companion object {
        // Key for retrieving the rental item from arguments
        private const val ARG_ITEM = "item"

        /**
         * Factory method to create a new instance of this fragment
         * @param item The rental item to be displayed
         * @return A new instance of RentalItemFragment
         */
        fun newInstance(item: RentalItem): RentalItemFragment {
            return RentalItemFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ITEM, item)
                }
            }
        }
    }
}