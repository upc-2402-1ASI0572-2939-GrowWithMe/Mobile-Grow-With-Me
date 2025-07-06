package com.example.prueba.Notifications.ViewHolders

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prueba.Notifications.Beans.Notification
import com.example.prueba.R

class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cardHeader = itemView.findViewById<View>(R.id.card_header)
    private val iconType = itemView.findViewById<ImageView>(R.id.icon_type)
    private val textTitle = itemView.findViewById<TextView>(R.id.text_title)
    private val textMessage = itemView.findViewById<TextView>(R.id.text_message)

    fun bind(notification: Notification) {
        textTitle.text = notification.title
        textMessage.text = notification.message

        val lower = notification.title.lowercase()

        when {
            "welcome" in lower -> {
                iconType.setImageResource(R.drawable.ic_person_add) // ícono de bienvenida
                cardHeader.setBackgroundColor(Color.parseColor("#e8f5e9")) // verde claro
            }
            "crop status" in lower || "estado del cultivo" in lower -> {
                iconType.setImageResource(R.drawable.ic_cultivo) // ícono de cultivo
                cardHeader.setBackgroundColor(Color.parseColor("#e3f2fd")) // azul claro
            }
            else -> {
                iconType.setImageResource(R.drawable.ic_person_add) // ícono genérico
                cardHeader.setBackgroundColor(Color.parseColor("#f5f5f5")) // gris
            }
        }
    }
}
