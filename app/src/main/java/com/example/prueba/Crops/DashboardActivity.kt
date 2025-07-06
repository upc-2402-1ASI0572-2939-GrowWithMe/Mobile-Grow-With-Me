package com.example.prueba.Monitoring

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.prueba.Crops.CropsActivity
import com.example.prueba.Devices.Beans.Device
import com.example.prueba.Devices.DevicesActivity
import com.example.prueba.Devices.Interfaces.PlaceHolder
import com.example.prueba.Devices.Models.RetrofitClient
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import com.example.prueba.Consultations.ConsultantActivity
import com.example.prueba.Consultations.ConsultationActivity
import com.example.prueba.MainActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navView: NavigationView
    private lateinit var lineChart: LineChart

    private var temperatureList: List<Double> = emptyList()
    private var humidityList: List<Double> = emptyList()

    private val isConsultant: Boolean
        get() {
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            return prefs.getString("role", "FARMER_ROLE") == "CONSULTANT_ROLE"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitoring)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        filterMenuByRole()

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_devices -> {
                    startActivity(Intent(this, DevicesActivity::class.java))
                    finish()
                }
                R.id.nav_notifications -> {
                    val target = if (isConsultant)
                        ConsultationActivity::class.java
                    else
                        NotificationsActivity::class.java
                    startActivity(Intent(this, target))
                    finish()
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                }
                R.id.nav_consultants -> {
                    startActivity(Intent(this, ConsultantActivity::class.java))
                    finish()
                }
                R.id.nav_crops -> {
                    startActivity(Intent(this, CropsActivity::class.java))
                    finish()
                }
                R.id.btn_logout -> {
                    val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    prefs.edit { clear() }
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val cropId = intent.getIntExtra("cropId", -1).toLong()
        val cropName = intent.getStringExtra("cropName") ?: "Cultivo"
        findViewById<TextView>(R.id.tv_dashboard_title).text = "Dashboard: $cropName"

        lineChart = findViewById(R.id.line_chart)

        if (cropId != -1L) {
            fetchSensorData(cropId)
        } else {
            Toast.makeText(this, "ID de cultivo no válido (cropId = -1)", Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchSensorData(cropId: Long) {
        val token = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("authToken", "") ?: ""
        val api: PlaceHolder = RetrofitClient.getClient(token)

        api.getSensorData(cropId).enqueue(object : Callback<Device> {
            override fun onResponse(call: Call<Device>, response: Response<Device>) {
                if (response.isSuccessful && response.body() != null) {
                    val device = response.body()!!
                    temperatureList = device.temperatureList
                    humidityList = device.humidityList

                    findViewById<TextView>(R.id.tv_temperature).text =
                        "${temperatureList.lastOrNull() ?: "--"}°C"
                    findViewById<TextView>(R.id.tv_humidity).text =
                        "${humidityList.lastOrNull() ?: "--"}%"

                    updateChart(temperatureList, humidityList)
                } else {
                    Toast.makeText(this@DashboardActivity, "Datos no disponibles", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Device>, t: Throwable) {
                Toast.makeText(this@DashboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateChart(temps: List<Double>, hums: List<Double>) {
        val entriesTemp = temps.mapIndexed { index, value -> Entry(index.toFloat(), value.toFloat()) }
        val entriesHumidity = hums.mapIndexed { index, value -> Entry(index.toFloat(), value.toFloat()) }

        val dataSetTemp = LineDataSet(entriesTemp, "Temperatura").apply {
            color = Color.RED
            valueTextColor = Color.BLACK
            lineWidth = 2f
            circleRadius = 3f
        }

        val dataSetHumidity = LineDataSet(entriesHumidity, "Humedad").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
            lineWidth = 2f
            circleRadius = 3f
        }

        val lineData = LineData(dataSetTemp, dataSetHumidity)
        lineChart.data = lineData
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.description.text = ""
        lineChart.invalidate()
    }

    private fun filterMenuByRole() {
        val menu: Menu = navView.menu
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val role = prefs.getString("role", "FARMER_ROLE")

        if (role == "CONSULTANT_ROLE") {
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
