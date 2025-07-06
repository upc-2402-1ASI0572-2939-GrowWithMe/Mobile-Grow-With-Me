package com.example.prueba.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.prueba.Crops.CropsActivity
import com.example.prueba.Devices.DevicesActivity
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import com.example.prueba.Consultations.ConsultantActivity
import com.example.prueba.Activity.Models.RetrofitClient
import com.example.prueba.Activity.Beans.CropActivity
import com.example.prueba.Activity.Beans.CropActivitySchema
import com.example.prueba.Consultations.ConsultationActivity
import com.example.prueba.Farmers.FarmerActivity
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*

class ActivityCropActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var tvCropName: TextView
    private lateinit var calendarView: CalendarView
    private val registeredDates = mutableSetOf<Long>()
    private var cropId: Int = -1
    private var cropName: String = ""

    private val userRole: String
        get() = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("role", "") ?: ""

    private val isConsultant: Boolean
        get() = userRole == "CONSULTANT_ROLE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crops_activities)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        tvCropName = findViewById(R.id.tv_crop_name)
        calendarView = findViewById(R.id.calendarView)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        cropId = intent.getIntExtra("cropId", -1)
        cropName = intent.getStringExtra("cropName") ?: "Cultivo"
        tvCropName.text = cropName

        calendarView.minDate = System.currentTimeMillis()

        filterMenuByRole()
        setupNavigation()
        loadCropActivities()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
            }
            val selectedDateMillis = selectedDate.timeInMillis
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)

            getClientWithToken().getActivitiesByCropId(cropId).enqueue(object : retrofit2.Callback<List<CropActivity>> {
                override fun onResponse(call: retrofit2.Call<List<CropActivity>>, response: retrofit2.Response<List<CropActivity>>) {
                    if (response.isSuccessful) {
                        val activities = response.body() ?: emptyList()
                        val existing = activities.find { it.activityDate == formattedDate }

                        // 🔧 CORREGIDO: mostrar solo si agricultor o si hay actividad
                        if (existing != null || !isConsultant) {
                            showRegisterDialog(formattedDate, existing, selectedDateMillis)
                        } else {
                            Toast.makeText(this@ActivityCropActivity, "No hay actividad registrada para esta fecha", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this@ActivityCropActivity, "Error al consultar actividades", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<CropActivity>>, t: Throwable) {
                    Toast.makeText(this@ActivityCropActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            })
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

    private fun getClientWithToken() =
        RetrofitClient.getClient(getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("token", "") ?: "")

    private fun loadCropActivities() {
        getClientWithToken().getActivitiesByCropId(cropId).enqueue(object : retrofit2.Callback<List<CropActivity>> {
            override fun onResponse(call: retrofit2.Call<List<CropActivity>>, response: retrofit2.Response<List<CropActivity>>) {
                if (response.isSuccessful) {
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    response.body()?.forEach {
                        try {
                            val date = format.parse(it.activityDate)
                            date?.let { d -> registeredDates.add(d.time) }
                        } catch (_: Exception) {}
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<CropActivity>>, t: Throwable) {
                Toast.makeText(this@ActivityCropActivity, "Error al cargar actividades", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRegisterDialog(date: String, existing: CropActivity?, key: Long) {
        val dialogView = layoutInflater.inflate(R.layout.activity_register_activity, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val etDate = dialogView.findViewById<EditText>(R.id.et_activity_date)
        val etCropName = dialogView.findViewById<EditText>(R.id.et_crop_name)
        val etDescription = dialogView.findViewById<EditText>(R.id.et_activity_description)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btn_submit_activity)
        val btnDelete = dialogView.findViewById<Button>(R.id.btn_delete_activity)

        etDate.setText(date)
        etCropName.setText(cropName)

        val client = getClientWithToken()

        if (existing != null) {
            etDescription.setText(existing.description)
            btnSubmit.text = "Actualizar"

            if (isConsultant) {
                btnSubmit.visibility = android.view.View.GONE
                btnDelete.visibility = android.view.View.GONE
                etDescription.isEnabled = false
            } else {
                btnDelete.visibility = android.view.View.VISIBLE

                btnSubmit.setOnClickListener {
                    val updatedActivity = CropActivity(
                        id = existing.id,
                        cropId = existing.cropId,
                        activityDate = date,
                        description = etDescription.text.toString()
                    )
                    client.updateActivity(existing.id, updatedActivity).enqueue(object : retrofit2.Callback<CropActivity> {
                        override fun onResponse(call: retrofit2.Call<CropActivity>, response: retrofit2.Response<CropActivity>) {
                            Toast.makeText(this@ActivityCropActivity, "Actividad actualizada", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }

                        override fun onFailure(call: retrofit2.Call<CropActivity>, t: Throwable) {
                            Toast.makeText(this@ActivityCropActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }

                btnDelete.setOnClickListener {
                    client.deleteActivity(existing.id).enqueue(object : retrofit2.Callback<Void> {
                        override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                            Toast.makeText(this@ActivityCropActivity, "Actividad eliminada", Toast.LENGTH_SHORT).show()
                            registeredDates.remove(key)
                            dialog.dismiss()
                        }

                        override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                            Toast.makeText(this@ActivityCropActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

        } else {
            if (isConsultant) {
                dialog.dismiss()
                return
            }

            btnSubmit.setOnClickListener {
                val description = etDescription.text.toString().trim()
                if (description.isBlank()) {
                    etDescription.error = "Requerido"
                    return@setOnClickListener
                }

                val activity = CropActivitySchema(cropId, date, description)

                Log.d("POST_ACTIVITY", "Enviando: cropId=$cropId, activityDate=$date, description=$description")

                client.createActivity(activity).enqueue(object : retrofit2.Callback<CropActivity> {
                    override fun onResponse(call: retrofit2.Call<CropActivity>, response: retrofit2.Response<CropActivity>) {
                        Toast.makeText(this@ActivityCropActivity, "Actividad registrada", Toast.LENGTH_SHORT).show()
                        registeredDates.add(key)
                        dialog.dismiss()
                    }

                    override fun onFailure(call: retrofit2.Call<CropActivity>, t: Throwable) {
                        Toast.makeText(this@ActivityCropActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

        dialog.show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
