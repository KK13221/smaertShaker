package com.smartshake.vending.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartshake.vending.R
import com.smartshake.vending.adapter.OrderAdapter
import com.smartshake.vending.databinding.FragmentPaymentBinding
import com.smartshake.vending.viewmodel.OrderViewModel

/**
 * Screen 4 — Payment.
 *
 * Layout: fragment_payment.xml  (3-column ConstraintLayout)
 *   Left panel   → Flavours RecyclerView + Base row
 *   Centre panel → Nutritional information table (6 rows)
 *   Right panel  → Scan & Pay QR + payment badge icons + Order Summary
 *   Bottom bar   → Back button only
 *
 * Nutrition values shown are illustrative averages per 300 ml serving.
 * Replace with your own dynamic calculation as needed.
 */
class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by activityViewModels()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOrderList()
        observeViewModel()
        setupNutrition()
        setupButtons()
    }

    // ── Left panel: Order items list ──────────────────────────────────────────

    private fun setupOrderList() {
        orderAdapter = OrderAdapter(
            onIncrement = { viewModel.increment(it.flavour) },
            onDecrement = { viewModel.decrement(it.flavour) },
            onRemove    = { viewModel.removeItem(it.flavour.id) }
        )

        binding.rvOrderItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
            setHasFixedSize(false)
        }
    }

    // ── Observe LiveData ──────────────────────────────────────────────────────

    private fun observeViewModel() {
        // Order list
        viewModel.orderItems.observe(viewLifecycleOwner) { items ->
            orderAdapter.submitList(items)
        }

        // Selected base label
        viewModel.selectedBase.observe(viewLifecycleOwner) { base ->
            binding.tvBaseValue.text = if (base != null)
                getString(base.labelRes)
            else
                getString(R.string.base_not_selected)
        }

        // Pricing summary
        viewModel.subtotal.observe(viewLifecycleOwner) { sub ->
            binding.tvSubtotalValue.text = getString(R.string.price_format, sub)
        }

        viewModel.taxAmount.observe(viewLifecycleOwner) { tax ->
            binding.tvTaxValue.text = getString(R.string.price_format, tax)
        }

        viewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.tvOrderTotal.text = getString(R.string.price_format, total)
        }
    }

    // ── Centre panel: Nutrition table (static averages) ───────────────────────

    private fun setupNutrition() {
        // Nutrition data per 300 ml serving (illustrative values)
        // These views are included via <include> tags: incNutri1 … incNutri6
        // Each included layout has tv_nutrition_label + tv_nutrition_value
        data class NutriRow(val label: String, val value: String)

        val rows = listOf(
            NutriRow(getString(R.string.nutri_calories),     "320 kcal"),
            NutriRow(getString(R.string.nutri_protein),      "24 g"),
            NutriRow(getString(R.string.nutri_carbs),        "38 g"),
            NutriRow(getString(R.string.nutri_fat),          "8 g"),
            NutriRow(getString(R.string.nutri_fiber),        "3 g"),
            NutriRow(getString(R.string.nutri_sugar),        "22 g")
        )

        val includes = listOf(
            binding.incNutri1,
            binding.incNutri2,
            binding.incNutri3,
            binding.incNutri4,
            binding.incNutri5,
            binding.incNutri6
        )

        rows.forEachIndexed { index, row ->
            includes[index].tvNutritionLabel.text = row.label
            includes[index].tvNutritionValue.text = row.value
        }
    }

    // ── Back button ───────────────────────────────────────────────────────────

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Optional: "New Order" / restart — navigates back to Welcome and clears order
        // Uncomment if you add such a button to the layout:
        // binding.btnNewOrder.setOnClickListener {
        //     viewModel.resetOrder()
        //     findNavController().navigate(R.id.action_payment_to_welcome)
        // }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
