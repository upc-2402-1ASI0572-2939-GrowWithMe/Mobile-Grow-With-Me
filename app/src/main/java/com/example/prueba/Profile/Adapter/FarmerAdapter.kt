package com.example.prueba.Farmers.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Farmers.ViewHolders.FarmerViewHolder
import com.example.prueba.Profile.Beans.FarmerProfile
import com.example.prueba.R

class FarmerAdapter(
    private val farmers: List<FarmerProfile>,
    private val onConsultsClick: (FarmerProfile) -> Unit,
    private val onCropsClick: (FarmerProfile) -> Unit
) : RecyclerView.Adapter<FarmerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_farmers, parent, false)
        return FarmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarmerViewHolder, position: Int) {
        val farmer = farmers[position]
        holder.bind(farmer, onConsultsClick, onCropsClick)
    }

    override fun getItemCount(): Int = farmers.size
}
