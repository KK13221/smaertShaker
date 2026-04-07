package com.example.smartshake

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartshake.data.model.FlavourItem

class FlavourAdapter(
    private val flavours: List<FlavourItem>,
    private val layoutResId: Int = R.layout.item_flavour_card,
    private val onScoopsChanged: (() -> Unit)? = null
) : RecyclerView.Adapter<FlavourAdapter.FlavourViewHolder>() {

    class FlavourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFlavourCard: ImageView? = itemView.findViewById(R.id.iv_flavour_card)
        val ivFlavourImage: ImageView? = itemView.findViewById(R.id.iv_flavour_image)
        val tvName: TextView? = itemView.findViewById(R.id.tv_flavour_name)
        val tvCount: TextView? = itemView.findViewById(R.id.tv_count)
        val tvQuantity: TextView? = itemView.findViewById(R.id.tv_quantity)
        val btnMinus: View? = itemView.findViewById(R.id.btn_decrease) ?: itemView.findViewById(R.id.iv_minus)
        val btnPlus: View? = itemView.findViewById(R.id.btn_increase) ?: itemView.findViewById(R.id.iv_plus)
        val tvPrice: TextView? = itemView.findViewById(R.id.tv_price_badge) ?: itemView.findViewById(R.id.tv_price)
    }

    private fun getCardImageRes(name: String): Int = when (name.lowercase()) {
        "chocolate"  -> R.drawable.ic_chocolate
        "vanilla"    -> R.drawable.ic_vanilla
        "banana"     -> R.drawable.bn
        "strawberry" -> R.drawable.ic_stawberry
        "coffee"     -> R.drawable.ic_coffee
        else         -> R.drawable.ic_chocolate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlavourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return FlavourViewHolder(view)
    }

    override fun getItemCount() = flavours.size

    override fun onBindViewHolder(holder: FlavourViewHolder, position: Int) {
        val flavour = flavours[position]

        val context = holder.itemView.context
        
        holder.ivFlavourCard?.let {
            Glide.with(context).load(flavour.image).placeholder(R.drawable.ic_chocolate).into(it)
        }
        holder.ivFlavourImage?.let {
            Glide.with(context).load(flavour.image).placeholder(R.drawable.ic_chocolate).into(it)
        }
        
        holder.tvName?.text = flavour.name
        holder.tvCount?.text = flavour.scoops.toString()
        holder.tvQuantity?.text = flavour.scoops.toString()
        
        val priceVal = flavour.price.toDoubleOrNull()?.toInt() ?: 0
        val priceText = if (layoutResId == R.layout.item_payment_flavour) "₹ ${flavour.scoops * priceVal}" else "₹ $priceVal"
        holder.tvPrice?.text = priceText

        holder.btnMinus?.setOnClickListener {
            if (flavour.scoops > 0) {
                flavour.scoops--
                notifyItemChanged(position)
                onScoopsChanged?.invoke()
            }
        }

        holder.btnPlus?.setOnClickListener {
            // Enforce single flavor selection: 
            // Reset all other flavors to zero scoops
            flavours.forEachIndexed { index, item ->
                if (index != position && item.scoops > 0) {
                    item.scoops = 0
                    notifyItemChanged(index)
                }
            }
            
            flavour.scoops++
            notifyItemChanged(position)
            onScoopsChanged?.invoke()
        }

        if (holder.btnMinus is ImageButton) {
            holder.btnMinus.isEnabled = flavour.scoops > 0
            holder.btnMinus.alpha = if (flavour.scoops > 0) 1f else 0.35f
        }
    }
}