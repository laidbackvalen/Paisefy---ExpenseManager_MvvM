package com.example.paisefy_moneymanager.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.paisefy_moneymanager.R
import com.example.paisefy_moneymanager.databinding.ActivityAddPasswordBinding
import com.example.paisefy_moneymanager.preferences.SharedPreference

class Add_Password : AppCompatActivity() {

    private lateinit var binding: ActivityAddPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val preference = SharedPreference(context = applicationContext)
        if (preference.getPassword() != null) {
            startActivity(Intent(this, PasswordAuthenticationActivity::class.java))
        }

        binding.submitButton.setOnClickListener {
            val pass = binding.passwordAdd.text
            val preference = SharedPreference(context = applicationContext).setPassword(pass.toString())
            startActivity(Intent(this, PasswordAuthenticationActivity::class.java))
        }
    }
}