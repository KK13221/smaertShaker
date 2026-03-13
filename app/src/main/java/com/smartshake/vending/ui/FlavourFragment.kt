package com.smartshake.vending.ui

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartshake.vending.R
import com.smartshake.vending.adapter.FlavourAdapter
import com.smartshake.vending.data.Flavour
import com.smartshake.vending.databinding.FragmentFlavourBinding
import com.smartshake.vending.viewmodel.OrderViewModel

/**
 * Screen 2 — Choose Your Flavour.
 *
 * Layout: fragment_flavour.xml
 *   - Logo chip (top-right)
 *   - Heading with "your flavour" in #BBF172 green (applied via SpannableString)
 *   - Horizontal RecyclerView of FlavourCards
 *   - Bottom bar: total amount + Back / Continue buttons
 */
class FlavourFragment : Fragment() {

    private var _binding: FragmentFlavourBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by activityViewModels()
    private lateinit var flavourAdapter: FlavourAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlavourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeading()
        setupRecyclerView()
        observeViewModel()
        setupButtons()
    }

    // ── Heading: "Choose your flavour" with green span ────────────────────────

    private fun setupHeading() {
        val full = getString(R.string.heading_choose_flavour)   // "Choose your flavour"
        val green = Color.parseColor("#BBF172")
        val span = SpannableString(full)
        // Colour "your flavour" (index 7 to end)
        val start = full.indexOf("your")
        if (start != -1) {
            span.setSpan(ForegroundColorSpan(green), start, full.length, 0)
        }
        binding.tvHeading.text = span
    }

    // ── RecyclerView setup ────────────────────────────────────────────────────

    private fun setupRecyclerView() {
        flavourAdapter = FlavourAdapter(
            flavours    = Flavour.catalogue(),
            getQty      = { viewModel.quantityFor(it.id) },
            onIncrement = { viewModel.increment(it) },
            onDecrement = { viewModel.decrement(it) }
        )

        binding.rvFlavours.apply {
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
            adapter = flavourAdapter
            setHasFixedSize(true)
        }
    }

    // ── Observe LiveData ──────────────────────────────────────────────────────

    private fun observeViewModel() {
        // Refresh card counters whenever quantities change
        viewModel.quantities.observe(viewLifecycleOwner) {
            flavourAdapter.notifyDataSetChanged()
        }

        // Enable / disable Continue button
        viewModel.hasFlavourSelection.observe(viewLifecycleOwner) { hasSelection ->
            binding.btnContinue.isEnabled = hasSelection
            binding.btnContinue.background = ContextCompat.getDrawable(
                requireContext(),
                if (hasSelection) R.drawable.bg_button_filled_green
                else R.drawable.bg_button_inactive
            )
        }

        // Update total in bottom bar
        viewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.tvTotalAmount.text = getString(R.string.total_price_format, total)
        }
    }

    // ── Button actions ────────────────────────────────────────────────────────

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_flavour_to_base)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
