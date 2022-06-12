package com.makscorp.chatty

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MessageAdapter(private val ctx: Context, private val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemReceived = 1
    private val itemSent = 2

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sent: TextView = itemView.findViewById(R.id.sentMessageTxt)
        val coordinatesSent: TextView = itemView.findViewById(R.id.coordinatesSentTxt)
    }

    class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val received: TextView = itemView.findViewById(R.id.receivedMessageTxt)
        val coordinatesReceived: TextView = itemView.findViewById(R.id.coordinatesReceivedTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            ReceivedViewHolder(
                LayoutInflater.from(ctx).inflate(R.layout.received, parent, false)
            )
        } else {
            SentViewHolder(
                LayoutInflater.from(ctx).inflate(R.layout.sent, parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (currentMessage.location?.isEmpty() == true) {
            try {
                val geo = Geocoder(ctx, Locale.getDefault())
                val addresses =
                    geo.getFromLocation(currentMessage.latitude!!, currentMessage.longitude!!, 1)
                if (addresses.isNotEmpty()) {
                    val address = addresses.first()
                    currentMessage.location =
                        "${address.thoroughfare ?: ""} ${address.subThoroughfare ?: ""}, ${address.postalCode} ${address.locality},  ${address.countryCode}"
                }
            } catch (e: Exception) {
                currentMessage.location = "${String.format("%.6f", currentMessage.latitude)}, ${String.format("%.6f", currentMessage.longitude)}"
            }
        }

        if (holder.javaClass == SentViewHolder::class.java) {
            holder as SentViewHolder
            holder.sent.text = currentMessage.message
            holder.coordinatesSent.text = currentMessage.location
        } else {
            holder as ReceivedViewHolder
            holder.received.text = currentMessage.message
            holder.coordinatesReceived.text = currentMessage.location
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            itemSent
        } else {
            itemReceived
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}