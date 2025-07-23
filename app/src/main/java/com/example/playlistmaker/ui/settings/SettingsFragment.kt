package com.example.playlistmaker.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.domain.settings.SettingsEvent
import com.example.playlistmaker.presentation.settings.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel { parametersOf(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.settingsEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                SettingsEvent.OpenSupport -> viewModel.supportEvent()
                SettingsEvent.OpenTerms -> viewModel.termsEvent()
                SettingsEvent.ShareApp -> viewModel.shareAppEvent()
                is SettingsEvent.SwapTheme -> {
                    binding.themeSwitcher.isChecked = event.boolean
                    viewModel.changeThemeEvent(event.boolean)

                }
            }
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, check ->
            viewModel.clickOnChangeTheme(check)
        }

        binding.linkApp.setOnClickListener {
            viewModel.clickOnShareApp()
        }

        binding.writeToSupport.setOnClickListener {
            viewModel.clickOnSupport()
        }

        binding.userAgreement.setOnClickListener {
            viewModel.clickOnTerms()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}