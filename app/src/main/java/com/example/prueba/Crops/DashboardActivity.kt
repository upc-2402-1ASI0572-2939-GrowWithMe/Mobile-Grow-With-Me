package com.example.prueba.Monitoring

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.prueba.Crops.CropsActivity
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream

class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var exportMessage: TextView
    private lateinit var exportButton: Button
    private lateinit var navView: NavigationView
    private lateinit var lineChart: LineChart

    private val isConsultant: Boolean
        get() {
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            return prefs.getString("user_role", "Agricultor") == "Consultor"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitoring)

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
                R.id.nav_devices -> startActivity(Intent(this, com.example.prueba.Devices.DevicesActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(this, NotificationsActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
                R.id.nav_consultants -> startActivity(Intent(this, com.example.prueba.Consultations.ConsultantActivity::class.java))
                R.id.nav_crops -> startActivity(Intent(this, CropsActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val cropName = intent.getStringExtra("cropName") ?: "Cultivo"
        val titleTextView = findViewById<TextView>(R.id.tv_dashboard_title)
        titleTextView.text = "Dashboard: $cropName"

        exportMessage = findViewById(R.id.tv_export_message)
        exportButton = findViewById(R.id.btn_export)

        findViewById<TextView>(R.id.tv_temperature).text = "24.5°C"
        findViewById<TextView>(R.id.tv_humidity).text = "65%"

        exportButton.setOnClickListener {
            exportDataToCSV()
        }

        lineChart = findViewById(R.id.line_chart)
        setupChart()
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

    private fun exportDataToCSV() {
        val csvData = StringBuilder()
        csvData.append("Date,Temperature,Humidity\n")
        csvData.append("2025-05-15,24.5°C,65%\n")
        csvData.append("2025-05-16,25.0°C,60%\n")
        csvData.append("2025-05-17,23.8°C,70%\n")

        try {
            val folder = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ExportedData")
            if (!folder.exists()) folder.mkdirs()

            val file = File(folder, "Dashboard_Data.csv")
            FileOutputStream(file).use { it.write(csvData.toString().toByteArray()) }

            exportMessage.text = "¡Datos exportados con éxito!\nArchivo: ${file.name}"
        } catch (e: Exception) {
            Log.e("EXPORT", "Error al exportar datos", e)
            exportMessage.text = "Error al exportar los datos."
        }

        exportMessage.visibility = TextView.VISIBLE
        exportMessage.postDelayed({
            exportMessage.visibility = TextView.GONE
        }, 3000)
    }

    private fun setupChart() {
        val entriesTemp = listOf(
            Entry(0f, 24.5f),
            Entry(1f, 25.0f),
            Entry(2f, 23.8f)
        )

        val entriesHumidity = listOf(
            Entry(0f, 65f),
            Entry(1f, 60f),
            Entry(2f, 70f)
        )

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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
