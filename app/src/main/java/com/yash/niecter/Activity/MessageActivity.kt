package com.yash.niecter.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.yash.niecter.Adapter.MessageAdapter
import com.yash.niecter.Dao.UserDao
import com.yash.niecter.Model.Message
import com.yash.niecter.Model.User
import com.yash.niecter.R
import com.yash.niecter.databinding.ActivityMessageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MessageActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList : ArrayList<Message>
    private lateinit var mDbref : DatabaseReference

    private var receiverRoom : String? = null
    private var senderRoom : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mDbref = FirebaseDatabase.getInstance().getReference()
        val receiverUid = intent.getStringExtra("senderId")
        getSender(receiverUid)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)


        val senderUid = FirebaseAuth.getInstance().currentUser!!.phoneNumber

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        binding.messageRV.adapter = messageAdapter
        binding.messageRV.layoutManager = LinearLayoutManager(this)


        mDbref.child("Chats").child(senderRoom!!).child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (postSnapShot in snapshot.children){
                    val message = postSnapShot.getValue(Message::class.java)
                    messageList.add(message!!)

                }
                messageAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        binding.sendMessageBT.setOnClickListener {
            val message = binding.messageEt.text.toString()
            val messageObject = Message(message,senderUid)
            if (message.isNotEmpty()){
                mDbref.child("Chats").child(senderRoom!!).child("messages").push().setValue(messageObject)
                    .addOnSuccessListener {
                        mDbref.child("Chats").child(receiverRoom!!).child("messages").push().setValue(messageObject).addOnSuccessListener {

                            binding.messageEt.setText("")
                        }
                    }
            }
        }

    }

    private fun getSender(senderID: String?) {
        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            val user = userDao.getUserById(senderID!!).await().getValue(User::class.java)
            withContext(Dispatchers.Main){
                binding.userName.text = user!!.username.toString()
            }
        }
    }
}