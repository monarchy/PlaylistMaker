package com.example.playlistmaker.main.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.main.ui.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            MainViewModel.getViewModelFactory(this)
        )[MainViewModel::class.java]

        binding.btnSearch.setOnClickListener {
            viewModel.startSearch()
        }

        binding.btnMedia.setOnClickListener {
            viewModel.startMedia()
        }

        binding.btnSettings.setOnClickListener {
            viewModel.startSettings()
        }
    }
}