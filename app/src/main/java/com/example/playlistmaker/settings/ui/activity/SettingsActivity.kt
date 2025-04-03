package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettingsScreenBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsScreenBinding

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(this)
        )[SettingsViewModel::class.java]

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
            settingsViewModel.shareApp()
        }

        binding.writeToSupport.setOnClickListener {
            settingsViewModel.openSupport()
        }

        binding.userAgreementButton.setOnClickListener {
            settingsViewModel.openTerms()
        }
    }
}