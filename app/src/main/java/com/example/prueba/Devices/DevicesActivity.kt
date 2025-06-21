package com.example.prueba.Devices

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Crops.CropsActivity
import com.example.prueba.Devices.Adapter.DeviceAdapter
import com.example.prueba.Devices.Beans.Device
import com.example.prueba.Devices.Interfaces.PlaceHolder
import com.example.prueba.Devices.Models.RetrofitClient
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

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
                R.id.nav_crops -> startActivity(Intent(this, CropsActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(this, NotificationsActivity::class.java))
                R.id.nav_consultants -> startActivity(Intent(this, com.example.prueba.Consultations.ConsultantActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        recyclerView = findViewById(R.id.recycler_devices)
        val registerButton = findViewById<Button>(R.id.btn_register_device)

        adapter = DeviceAdapter(devices) { device ->
            showConfirmToggleDialog(device)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        registerButton.setOnClickListener {
            showRegisterDeviceDialog()
        }

        loadDevices()
    }
    private fun showRegisterDeviceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_register_device, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.et_device_name)
        val tokenInput = dialogView.findViewById<EditText>(R.id.et_device_token)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.spinner_device_type)
        val submitButton = dialogView.findViewById<Button>(R.id.btn_submit_device)

        val deviceTypes = listOf("Humidity Sensor", "Temperature Sensor")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceTypes)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = adapterSpinner

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        submitButton.setOnClickListener {
            val name = nameInput.text.toString()
            val token = tokenInput.text.toString()
            val type = typeSpinner.selectedItem.toString()

            if (name.isBlank() || token.isBlank()) return@setOnClickListener

            val deviceJson = JsonObject().apply {
                addProperty("name", name)
                addProperty("token", token)
                addProperty("deviceType", type)
                addProperty("status", "Connected")
            }

            val api = RetrofitClient.getClient("")
            api.createDevice(deviceJson).enqueue(object : Callback<Device> {
                override fun onResponse(call: Call<Device>, response: Response<Device>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            devices.add(it)
                            this@DevicesActivity.adapter.notifyDataSetChanged()
                            alertDialog.dismiss()
                            showDeviceStatusDialog(true)
                        }
                    } else {
                        Log.e("DEVICE_CREATE", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Device>, t: Throwable) {
                    Log.e("DEVICE_CREATE", "Fallo en la creación", t)
                }
            })
        }

        alertDialog.show()
    }

    private fun showConfirmToggleDialog(device: Device) {
        val isConnected = device.status == "Connected"
        val newStatus = if (isConnected) "Disconnected" else "Connected"
        val message = if (isConnected)
            "¿Deseas desconectar este dispositivo?" else "¿Deseas conectar este dispositivo?"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Confirmar acción")
            .setMessage(message)
            .setPositiveButton("Sí") { _, _ ->
                updateDeviceStatus(device, newStatus)
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun updateDeviceStatus(device: Device, newStatus: String) {
        val api: PlaceHolder = RetrofitClient.getClient("")
        val statusUpdate = JsonObject().apply {
            addProperty("id", device.id)
            addProperty("name", device.name)
            addProperty("token", device.token)
            addProperty("deviceType", device.deviceType)
            addProperty("status", newStatus)
        }


        api.updateDeviceStatus(device.id, statusUpdate).enqueue(object : Callback<Device> {
            override fun onResponse(call: Call<Device>, response: Response<Device>) {
                if (response.isSuccessful) {
                    device.status = newStatus
                    adapter.notifyDataSetChanged()
                    showDeviceStatusDialog(newStatus == "Connected")
                } else {
                    Log.e("API_PUT", "Error al actualizar estado: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Device>, t: Throwable) {
                Log.e("API_PUT", "Fallo en PUT", t)
            }
        })
    }

    private fun showDeviceStatusDialog(isConnected: Boolean) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirmation_connection, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val message = if (isConnected)
            "Dispositivo conectado exitosamente"
        else
            "Dispositivo desconectado exitosamente"

        dialogView.findViewById<TextView>(R.id.tv_device_status_message).text = message
        dialogView.findViewById<Button>(R.id.btn_ok_device_status).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadDevices() {
        val api: PlaceHolder = RetrofitClient.getClient("")
        api.getDevices().enqueue(object : Callback<List<Device>> {
            override fun onResponse(call: Call<List<Device>>, response: Response<List<Device>>) {
                if (response.isSuccessful) {
                    devices.clear()
                    devices.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("API_RESPONSE", "Respuesta no exitosa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Device>>, t: Throwable) {
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
