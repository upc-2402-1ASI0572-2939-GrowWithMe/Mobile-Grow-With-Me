package com.example.prueba.Devices.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Devices.Beans.Device
import com.example.prueba.Devices.ViewHolders.DeviceViewHolder
import com.example.prueba.R

class DeviceAdapter(
    private val devices: List<Device>
) : RecyclerView.Adapter<DeviceViewHolder>() {

    private var cropNameMap: Map<Int, String> = emptyMap()

    fun updateCropsMap(newMap: Map<Int, String>) {
        cropNameMap = newMap
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_devices, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position], cropNameMap)
    }

    override fun getItemCount(): Int = devices.size
}
