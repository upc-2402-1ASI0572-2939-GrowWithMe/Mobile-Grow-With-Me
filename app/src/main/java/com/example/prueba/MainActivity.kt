package com.example.prueba

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.prueba.Login.LoginActivity
import com.example.prueba.Register.RegisterActivity

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        val btnRegister = findViewById<Button>(R.id.btn_register)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        btnRegister.setOnClickListener {
            // Lógica para ir a pantalla de registro
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val token = prefs.getString("token", null)
            val userId = prefs.getInt("user_id", -1)
            val role = prefs.getString("role", null)

            if (!token.isNullOrEmpty() && userId != -1 && !role.isNullOrEmpty()) {
                // Ya está logueado, ir directo al HomeActivity
                val intent = Intent(this, com.example.prueba.Home.HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // Si no está logueado, ir al LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

    }
}
