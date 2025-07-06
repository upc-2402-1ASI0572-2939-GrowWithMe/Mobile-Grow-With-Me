package com.example.prueba.Crops

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
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
import com.example.prueba.Crops.Beans.CropSchema
import com.example.prueba.Crops.Beans.CropSchema2
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

class CropsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CropAdapter
    private var crops = mutableListOf<Crop>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar

    private val isConsultant: Boolean
        get() {
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            return prefs.getString("role", "FARMER_ROLE") == "CONSULTANT_ROLE"
        }

    val userId: Int
        get() {
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            return prefs.getInt("user_id", -1)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crops)

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
                R.id.nav_devices -> startActivity(Intent(this, DevicesActivity::class.java))
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

        recyclerView = findViewById(R.id.recycler_crops)
        val registerButton = findViewById<Button>(R.id.btn_register_crop)

        adapter = CropAdapter(
            crops,
            isConsultant,
            onEdit = { crop, position -> showEditCropDialog(crop, position) },
            onDelete = { crop -> showDeleteConfirmationDialog(crop) },
            onView = { crop -> openCropActivities(crop) },
            onGraphic = { crop -> openDashboardActivity(crop) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        if (isConsultant) {
            registerButton.visibility = Button.GONE
        } else {
            registerButton.setOnClickListener {
                showRegisterCropDialog()
            }
        }

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        val api: PlaceHolder = RetrofitClient.getClient(token)

        api.getCropsByFarmerId(userId.toLong()).enqueue(object : Callback<List<Crop>> {
            override fun onResponse(call: Call<List<Crop>>, response: Response<List<Crop>>) {
                if (response.isSuccessful) {
                    crops.clear()
                    crops.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("API_RESPONSE", "Respuesta no exitosa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Crop>>, t: Throwable) {
                t.printStackTrace()
            }
        })
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
                R.id.nav_notifications -> startActivity(Intent(this, NotificationsActivity::class.java))
                R.id.nav_consultants -> startActivity(Intent(this, ConsultantActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    private fun openDashboardActivity(crop: Crop) {
        Log.d("CROPS", "Abriendo dashboard para cropId=${crop.id}")
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("cropId", crop.id)
        intent.putExtra("cropName", crop.productName)
        startActivity(intent)
    }

    private fun openCropActivities(crop: Crop) {
        val intent = Intent(this, ActivityCropActivity::class.java)
        intent.putExtra("cropId", crop.id)
        intent.putExtra("cropName", crop.productName)
        intent.putExtra("isConsultant", isConsultant)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun showRegisterCropDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_register_crop, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val spinner = dialogView.findViewById<Spinner>(R.id.spinner_crop_category)
        val etName = dialogView.findViewById<EditText>(R.id.et_crop_name)
        val etArea = dialogView.findViewById<EditText>(R.id.et_crop_area)
        val etCode = dialogView.findViewById<EditText>(R.id.et_crop_code)
        val etLocation = dialogView.findViewById<EditText>(R.id.et_crop_location)
        val btnAccept = dialogView.findViewById<Button>(R.id.btn_accept)

        val categories = listOf("VEGETABLE", "FRUIT", "HERB", "FLOWER", "GRAIN", "NUT", "LEGUME")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        btnAccept.setOnClickListener {
            val category = spinner.selectedItem.toString()
            val name = etName.text.toString().trim()
            val code = etCode.text.toString().trim()
            val area = etArea.text.toString().toFloatOrNull() ?: -1f
            val location = etLocation.text.toString().trim()

            if (name.isBlank()) {
                etName.error = "Nombre obligatorio"
                return@setOnClickListener
            }
            if (code.isBlank()) {
                etCode.error = "Código obligatorio"
                return@setOnClickListener
            }
            if (area <= 0f) {
                etArea.error = "Área inválida"
                return@setOnClickListener
            }
            if (location.isBlank()) {
                etLocation.error = "Ubicación obligatoria"
                return@setOnClickListener
            }

            val newCrop = CropSchema(
                farmerId = userId,
                cropActivities = emptyList(),
                productName = name,
                code = code,
                category = category,
                status = "EMPTY",
                area = area,
                location = location,
                cost = 0,
                registrationDate = "2025-07-05"
            )

            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val token = prefs.getString("token", "") ?: ""
            val api = RetrofitClient.getClient(token)

            api.createCrop(newCrop).enqueue(object : Callback<Crop> {
                override fun onResponse(call: Call<Crop>, response: Response<Crop>) {
                    if (response.isSuccessful && response.body() != null) {
                        crops.add(response.body()!!)
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                        showConfirmationDialog("Cultivo creado exitosamente")
                    } else {
                        Log.e("CREATE_CROP", "Error ${response.code()}")
                        Toast.makeText(this@CropsActivity, "Error al crear cultivo", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Crop>, t: Throwable) {
                    t.printStackTrace()
                    Toast.makeText(this@CropsActivity, "Error de red", Toast.LENGTH_SHORT).show()
                }
            })
        }

        dialog.show()
    }

    private fun showConfirmationDialog(message: String) {
        val view = layoutInflater.inflate(R.layout.dialog_confirmation_register, null)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        val btnOk = view.findViewById<Button>(R.id.btn_ok)
        val txtMsg = view.findViewById<TextView>(R.id.tv_message)
        txtMsg?.text = message

        btnOk.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
    private fun showEditCropDialog(crop: Crop, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_register_crop, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val spinner = dialogView.findViewById<Spinner>(R.id.spinner_crop_category)
        val etName = dialogView.findViewById<EditText>(R.id.et_crop_name)
        val etArea = dialogView.findViewById<EditText>(R.id.et_crop_area)
        val etCode = dialogView.findViewById<EditText>(R.id.et_crop_code)
        val etLocation = dialogView.findViewById<EditText>(R.id.et_crop_location)
        val btnAccept = dialogView.findViewById<Button>(R.id.btn_accept)

        val categories = listOf("VEGETABLE", "FRUIT", "HERB", "FLOWER", "GRAIN", "NUT", "LEGUME")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = spinnerAdapter

        etName.setText(crop.productName)
        etCode.setText(crop.code)
        etArea.setText(crop.area.toString())
        etLocation.setText(crop.location)
        val selectedIndex = categories.indexOf(crop.category)
        if (selectedIndex >= 0) spinner.setSelection(selectedIndex)

        btnAccept.text = "Actualizar"
        btnAccept.setOnClickListener {
            val type = spinner.selectedItem.toString()
            val newName = etName.text.toString().trim()
            val newCode = etCode.text.toString().trim()
            val newArea = etArea.text.toString().toFloatOrNull() ?: 0f
            val newLocation = etLocation.text.toString().trim()

            if (newName.isBlank()) {
                etName.error = "Nombre obligatorio"
                return@setOnClickListener
            }
            if (newCode.isBlank()) {
                etCode.error = "Código obligatorio"
                return@setOnClickListener
            }
            if (newArea <= 0f) {
                etArea.error = "Área inválida"
                return@setOnClickListener
            }
            if (newLocation.isBlank()) {
                etLocation.error = "Ubicación obligatoria"
                return@setOnClickListener
            }

            val updatedCrop = CropSchema2(
                productName = newName,
                code = newCode,
                category = type,
                area = newArea,
                location = newLocation
            )

            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val token = prefs.getString("token", "") ?: ""
            val api = RetrofitClient.getClient(token)

            api.updateCrop(crop.id.toLong(), updatedCrop).enqueue(object : Callback<Crop> {
                override fun onResponse(call: Call<Crop>, response: Response<Crop>) {
                    if (response.isSuccessful && response.body() != null) {
                        crops[position] = response.body()!!
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                        showConfirmationDialog("Cultivo actualizado correctamente")
                    } else {
                        Log.e("UPDATE_CROP", "Error al actualizar: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Crop>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }

        dialog.show()
    }


    private fun showDeleteConfirmationDialog(crop: Crop) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("¿Eliminar cultivo?")
            .setMessage("¿Estás seguro(a) de que deseas eliminar \"${crop.productName}\"?")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val token = prefs.getString("token", "") ?: ""
                val api = RetrofitClient.getClient(token)

                api.deleteCrop(crop.id.toLong()).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            crops.remove(crop)
                            adapter.notifyDataSetChanged()
                        } else {
                            Log.e("DELETE_CROP", "Error al eliminar: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }
    override fun onResume() {
        super.onResume()
        loadCrops()
    }
    private fun loadCrops() {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val token = prefs.getString("token", "") ?: ""
        val role = prefs.getString("role", "") ?: ""
        val api: PlaceHolder = RetrofitClient.getClient(token)

        val call: Call<List<Crop>> = if (role == "CONSULTANT_ROLE") {
            api.getCrops()
        } else {
            api.getCropsByFarmerId(userId.toLong())
        }

        call.enqueue(object : Callback<List<Crop>> {
            override fun onResponse(call: Call<List<Crop>>, response: Response<List<Crop>>) {
                if (response.isSuccessful) {
                    crops.clear()
                    crops.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("API_RESPONSE", "Respuesta no exitosa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Crop>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}
