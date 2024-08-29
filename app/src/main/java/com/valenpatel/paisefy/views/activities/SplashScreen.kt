package com.valenpatel.paisefy.views.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.valenpatel.paisefy.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val handler = Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 5000)

        try {
            var versionName = getApplicationContext().getPackageManager()
                .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName
            findViewById<TextView>(R.id.version).text = "APP VERSION "+versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}