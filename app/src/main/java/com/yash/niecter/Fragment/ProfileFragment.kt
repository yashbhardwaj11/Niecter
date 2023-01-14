package com.yash.niecter.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.yash.niecter.Activity.AboutUserActivity
import com.yash.niecter.Activity.SignUpActivity
import com.yash.niecter.Dao.UserDao
import com.yash.niecter.Model.User
import com.yash.niecter.R
import com.yash.niecter.databinding.FragmentHomeBinding
import com.yash.niecter.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var user : User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater,container,false)

        getData()

        binding.startBT.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(),SignUpActivity::class.java))
            requireActivity().finish()
        }

        binding.editBT.setOnClickListener {
            val i = Intent(requireContext(),AboutUserActivity::class.java)
            i.putExtra("userName",user?.username)
            i.putExtra("userBio",user?.userBio)
            i.putExtra("userImage",user?.userImage)
            startActivity(i)
        }
        return binding.root
    }

    private fun getData() {
        val userDao = UserDao()
        GlobalScope.launch(Dispatchers.IO) {
            user = userDao.getUserById(FirebaseAuth.getInstance().currentUser!!.phoneNumber.toString()).await().getValue(User::class.java)
            withContext(Dispatchers.Main){
                binding.userImagePP.load(user!!.userImage){
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
                binding.userNameETPP.setText(user?.username)
                binding.userBioEtPP.setText(user?.userBio)


            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}