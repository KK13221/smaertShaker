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
import com.smartshake.vending.R
import com.smartshake.vending.data.Base
import com.smartshake.vending.databinding.FragmentBaseBinding
import com.smartshake.vending.viewmodel.OrderViewModel

/**
 * Screen 3 — Choose Your Base (Milk or Water).
 *
 * Layout: fragment_base.xml
 *   - Logo chip (top-right, included view)
 *   - Heading with "your base" in #BBF172 green
 *   - Two selection cards (Milk / Water) using bg_base_card_selector
 *   - Bottom bar: total amount + Back / Continue buttons
 */
class BaseFragment : Fragment() {

    private var _binding: FragmentBaseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeading()
        setupBaseCards()
        observeViewModel()
        setupButtons()
    }

    // ── Heading: "Choose your base" with green span ───────────────────────────

    private fun setupHeading() {
        val full = getString(R.string.heading_choose_base)   // "Choose your base"
        val green = Color.parseColor("#BBF172")
        val span = SpannableString(full)
        val start = full.indexOf("your")
        if (start != -1) {
            span.setSpan(ForegroundColorSpan(green), start, full.length, 0)
        }
        binding.tvHeading.text = span
    }

    // ── Base card click handlers ──────────────────────────────────────────────

    private fun setupBaseCards() {
        binding.cardMilk.setOnClickListener {
            viewModel.selectBase(Base.MILK)
        }

        binding.cardWater.setOnClickListener {
            viewModel.selectBase(Base.WATER)
        }
    }

    // ── Observe ───────────────────────────────────────────────────────────────

    private fun observeViewModel() {
        viewModel.selectedBase.observe(viewLifecycleOwner) { base ->
            // Apply selector state to cards
            binding.cardMilk.isSelected  = (base == Base.MILK)
            binding.cardWater.isSelected = (base == Base.WATER)

            // Milk text color changes based on selection
            val milkNameColor = if (base == Base.MILK)
                ContextCompat.getColor(requireContext(), R.color.ss_background)
            else
                ContextCompat.getColor(requireContext(), R.color.ss_text_primary)

            val waterNameColor = if (base == Base.WATER)
                ContextCompat.getColor(requireContext(), R.color.ss_background)
            else
                ContextCompat.getColor(requireContext(), R.color.ss_text_primary)

            binding.tvMilkName.setTextColor(milkNameColor)
            binding.tvWaterName.setTextColor(waterNameColor)

            // Enable Continue when a base is chosen
            val hasBase = base != null
            binding.btnContinue.isEnabled = hasBase
            binding.btnContinue.background = ContextCompat.getDrawable(
                requireContext(),
                if (hasBase) R.drawable.bg_button_filled_green
                else R.drawable.bg_button_inactive
            )
        }

        // Total in bottom bar
        viewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.tvTotalAmount.text = getString(R.string.total_price_format, total)
        }
    }

    // ── Buttons ───────────────────────────────────────────────────────────────

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_base_to_payment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
