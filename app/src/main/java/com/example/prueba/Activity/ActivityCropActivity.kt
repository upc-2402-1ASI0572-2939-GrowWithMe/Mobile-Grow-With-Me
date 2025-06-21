// Aquí comienza tu código con la cabecera correcta
package com.example.prueba.Activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.CalendarView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.prueba.Crops.CropsActivity
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*

class ActivityCropActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var tvCropName: TextView
    private lateinit var tvTitle: TextView
    private lateinit var calendarView: CalendarView
    private val registeredDates = mutableSetOf<Long>()
    private var cropId: Int = -1
    private var isConsultant: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crops_activities)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        tvCropName = findViewById(R.id.tv_crop_name)
        tvTitle = findViewById(R.id.tv_calendar_title)
        calendarView = findViewById(R.id.calendarView)
        calendarView.minDate = System.currentTimeMillis()

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        filterMenuByRole()

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_devices -> startActivity(Intent(this, com.example.prueba.Devices.DevicesActivity::class.java))
                R.id.nav_crops -> startActivity(Intent(this, CropsActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(this, NotificationsActivity::class.java))
                R.id.nav_consultants -> startActivity(Intent(this, com.example.prueba.Consultations.ConsultantActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        cropId = intent.getIntExtra("cropId", -1)
        isConsultant = intent.getBooleanExtra("isConsultant", false)
        val cropName = intent.getStringExtra("cropName") ?: "Cultivo"
        tvCropName.text = cropName

        loadCropActivities(cropId)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val selectedDate = calendar.timeInMillis
            showRegisterActivityDialog(year, month, dayOfMonth)
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
    private fun loadCropActivities(cropId: Int) {
        val call = com.example.prueba.Activity.Models.RetrofitClient.getClient("")
            .getActivitiesByCropId(cropId)

        call.enqueue(object : retrofit2.Callback<List<com.example.prueba.Activity.Beans.CropActivity>> {
            override fun onResponse(call: retrofit2.Call<List<com.example.prueba.Activity.Beans.CropActivity>>, response: retrofit2.Response<List<com.example.prueba.Activity.Beans.CropActivity>>) {
                if (response.isSuccessful) {
                    val activities = response.body() ?: return
                    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    activities.forEach {
                        try {
                            val date = format.parse(it.activityDate)
                            date?.let { d ->
                                val cal = Calendar.getInstance().apply {
                                    time = d
                                    set(Calendar.HOUR_OF_DAY, 0)
                                    set(Calendar.MINUTE, 0)
                                    set(Calendar.SECOND, 0)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                registeredDates.add(cal.timeInMillis)
                            }
                        } catch (_: Exception) {}
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<com.example.prueba.Activity.Beans.CropActivity>>, t: Throwable) {
                android.widget.Toast.makeText(this@ActivityCropActivity, "Error al cargar actividades", android.widget.Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun showRegisterActivityDialog(year: Int, month: Int, day: Int) {
        val formattedDate = String.format("%02d/%02d/%04d", day, month + 1, year)

        val selectedDateKey = Calendar.getInstance().apply {
            set(year, month, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val dateToCompare = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDateKey))
        val client = com.example.prueba.Activity.Models.RetrofitClient.getClient("")

        client.getActivitiesByCropId(cropId).enqueue(object : retrofit2.Callback<List<com.example.prueba.Activity.Beans.CropActivity>> {
            override fun onResponse(
                call: retrofit2.Call<List<com.example.prueba.Activity.Beans.CropActivity>>,
                response: retrofit2.Response<List<com.example.prueba.Activity.Beans.CropActivity>>
            ) {
                if (response.isSuccessful) {
                    val existing = response.body()?.find { it.activityDate.startsWith(dateToCompare) }

                    // Si es consultor y no hay actividad: solo mostrar Toast
                    if (isConsultant && existing == null) {
                        android.widget.Toast.makeText(
                            this@ActivityCropActivity,
                            "No hay actividades registradas para esta fecha.",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    val dialogView = layoutInflater.inflate(R.layout.activity_register_activity, null)
                    val dialog = androidx.appcompat.app.AlertDialog.Builder(this@ActivityCropActivity)
                        .setView(dialogView)
                        .setCancelable(true)
                        .create()

                    val etDate = dialogView.findViewById<EditText>(R.id.et_activity_date)
                    val etCropName = dialogView.findViewById<EditText>(R.id.et_crop_name)
                    val etTime = dialogView.findViewById<EditText>(R.id.et_activity_time)
                    val etDescription = dialogView.findViewById<EditText>(R.id.et_activity_description)
                    val btnSubmit = dialogView.findViewById<android.widget.Button>(R.id.btn_submit_activity)
                    val btnDelete = dialogView.findViewById<android.widget.Button>(R.id.btn_delete_activity)

                    etDate.setText(formattedDate)
                    etCropName.setText(tvCropName.text.toString())

                    if (existing != null) {
                        val timePart = existing.activityDate.split(" ").getOrNull(1) ?: ""
                        etTime.setText(timePart)
                        etDescription.setText(existing.description)
                        btnSubmit.text = "Actualizar"

                        if (isConsultant) {
                            btnSubmit.visibility = android.view.View.GONE
                            btnDelete.visibility = android.view.View.GONE
                            etTime.isEnabled = false
                            etDescription.isEnabled = false
                        } else {
                            btnDelete.visibility = android.view.View.VISIBLE

                            btnSubmit.setOnClickListener {
                                val updatedActivity = com.example.prueba.Activity.Beans.CropActivity(
                                    id = existing.id,
                                    cropId = existing.cropId,
                                    activityDate = "${etDate.text} ${etTime.text}",
                                    description = etDescription.text.toString()
                                )
                                client.updateActivity(existing.id, updatedActivity)
                                    .enqueue(object : retrofit2.Callback<com.example.prueba.Activity.Beans.CropActivity> {
                                        override fun onResponse(call: retrofit2.Call<com.example.prueba.Activity.Beans.CropActivity>, response: retrofit2.Response<com.example.prueba.Activity.Beans.CropActivity>) {
                                            if (response.isSuccessful) {
                                                android.widget.Toast.makeText(this@ActivityCropActivity, "Actividad actualizada", android.widget.Toast.LENGTH_SHORT).show()
                                                dialog.dismiss()
                                            } else {
                                                android.widget.Toast.makeText(this@ActivityCropActivity, "Error al actualizar", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onFailure(call: retrofit2.Call<com.example.prueba.Activity.Beans.CropActivity>, t: Throwable) {
                                            android.widget.Toast.makeText(this@ActivityCropActivity, "Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    })
                            }

                            btnDelete.setOnClickListener {
                                client.deleteActivity(existing.id).enqueue(object : retrofit2.Callback<Void> {
                                    override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                                        if (response.isSuccessful) {
                                            android.widget.Toast.makeText(this@ActivityCropActivity, "Actividad eliminada", android.widget.Toast.LENGTH_SHORT).show()
                                            registeredDates.remove(selectedDateKey)
                                            dialog.dismiss()
                                        } else {
                                            android.widget.Toast.makeText(this@ActivityCropActivity, "Error al eliminar", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                                        android.widget.Toast.makeText(this@ActivityCropActivity, "Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        }
                    } else {
                        // No hay actividad, pero el usuario NO es consultor
                        btnSubmit.setOnClickListener {
                            val description = etDescription.text.toString().trim()
                            val time = etTime.text.toString().trim()

                            if (description.isEmpty() || time.isEmpty()) {
                                etDescription.error = if (description.isEmpty()) "Requerido" else null
                                etTime.error = if (time.isEmpty()) "Requerido" else null
                                return@setOnClickListener
                            }

                            val activityDate = "${etDate.text} $time"
                            val newActivity = com.example.prueba.Activity.Beans.CropActivitySchema(
                                cropId = cropId,
                                activityDate = activityDate,
                                description = description
                            )

                            client.createActivity(newActivity).enqueue(object : retrofit2.Callback<com.example.prueba.Activity.Beans.CropActivity> {
                                override fun onResponse(call: retrofit2.Call<com.example.prueba.Activity.Beans.CropActivity>, response: retrofit2.Response<com.example.prueba.Activity.Beans.CropActivity>) {
                                    if (response.isSuccessful) {
                                        android.widget.Toast.makeText(this@ActivityCropActivity, "Actividad registrada", android.widget.Toast.LENGTH_SHORT).show()
                                        registeredDates.add(selectedDateKey)
                                        dialog.dismiss()
                                    } else {
                                        android.widget.Toast.makeText(this@ActivityCropActivity, "Error al registrar", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: retrofit2.Call<com.example.prueba.Activity.Beans.CropActivity>, t: Throwable) {
                                    android.widget.Toast.makeText(this@ActivityCropActivity, "Error: ${t.message}", android.widget.Toast.LENGTH_LONG).show()
                                }
                            })
                        }
                    }

                    // Solo se muestra el diálogo si hay actividad o si es agricultor
                    dialog.show()
                }
            }

            override fun onFailure(call: retrofit2.Call<List<com.example.prueba.Activity.Beans.CropActivity>>, t: Throwable) {
                android.widget.Toast.makeText(this@ActivityCropActivity, "Error al consultar actividad", android.widget.Toast.LENGTH_SHORT).show()
            }
        })
    }

    @Deprecated("Usar OnBackPressedDispatcher")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
