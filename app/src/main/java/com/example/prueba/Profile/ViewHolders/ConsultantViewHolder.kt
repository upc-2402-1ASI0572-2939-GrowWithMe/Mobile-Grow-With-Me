package com.example.prueba.Consultants.ViewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Profile.Beans.ConsultantProfile
import com.example.prueba.R

class ConsultantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val imgConsultant: ImageView = view.findViewById(R.id.img_consultant)
    private val nameText: TextView = view.findViewById(R.id.txt_consultant_name)
    private val emailText: TextView = view.findViewById(R.id.txt_consultant_email)
    private val phoneText: TextView = view.findViewById(R.id.txt_consultant_phone)

    fun bind(consultant: ConsultantProfile, onItemClick: (ConsultantProfile) -> Unit) {
        nameText.text = "${consultant.firstName} ${consultant.lastName}"
        emailText.text = consultant.email
        phoneText.text = consultant.phone
    }
}
