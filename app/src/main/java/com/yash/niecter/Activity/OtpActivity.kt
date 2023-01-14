package com.yash.niecter.Activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.yash.niecter.Dao.UserDao
import com.yash.niecter.MainActivity
import com.yash.niecter.Model.User
import com.yash.niecter.R
import com.yash.niecter.databinding.ActivityOtpBinding
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOtpBinding
    private lateinit var otp : String
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber : String
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()
        otp = intent.getStringExtra("otp").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber").toString()
        init()
        addTextChangeListener()
        resendOTPvisiblity()

        binding.resendTextView.setOnClickListener {
            resendOtp()
            resendOTPvisiblity()
        }

        binding.verifyOTPBtn.setOnClickListener {
            val typedOtp = (binding.otpEditText1.text.toString() + binding.otpEditText2.text.toString() +binding.otpEditText3.text.toString()
                    + binding.otpEditText4.text.toString() +binding.otpEditText5.text.toString()+binding.otpEditText6.text.toString())

            if (typedOtp.isNotEmpty()){
                if(typedOtp.length == 6){
                    binding.otpProgressBar.visibility = View.VISIBLE
                    val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        otp , typedOtp
                    )
                    signInWithPhoneAuthCredential(credential)
                }else{
                    Toast.makeText(applicationContext, "Please Enter Valid OTP", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(applicationContext, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun resendOTPvisiblity(){
        binding.otpEditText1.setText("")
        binding.otpEditText2.setText("")
        binding.otpEditText3.setText("")
        binding.otpEditText4.setText("")
        binding.otpEditText5.setText("")
        binding.otpEditText6.setText("")
        binding.resendTextView.visibility = View.INVISIBLE
        binding.resendTextView.isEnabled = false

        Handler().postDelayed(Runnable {
            binding.resendTextView.visibility = View.VISIBLE
            binding.resendTextView.isEnabled = true
        },60000)

    }

    private fun resendOtp(){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {

            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(applicationContext, "${e.message.toString()}", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(applicationContext, "${e.message.toString()}", Toast.LENGTH_SHORT).show()
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            otp = verificationId
            resendToken = token

        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    binding.otpProgressBar.visibility=  View.INVISIBLE
                    val user = task.result?.user
                    Toast.makeText(applicationContext, "User is verified", Toast.LENGTH_SHORT).show()
                    updateUI(user)
                } else {
                    
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(applicationContext, "OTP is not valid", Toast.LENGTH_SHORT).show()
                        binding.otpProgressBar.visibility = View.INVISIBLE
                    }
                    // Update UI
                }
            }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser!=null){
            checkUserExists()
        }
        else{
            binding.otpProgressBar.visibility = View.INVISIBLE
        }
    }

    private fun checkUserExists() {
        val userDao = UserDao()
        userDao.userCollection.child(phoneNumber).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    startActivity(Intent(applicationContext,MainActivity::class.java))
                    finish()
                }else{
                    val i = Intent(applicationContext,AboutUserActivity::class.java)
                    i.putExtra("uid",phoneNumber)
                    startActivity(i)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addTextChangeListener(){
        binding.otpEditText1.addTextChangedListener(EditTextWatcher(binding.otpEditText1))
        binding.otpEditText2.addTextChangedListener(EditTextWatcher(binding.otpEditText2))
        binding.otpEditText3.addTextChangedListener(EditTextWatcher(binding.otpEditText3))
        binding.otpEditText4.addTextChangedListener(EditTextWatcher(binding.otpEditText4))
        binding.otpEditText5.addTextChangedListener(EditTextWatcher(binding.otpEditText5))
        binding.otpEditText6.addTextChangedListener(EditTextWatcher(binding.otpEditText6))
    }

    private fun init(){
        auth = FirebaseAuth.getInstance()
    }

    inner class EditTextWatcher(private val view : View) : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            when(view.id){
                R.id.otpEditText1 -> if (text.length == 1) binding.otpEditText2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) binding.otpEditText3.requestFocus() else if (text.isEmpty()) binding.otpEditText1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) binding.otpEditText4.requestFocus() else if (text.isEmpty()) binding.otpEditText2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) binding.otpEditText5.requestFocus() else if (text.isEmpty()) binding.otpEditText3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) binding.otpEditText6.requestFocus() else if (text.isEmpty()) binding.otpEditText4.requestFocus()
                R.id.otpEditText6 -> if (text.isEmpty()) binding.otpEditText5.requestFocus()
            }
        }

    }

}