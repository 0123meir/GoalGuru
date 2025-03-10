package com.example.goalguru

import android.content.Context
import androidx.appcompat.app.AlertDialog

object AppUtils {
    fun showExitConfirmationDialog(context: Context, onExitConfirmed: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                onExitConfirmed()
            }
            .setNegativeButton("No", null)
            .show()
    }
}