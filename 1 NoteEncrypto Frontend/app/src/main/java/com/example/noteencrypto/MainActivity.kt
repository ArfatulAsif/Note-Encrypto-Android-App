package com.example.noteencrypto

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            checkLoginStatus()
            // close this activity
            finish()
        }, 3000)
    }

    private fun checkLoginStatus() {
        val token = sharedPreferences.getString("TOKEN", null)
        val encryptionNumber = sharedPreferences.getInt("LARGE_INTEGER", 0)

        when {
            token.isNullOrEmpty() -> {
                // Token not found, navigate to WelcomeActivity
                navigateTo(StatusActivity::class.java)
            }
            encryptionNumber == 0 -> {
                // Encryption number not set, navigate to EncryptoActivity
                navigateTo(StatusActivity::class.java)
            }
            else -> {
                // Both token and encryption number are present, navigate to AllNotesActivity
                navigateTo(StatusActivity::class.java)
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        logout()
    }

    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
