package com.example.prueba.Consultants.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Consultants.ViewHolders.ConsultantViewHolder
import com.example.prueba.Profile.Beans.ConsultantProfile
import com.example.prueba.R

class ConsultantAdapter(
    private val consultants: List<ConsultantProfile>,
    private val onItemClick: (ConsultantProfile) -> Unit
) : RecyclerView.Adapter<ConsultantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_consultants, parent, false)
        return ConsultantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultantViewHolder, position: Int) {
        holder.bind(consultants[position], onItemClick)
    }

    override fun getItemCount(): Int = consultants.size
}
