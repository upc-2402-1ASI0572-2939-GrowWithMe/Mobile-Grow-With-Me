package com.example.prueba.Profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.prueba.MainActivity
import com.example.prueba.Profile.Beans.ConsultantProfile
import com.example.prueba.Profile.Beans.FarmerProfile
import com.example.prueba.Profile.Models.RetrofitClient
import com.example.prueba.R
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        txtFullName = findViewById(R.id.txt_full_name)
        txtEmail = findViewById(R.id.txt_email)
        txtPhone = findViewById(R.id.txt_phone)
        imgProfile = findViewById(R.id.img_profile)
        imgProfile.setOnClickListener {
            showImagePickerDialog()
        }

        btnEdit = findViewById(R.id.btn_edit_profile)
        btnLogout = findViewById(R.id.btn_logout)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.btn_edit_profile -> showEditProfileDialog()
                R.id.btn_logout -> finishAffinity()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        btnEdit.setOnClickListener {
            showEditProfileDialog()
        }

        btnLogout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        loadProfile()
    }
    private fun openCamera() {
        Toast.makeText(this, "Abrir cámara (a implementar)", Toast.LENGTH_SHORT).show()
    }

    private fun openGallery() {
        Toast.makeText(this, "Abrir galería (a implementar)", Toast.LENGTH_SHORT).show()
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

    private fun loadProfile() {
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val role = prefs.getString("user_role", "Agricultor") ?: "Agricultor"
        val api = RetrofitClient.getClient("")

        if (role == "Consultor") {
            api.getConsultantById(2).enqueue(object : Callback<ConsultantProfile> {
                override fun onResponse(call: Call<ConsultantProfile>, response: Response<ConsultantProfile>) {
                    response.body()?.let {
                        txtFullName.text = "${it.firstName} ${it.lastName}"
                        txtEmail.text = it.email
                        txtPhone.text = it.phone
                    }
                }
                override fun onFailure(call: Call<ConsultantProfile>, t: Throwable) {
                    Log.e("PROFILE", "Error", t)
                }
            })
        } else {
            api.getFarmerById(3).enqueue(object : Callback<FarmerProfile> {
                override fun onResponse(call: Call<FarmerProfile>, response: Response<FarmerProfile>) {
                    response.body()?.let {
                        txtFullName.text = "${it.firstName} ${it.lastName}"
                        txtEmail.text = it.email
                        txtPhone.text = it.phone
                    }
                }
                override fun onFailure(call: Call<FarmerProfile>, t: Throwable) {
                    Log.e("PROFILE", "Error", t)
                }
            })
        }
    }

    private fun showEditProfileDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val etFirstName = view.findViewById<EditText>(R.id.et_first_name)
        val etLastName = view.findViewById<EditText>(R.id.et_last_name)
        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val etPhone = view.findViewById<EditText>(R.id.et_phone)
        val etDni = view.findViewById<EditText>(R.id.et_dni)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val role = prefs.getString("user_role", "Agricultor") ?: "Agricultor"
        val api = RetrofitClient.getClient("")

        val dialog = AlertDialog.Builder(this).setView(view).create()

        fun showAndPopulateDialog(
            firstName: String,
            lastName: String,
            email: String,
            phone: String,
            dni: String
        ) {
            etFirstName.setText(firstName)
            etLastName.setText(lastName)
            etEmail.setText(email)
            etPhone.setText(phone)
            etDni.setText(dni)
            dialog.show()
        }

        if (role == "Consultor") {
            api.getConsultantById(2).enqueue(object : Callback<ConsultantProfile> {
                override fun onResponse(call: Call<ConsultantProfile>, response: Response<ConsultantProfile>) {
                    response.body()?.let {
                        showAndPopulateDialog(it.firstName, it.lastName, it.email, it.phone, it.dni ?: "")
                    }
                }

                override fun onFailure(call: Call<ConsultantProfile>, t: Throwable) {}
            })
        } else {
            api.getFarmerById(3).enqueue(object : Callback<FarmerProfile> {
                override fun onResponse(call: Call<FarmerProfile>, response: Response<FarmerProfile>) {
                    response.body()?.let {
                        showAndPopulateDialog(it.firstName, it.lastName, it.email, it.phone, it.dni ?: "")
                    }
                }

                override fun onFailure(call: Call<FarmerProfile>, t: Throwable) {}
            })
        }

        btnSave.setOnClickListener {
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val dni = etDni.text.toString().toIntOrNull() ?: return@setOnClickListener

            val json = JsonObject().apply {
                addProperty("firstName", firstName)
                addProperty("lastName", lastName)
                addProperty("email", email)
                addProperty("phone", phone)
                addProperty("photoUrl", "https://example.com/photo.jpg")
                addProperty("dni", dni.toString())
            }

            if (role == "Consultor") {
                api.updateConsultant(2, json).enqueue(object : Callback<ConsultantProfile> {
                    override fun onResponse(call: Call<ConsultantProfile>, response: Response<ConsultantProfile>) {
                        dialog.dismiss()
                        loadProfile()
                        showConfirmationDialog()
                    }

                    override fun onFailure(call: Call<ConsultantProfile>, t: Throwable) {}
                })
            } else {
                api.updateFarmer(3, json).enqueue(object : Callback<FarmerProfile> {
                    override fun onResponse(call: Call<FarmerProfile>, response: Response<FarmerProfile>) {
                        dialog.dismiss()
                        loadProfile()
                        showConfirmationDialog()
                    }

                    override fun onFailure(call: Call<FarmerProfile>, t: Throwable) {}
                })
            }
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
