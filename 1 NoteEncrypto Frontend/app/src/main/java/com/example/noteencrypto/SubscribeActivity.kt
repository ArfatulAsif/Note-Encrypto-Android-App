package com.example.noteencrypto

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class SubscribeActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscrible)

        // Corrected ID references for EditText and Button
        val etOTP = findViewById<EditText>(R.id.etPhone)
        val btnSubmit = findViewById<Button>(R.id.btnSubscribe)

        btnSubmit.setOnClickListener {
            val otp = etOTP.text.toString()
            sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val referenceNo = sharedPreferences.getString("referenceNo", "null").toString()
            verifyOTP(otp, referenceNo)
        }
    }

    private fun verifyOTP(otp: String, referenceNo: String) {
        val url = "https://note-encrypto-android-app.onrender.com/asif/subscription/otp/verify"
        val json = """
            {
                "appId": "APP_118909",
                "password": "32cda28df843036ce96e051e93c35599",
                "referenceNo": "$referenceNo",
                "otp": "$otp"
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@SubscribeActivity, "Request failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        val jsonObject = JSONObject(responseBody)
                        val statusDetail = jsonObject.optString("statusDetail")
                        val subscriptionStatus = jsonObject.optString("subscriptionStatus")

                        runOnUiThread {
                            if (subscriptionStatus == "REGISTERED") {
                                val intent = Intent(this@SubscribeActivity, WelcomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this@SubscribeActivity, "Verification failed: $statusDetail", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@SubscribeActivity, "Failed to verify OTP: ${response.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })

    }
}
