package com.example.prueba.Consultations.ViewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Consultations.Beans.Consultation
import com.example.prueba.R

class ConsultationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val txtTitle: TextView = view.findViewById(R.id.txt_consultation_title)
    private val txtDescription: TextView = view.findViewById(R.id.txt_consultation_description)

    fun bind(consultation: Consultation) {
        txtTitle.text = consultation.title
        txtDescription.text = consultation.description
    }
}
