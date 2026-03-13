package com.smartshake.vending.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.smartshake.vending.data.Base
import com.smartshake.vending.data.Flavour
import com.smartshake.vending.data.OrderItem

/**
 * Shared ViewModel for the entire order flow.
 * Survives fragment transactions so all screens read the same state.
 */
class OrderViewModel : ViewModel() {

    // ─── Flavour quantities ──────────────────────────────────────────────────

    /** Maps flavour id → quantity selected (0 = not added). */
    private val _quantities = MutableLiveData<Map<String, Int>>(emptyMap())
    val quantities: LiveData<Map<String, Int>> = _quantities

    /** Convenience: current quantity for one flavour id. */
    fun quantityFor(flavourId: String): Int = _quantities.value?.get(flavourId) ?: 0

    /** Increment quantity for a flavour (max 9). */
    fun increment(flavour: Flavour) {
        val current = _quantities.value?.toMutableMap() ?: mutableMapOf()
        val qty = (current[flavour.id] ?: 0)
        if (qty < 9) current[flavour.id] = qty + 1
        _quantities.value = current
    }

    /** Decrement quantity for a flavour (min 0, removes key at 0). */
    fun decrement(flavour: Flavour) {
        val current = _quantities.value?.toMutableMap() ?: mutableMapOf()
        val qty = (current[flavour.id] ?: 0)
        if (qty > 0) {
            if (qty == 1) current.remove(flavour.id) else current[flavour.id] = qty - 1
        }
        _quantities.value = current
    }

    /** Remove a flavour from the order entirely (used by payment remove button). */
    fun removeItem(flavourId: String) {
        val current = _quantities.value?.toMutableMap() ?: return
        current.remove(flavourId)
        _quantities.value = current
    }

    // ─── Base selection ──────────────────────────────────────────────────────

    private val _selectedBase = MutableLiveData<Base?>(null)
    val selectedBase: LiveData<Base?> = _selectedBase

    fun selectBase(base: Base) {
        _selectedBase.value = base
    }

    // ─── Derived order list (for payment screen) ─────────────────────────────

    /**
     * LiveData that emits the current list of OrderItems
     * (only flavours with qty > 0), built from [quantities] and the catalogue.
     */
    val orderItems: LiveData<List<OrderItem>> = _quantities.map { map ->
        Flavour.catalogue()
            .filter { (map[it.id] ?: 0) > 0 }
            .map { OrderItem(it, map[it.id] ?: 0) }
    }

    // ─── Pricing ─────────────────────────────────────────────────────────────

    private val TAX_RATE = 0.05  // 5 %

    val subtotal: LiveData<Int> = orderItems.map { items ->
        items.sumOf { it.lineTotal }
    }

    val taxAmount: LiveData<Int> = subtotal.map { s ->
        (s * TAX_RATE).toInt()
    }

    val totalAmount: LiveData<Int> = subtotal.map { s ->
        s + (s * TAX_RATE).toInt()
    }

    // ─── Validation helpers ──────────────────────────────────────────────────

    /** True if at least one flavour has been selected. */
    val hasFlavourSelection: LiveData<Boolean> = _quantities.map { it.isNotEmpty() }

    /** True if a base has been chosen. */
    val hasBaseSelection: LiveData<Boolean> = _selectedBase.map { it != null }

    // ─── Reset ───────────────────────────────────────────────────────────────

    /** Call when the order is complete or user returns to Welcome. */
    fun resetOrder() {
        _quantities.value = emptyMap()
        _selectedBase.value = null
    }
}
