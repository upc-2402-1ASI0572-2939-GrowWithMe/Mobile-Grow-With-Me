package com.example.prueba.Profile.ViewHolders

import android.graphics.BitmapFactory
import android.os.StrictMode
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.example.prueba.Profile.Beans.ConsultantProfile
import com.example.prueba.R
import java.net.URL

class ConsultantProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val imgProfile: ImageView = view.findViewById(R.id.img_profile)
    private val nameText: TextView = view.findViewById(R.id.txt_full_name)
    private val emailText: TextView = view.findViewById(R.id.txt_email)
    private val phoneText: TextView = view.findViewById(R.id.txt_phone)

    fun bind(profile: ConsultantProfile, onItemClick: (ConsultantProfile) -> Unit) {
        nameText.text = "${profile.firstName} ${profile.lastName}"
        emailText.text = profile.email
        phoneText.text = profile.phone

        try {
            val imageUrl = MediaManager.get().url()
                .secure(true)
                .generate(profile.photoUrl)

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val input = URL(imageUrl).openStream()
            val bitmap = BitmapFactory.decodeStream(input)
            imgProfile.setImageBitmap(bitmap)
        } catch (e: Exception) {
            imgProfile.setImageResource(R.drawable.ic_person)
        }

        itemView.setOnClickListener { onItemClick(profile) }
    }
}
