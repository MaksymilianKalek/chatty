package com.makscorp.chatty

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val ctx: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemReceived = 1
    private val itemSent = 2

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sent: TextView = itemView.findViewById<TextView>(R.id.sentMessageTxt)
    }

    class ReceivedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val received = itemView.findViewById<TextView>(R.id.receivedMessageTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == 1) {
            return MessageAdapter.ReceivedViewHolder(
                LayoutInflater.from(ctx).inflate(R.layout.received, parent, false)
            )
        } else {
            return MessageAdapter.SentViewHolder(
                LayoutInflater.from(ctx).inflate(R.layout.sent, parent, false)
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java) {
            val viewHolder = holder as SentViewHolder
            holder.sent.text = currentMessage.message
        } else {
            val viewHolder = holder as ReceivedViewHolder
            holder.received.text = currentMessage.message
        }
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return itemSent
        } else {
            return itemReceived
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}