package com.makscorp.chatty

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val ctx: Context, private val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUsername: TextView = itemView.findViewById(R.id.txt_username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(LayoutInflater.from(ctx).inflate(R.layout.user_layout, parent, false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.txtUsername.text = currentUser.username

        holder.itemView.setOnClickListener {
            val intent = Intent(ctx, ChatActivity::class.java)
            intent.putExtra("username", currentUser.username)
            intent.putExtra("uid", currentUser.uid)
            ctx.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}