package com.example.prueba.Crops

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Activity.ActivityCropActivity
import com.example.prueba.Crops.Adapter.CropAdapter
import com.example.prueba.Crops.Beans.Crop
import com.example.prueba.Crops.Interfaces.PlaceHolder
import com.example.prueba.Crops.Models.RetrofitClient
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

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        recyclerView = findViewById(R.id.recycler_crops)
        val registerButton = findViewById<Button>(R.id.btn_register_crop)

        adapter = CropAdapter(crops,
            onEdit = { crop, position -> showEditCropDialog(crop, position) },
            onDelete = { crop -> showDeleteConfirmationDialog(crop) },
            onView = { crop -> openCropActivities(crop) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        registerButton.setOnClickListener {
            showRegisterCropDialog()
        }

        val api: PlaceHolder = RetrofitClient.getClient("")

        api.getCrops().enqueue(object : Callback<List<Crop>> {
            override fun onResponse(call: Call<List<Crop>>, response: Response<List<Crop>>) {
                if (response.isSuccessful) {
                    val allCrops = response.body() ?: emptyList()
                    crops.clear()
                    crops.addAll(allCrops.filter { it.profileId == 1 })
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

    private fun openCropActivities(crop: Crop) {
        val intent = Intent(this, ActivityCropActivity::class.java)
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

    private fun showRegisterCropDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_register_crop, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val spinner = dialogView.findViewById<Spinner>(R.id.spinner_crop_category)
        val etName = dialogView.findViewById<EditText>(R.id.et_crop_name)
        val etArea = dialogView.findViewById<EditText>(R.id.et_crop_area)
        val btnAccept = dialogView.findViewById<Button>(R.id.btn_accept)

        val categories = listOf("Vegetables", "Fruits", "Greens", "Species")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = spinnerAdapter

        btnAccept.setOnClickListener {
            val type = spinner.selectedItem.toString()
            val name = etName.text.toString()
            val area = etArea.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isNotBlank()) {
                val crop = Crop(0, "TEMP_CODE", name, type, area.toInt(), "Lima", "Activo", 100, 150, 1)

                val api = RetrofitClient.getClient("")
                api.createCrop(crop).enqueue(object : Callback<Crop> {
                    override fun onResponse(call: Call<Crop>, response: Response<Crop>) {
                        if (response.isSuccessful) {
                            crops.add(response.body()!!)
                            adapter.notifyDataSetChanged()
                            dialog.dismiss()
                            showConfirmationDialog()
                        } else {
                            Log.e("CREATE_CROP", "Error al crear: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Crop>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            } else {
                etName.error = "Obligatorio"
            }
        }

        dialog.show()
    }

    private fun showConfirmationDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_confirmation_register, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(view)
            .create()

        val btnOk = view.findViewById<Button>(R.id.btn_ok)
        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditCropDialog(crop: Crop, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_register_crop, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val spinner = dialogView.findViewById<Spinner>(R.id.spinner_crop_category)
        val etName = dialogView.findViewById<EditText>(R.id.et_crop_name)
        val etArea = dialogView.findViewById<EditText>(R.id.et_crop_area)
        val btnAccept = dialogView.findViewById<Button>(R.id.btn_accept)

        val categories = listOf("Vegetables", "Fruits", "Greens", "Species")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinner.adapter = spinnerAdapter

        etName.setText(crop.productName)
        etArea.setText(crop.area.toString())
        val selectedIndex = categories.indexOf(crop.category)
        if (selectedIndex >= 0) spinner.setSelection(selectedIndex)

        btnAccept.text = "Actualizar"

        btnAccept.setOnClickListener {
            val type = spinner.selectedItem.toString()
            val newName = etName.text.toString()
            val newArea = etArea.text.toString().toDoubleOrNull() ?: 0.0

            if (newName.isNotBlank()) {
                val updatedCrop = Crop(
                    crop.id, crop.code, newName, type, newArea.toInt(), crop.location, crop.status,
                    crop.cost, crop.profitReturn, crop.profileId
                )

                val api = RetrofitClient.getClient("")
                api.updateCrop(crop.id.toLong(), updatedCrop).enqueue(object : Callback<Crop> {
                    override fun onResponse(call: Call<Crop>, response: Response<Crop>) {
                        if (response.isSuccessful) {
                            crops[position] = response.body()!!
                            adapter.notifyDataSetChanged()
                            dialog.dismiss()
                            showConfirmationDialog()
                        } else {
                            Log.e("UPDATE_CROP", "Error al actualizar: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Crop>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            } else {
                etName.error = "Obligatorio"
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(crop: Crop) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("¿Eliminar cultivo?")
            .setMessage("¿Estás seguro(a) de que deseas eliminar \"${crop.productName}\"?")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                val api = RetrofitClient.getClient("")
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
}
