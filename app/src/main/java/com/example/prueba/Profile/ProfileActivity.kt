package com.example.prueba.Profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.cloudinary.android.MediaManager
import com.example.prueba.Consultations.ConsultationActivity
import com.example.prueba.MainActivity
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.Beans.ConsultantProfile
import com.example.prueba.Profile.Beans.FarmerProfile
import com.example.prueba.Profile.Models.RetrofitClient
import com.example.prueba.R
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import android.graphics.BitmapFactory
import android.os.StrictMode

class ProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar

    private lateinit var txtFullName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtPhone: TextView
    private lateinit var imgProfile: ImageView
    private lateinit var btnEdit: Button
    private lateinit var btnLogout: Button

    private val isConsultant: Boolean
        get() {
            val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            return prefs.getString("role", "FARMER_ROLE") == "CONSULTANT_ROLE"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // ✅ Inicializar Cloudinary si no ha sido inicializado
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

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Perfil"

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

        txtFullName = findViewById(R.id.txt_full_name)
        txtEmail = findViewById(R.id.txt_email)
        txtPhone = findViewById(R.id.txt_phone)
        imgProfile = findViewById(R.id.img_profile)
        btnEdit = findViewById(R.id.btn_edit_profile)
        btnLogout = findViewById(R.id.btn_logout)

        imgProfile.setOnClickListener {
            showImagePickerDialog()
        }

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_devices -> {
                    startActivity(Intent(this, com.example.prueba.Devices.DevicesActivity::class.java))
                    finish()
                }
                R.id.nav_notifications -> {
                    val target = if (isConsultant)
                        ConsultationActivity::class.java
                    else
                        NotificationsActivity::class.java
                    startActivity(Intent(this, target))
                }
                R.id.nav_profile -> { }
                R.id.nav_consultants -> {
                    startActivity(Intent(this, com.example.prueba.Consultations.ConsultantActivity::class.java))
                    finish()
                }
                R.id.nav_crops -> {
                    startActivity(Intent(this, com.example.prueba.Crops.CropsActivity::class.java))
                    finish()
                }
                R.id.btn_logout -> {
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        btnEdit.setOnClickListener {
            // Funcionalidad futura
        }

        btnLogout.setOnClickListener {
            val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            prefs.edit { clear() }
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        loadProfile()
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

    private fun loadProfile() {
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val role = prefs.getString("role", "FARMER_ROLE") ?: "FARMER_ROLE"
        val userId = prefs.getInt("user_id", -1)
        val api = RetrofitClient.getClient("")

        if (userId == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        if (role == "CONSULTANT_ROLE") {
            api.getAllConsultants().enqueue(object : Callback<List<ConsultantProfile>> {
                override fun onResponse(call: Call<List<ConsultantProfile>>, response: Response<List<ConsultantProfile>>) {
                    val consultant = response.body()?.find { it.id == userId }
                    consultant?.let {
                        txtFullName.text = "${it.firstName} ${it.lastName}"
                        txtEmail.text = it.email
                        txtPhone.text = it.phone

                        try {
                            val imageUrl = MediaManager.get()
                                .url()
                                .secure(true)
                                .generate(it.photoUrl)

                            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                            StrictMode.setThreadPolicy(policy)

                            val input = URL(imageUrl).openStream()
                            val bitmap = BitmapFactory.decodeStream(input)
                            imgProfile.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            imgProfile.setImageResource(R.drawable.ic_person)
                        }
                    }
                }

                override fun onFailure(call: Call<List<ConsultantProfile>>, t: Throwable) {
                    Log.e("PROFILE", "Error", t)
                }
            })
        } else {
            api.getAllFarmers().enqueue(object : Callback<List<FarmerProfile>> {
                override fun onResponse(call: Call<List<FarmerProfile>>, response: Response<List<FarmerProfile>>) {
                    val farmer = response.body()?.find { it.id == userId }
                    farmer?.let {
                        txtFullName.text = "${it.firstName} ${it.lastName}"
                        txtEmail.text = it.email
                        txtPhone.text = it.phone

                        try {
                            val imageUrl = MediaManager.get()
                                .url()
                                .secure(true)
                                .generate(it.photoUrl)

                            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                            StrictMode.setThreadPolicy(policy)

                            val input = URL(imageUrl).openStream()
                            val bitmap = BitmapFactory.decodeStream(input)
                            imgProfile.setImageBitmap(bitmap)
                        } catch (e: Exception) {
                            imgProfile.setImageResource(R.drawable.ic_person)
                        }
                    }
                }

                override fun onFailure(call: Call<List<FarmerProfile>>, t: Throwable) {
                    Log.e("PROFILE", "Error", t)
                }
            })
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Tomar foto", "Elegir de galería")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cambiar foto de perfil")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        Toast.makeText(this, "Abrir cámara (a implementar)", Toast.LENGTH_SHORT).show()
    }

    private fun openGallery() {
        Toast.makeText(this, "Abrir galería (a implementar)", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun showConfirmationDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_editprofile_confirmation, null)
        val dialog = AlertDialog.Builder(this).setView(view).create()
        view.findViewById<Button>(R.id.btn_ok).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
