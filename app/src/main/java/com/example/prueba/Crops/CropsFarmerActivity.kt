package com.example.prueba.Crops

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Activity.ActivityCropActivity
import com.example.prueba.Consultations.ConsultantActivity
import com.example.prueba.Consultations.ConsultationActivity
import com.example.prueba.Crops.Adapter.CropAdapter
import com.example.prueba.Crops.Beans.Crop
import com.example.prueba.Crops.Interfaces.PlaceHolder
import com.example.prueba.Crops.Models.RetrofitClient
import com.example.prueba.Devices.DevicesActivity
import com.example.prueba.Farmers.FarmerActivity
import com.example.prueba.Monitoring.DashboardActivity
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CropsFarmerActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CropAdapter
    private val crops = mutableListOf<Crop>()

    private var farmerId: Int = -1

    private val isConsultant: Boolean
        get() = true // siempre consultor en este activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crops)

        farmerId = intent.getIntExtra("farmerId", -1)
        if (farmerId == -1) {
            Toast.makeText(this, "ID de agricultor no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Toolbar y Drawer
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        filterMenuByRole()
        setupNavigation()

        // Ocultar botón de registrar cultivo si es consultor
        val btnRegister = findViewById<Button>(R.id.btn_register_crop)
        btnRegister.visibility = Button.GONE

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recycler_crops)
        adapter = CropAdapter(
            crops,
            isConsultant = true,
            onEdit = { _, _ -> },
            onDelete = { _ -> },
            onView = { crop -> openCropActivities(crop) },
            onGraphic = { crop -> openDashboardActivity(crop) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadCropsForFarmer()
    }

    private fun filterMenuByRole() {
        val menu: Menu = navView.menu
        menu.findItem(R.id.nav_consultants)?.title = "Agricultores"
        menu.findItem(R.id.nav_devices)?.isVisible = false
    }

    private fun setupNavigation() {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_crops -> {
                    startActivity(Intent(this, CropsActivity::class.java))
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, ConsultationActivity::class.java))
                }
                R.id.nav_consultants -> {
                    startActivity(Intent(this, FarmerActivity::class.java))
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun loadCropsForFarmer() {
        val token = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("token", "") ?: ""
        val api: PlaceHolder = RetrofitClient.getClient(token)

        api.getCropsByFarmerId(farmerId.toLong()).enqueue(object : Callback<List<Crop>> {
            override fun onResponse(call: Call<List<Crop>>, response: Response<List<Crop>>) {
                if (response.isSuccessful) {
                    crops.clear()
                    crops.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("CROPS_FARMER", "Respuesta no exitosa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Crop>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@CropsFarmerActivity, "Error al cargar cultivos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openCropActivities(crop: Crop) {
        val intent = Intent(this, ActivityCropActivity::class.java)
        intent.putExtra("cropId", crop.id)
        intent.putExtra("cropName", crop.productName)
        intent.putExtra("isConsultant", true)
        startActivity(intent)
    }

    private fun openDashboardActivity(crop: Crop) {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("cropId", crop.id)
        intent.putExtra("cropName", crop.productName)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
