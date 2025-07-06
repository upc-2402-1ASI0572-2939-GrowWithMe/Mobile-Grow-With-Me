package com.example.prueba.Consultations

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.example.prueba.Consultants.Adapter.ConsultantAdapter
import com.example.prueba.Consultations.Beans.Consultation
import com.example.prueba.Consultations.Models.RetrofitClient
import com.example.prueba.Profile.Models.RetrofitClient as RetrofitClientConsultant
import com.example.prueba.Crops.CropsActivity
import com.example.prueba.Devices.DevicesActivity
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.Beans.ConsultantProfile
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsultantActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ConsultantAdapter
    private var consultants = mutableListOf<ConsultantProfile>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultants)

        // ✅ Inicializar Cloudinary desde valores
        try {
            MediaManager.get() // intenta obtener instancia
        } catch (e: Exception) {
            val config = hashMapOf(
                "cloud_name" to getString(R.string.cloudinary_cloud_name),
                "api_key" to getString(R.string.cloudinary_api_key),
                "api_secret" to getString(R.string.cloudinary_api_secret),
                "secure" to "true"
            )
            MediaManager.init(this, config)
        }


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

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_devices -> startActivity(Intent(this, DevicesActivity::class.java))
                R.id.nav_crops -> startActivity(Intent(this, CropsActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(this, NotificationsActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        recyclerView = findViewById(R.id.recycler_consultants)
        val btnConsult = findViewById<Button>(R.id.btn_make_consultation)
        val btnHistory = findViewById<Button>(R.id.btn_view_history)

        adapter = ConsultantAdapter(consultants) { consultant -> }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnConsult.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_register_consultation, null)
            val titleInput = dialogView.findViewById<EditText>(R.id.et_title)
            val descInput = dialogView.findViewById<EditText>(R.id.et_description)
            val btnSend = dialogView.findViewById<Button>(R.id.btn_accept)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            btnSend.setOnClickListener {
                val title = titleInput.text.toString().trim()
                val description = descInput.text.toString().trim()

                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(this, "Completa correctamente todos los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                dialog.dismiss()

                val json = JsonObject().apply {
                    addProperty("id", 0)
                    addProperty("title", title)
                    addProperty("description", description)
                    addProperty("farmerId", 1)
                }

                val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val token = prefs.getString("token", "") ?: ""
                val api = RetrofitClient.getClient(token)
                api.createConsultation(json).enqueue(object : Callback<Consultation> {
                    override fun onResponse(call: Call<Consultation>, response: Response<Consultation>) {
                        if (response.isSuccessful) {
                            val confirmView = LayoutInflater.from(this@ConsultantActivity)
                                .inflate(R.layout.dialog_consultation_confirmation, null)
                            val confirmDialog = AlertDialog.Builder(this@ConsultantActivity)
                                .setView(confirmView)
                                .create()
                            confirmView.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                                confirmDialog.dismiss()
                            }
                            confirmDialog.show()
                        } else {
                            Toast.makeText(this@ConsultantActivity, "Error al crear consulta", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Consultation>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(this@ConsultantActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            dialog.show()
        }

        btnHistory.setOnClickListener {
            startActivity(Intent(this, ConsultationActivity::class.java))
        }

        val api = RetrofitClientConsultant.getClient("")
        api.getAllConsultants().enqueue(object : Callback<List<ConsultantProfile>> {
            override fun onResponse(call: Call<List<ConsultantProfile>>, response: Response<List<ConsultantProfile>>) {
                if (response.isSuccessful) {
                    consultants.clear()
                    consultants.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("API_CONSULTANT", "Respuesta no exitosa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ConsultantProfile>>, t: Throwable) {
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
