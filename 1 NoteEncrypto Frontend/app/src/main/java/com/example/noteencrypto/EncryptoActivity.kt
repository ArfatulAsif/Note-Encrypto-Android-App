package com.example.noteencrypto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EncryptoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encrypto)

        val etLargeInteger = findViewById<EditText>(R.id.etLargeInteger)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnProfile = findViewById<Button>(R.id.btnProfile)

        btnSubmit.setOnClickListener {
            val largeIntegerStr = etLargeInteger.text.toString()
            val largeInteger = largeIntegerStr.toIntOrNull()
            if (largeInteger == null) {
                Toast.makeText(this, "Please enter a valid integer", Toast.LENGTH_LONG).show()
            } else if (largeIntegerStr.length < 4 || largeIntegerStr.length > 8) {
                Toast.makeText(this, "Integer size should be between 4 and 8 digits", Toast.LENGTH_LONG).show()
            } else {
                // Store the integer in SharedPreferences
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt("LARGE_INTEGER", largeInteger)
                editor.apply()

                // Navigate to AllNotesActivity
                val intent = Intent(this, AllNotesActivity::class.java)
                startActivity(intent)
            }
        }

        btnProfile.setOnClickListener {
            // Navigate to ProfileActivity
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
