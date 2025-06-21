package com.example.prueba.Profile.ViewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Profile.Beans.ConsultantProfile
import com.example.prueba.R

class ConsultantProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val imgProfile: ImageView = view.findViewById(R.id.img_profile)
    private val nameText: TextView = view.findViewById(R.id.txt_full_name)
    private val emailText: TextView = view.findViewById(R.id.txt_email)
    private val phoneText: TextView = view.findViewById(R.id.txt_phone)

    fun bind(profile: ConsultantProfile, onItemClick: (ConsultantProfile) -> Unit) {
        nameText.text = "${profile.firstName} ${profile.lastName}"
        emailText.text = profile.email
        phoneText.text = profile.phone
        // Si tuvieras imagen desde red, aquí podrías usar Glide o Picasso
        // Glide.with(itemView).load(profile.imageUrl).into(imgProfile)
    }
}
