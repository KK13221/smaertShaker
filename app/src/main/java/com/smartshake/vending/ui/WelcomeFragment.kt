package com.smartshake.vending.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smartshake.vending.R
import com.smartshake.vending.databinding.FragmentWelcomeBinding

/**
 * Screen 1 — Welcome / brand splash.
 *
 * Layout: fragment_welcome.xml
 *   Left panel  → SMARTSHAKE wordmark, VENDING badge, subtitle, "Start Your Shake" CTA
 *   Right panel → concentric circles + shake illustration
 */
class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "Start Your Shake" → navigate to Flavour selection
        binding.btnStart.setOnClickListener {
            findNavController().navigate(R.id.action_welcome_to_flavour)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
