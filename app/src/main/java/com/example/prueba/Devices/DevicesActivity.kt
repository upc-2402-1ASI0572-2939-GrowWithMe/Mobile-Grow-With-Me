package com.example.prueba.Devices

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Consultations.ConsultantActivity
import com.example.prueba.Crops.Beans.Crop
import com.example.prueba.Crops.Models.RetrofitClient as CropsRetrofitClient
import com.example.prueba.Devices.Models.RetrofitClient as DevicesRetrofitClient
import com.example.prueba.Devices.Adapter.DeviceAdapter
import com.example.prueba.Devices.Beans.Device
import com.example.prueba.Devices.Beans.DeviceSchema
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import com.example.prueba.Crops.CropsActivity
import com.example.prueba.Farmers.FarmerActivity
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DevicesActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DeviceAdapter
    private var devices = mutableListOf<Device>()
    private var cropsMap = mutableMapOf<Int, String>()
    private var farmerId = -1

    private val isConsultant: Boolean
        get() {
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            return prefs.getString("user_role", "Agricultor") == "Consultor"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        farmerId = prefs.getInt("user_id", -1)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_devices -> startActivity(Intent(this, DevicesActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(this, NotificationsActivity::class.java))
                R.id.nav_crops -> {
                    startActivity(Intent(this, CropsActivity::class.java))
                    finish()
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

        recyclerView = findViewById(R.id.recycler_devices)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DeviceAdapter(devices)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btn_register_device).setOnClickListener {
            showRegisterDialog()
        }

        loadCropsThenDevices()
    }

    private fun loadCropsThenDevices() {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""

        CropsRetrofitClient.getClient(token).getCropsByFarmerId(farmerId.toLong())
            .enqueue(object : Callback<List<Crop>> {
                override fun onResponse(call: Call<List<Crop>>, response: Response<List<Crop>>) {
                    if (response.isSuccessful) {
                        cropsMap.clear()
                        response.body()?.forEach {
                            cropsMap[it.id] = it.productName
                        }
                        loadDevices(token)
                    }
                }

                override fun onFailure(call: Call<List<Crop>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun loadDevices(token: String) {
        DevicesRetrofitClient.getClient(token).getDevicesByFarmerId()
            .enqueue(object : Callback<List<Device>> {
                override fun onResponse(call: Call<List<Device>>, response: Response<List<Device>>) {
                    if (response.isSuccessful) {
                        devices.clear()
                        devices.addAll(response.body() ?: emptyList())
                        adapter.updateCropsMap(cropsMap)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<List<Device>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun showRegisterDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_register_device, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.et_device_name)
        val cropSpinner = dialogView.findViewById<Spinner>(R.id.spinner_crop)

        val cropNames = cropsMap.map { it.value }
        val cropIds = cropsMap.map { it.key }

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cropNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cropSpinner.adapter = spinnerAdapter

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btn_submit_device).setOnClickListener {
            val name = nameInput.text.toString()
            val selectedCropId = cropIds.getOrNull(cropSpinner.selectedItemPosition) ?: return@setOnClickListener

            if (name.isBlank()) return@setOnClickListener

            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val token = prefs.getString("token", "") ?: ""

            val farmerId = getSharedPreferences("MyPrefs", MODE_PRIVATE).getInt("user_id", -1)

            val schema = DeviceSchema(
                cropId = selectedCropId,
                farmerId = farmerId,
                name = name,
                temperatureList = emptyList(),
                humidityList = emptyList(),
                isActive = false
            )


            DevicesRetrofitClient.getClient(token).createDevice(schema)
                .enqueue(object : Callback<Device> {
                    override fun onResponse(call: Call<Device>, response: Response<Device>) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                devices.add(it)
                                adapter.notifyDataSetChanged()
                                alertDialog.dismiss()
                                showStatusDialog("Dispositivo registrado")
                            }
                        }
                    }

                    override fun onFailure(call: Call<Device>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
        }

        alertDialog.show()
    }

    private fun showStatusDialog(msg: String) {
        AlertDialog.Builder(this)
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
