package com.yash.niecter.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.yash.niecter.Adapter.PerfectAdapter
import com.yash.niecter.Adapter.matchAdapter
import com.yash.niecter.Dao.UserDao
import com.yash.niecter.Model.User
import com.yash.niecter.R
import com.yash.niecter.databinding.FragmentMatchesBinding

class MatchesFragment : Fragment() {
    private var _binding : FragmentMatchesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMatchesBinding.inflate(layoutInflater,container,false)
        getMatchData()

        return binding.root
    }


    private fun getMatchData() {
        val userDao = UserDao()
        userDao.userCollection.child(FirebaseAuth.getInstance().currentUser!!.phoneNumber.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    val match = arrayListOf<String>()
                    val potential = arrayListOf<String>()
                    Log.d("user",user.toString())
                    for (i in 0 until user?.match!!.size){
                        match.add(user.match.get(i))
                    }
                    for (i in 0 until user.potentialMatch!!.size){
                        potential.add(user.potentialMatch.get(i))
                    }
                    Log.d("match",match.toString())
                    Log.d("potential",potential.toString())

                    activity?.let {
                        binding.newMatchRV.adapter = matchAdapter(requireContext(),potential)
                        binding.newMatchRV.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                        binding.newMatchRV.setHasFixedSize(true)

                        binding.perfectRV.adapter = PerfectAdapter(requireContext(),match)
                        binding.perfectRV.layoutManager = LinearLayoutManager(requireContext())
                        binding.perfectRV.setHasFixedSize(true)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}