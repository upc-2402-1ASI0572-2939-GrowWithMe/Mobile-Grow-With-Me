package com.example.prueba.Devices.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Devices.Beans.Device
import com.example.prueba.Devices.ViewHolders.DeviceViewHolder
import com.example.prueba.R

class DeviceAdapter(
    private val devices: List<Device>,
    private val onToggleStatus: (Device) -> Unit
) : RecyclerView.Adapter<DeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_devices, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position], onToggleStatus)
    }

    override fun getItemCount(): Int = devices.size
}
