package com.example.prueba.Farmers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.example.prueba.Consultations.ConsultationActivity
import com.example.prueba.Crops.CropsActivity
import com.example.prueba.Devices.DevicesActivity
import com.example.prueba.Farmers.Adapter.FarmerAdapter
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.Beans.FarmerProfile
import com.example.prueba.Profile.Models.RetrofitClient
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FarmerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FarmerAdapter
    private var farmers = mutableListOf<FarmerProfile>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmers)

        // ✅ Inicializar Cloudinary si no está iniciado
        try {
            MediaManager.get()
        } catch (e: Exception) {
            val config = hashMapOf(
                "cloud_name" to getString(R.string.cloudinary_cloud_name),
                "api_key" to getString(R.string.cloudinary_api_key),
                "api_secret" to getString(R.string.cloudinary_api_secret),
                "secure" to "true"
            )
            MediaManager.init(this, config)
        }

        // 🧭 Configurar Toolbar y Drawer
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

        // ✏️ Ajuste del menú para consultor
        val menu = navView.menu
        menu.findItem(R.id.nav_consultants)?.title = "Agricultores"
        menu.findItem(R.id.nav_devices)?.isVisible = false

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_crops -> startActivity(Intent(this, CropsActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(this, ConsultationActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // 🧑‍🌾 Configurar RecyclerView
        recyclerView = findViewById(R.id.recycler_farmers)
        adapter = FarmerAdapter(
            farmers,
            onConsultsClick = { farmer ->
                Log.d("FARMER", "Ver consultas de ${farmer.firstName} ${farmer.lastName}")
            },
            onCropsClick = { farmer ->
                val intent = Intent(this, com.example.prueba.Crops.CropsFarmerActivity::class.java)
                intent.putExtra("farmerId", farmer.id)
                startActivity(intent)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 🔗 Llamada API para obtener agricultores
        val api = RetrofitClient.getClient("")
        api.getAllFarmers().enqueue(object : Callback<List<FarmerProfile>> {
            override fun onResponse(call: Call<List<FarmerProfile>>, response: Response<List<FarmerProfile>>) {
                if (response.isSuccessful) {
                    farmers.clear()
                    farmers.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("API_FARMERS", "Respuesta no exitosa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<FarmerProfile>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
