package com.yash.niecter.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.yash.niecter.Dao.UserDao
import com.yash.niecter.Model.User
import com.yash.niecter.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.internal.wait

class matchAdapter(val context : Context, val mlist : ArrayList<String>) : RecyclerView.Adapter<matchAdapter.MatchViewHolder>() {
    inner class MatchViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val matchesProfilePic : ImageView = view.findViewById(R.id.matchesProfilePic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        return MatchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_new_match,parent,false))
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val current = mlist[position]
        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            val currentUser = userDao.getUserById(current).await().getValue(User::class.java)
            withContext(Dispatchers.Main){
                holder.matchesProfilePic.load(currentUser!!.userImage){
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
            }
        }
    }

    override fun getItemCount(): Int = mlist.size
}