package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val settingsButton = findViewById<Button>(R.id.btnSettings)
        settingsButton.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
        
        val mediatekaButton = findViewById<Button>(R.id.btnMedia)
        mediatekaButton.setOnClickListener{
            val displayIntent = Intent(this, MediatekaActivity::class.java)
            startActivity(displayIntent)
        }

        val searchButton = findViewById<Button>(R.id.btnSearch)
        searchButton.setOnClickListener{
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }
    }
}