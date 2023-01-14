package com.yash.niecter.Dao

import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.yash.niecter.Model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserDao {
    private val db = Firebase.database
    val userCollection = db.getReference("User")
    private val auth = FirebaseAuth.getInstance()

    fun addUser(user : User?){
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                userCollection.child(user.uid!!).setValue(it)
            }
        }
    }

    fun onSwipeRight(currentUser : String,potentialUser : String){
        GlobalScope.launch(Dispatchers.IO) {
            val secondUser = getUserById(potentialUser).await().getValue(User::class.java)
            val user = getUserById(auth.currentUser!!.phoneNumber.toString()).await().getValue(User::class.java)
            if (secondUser!!.potentialMatch!!.contains(user!!.uid.toString())){
                secondUser.match!!.add(user.uid!!)
                user.match!!.add(secondUser.uid!!)
                secondUser.potentialMatch!!.remove(user.uid)
                userCollection.child(secondUser.uid.toString()).setValue(secondUser)
                userCollection.child(user.uid.toString()).setValue(user)
            }
            else{
                user.potentialMatch?.add(potentialUser)
                userCollection.child(currentUser).setValue(user)
            }
        }

    }

    fun getUserById(uid : String) : Task<DataSnapshot> {
        return userCollection.child(uid).get()
    }


//    fun getMainUser(uid : String) : Task<DocumentSnapshot>{
//        return aboutUserCollection.document(uid).get()
//    }

}