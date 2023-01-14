package com.yash.niecter.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.yash.niecter.Model.Message
import com.yash.niecter.R

class MessageAdapter(val context : Context,val mlist : ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun getItemViewType(position: Int): Int {
        val current = mlist[position]
        if (FirebaseAuth.getInstance().currentUser!!.phoneNumber.toString() == current.senderId){
            return ITEM_SENT
        }else{
            return ITEM_RECEIVE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.receive_layout,parent,false)
            return ReceiveViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.sent_layout,parent,false)
            return SentViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val current = mlist[position]
        if (holder.javaClass == SentViewHolder::class.java){
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = current.message
        }
        else{
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = current.message
        }
    }

    override fun getItemCount(): Int = mlist.size

    inner class SentViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val sentMessage : TextView = view.findViewById(R.id.txt_send_message)
    }
    inner class ReceiveViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val receiveMessage : TextView = view.findViewById(R.id.txt_receive_message)
    }
}