package com.example.prueba.Consultants.ViewHolders

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

class ConsultantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val imgConsultant: ImageView = view.findViewById(R.id.img_consultant)
    private val nameText: TextView = view.findViewById(R.id.txt_consultant_name)
    private val emailText: TextView = view.findViewById(R.id.txt_consultant_email)
    private val phoneText: TextView = view.findViewById(R.id.txt_consultant_phone)

    fun bind(consultant: ConsultantProfile, onItemClick: (ConsultantProfile) -> Unit) {
        nameText.text = "${consultant.firstName} ${consultant.lastName}"
        emailText.text = consultant.email
        phoneText.text = consultant.phone

        // Generar URL segura desde Cloudinary
        val imageUrl = MediaManager.get().url()
            .secure(true)
            .generate(consultant.photoUrl)

        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val input = URL(imageUrl).openStream()
            val bitmap = BitmapFactory.decodeStream(input)
            imgConsultant.setImageBitmap(bitmap)
        } catch (e: Exception) {
            imgConsultant.setImageResource(R.drawable.ic_person)
        }

        itemView.setOnClickListener { onItemClick(consultant) }
    }
}
