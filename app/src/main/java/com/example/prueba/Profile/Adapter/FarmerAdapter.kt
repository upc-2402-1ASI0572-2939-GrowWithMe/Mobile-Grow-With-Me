package com.example.prueba.Farmers.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.example.prueba.Consultations.ConsultationActivity
import com.example.prueba.Profile.Beans.FarmerProfile
import com.example.prueba.R
import java.net.URL

class FarmerAdapter(
    private val farmers: List<FarmerProfile>,
    private val onConsultsClick: (FarmerProfile) -> Unit,
    private val onCropsClick: (FarmerProfile) -> Unit
) : RecyclerView.Adapter<FarmerAdapter.FarmerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_farmers, parent, false)
        return FarmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarmerViewHolder, position: Int) {
        holder.bind(farmers[position])
    }

    override fun getItemCount(): Int = farmers.size

    inner class FarmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(farmer: FarmerProfile) {
            val context = itemView.context
            val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val role = prefs.getString("role", "FARMER_ROLE")

            val nameText = itemView.findViewById<TextView>(R.id.txt_farmer_name)
            val emailText = itemView.findViewById<TextView>(R.id.txt_farmer_email)
            val phoneText = itemView.findViewById<TextView>(R.id.txt_farmer_phone)
            val imageView = itemView.findViewById<ImageView>(R.id.img_consultant)

            nameText.text = "${farmer.firstName} ${farmer.lastName}"
            emailText.text = farmer.email
            phoneText.text = farmer.phone

            // 🔁 Cargar imagen desde Cloudinary
            try {
                val imageUrl = MediaManager.get().url()
                    .secure(true)
                    .generate(farmer.photoUrl)

                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)
                val input = URL(imageUrl).openStream()
                val bitmap = BitmapFactory.decodeStream(input)
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                imageView.setImageResource(R.drawable.ic_person)
            }

            itemView.findViewById<ImageButton>(R.id.btn_view_consults).setOnClickListener {
                if (role == "CONSULTANT_ROLE") {
                    context.startActivity(Intent(context, ConsultationActivity::class.java))
                } else {
                    onConsultsClick(farmer)
                }
            }

            itemView.findViewById<ImageButton>(R.id.btn_view_crops).setOnClickListener {
                onCropsClick(farmer)
            }
        }
    }
}
