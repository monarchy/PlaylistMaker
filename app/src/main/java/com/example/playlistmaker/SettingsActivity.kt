package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitch: SwitchCompat
    private lateinit var sharedPrefs: SharedPreferences

    @SuppressLint("MissingInflatedId", "QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        sharedPrefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        setTheme(isDarkMode)
        setContentView(R.layout.activity_settings_screen)
        themeSwitch = findViewById(R.id.darkThemeSwitch)
        themeSwitch.thumbTintList = ContextCompat.getColorStateList(this, R.color.switch_thumb_color)
        themeSwitch.trackTintList = ContextCompat.getColorStateList(this, R.color.switch_track_color)
        themeSwitch.isChecked = isDarkMode
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setTheme(isChecked)
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()
        }

        val goBackButton = findViewById<MaterialToolbar>(R.id.goBackButton)
        goBackButton.setNavigationOnClickListener {
            finish()
        }

        val writeToSupport = findViewById<MaterialTextView>(R.id.writeToSupport)
        writeToSupport.setOnClickListener {
            val message: String = getString(R.string.message_to)
            val subject: String = getString(R.string.subject)
            val myEmail: String = getString(R.string.my_email)
            val writeIntent = Intent(Intent.ACTION_SENDTO)
            writeIntent.data = Uri.parse("mailto:")
            writeIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(myEmail))
            writeIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            writeIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(writeIntent)
        }

        fun shareContent(title: String, text: String) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, title)
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.choose_app)))
        }

        val shareButton = findViewById<MaterialTextView>(R.id.shareButton)
        shareButton.setOnClickListener {
            shareContent(getString(R.string.share_title), getString(R.string.share_message))
        }

        val userAgreementButton = findViewById<MaterialTextView>(R.id.user_agreement_button)
        userAgreementButton.setOnClickListener{
            val userAgreementIntent = Intent(Intent.ACTION_VIEW).apply {
                val url = getString(R.string.agreement_url)
                data = Uri.parse(url)
            }
            if (intent.resolveActivity(packageManager)!=null){
                startActivity(userAgreementIntent)
            }
        }
    }

    private fun setTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}