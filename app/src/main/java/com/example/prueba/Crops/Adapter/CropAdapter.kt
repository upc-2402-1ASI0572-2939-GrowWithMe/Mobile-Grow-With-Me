package com.example.prueba.Crops.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Crops.Beans.Crop
import com.example.prueba.Crops.ViewHolders.CropViewHolder
import com.example.prueba.R

class CropAdapter(
    private val crops: List<Crop>,
    private val isConsultant: Boolean,
    private val onEdit: (Crop, Int) -> Unit,
    private val onDelete: (Crop) -> Unit,
    private val onView: (Crop) -> Unit,
    private val onGraphic: (Crop) -> Unit
) : RecyclerView.Adapter<CropViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crops, parent, false)
        return CropViewHolder(view)
    }

    override fun onBindViewHolder(holder: CropViewHolder, position: Int) {
        holder.bind(crops[position], isConsultant, onEdit, onDelete, onView, onGraphic)
    }

    override fun getItemCount(): Int = crops.size
}
