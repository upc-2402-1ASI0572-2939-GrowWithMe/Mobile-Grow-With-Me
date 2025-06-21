package com.example.prueba.Profile.ViewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Profile.Beans.FarmerProfile
import com.example.prueba.R

class FarmerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val imgProfile: ImageView = view.findViewById(R.id.img_profile)
    private val txtFullName: TextView = view.findViewById(R.id.txt_full_name)
    private val txtEmail: TextView = view.findViewById(R.id.txt_email)
    private val txtPhone: TextView = view.findViewById(R.id.txt_phone)

    fun bind(profile: FarmerProfile) {
        txtFullName.text = "${profile.firstName} ${profile.lastName}"
        txtEmail.text = profile.email
        txtPhone.text = profile.phone
    }
}
