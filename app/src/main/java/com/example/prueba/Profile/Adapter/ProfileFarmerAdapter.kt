package com.example.prueba.Profile.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Profile.Beans.FarmerProfile
import com.example.prueba.Profile.ViewHolders.FarmerProfileViewHolder
import com.example.prueba.R

class ProfileFarmerAdapter(
    private val profile: FarmerProfile,
    private val onItemClick: (FarmerProfile) -> Unit
) : RecyclerView.Adapter<FarmerProfileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        return FarmerProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarmerProfileViewHolder, position: Int) {
        holder.bind(profile, onItemClick)
    }

    override fun getItemCount(): Int = 1
}
