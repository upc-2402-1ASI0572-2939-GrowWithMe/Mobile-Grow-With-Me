package com.example.prueba.Register

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.prueba.R
import com.example.prueba.StartingPoint.Beans.User
import com.example.prueba.StartingPoint.Beans.UserAuth
import com.example.prueba.StartingPoint.Beans.UserAuthenticated
import com.example.prueba.StartingPoint.Beans.UserSchema
import com.example.prueba.StartingPoint.Models.RetrofitClient
import com.google.gson.Gson

class RegisterActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var spinnerRole: Spinner
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etDni: EditText
    private lateinit var etPhone: EditText
    private lateinit var ivPhoto: ImageView
    private lateinit var etPassword: EditText
    private lateinit var etRepeatPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnSelectPhoto: Button

    private var selectedImageUri: Uri? = null
    private var selectedBitmap: Bitmap? = null
    private var defaultImageUri: Uri? = null

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
        private const val GALLERY_REQUEST_CODE = 1002
        private const val PERMISSION_CAMERA = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initComponents()

        btnSelectPhoto.setOnClickListener { pickImage() }
        btnRegister.setOnClickListener {
            if (validateForm()) {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        val photoUrl = "https://example.com/default_profile.png"
        val roleSelected = when (spinnerRole.selectedItem.toString()) {
            "Agricultor" -> "FARMER_ROLE"
            "Consultor" -> "CONSULTANT_ROLE"
            else -> "FARMER_ROLE"
        }

        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        val user = UserSchema(
            email = email,
            password = password,
            roles = listOf(roleSelected),
            firstName = etFirstName.text.toString().trim(),
            lastName = etLastName.text.toString().trim(),
            phone = etPhone.text.toString().trim(),
            photoUrl = photoUrl,
            dni = etDni.text.toString().trim()
        )

        Log.d("SIGN_UP_PAYLOAD", Gson().toJson(user))

        RetrofitClient.placeHolder.signUp(user).enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: retrofit2.Call<User>, response: retrofit2.Response<User>) {
                if (response.isSuccessful) {
                    showToast("Registro exitoso. Iniciando sesión...")

                    // 👉 Aquí se hace login automáticamente
                    signInUser(email, password)
                } else {
                    showToast("Error en el registro: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                showToast("Fallo de red: ${t.message}")
            }
        })
    }

    private fun signInUser(email: String, password: String) {
        val auth = UserAuth(email, password)

        RetrofitClient.placeHolder.signIn(auth).enqueue(object : retrofit2.Callback<UserAuthenticated> {
            override fun onResponse(
                call: retrofit2.Call<UserAuthenticated>,
                response: retrofit2.Response<UserAuthenticated>
            ) {
                if (response.isSuccessful) {
                    val userAuth = response.body()
                    showToast("Inicio de sesión exitoso")

                    // Guardar datos si necesitas
                    val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                    prefs.putString("token", userAuth?.token ?: "")
                    prefs.putString("email", userAuth?.email ?: "")
                    prefs.putInt("user_id", userAuth?.id ?: -1)
                    prefs.putString("role", userAuth?.roles?.firstOrNull() ?: "")
                    prefs.apply()

                    // Redirigir a HomeActivity
                    val intent = Intent(this@RegisterActivity, com.example.prueba.Home.HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    showToast("Inicio de sesión fallido tras registro")
                }
            }

            override fun onFailure(call: retrofit2.Call<UserAuthenticated>, t: Throwable) {
                showToast("Error al iniciar sesión: ${t.message}")
            }
        })
    }

    private fun initComponents() {
        etEmail = findViewById(R.id.et_email)
        spinnerRole = findViewById(R.id.spinner_role)
        etFirstName = findViewById(R.id.et_firstname)
        etLastName = findViewById(R.id.et_lastname)
        etDni = findViewById(R.id.et_dni)
        etPhone = findViewById(R.id.et_phone)
        ivPhoto = findViewById(R.id.iv_selected_photo)
        etPassword = findViewById(R.id.et_password)
        etRepeatPassword = findViewById(R.id.et_repeat_password)
        btnRegister = findViewById(R.id.btn_register)
        btnSelectPhoto = findViewById(R.id.btn_select_photo)

        val roles = listOf("Agricultor", "Consultor")
        spinnerRole.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)

        defaultImageUri = Uri.parse("android.resource://${packageName}/${R.drawable.default_profile}")
        selectedImageUri = defaultImageUri
        ivPhoto.setImageURI(defaultImageUri)
        ivPhoto.visibility = ImageView.VISIBLE
    }

    private fun pickImage() {
        val options = arrayOf("Tomar foto", "Elegir desde galería")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona una opción")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> requestCameraPermission()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_CAMERA
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            showToast("La cámara no está disponible.")
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CAMERA && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            showToast("Permiso de cámara denegado.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    selectedImageUri = data?.data
                    selectedImageUri?.let {
                        ivPhoto.setImageURI(it)
                        ivPhoto.visibility = ImageView.VISIBLE
                        selectedBitmap = null
                    }
                }

                CAMERA_REQUEST_CODE -> {
                    selectedBitmap = data?.extras?.get("data") as? Bitmap
                    selectedBitmap?.let {
                        ivPhoto.setImageBitmap(it)
                        ivPhoto.visibility = ImageView.VISIBLE
                        selectedImageUri = null
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        val email = etEmail.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString()
        val repeatPassword = etRepeatPassword.text.toString()

        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$")

        if (selectedImageUri == null && selectedBitmap == null) {
            selectedImageUri = defaultImageUri
            ivPhoto.setImageURI(defaultImageUri)
            ivPhoto.visibility = ImageView.VISIBLE
            showToast("No seleccionaste imagen. Se usará una por defecto.")
        }

        return when {
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Ingresa un correo válido")
                false
            }
            firstName.isEmpty() || lastName.isEmpty() -> {
                showToast("Nombre y apellido son obligatorios")
                false
            }
            dni.length != 8 || dni.any { !it.isDigit() } -> {
                showToast("El DNI debe tener exactamente 8 números")
                false
            }
            phone.length != 9 || phone.any { !it.isDigit() } -> {
                showToast("El teléfono debe tener exactamente 9 dígitos")
                false
            }
            !password.matches(passwordRegex) -> {
                showToast("La contraseña debe tener al menos 8 caracteres e incluir mayúscula, minúscula, número y símbolo")
                false
            }
            password != repeatPassword -> {
                showToast("Las contraseñas no coinciden")
                false
            }
            else -> true
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
