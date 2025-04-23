package com.example.noteencrypto

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class AllNotesActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var noteIds: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_notes)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        noteIds = mutableListOf()

        val btnAddNote = findViewById<Button>(R.id.btnAddNote)
        val btnProfile = findViewById<Button>(R.id.btnProfile)

        btnAddNote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        getAllNotes()
    }

    private fun getAllNotes() {
        val token = sharedPreferences.getString("TOKEN", null)

        if (token == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
            return
        }

        println("$token here")

        val url = "https://note-encrypto-android-app.onrender.com/note/getnotes"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@AllNotesActivity, "Failed to fetch notes: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val notes = parseNotes(responseBody)
                    runOnUiThread {
                        displayNotes(notes)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@AllNotesActivity, "Failed to fetch notes: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun parseNotes(responseBody: String?): List<String> {
        val notes = mutableListOf<String>()
        val encryptionKey = sharedPreferences.getInt("LARGE_INTEGER", 0)

        try {
            val jsonObject = JSONObject(responseBody)
            val jsonArray = jsonObject.getJSONArray("notes")

            for (i in 0 until jsonArray.length()) {
                val noteObject = jsonArray.getJSONObject(i)
                val encryptedTopicName = noteObject.getString("topic_name")
                val decryptedTopicName = decrypt(encryptedTopicName, encryptionKey)
                notes.add(decryptedTopicName)
                noteIds.add(noteObject.getString("_id"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return notes
    }

    private fun displayNotes(notes: List<String>) {
        val listView = findViewById<ListView>(R.id.listView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notes)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val noteId = noteIds[position]
            val intent = Intent(this, NoteDetailActivity::class.java)
            intent.putExtra("NOTE_ID", noteId)
            startActivity(intent)
        }
    }

    private fun decrypt(input: String, key: Int): String {
        return input.map { (((it.code - key) % 256 + 256) % 256).toChar() }.joinToString("")
    }
}
