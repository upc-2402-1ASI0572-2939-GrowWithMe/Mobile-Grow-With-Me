package com.example.prueba.Crops

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.R

class CropAdapter(
    private val crops: List<Crop>,
    private val onEdit: (Crop, Int) -> Unit,
    private val onDelete: (Crop) -> Unit
) : RecyclerView.Adapter<CropAdapter.CropViewHolder>() {

    class CropViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txt_crop_name)
        val edit: ImageButton = view.findViewById(R.id.btn_edit)
        val delete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crops, parent, false)
        return CropViewHolder(view)
    }

    override fun onBindViewHolder(holder: CropViewHolder, position: Int) {
        val crop = crops[position]
        holder.name.text = crop.name
        holder.edit.setOnClickListener { onEdit(crop, holder.adapterPosition) }
        holder.delete.setOnClickListener { onDelete(crop) }
    }

    override fun getItemCount() = crops.size
}
