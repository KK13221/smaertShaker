package com.smartshake.vending.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smartshake.vending.R
import com.smartshake.vending.data.OrderItem
import com.smartshake.vending.databinding.ItemOrderRowBinding

/**
 * RecyclerView adapter for the order summary list in the Payment screen (Screen 4).
 *
 * Each row shows:
 *  - Flavour thumbnail image
 *  - Flavour name
 *  - Quantity counter [−] qty [+]
 *  - Line-item price (qty × unit price)
 *  - Remove (×) button
 *
 * Uses [ListAdapter] with [DiffUtil] for efficient, animated updates.
 *
 * @param onIncrement Callback when + tapped — passes the OrderItem
 * @param onDecrement Callback when − tapped — passes the OrderItem
 * @param onRemove    Callback when × tapped — passes the OrderItem
 */
class OrderAdapter(
    private val onIncrement: (OrderItem) -> Unit,
    private val onDecrement: (OrderItem) -> Unit,
    private val onRemove:    (OrderItem) -> Unit
) : ListAdapter<OrderItem, OrderAdapter.OrderViewHolder>(DIFF_CALLBACK) {

    inner class OrderViewHolder(
        private val binding: ItemOrderRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderItem) {
            val ctx = binding.root.context

            // ── Image ────────────────────────────────────────────────────────
            binding.ivOrderImage.setImageResource(item.flavour.imageRes)

            // ── Name ─────────────────────────────────────────────────────────
            binding.tvOrderName.text = item.flavour.name

            // ── Counter ──────────────────────────────────────────────────────
            binding.tvOrderQty.text   = item.quantity.toString()
            binding.btnOrderMinus.setOnClickListener { onDecrement(item) }
            binding.btnOrderPlus.setOnClickListener  { onIncrement(item) }

            // Dim minus at qty = 1 (cannot go below 1 in order list; use × to remove)
            binding.btnOrderMinus.alpha = if (item.quantity > 1) 1f else 0.4f

            // ── Line price ────────────────────────────────────────────────────
            binding.tvOrderPrice.text = ctx.getString(R.string.price_format, item.lineTotal)

            // ── Remove ────────────────────────────────────────────────────────
            binding.btnRemove.setOnClickListener { onRemove(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OrderItem>() {
            override fun areItemsTheSame(old: OrderItem, new: OrderItem) =
                old.flavour.id == new.flavour.id

            override fun areContentsTheSame(old: OrderItem, new: OrderItem) =
                old == new
        }
    }
}
