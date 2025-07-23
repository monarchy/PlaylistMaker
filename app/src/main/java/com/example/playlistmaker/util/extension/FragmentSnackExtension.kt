package com.example.playlistmaker.util.extension

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.google.android.material.snackbar.Snackbar

object FragmentSnackExtension {
    fun Fragment.showSnackBar(
        rootView: View,
        message: String,
        @LayoutRes layoutRes: Int = R.layout.snack_bar,
        @DimenRes heightRes: Int = R.dimen.snackbar_height,
        duration: Int = Snackbar.LENGTH_LONG
    ) {
        val customView = LayoutInflater.from(requireContext()).inflate(layoutRes, null).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(heightRes)
            )
            findViewById<TextView>(R.id.snackbar_text).text = message
        }

        Snackbar.make(rootView, "", duration).apply {
            view.setBackgroundColor(Color.TRANSPARENT)
            view.setPadding(0, 0, 0, 0)
            (view as ViewGroup).apply {
                removeAllViews()
                addView(customView)
            }
        }.show()
    }
}