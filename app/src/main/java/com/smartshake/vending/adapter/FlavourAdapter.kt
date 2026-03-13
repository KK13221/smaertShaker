package com.smartshake.vending.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.smartshake.vending.R
import com.smartshake.vending.data.Flavour
import com.smartshake.vending.databinding.ItemFlavourCardBinding

/**
 * RecyclerView adapter for the horizontal flavour card list (Screen 2).
 *
 * Each card shows:
 *  - Food photo (top, centercrop)
 *  - Wave overlay (VectorDrawable at card image bottom)
 *  - Price badge (top-right pill)
 *  - Flavour name (Poppins SemiBold, coloured per flavour)
 *  - Quantity counter  [−] qty [+]
 *
 * @param flavours    Static catalogue list
 * @param getQty      Lambda to retrieve current quantity from ViewModel
 * @param onIncrement Callback when + is tapped
 * @param onDecrement Callback when − is tapped
 */
class FlavourAdapter(
    private val flavours: List<Flavour>,
    private val getQty: (Flavour) -> Int,
    private val onIncrement: (Flavour) -> Unit,
    private val onDecrement: (Flavour) -> Unit
) : RecyclerView.Adapter<FlavourAdapter.FlavourViewHolder>() {

    inner class FlavourViewHolder(
        private val binding: ItemFlavourCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(flavour: Flavour) {
            val ctx = binding.root.context

            // ── Image ───────────────────────────────────────────────────────
            binding.ivFlavourImage.setImageResource(flavour.imageRes)

            // ── Price badge ─────────────────────────────────────────────────
            binding.tvPrice.text = ctx.getString(R.string.price_format, flavour.priceRupees)

            // ── Name ────────────────────────────────────────────────────────
            binding.tvFlavourName.text = flavour.name
            binding.tvFlavourName.setTextColor(
                ContextCompat.getColor(ctx, flavour.textColorRes)
            )

            // ── Counter ──────────────────────────────────────────────────────
            val qty = getQty(flavour)
            binding.tvQty.text = qty.toString()

            // Show counter row only when qty > 0, hide otherwise (saves space)
            binding.layoutCounter.visibility = if (qty > 0) View.VISIBLE else View.INVISIBLE

            binding.btnMinus.setOnClickListener { onDecrement(flavour) }
            binding.btnPlus.setOnClickListener  { onIncrement(flavour) }

            // Dim minus button at 0
            binding.btnMinus.alpha = if (qty > 0) 1f else 0.4f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlavourViewHolder {
        val binding = ItemFlavourCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FlavourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlavourViewHolder, position: Int) {
        holder.bind(flavours[position])
    }

    override fun getItemCount(): Int = flavours.size
}
