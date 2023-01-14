package com.yash.niecter.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yash.niecter.Adapter.DatingAdapter
import com.yash.niecter.Dao.UserDao
import com.yash.niecter.Model.User
import com.yash.niecter.R
import com.yash.niecter.databinding.FragmentHomeBinding
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var usersList : ArrayList<User>
    private lateinit var manager : CardStackLayoutManager
    private  var currentUser : User? = null
    private lateinit var auth : FirebaseAuth
    private lateinit var adapter: DatingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        auth = FirebaseAuth.getInstance()
        usersList = arrayListOf()
        getCurrentUser()
        getData()

        return binding.root
    }

    private fun init() {
        manager = CardStackLayoutManager(requireContext(),object : CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {


                if (direction == Direction.Left){
                    Toast.makeText(requireContext(), "Left swiped", Toast.LENGTH_SHORT).show()
                    val userDao = UserDao()
                    val match = usersList[manager.topPosition-1]
                    userDao.onSwipeRight(auth.currentUser!!.phoneNumber.toString(),match.uid.toString())
                    usersList.removeAt(manager.topPosition-1)
                }
                if (direction == Direction.Right){
                    Toast.makeText(requireContext(), "Right swiped", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {
//                Log.d("Appeared","${usersList[position].toString()} and this card position is $position == ${manager.topPosition}")

                
            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }

        })
        manager.setVisibleCount(3)
        manager.setCanScrollVertical(false)
        manager.setCanScrollHorizontal(true)
        manager.setTranslationInterval(0.6f)
        manager.setScaleInterval(0.8f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
    }

    fun getCurrentUser(){
        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            currentUser = userDao.getUserById(auth.currentUser!!.phoneNumber.toString()).await().getValue(User::class.java)
        }
    }

    private fun getData() {
        FirebaseDatabase.getInstance().getReference("User")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (data in snapshot.children){
                            val model = data.getValue(User::class.java)
                            if (model!!.uid != FirebaseAuth.getInstance().currentUser!!.phoneNumber &&
                                currentUser?.userGender!=model.userGender && currentUser?.match?.contains(model.uid!!) == false
                                && currentUser?.potentialMatch?.contains(model.uid!!) == false
                            ){
                                usersList.add(model)
                            }
                        }
                        usersList.shuffle()
                        init()
                        binding.cardStackView.layoutManager = manager
                        binding.cardStackView.itemAnimator = DefaultItemAnimator()
                        binding.cardStackView.adapter = DatingAdapter(requireContext(),usersList)
                    }
                    else{
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}