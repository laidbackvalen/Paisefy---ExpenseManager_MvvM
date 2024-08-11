package com.valenpatel.paisefy.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.valenpatel.paisefy.R
import com.valenpatel.paisefy.preferences.SharedPreference
import com.valenpatel.paisefy.views.activities.SplashScreen

class PasswordAuthenticationActivity : AppCompatActivity() {
    // Initialize shared preferences with application context
    private lateinit var sharedPreference: SharedPreference
    private lateinit var PASSWORD: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_authentication)

        // Initialize shared preference
        sharedPreference = SharedPreference(context = applicationContext)
        PASSWORD = sharedPreference.getPassword().toString()

        // Show password dialog
        findViewById<Button>(R.id.submitButton).setOnClickListener{
        showPasswordDialog()
        }
        findViewById<ImageView>(R.id.imageView5).setOnClickListener{
            startActivity(Intent(this, Biometric_Authentication_Activity::class.java))
            finish()
        }
    }

    private fun showPasswordDialog() {
        val enterPassword = findViewById<TextInputEditText>(R.id.passwordToVerify).text.toString()
            if (enterPassword == PASSWORD) {
                startActivity(Intent(this, SplashScreen::class.java))
                finish()
            } else {
               Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

    }




