package com.example.prueba

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prueba.Crops.CropsActivity

class MainActivity : AppCompatActivity() {
    class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Lanzar CropsActivity apenas inicia
            val intent = Intent(this, CropsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}