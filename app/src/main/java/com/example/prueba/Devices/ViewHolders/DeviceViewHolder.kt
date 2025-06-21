package com.example.prueba.Devices.ViewHolders

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Devices.Beans.Device
import com.example.prueba.R

class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val txtDeviceName: TextView = view.findViewById(R.id.txt_device_name)
    private val txtDeviceType: TextView = view.findViewById(R.id.txt_device_type)
    private val txtDeviceStatus: TextView = view.findViewById(R.id.txt_device_status)
    private val btnToggleStatus: ImageButton = view.findViewById(R.id.btn_toggle_connection)

    fun bind(
        device: Device,
        onToggleStatus: (Device) -> Unit
    ) {
        txtDeviceName.text = device.name
        txtDeviceType.text = device.deviceType
        txtDeviceStatus.text = "Estado: ${device.status}"

        val color = if (device.status == "Connected")
            R.color.green
        else
            R.color.red

        txtDeviceStatus.setTextColor(ContextCompat.getColor(itemView.context, color))

        btnToggleStatus.setOnClickListener {
            onToggleStatus(device)
        }
    }
}
