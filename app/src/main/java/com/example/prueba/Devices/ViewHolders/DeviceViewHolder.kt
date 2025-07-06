package com.example.prueba.Devices.ViewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Devices.Beans.Device
import com.example.prueba.R

class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val txtDeviceName: TextView = view.findViewById(R.id.txt_device_name)
    private val txtCropName: TextView = view.findViewById(R.id.txt_crop_name)

    fun bind(
        device: Device,
        cropNameMap: Map<Int, String>
    ) {
        txtDeviceName.text = device.name
        txtCropName.text = "Cultivo: ${cropNameMap[device.cropId] ?: "Desconocido"}"
    }
}
