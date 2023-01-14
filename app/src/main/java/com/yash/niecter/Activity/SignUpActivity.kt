package com.yash.niecter.Activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.ktx.Firebase
import com.yash.niecter.Dao.UserDao
import com.yash.niecter.MainActivity
import com.yash.niecter.Model.User
import com.yash.niecter.R
import com.yash.niecter.databinding.ActivitySignUpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var phoneNumber : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        init()
        binding.sendOTPBtn.setOnClickListener {
            if (binding.phoneEditTextNumber.text.trim().isNotEmpty()){
                if (binding.phoneEditTextNumber.length()==10){
                    binding.phoneProgressBar.visibility = View.VISIBLE
                    phoneNumber = "+91${ binding.phoneEditTextNumber.text.trim() }"
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }else{
                    Toast.makeText(applicationContext, "Enter valid number", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(applicationContext, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun init(){
        auth = FirebaseAuth.getInstance()
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {


            if (e is FirebaseAuthInvalidCredentialsException) {

                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(applicationContext, "${e.message} exceeded", Toast.LENGTH_SHORT).show()
            }

        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {

            binding.phoneProgressBar.visibility = View.INVISIBLE
            val intent = Intent(applicationContext,OtpActivity::class.java)
            intent.putExtra("otp",verificationId)
            intent.putExtra("phoneNumber",phoneNumber)
            intent.putExtra("resendToken",token)
            startActivity(intent)

        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
//                    updateUI(user)
                } else {
                     if (task.exception is FirebaseAuthInvalidCredentialsException) {
                         Toast.makeText(applicationContext, "Something went wrong. try again later", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser!=null){
            //
        }
        else{
            binding.phoneProgressBar.visibility = View.INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser!=null){
            startActivity(Intent(applicationContext,MainActivity::class.java))
            finish()
        }
    }

}