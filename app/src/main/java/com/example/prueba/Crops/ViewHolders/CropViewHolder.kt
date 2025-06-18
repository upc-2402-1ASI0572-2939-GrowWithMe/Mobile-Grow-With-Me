package com.example.prueba.Crops.ViewHolders

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.R
import com.example.prueba.Crops.Beans.Crop

class CropViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val cropName: TextView = view.findViewById(R.id.txt_crop_name)
    private val cropCategory = itemView.findViewById<TextView>(R.id.txt_crop_category)
    private val cropArea = itemView.findViewById<TextView>(R.id.txt_crop_area)

    private val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
    private val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    private val btnViewCalendar: ImageButton = view.findViewById(R.id.btn_view)

    fun bind(
        crop: Crop,
        onEdit: (Crop, Int) -> Unit,
        onDelete: (Crop) -> Unit,
        onView: (Crop) -> Unit
    ) {
        cropName.text = crop.productName
        cropCategory.text = crop.category
        cropArea.text = "${crop.area} m²"
        btnEdit.setOnClickListener { onEdit(crop, adapterPosition) }
        btnDelete.setOnClickListener { onDelete(crop) }
        btnViewCalendar.setOnClickListener { onView(crop) }
    }
}
