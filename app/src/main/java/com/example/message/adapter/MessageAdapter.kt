package com.example.message.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.message.R
import com.example.message.model.Message

class MessageAdapter(private var messageList: List<Message>, private var currentUserId: String) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENDER = 1
        const val VIEW_TYPE_RECIPIENT = 2
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView

        init {
            message = view.findViewById(R.id.message)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutRes = if(viewType == VIEW_TYPE_SENDER) {
            R.layout.message_sender_item
        }else{
            R.layout.message_recipient_item
        }

        val view =
            LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]

        holder.message.text = message.message
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]

        return if (message.senderId == currentUserId) {
            VIEW_TYPE_SENDER
        } else {
            VIEW_TYPE_RECIPIENT
        }
    }
}