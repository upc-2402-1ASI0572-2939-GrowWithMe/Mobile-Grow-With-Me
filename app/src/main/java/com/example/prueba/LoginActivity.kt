package com.example.prueba.Login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.prueba.Home.HomeActivity
import com.example.prueba.R
import com.example.prueba.StartingPoint.Beans.UserAuth
import com.example.prueba.StartingPoint.Beans.UserAuthenticated
import com.example.prueba.StartingPoint.Models.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)

        btnLogin.setOnClickListener {
            if (validateInputs()) {
                signIn()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        return when {
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Ingresa un correo válido")
                false
            }
            password.isEmpty() -> {
                showToast("Ingresa la contraseña")
                false
            }
            else -> true
        }
    }

    private fun signIn() {
        val loginData = UserAuth(
            email = etEmail.text.toString().trim(),
            password = etPassword.text.toString()
        )

        RetrofitClient.placeHolder.signIn(loginData).enqueue(object : Callback<UserAuthenticated> {
            override fun onResponse(call: Call<UserAuthenticated>, response: Response<UserAuthenticated>) {
                if (response.isSuccessful) {
                    val userAuth = response.body()

                    // Guardar en SharedPreferences si deseas
                    val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit()
                    prefs.putString("token", userAuth?.token ?: "")
                    prefs.putString("email", userAuth?.email ?: "")
                    prefs.putInt("user_id", userAuth?.id ?: -1)
                    prefs.putString("role", userAuth?.roles?.firstOrNull() ?: "")
                    prefs.apply()

                    showToast("Bienvenido ${userAuth?.email}")

                    // Ir a HomeActivity
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    showToast("Credenciales inválidas")
                }
            }

            override fun onFailure(call: Call<UserAuthenticated>, t: Throwable) {
                showToast("Error de red: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
