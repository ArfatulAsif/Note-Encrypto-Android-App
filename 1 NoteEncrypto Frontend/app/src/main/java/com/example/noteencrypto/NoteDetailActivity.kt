package com.example.noteencrypto

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class NoteDetailActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        val noteId = intent.getStringExtra("NOTE_ID")
        getNoteDetail(noteId)

        val btnDeleteNote = findViewById<Button>(R.id.btnDeleteNote)
        btnDeleteNote.setOnClickListener {
            showDeleteConfirmationDialog(noteId)
        }
    }

    private fun getNoteDetail(noteId: String?) {
        val token = sharedPreferences.getString("TOKEN", null)
        if (token == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
            return
        }

        if (noteId == null) {
            Toast.makeText(this, "Note ID is missing", Toast.LENGTH_LONG).show()
            return
        }

        val url = "https://note-encrypto-android-app.onrender.com/note/getnotebyid"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("note_id", noteId)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@NoteDetailActivity, "Failed to fetch note detail: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val noteDetail = parseNoteDetail(responseBody)
                    runOnUiThread {
                        displayNoteDetail(noteDetail)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@NoteDetailActivity, "Failed to fetch note detail: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun parseNoteDetail(responseBody: String?): String {
        val encryptionKey = sharedPreferences.getInt("LARGE_INTEGER", 0)
        var noteText = ""

        try {
            val jsonObject = JSONObject(responseBody)
            val encryptedNoteText = jsonObject.getString("note_text")
            noteText = decrypt(encryptedNoteText, encryptionKey)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return noteText
    }

    private fun displayNoteDetail(noteDetail: String) {
        val tvNoteDetail = findViewById<TextView>(R.id.tvNoteText)
        tvNoteDetail.text = noteDetail
    }

    private fun decrypt(input: String, key: Int): String {
        return input.map { (((it.code - key) % 256 + 256) % 256).toChar() }.joinToString("")
    }

    private fun showDeleteConfirmationDialog(noteId: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { dialog, id ->
                deleteNote(noteId)
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun deleteNote(noteId: String?) {
        val token = sharedPreferences.getString("TOKEN", null)
        if (token == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show()
            return
        }

        if (noteId == null) {
            Toast.makeText(this, "Note ID is missing", Toast.LENGTH_LONG).show()
            return
        }

        val url = "https://note-encrypto-android-app.onrender.com/note/deletenote"

        val requestBody = "".toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("note_id", noteId)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@NoteDetailActivity, "Failed to delete note: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@NoteDetailActivity, "Note deleted successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@NoteDetailActivity, AllNotesActivity::class.java)
                        startActivity(intent)
                        finish() // Close the activity after deletion
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@NoteDetailActivity, "Failed to delete note: ${response.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
