package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsScreenBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsScreenBinding

    private val settingsViewModel by viewModel<SettingsViewModel>() { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        settingsViewModel.darkThemeEnabled.observe(this) {
            binding.darkThemeSwitch.isChecked = settingsViewModel.isDarkThemeEnabled()
        }

        binding.darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
        }

        binding.goBackButton.setOnClickListener {
            finish()
        }

        binding.shareButton.setOnClickListener {
            settingsViewModel.shareApp(this)
        }

        binding.writeToSupport.setOnClickListener {
            settingsViewModel.openSupport(this)
        }

        binding.userAgreementButton.setOnClickListener {
            settingsViewModel.openTerms(this)
        }
    }
}