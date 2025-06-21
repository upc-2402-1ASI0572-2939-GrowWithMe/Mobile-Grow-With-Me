package com.example.prueba.Consultations.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Consultations.Beans.Consultation
import com.example.prueba.Consultations.ViewHolders.ConsultationViewHolder
import com.example.prueba.R

class ConsultationAdapter(
    private val consultations: List<Consultation>
) : RecyclerView.Adapter<ConsultationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consultations, parent, false)
        return ConsultationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultationViewHolder, position: Int) {
        holder.bind(consultations[position])
    }

    override fun getItemCount(): Int = consultations.size
}
