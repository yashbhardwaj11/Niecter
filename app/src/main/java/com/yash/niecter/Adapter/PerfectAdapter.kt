package com.yash.niecter.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.yash.niecter.Activity.MessageActivity
import com.yash.niecter.Dao.UserDao
import com.yash.niecter.Model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class PerfectAdapter(val context : Context,val mlist : ArrayList<String>) : RecyclerView.Adapter<PerfectAdapter.PerfectViewHOlder>(){
    inner class PerfectViewHOlder(view : View) : RecyclerView.ViewHolder(view){
        val userNamePM : TextView = view.findViewById(com.yash.niecter.R.id.userNamePM)
        val userImagePM : ImageView = view.findViewById(com.yash.niecter.R.id.userImagePM)
        val userCardLayout : CardView = view.findViewById(com.yash.niecter.R.id.userCardLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerfectViewHOlder {
        return PerfectViewHOlder(LayoutInflater.from(parent.context).inflate(com.yash.niecter.R.layout.item_perfect_match,parent,false))
    }

    override fun onBindViewHolder(holder: PerfectViewHOlder, position: Int) {
        val current = mlist[position]
        GlobalScope.launch (Dispatchers.IO){
            val userDao = UserDao()
            val user = userDao.getUserById(current).await().getValue(User::class.java)
            withContext(Dispatchers.Main){
                holder.userImagePM.load(user!!.userImage){
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
                holder.userNamePM.text = user.username

                holder.userCardLayout.setOnClickListener{
                    val intent = Intent(context, MessageActivity::class.java)
                    intent.putExtra("senderId",current.toString())
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = mlist.size
}