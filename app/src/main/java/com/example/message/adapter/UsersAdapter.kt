package com.example.message.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.message.R
import com.example.message.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UsersAdapter(
    private val userList: MutableList<User>,
    private val onItemClick: (User) -> Unit,
) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: CircleImageView
        val displayName: TextView
        val lastMessage: TextView

        init {
            avatar = view.findViewById(R.id.avatarImg)
            displayName = view.findViewById(R.id.displayNameTv)
            lastMessage = view.findViewById(R.id.lastMessageTv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.displayName.text = user.displayName ?: user.email
//        holder.avatar.setImageURI(userList[position].photoURL)
//        holder.lastMessage.text =

        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
    }
}