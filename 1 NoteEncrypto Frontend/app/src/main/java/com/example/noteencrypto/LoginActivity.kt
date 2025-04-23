package com.example.noteencrypto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        val url = "https://note-encrypto-android-app.onrender.com/auth/login" // Ensure this is correct

        val json = """
            {
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Login failed: Incorrect credential or network error.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    // Parse the response and extract the user's name and token
                    val userName = parseUserName(responseBody)
                    val token = parseToken(responseBody)

                    // Store the token in SharedPreferences
                    val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("TOKEN", token)
                    editor.apply()

                    // Launch the EncryptoActivity (Decryption Page)
                    val intent = Intent(this@LoginActivity, EncryptoActivity::class.java)
                    intent.putExtra("USER_NAME", userName)
                    intent.putExtra("TOKEN", token)
                    startActivity(intent)
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Login failed: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun parseUserName(responseBody: String?): String {
        // Check if the response body is not null
        if (responseBody != null) {
            // Parse the response body as a JSON object
            val jsonObject = JSONObject(responseBody)
            // Extract the user's name from the JSON object
            if (jsonObject.has("name")) {
                return jsonObject.getString("name")
            }
        }
        // Return a default value or throw an exception if the name is not found
        return "User"
    }

    private fun parseToken(responseBody: String?): String {
        // Check if the response body is not null
        if (responseBody != null) {
            // Parse the response body as a JSON object
            val jsonObject = JSONObject(responseBody)
            // Extract the token from the JSON object
            if (jsonObject.has("token")) {
                return jsonObject.getString("token")
            }
        }
        // Return a default value or throw an exception if the token is not found
        return "token"
    }
}
