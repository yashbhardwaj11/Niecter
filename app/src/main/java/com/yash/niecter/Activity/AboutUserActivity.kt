package com.yash.niecter.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yash.niecter.Dao.UserDao
import com.yash.niecter.MainActivity
import com.yash.niecter.Model.User
import com.yash.niecter.R
import com.yash.niecter.databinding.ActivityAboutUserBinding

class AboutUserActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAboutUserBinding
    private lateinit var uid : String
    private lateinit var storageRef : StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var imageUri : Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = intent.getStringExtra("userName")
        val userBio = intent.getStringExtra("userBio")
        val userImage = intent.getStringExtra("userImage")

        binding.userNameET.setText(userName)
        binding.userBioEt.setText(userBio)
        binding.profilePicture.load(userImage){
            placeholder(R.drawable.ic_launcher_background)
            transformations(CircleCropTransformation())
        }

        uid = intent.getStringExtra("uid").toString()

        init()

        binding.profilePicture.setOnClickListener{
            resultLauncher.launch("image/*")
        }

        binding.startBT.setOnClickListener {
            binding.aboutprogressbar.visibility = View.VISIBLE
            binding.startBT.visibility = View.INVISIBLE
            uploadData()
        }
    }

    private fun init(){
        storageRef = FirebaseStorage.getInstance().reference.child("userImages")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()){
        imageUri = it
        binding.profilePicture.setImageURI(it)
    }

    private fun uploadData() {

        storageRef = storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let {
            storageRef.putFile(it).addOnCompleteListener{ result->
                if (result.isSuccessful){
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        var id: Int = binding.radioGroup.checkedRadioButtonId
                        if (id!=-1){
                            val radio: RadioButton = findViewById(id)
                            if (uri.toString().isNotEmpty()){
                                if ( radio.text.isNotEmpty() && binding.userNameET.text!!.isNotEmpty() && binding.userBioEt.text!!.isNotEmpty()){

                                    val userDao = UserDao()
                                    val user = User(uid,binding.userNameET.text.toString(),binding.userBioEt.text.toString(),radio.text.toString(),uri.toString())
                                    userDao.addUser(user)
                                    startActivity(Intent(applicationContext,MainActivity::class.java))
                                }
                            }
                            else{
                                binding.aboutprogressbar.visibility = View.INVISIBLE
                                binding.startBT.visibility = View.VISIBLE
                                Toast.makeText(applicationContext, "Please select a image", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            binding.aboutprogressbar.visibility = View.INVISIBLE
                            binding.startBT.visibility = View.VISIBLE
                            Toast.makeText(applicationContext,"Please select your gender",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(applicationContext, "Please select image again ${result.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}