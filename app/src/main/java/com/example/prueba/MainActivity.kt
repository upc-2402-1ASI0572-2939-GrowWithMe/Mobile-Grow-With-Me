package com.example.prueba

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.prueba.Crops.CropsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var txtRole: TextView
    private lateinit var drawerLayout: DrawerLayout

    private val roles = listOf("Agricultor", "Consultor")
    private var currentRoleIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Views
        drawerLayout = findViewById(R.id.drawer_layout)
        txtRole = findViewById(R.id.txt_role)
        val btnSwitchRole = findViewById<Button>(R.id.btn_switch_role)

        // Cargar rol desde SharedPreferences
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedRole = prefs.getString("user_role", roles[0])
        currentRoleIndex = roles.indexOf(savedRole)
        updateRoleUI()

        btnSwitchRole.setOnClickListener {
            currentRoleIndex = (currentRoleIndex + 1) % roles.size
            val newRole = roles[currentRoleIndex]

            txtRole.text = "Rol actual: $newRole"
            prefs.edit().putString("user_role", newRole).apply()
        }
        val btnStart = findViewById<Button>(R.id.btn_start)
        btnStart.setOnClickListener {
            startActivity(Intent(this, CropsActivity::class.java))
        }

    }

    private fun updateRoleUI() {
        txtRole.text = "Rol actual: ${roles[currentRoleIndex]}"
    }
}
