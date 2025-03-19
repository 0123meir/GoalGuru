package com.example.goalguru

import UserViewModel
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog

object AppUtils {
    fun showExitConfirmationDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                UserViewModel().logoutUser()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
            .setNegativeButton("No", null)
            .show()
    }
}