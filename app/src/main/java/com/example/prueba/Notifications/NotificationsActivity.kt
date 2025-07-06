package com.example.prueba.Notifications

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.prueba.Activity.ActivityCropActivity
import com.example.prueba.Consultations.ConsultantActivity
import com.example.prueba.Crops.CropsActivity
import com.example.prueba.Devices.DevicesActivity
import com.example.prueba.Farmers.FarmerActivity
import com.example.prueba.Profile.ProfileActivity
import com.example.prueba.R
import com.example.prueba.Notifications.Beans.Notification
import com.example.prueba.Notifications.Interfaces.PlaceHolder
import com.example.prueba.Notifications.Models.RetrofitClient
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var checkboxSelectAll: CheckBox
    private lateinit var btnDeleteSelected: Button
    private lateinit var notificationList: LinearLayout
    private lateinit var inputSearch: EditText

    private lateinit var client: PlaceHolder
    private var allNotifications: List<Notification> = emptyList()
    private val selectedIds = mutableSetOf<Int>()

    private val userRole: String
        get() = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("role", "") ?: ""

    private val isConsultant: Boolean
        get() = userRole == "CONSULTANT_ROLE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        checkboxSelectAll = findViewById(R.id.checkbox_select_all)
        btnDeleteSelected = findViewById(R.id.btn_delete_selected)
        notificationList = findViewById(R.id.item_notification)
        inputSearch = findViewById(R.id.input_search)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val token = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("token", "") ?: ""
        client = RetrofitClient.getClient(token)

        filterMenuByRole()
        setupNavigation()
        setupEvents()
        loadNotifications()
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
                R.id.nav_notifications -> drawerLayout.closeDrawer(GravityCompat.START)
                R.id.nav_consultants -> {
                    val target = if (isConsultant) FarmerActivity::class.java else ConsultantActivity::class.java
                    startActivity(Intent(this, target))
                }
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupEvents() {
        checkboxSelectAll.setOnCheckedChangeListener { _, isChecked ->
            // Marcar/desmarcar todos los checkboxes
            for (i in 0 until notificationList.childCount) {
                val item = notificationList.getChildAt(i)
                val checkBox = item.findViewById<CheckBox>(R.id.checkbox_notification)
                checkBox.isChecked = isChecked
            }
        }

        btnDeleteSelected.setOnClickListener {
            val toRemove = mutableListOf<Int>()

            for (i in 0 until notificationList.childCount) {
                val item = notificationList.getChildAt(i)
                val checkBox = item.findViewById<CheckBox>(R.id.checkbox_notification)
                if (checkBox.isChecked) {
                    val id = item.tag as? Int
                    if (id != null) toRemove.add(i)
                }
            }

            toRemove.sortedDescending().forEach { index ->
                notificationList.removeViewAt(index)
            }

            selectedIds.clear()
            checkboxSelectAll.isChecked = false
            btnDeleteSelected.isEnabled = false
            Toast.makeText(this, "Notificaciones eliminadas", Toast.LENGTH_SHORT).show()
        }

        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim()?.lowercase() ?: ""
                val filtered = allNotifications.filter {
                    it.title.lowercase().contains(query) || it.message.lowercase().contains(query)
                }
                showNotifications(filtered)
            }
        })
    }

    private fun loadNotifications() {
        client.getNotifications().enqueue(object : Callback<List<Notification>> {
            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                if (response.isSuccessful) {
                    allNotifications = response.body() ?: emptyList()
                    showNotifications(allNotifications)
                } else {
                    Toast.makeText(this@NotificationsActivity, "Error al obtener notificaciones", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                Toast.makeText(this@NotificationsActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showNotifications(list: List<Notification>) {
        notificationList.removeAllViews()
        val inflater = LayoutInflater.from(this)

        for (notification in list) {
            val itemView = inflater.inflate(R.layout.item_notification, notificationList, false)

            val titleTextView = itemView.findViewById<TextView>(R.id.text_title)
            val messageTextView = itemView.findViewById<TextView>(R.id.text_message)
            val iconImageView = itemView.findViewById<ImageView>(R.id.icon_type)
            val cardHeader = itemView.findViewById<LinearLayout>(R.id.card_header)
            val checkbox = itemView.findViewById<CheckBox>(R.id.checkbox_notification)

            val lower = notification.title.lowercase()

            val bgColor = when {
                lower.contains("welcome") -> 0xFFE8F5E9.toInt()
                lower.contains("crop status") -> 0xFFE3F2FD.toInt()
                lower.contains("irrigation") -> 0xFFFFF3E0.toInt()
                else -> 0xFFF5F5F5.toInt()
            }

            val iconRes = when {
                lower.contains("welcome") -> R.drawable.ic_person_add
                lower.contains("crop status") -> R.drawable.ic_cultivo
                lower.contains("irrigation") -> R.drawable.ic_person_add
                else -> R.drawable.ic_person_add
            }

            titleTextView.text = notification.title
            messageTextView.text = notification.message
            iconImageView.setImageResource(iconRes)
            cardHeader.setBackgroundColor(bgColor)

            itemView.tag = notification.id

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedIds.add(notification.id) else selectedIds.remove(notification.id)
                btnDeleteSelected.isEnabled = selectedIds.isNotEmpty()
            }

            notificationList.addView(itemView)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
