package com.example.noteencrypto

import android.content.Context
import android.content.Intent
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

class StatusActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        val etPhone = findViewById<EditText>(R.id.etLargePhone)
        val btnSubmit = findViewById<Button>(R.id.btnVerify)

        btnSubmit.setOnClickListener {
            val phone = etPhone.text.toString()
            if (isValidPhone(phone)) {
                requestSubscriptionStatus(phone)
            } else {
                Toast.makeText(
                    this,
                    "Invalid phone number. It must start with 880 and be 13 digits long.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.startsWith("880") && phone.length == 13
    }

    private fun requestSubscriptionStatus(phone: String) {
        val url = "https://note-encrypto-android-app.onrender.com/asif/subscription/otp/request"
        val json = """
            {
                "appId": "APP_118909",
                "password": "32cda28df843036ce96e051e93c35599",
                "mobile": "$phone"
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
                    Toast.makeText(this@StatusActivity, "Request failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    // Assuming the API returns a JSON object
                    val jsonObject = JSONObject(responseBody)
                    val statusDetail = jsonObject.optString("statusDetail")
                    val referenceNo = jsonObject.optString("referenceNo");

                    runOnUiThread {
                        if (statusDetail == "Success") {
                            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("ReferenceNo", referenceNo)
                            editor.apply()

                            val intent = Intent(this@StatusActivity, SubscribeActivity::class.java)
                            startActivity(intent)


                        } else {
                            Toast.makeText(this@StatusActivity, "Subscription check failed: $statusDetail", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@StatusActivity, "Failed to fetch subscription status: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
