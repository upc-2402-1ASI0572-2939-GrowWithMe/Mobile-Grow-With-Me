package com.example.prueba.Crops

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Notifications.NotificationsActivity
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import com.google.android.material.navigation.NavigationView

class CropsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CropAdapter
    private var crops = mutableListOf<Crop>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crops) // Asegúrate de que este layout tenga DrawerLayout como raíz

        // CONFIGURA TOOLBAR Y MENÚ HAMBURGUESA
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
                R.id.nav_crops -> {
                    // Ya estás aquí
                }
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

        // CONFIGURA RECYCLERVIEW
        recyclerView = findViewById(R.id.recycler_crops)
        val registerButton = findViewById<Button>(R.id.btn_register_crop)

        adapter = CropAdapter(crops,
            onEdit = { crop, position -> showEditCropDialog(crop, position) },
            onDelete = { crop -> showDeleteConfirmationDialog(crop)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        registerButton.setOnClickListener {
            // TODO: abrir pantalla registrar cultivo
        }

        // DATOS DE PRUEBA
        crops.addAll(listOf(
            Crop(1, "Cultivo 1"),
            Crop(2, "Cultivo 2")
        ))
        adapter.notifyDataSetChanged()

        registerButton.setOnClickListener {
            showRegisterCropDialog()
        }

    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Opcional: manejar botón atrás para cerrar menú

    private fun showRegisterCropDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_register_crop, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val etType = dialogView.findViewById<EditText>(R.id.et_crop_type)
        val etName = dialogView.findViewById<EditText>(R.id.et_crop_name)
        val etArea = dialogView.findViewById<EditText>(R.id.et_crop_area)
        val btnAccept = dialogView.findViewById<Button>(R.id.btn_accept)

        btnAccept.setOnClickListener {
            val type = etType.text.toString()
            val name = etName.text.toString()
            val area = etArea.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isNotBlank() && type.isNotBlank()) {
                crops.add(Crop(id = crops.size + 1, name = "$type - $name ($area m²)"))
                adapter.notifyDataSetChanged()
                dialog.dismiss()
                showConfirmationDialog() // <- aquí lo agregas
            } else {
                etName.error = "Obligatorio"
                etType.error = "Obligatorio"
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

        val etType = dialogView.findViewById<EditText>(R.id.et_crop_type)
        val etName = dialogView.findViewById<EditText>(R.id.et_crop_name)
        val etArea = dialogView.findViewById<EditText>(R.id.et_crop_area)
        val btnAccept = dialogView.findViewById<Button>(R.id.btn_accept)

        // Rellenar campos con datos existentes
        val parts = crop.name.split(" - ", "(", ignoreCase = true)
        if (parts.size >= 3) {
            etType.setText(parts[0])
            etName.setText(parts[1])
            etArea.setText(parts[2].replace("m²)", "").trim())
        }

        btnAccept.text = "Actualizar"

        btnAccept.setOnClickListener {
            val type = etType.text.toString()
            val name = etName.text.toString()
            val area = etArea.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isNotBlank() && type.isNotBlank()) {
                crops[position] = Crop(id = crop.id, name = "$type - $name ($area m²)")
                adapter.notifyDataSetChanged()
                dialog.dismiss()
                showConfirmationDialog() // opcional: puedes poner uno especial para edición si quieres
            } else {
                etName.error = "Obligatorio"
                etType.error = "Obligatorio"
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(crop: Crop) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("¿Eliminar cultivo?")
            .setMessage("¿Estás seguro(a) de que deseas eliminar \"${crop.name}\"?")
            .setPositiveButton("Sí, eliminar") { _, _ ->
                crops.remove(crop)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }


}
