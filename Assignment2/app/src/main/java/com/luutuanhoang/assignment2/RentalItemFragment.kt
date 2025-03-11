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

class RentalItemFragment : Fragment() {
    private var item: RentalItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ARG_ITEM, RentalItem::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelable(ARG_ITEM)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.item_rental, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateViews(view)
    }

    private fun updateViews(view: View) {
        item?.let { rentalItem ->
            view.findViewById<TextView>(R.id.itemName).text = rentalItem.name
            view.findViewById<RatingBar>(R.id.ratingBar).rating = rentalItem.rating
            view.findViewById<TextView>(R.id.priceText).text =
                getString(R.string.price_format, rentalItem.pricePerMonth)
            view.findViewById<TextView>(R.id.descriptionText).text = rentalItem.description

            val chipGroup = view.findViewById<ChipGroup>(R.id.attributeChips)
            chipGroup.removeAllViews()
            rentalItem.multiChoiceAttribute.forEach { attribute ->
                val chip = Chip(requireContext())
                chip.text = attribute
                chip.isCheckable = false
                chipGroup.addView(chip)
            }
        }
    }

    fun updateItem(newItem: RentalItem) {
        item = newItem
        view?.let { updateViews(it) }
    }

    companion object {
        private const val ARG_ITEM = "item"
    }
}