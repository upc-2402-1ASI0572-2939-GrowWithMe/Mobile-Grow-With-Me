package com.example.prueba.Consultations

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Consultations.Adapter.ConsultationAdapter
import com.example.prueba.Consultations.Beans.Consultation
import com.example.prueba.Consultations.Models.RetrofitClient
import com.example.prueba.Consultations.Interfaces.PlaceHolder
import com.example.prueba.Crops.CropsActivity
import com.example.prueba.Devices.DevicesActivity
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.navigation.NavigationView
import com.example.prueba.Farmers.FarmerActivity
import com.example.prueba.Consultations.ConsultantActivity

class ConsultationActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ConsultationAdapter
    private val consultations = mutableListOf<Consultation>()

    private val isConsultant: Boolean
        get() {
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            return prefs.getString("role", "FARMER_ROLE") == "CONSULTANT_ROLE"
        }

    private val userId: Int
        get() {
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            return prefs.getInt("user_id", 0)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultations)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        filterMenuByRole()

        recyclerView = findViewById(R.id.recycler_consultations)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigation()

        adapter = ConsultationAdapter(consultations)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadConsultations()
    }

    private fun filterMenuByRole() {
        val menu = navView.menu
        if (isConsultant) {
            menu.findItem(R.id.nav_consultants)?.title = "Agricultores"
            menu.findItem(R.id.nav_devices)?.isVisible = false
        } else {
            menu.findItem(R.id.nav_crops)?.isVisible = true
            menu.findItem(R.id.nav_consultants)?.title = "Consultores"
            menu.findItem(R.id.nav_devices)?.isVisible = true
        }
    }

    private fun setupNavigation() {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_devices -> startActivity(Intent(this, DevicesActivity::class.java))
                R.id.nav_crops -> startActivity(Intent(this, CropsActivity::class.java))
                R.id.nav_notifications -> {
                    val target = if (isConsultant)
                        ConsultationActivity::class.java
                    else
                        NotificationsActivity::class.java
                    startActivity(Intent(this, target))
                }
                R.id.nav_consultants -> {
                    val target = if (isConsultant)
                        FarmerActivity::class.java
                    else
                        ConsultantActivity::class.java
                    startActivity(Intent(this, target))
                }
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun loadConsultations() {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        val api: PlaceHolder = RetrofitClient.getClient(token)

        val call: Call<List<Consultation>> =
            if (isConsultant) api.getConsultation()
            else api.getConsultationsByFarmerId(userId)

        call.enqueue(object : Callback<List<Consultation>> {
            override fun onResponse(call: Call<List<Consultation>>, response: Response<List<Consultation>>) {
                if (response.isSuccessful) {
                    consultations.clear()
                    consultations.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("Consultations", "Error al obtener las consultas: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Consultation>>, t: Throwable) {
                t.printStackTrace()
                Log.e("Consultations", "Fallo de red: ${t.localizedMessage}")
            }
        })
    }

    @Deprecated("Usa OnBackPressedDispatcher en nuevas implementaciones")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
