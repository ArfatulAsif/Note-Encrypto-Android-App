package com.example.noteencrypto

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
import java.io.IOException

class AddNoteActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        val etTopicName = findViewById<EditText>(R.id.etTopicName)
        val etNoteText = findViewById<EditText>(R.id.etNoteText)
        val btnAddNote = findViewById<Button>(R.id.btnAddNote)

        btnAddNote.setOnClickListener {
            val topicName = etTopicName.text.toString()
            val noteText = etNoteText.text.toString()

            if (topicName.isEmpty() || noteText.isEmpty()) {
                Toast.makeText(this, "Add both fields", Toast.LENGTH_SHORT).show()
            } else {
                addNote(
                    encrypt(topicName, sharedPreferences.getInt("LARGE_INTEGER", 0)),
                    encrypt(noteText, sharedPreferences.getInt("LARGE_INTEGER", 0))
                )
            }
        }
    }

    private fun addNote(topicName: String, noteText: String) {
        val token = sharedPreferences.getString("TOKEN", null)
        if (token == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
            return
        }

        val url = "https://note-encrypto-android-app.onrender.com/note/addnote"

        val json = """
            {
                "topic_name": "$topicName",
                "note_text": "$noteText"
            }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@AddNoteActivity, "Failed to add note: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@AddNoteActivity, "Note added successfully!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@AddNoteActivity, AllNotesActivity::class.java)
                        startActivity(intent)
                        finish() // Close the activity after adding the note
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@AddNoteActivity, "Failed to add note: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun encrypt(input: String, key: Int): String {
        println(key)
        return input.map { ((it.code + key) % 256).toChar() }.joinToString("")
    }
}
