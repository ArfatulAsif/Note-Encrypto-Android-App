package com.example.noteencrypto

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        animateText(tvWelcome)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun animateText(view: TextView) {
        val translate = TranslateAnimation(0f, 0f, 80f, 0f).apply {
            duration = 4000
        }
        val alpha = AlphaAnimation(0f, 1f).apply {
            duration = 4000
        }
        val animationSet = AnimationSet(true).apply {
            addAnimation(translate)
            addAnimation(alpha)
        }
        view.startAnimation(animationSet)
    }
}
