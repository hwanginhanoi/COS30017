package com.luutuanhoang.assignment2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

/**
 * Adapter for displaying rental items in a ViewPager2.
 * Handles the creation and binding of rental item views.
 *
 * @property rentalItems List of rental items to display in the ViewPager
 * @property activity Reference to the hosting activity for context and lifecycle management
 */
class RentalPagerAdapter(
    private val rentalItems: List<RentalItem>,
    private val activity: AppCompatActivity
) : RecyclerView.Adapter<RentalPagerAdapter.RentalViewHolder>() {

    /**
     * Creates a new ViewHolder when needed by the RecyclerView
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View (not used in this implementation)
     * @return A new RentalViewHolder that holds a view for a rental item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rental, parent, false)
        return RentalViewHolder(view)
    }

    /**
     * Binds the data from a rental item to a ViewHolder
     * @param holder The ViewHolder to update with rental item data
     * @param position The position of the item in the data set
     */
    override fun onBindViewHolder(holder: RentalViewHolder, position: Int) {
        val rentalItem = rentalItems[position]
        holder.bind(rentalItem)
    }

    /**
     * Returns the total number of rental items in the data set
     * @return Count of rental items
     */
    override fun getItemCount(): Int = rentalItems.size

    /**
     * ViewHolder class that holds references to the UI components for each rental item
     * Uses inner class to maintain a reference to the adapter
     */
    inner class RentalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // UI component references
        private val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        private val itemName: TextView = itemView.findViewById(R.id.itemName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val attributeChips: ChipGroup = itemView.findViewById(R.id.attributeChips)
        private val priceText: TextView = itemView.findViewById(R.id.priceText)
        private val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)

        /**
         * Populates the ViewHolder's UI components with data from a rental item
         * @param rentalItem The rental item whose data should be displayed
         */
        fun bind(rentalItem: RentalItem) {
            // Set basic properties
            itemImage.setImageResource(rentalItem.imageResId)
            itemName.text = rentalItem.name
            ratingBar.rating = rentalItem.rating
            priceText.text = itemView.context.getString(R.string.price_format_dollars, rentalItem.pricePerMonth)
            descriptionText.text = rentalItem.description

            // Clear existing chips and create new ones for each attribute
            attributeChips.removeAllViews()
            rentalItem.multiChoiceAttribute.forEach { attribute ->
                val chip = Chip(activity).apply {
                    text = attribute
                    isClickable = false  // Display only, not interactive
                }
                attributeChips.addView(chip)
            }
        }
    }
}