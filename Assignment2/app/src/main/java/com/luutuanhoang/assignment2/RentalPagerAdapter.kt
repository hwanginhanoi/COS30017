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

class RentalPagerAdapter(
    private val rentalItems: List<RentalItem>,
    private val activity: AppCompatActivity
) : RecyclerView.Adapter<RentalPagerAdapter.RentalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rental, parent, false)
        return RentalViewHolder(view)
    }

    override fun onBindViewHolder(holder: RentalViewHolder, position: Int) {
        val rentalItem = rentalItems[position]
        holder.bind(rentalItem)
    }

    override fun getItemCount(): Int = rentalItems.size

    inner class RentalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        private val itemName: TextView = itemView.findViewById(R.id.itemName)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val attributeChips: ChipGroup = itemView.findViewById(R.id.attributeChips)
        private val priceText: TextView = itemView.findViewById(R.id.priceText)
        private val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)

        fun bind(rentalItem: RentalItem) {
            itemImage.setImageResource(rentalItem.imageResId)
            itemName.text = rentalItem.name
            ratingBar.rating = rentalItem.rating
            priceText.text = itemView.context.getString(R.string.price_format_dollars, rentalItem.pricePerMonth)
            descriptionText.text = rentalItem.description

            attributeChips.removeAllViews()
            rentalItem.multiChoiceAttribute.forEach { attribute ->
                val chip = Chip(activity).apply {
                    text = attribute
                    isClickable = false
                }
                attributeChips.addView(chip)
            }
        }
    }
}