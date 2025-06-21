package com.example.prueba.Profile.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Profile.Beans.ConsultantProfile
import com.example.prueba.Profile.ViewHolders.ConsultantProfileViewHolder
import com.example.prueba.R

class ProfileConsultantAdapter(
    private val profile: ConsultantProfile,
    private val onItemClick: (ConsultantProfile) -> Unit
) : RecyclerView.Adapter<ConsultantProfileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        return ConsultantProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultantProfileViewHolder, position: Int) {
        holder.bind(profile, onItemClick)
    }

    override fun getItemCount(): Int = 1
}
