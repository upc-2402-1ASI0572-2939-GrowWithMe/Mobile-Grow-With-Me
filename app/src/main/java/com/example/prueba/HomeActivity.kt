package com.example.prueba.Home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.prueba.Crops.CropsActivity
import com.example.prueba.Devices.DevicesActivity
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.Consultations.ConsultantActivity
import com.example.prueba.Consultations.ConsultationActivity
import com.example.prueba.Farmers.FarmerActivity
import com.example.prueba.R
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar

    private val isConsultant: Boolean
        get() {
            val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            return prefs.getString("role", "FARMER_ROLE") == "CONSULTANT_ROLE"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        filterMenuByRole()

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_crops -> startActivity(Intent(this, CropsActivity::class.java))
                R.id.nav_devices -> startActivity(Intent(this, DevicesActivity::class.java))
                R.id.nav_notifications -> {
                    val target = if (isConsultant)
                        ConsultationActivity::class.java
                    else
                        NotificationsActivity::class.java
                    startActivity(Intent(this, target))
                }

                R.id.nav_consultants -> {
                    val target = if (isConsultant) FarmerActivity::class.java else ConsultantActivity::class.java
                    startActivity(Intent(this, target))
                }
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun filterMenuByRole() {
        val menu: Menu = navView.menu
        if (isConsultant) {
            menu.findItem(R.id.nav_consultants)?.title = "Agricultores"
            menu.findItem(R.id.nav_devices)?.isVisible = false
        } else {
            menu.findItem(R.id.nav_crops)?.isVisible = true
            menu.findItem(R.id.nav_consultants)?.title = "Consultores"
            menu.findItem(R.id.nav_devices)?.isVisible = true
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
