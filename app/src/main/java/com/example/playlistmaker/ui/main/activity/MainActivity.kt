package com.example.playlistmaker.ui.main.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        systemBottomPadding(REMOVE_BOTTOM_PADDING)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.root_container) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerFragment, R.id.playlistCreateFragment -> {
                    binding.bottomNav.visibility = View.GONE
                    systemBottomPadding(APPLY_BOTTOM_PADDING)
                }

                else -> {
                    binding.bottomNav.visibility = View.VISIBLE
                    systemBottomPadding(REMOVE_BOTTOM_PADDING)
                }
            }
        }

    }

    private fun systemBottomPadding(boolean: Boolean) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                if (boolean) systemBars.bottom else 0
            )
            insets
        }
    }

    companion object {
        private const val APPLY_BOTTOM_PADDING = true
        private const val REMOVE_BOTTOM_PADDING = false
    }
}
