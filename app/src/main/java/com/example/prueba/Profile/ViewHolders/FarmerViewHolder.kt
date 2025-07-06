package com.example.prueba.Farmers.ViewHolders

import android.graphics.BitmapFactory
import android.os.StrictMode
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.example.prueba.Profile.Beans.FarmerProfile
import com.example.prueba.R
import java.net.URL

class FarmerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val imgFarmer: ImageView = view.findViewById(R.id.img_consultant)
    private val nameText: TextView = view.findViewById(R.id.txt_farmer_name)
    private val emailText: TextView = view.findViewById(R.id.txt_farmer_email)
    private val phoneText: TextView = view.findViewById(R.id.txt_farmer_phone)

    private val btnConsults: ImageButton = view.findViewById(R.id.btn_view_consults)
    private val btnCrops: ImageButton = view.findViewById(R.id.btn_view_crops)

    fun bind(
        farmer: FarmerProfile,
        onConsultsClick: (FarmerProfile) -> Unit,
        onCropsClick: (FarmerProfile) -> Unit
    ) {
        nameText.text = "${farmer.firstName} ${farmer.lastName}"
        emailText.text = farmer.email
        phoneText.text = farmer.phone

        try {
            val imageUrl = MediaManager.get().url()
                .secure(true)
                .generate(farmer.photoUrl)

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val input = URL(imageUrl).openStream()
            val bitmap = BitmapFactory.decodeStream(input)
            imgFarmer.setImageBitmap(bitmap)
        } catch (e: Exception) {
            imgFarmer.setImageResource(R.drawable.ic_person)
        }

        btnConsults.setOnClickListener { onConsultsClick(farmer) }
        btnCrops.setOnClickListener { onCropsClick(farmer) }
    }
}
