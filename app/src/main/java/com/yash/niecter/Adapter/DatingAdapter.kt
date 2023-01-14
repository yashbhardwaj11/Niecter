package com.yash.niecter.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.yash.niecter.Dao.UserDao
import com.yash.niecter.Model.User
import com.yash.niecter.databinding.ItemUserLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DatingAdapter(val context : Context,val mlist : ArrayList<User>) : RecyclerView.Adapter<DatingAdapter.DatingViewHolder>() {
    inner class DatingViewHolder(val binding : ItemUserLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatingViewHolder {
        return DatingViewHolder(ItemUserLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: DatingViewHolder, position: Int) {
        val current = mlist[position]
        holder.binding.userImageMain.load(current.userImage){
            crossfade(true)
        }
        holder.binding.userName.text = current.username
        holder.binding.userBio.text = current.userBio
        holder.binding.tinderHeart.setOnClickListener{
//            GlobalScope.launch(Dispatchers.IO) {
//                val userDao = UserDao()
//                val currentUser = userDao.getUserById(FirebaseAuth.getInstance().currentUser!!.phoneNumber.toString()).await().getValue(User::class.java)
//                if (current.uid != FirebaseAuth.getInstance().currentUser!!.phoneNumber && currentUser?.userGender!=current.userGender){
//                    userDao.onSwipeRight(FirebaseAuth.getInstance().currentUser!!.phoneNumber.toString(),current.uid.toString())
//                }
//            }
        }

    }

    override fun getItemCount(): Int = mlist.size
}