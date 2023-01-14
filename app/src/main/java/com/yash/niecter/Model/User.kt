package com.yash.niecter.Model

data class User (
    val uid : String? = "",
    val username : String? = "",
    val userBio : String? = "",
    val userGender : String? = "",
    val userImage : String? ="",
    val potentialMatch : ArrayList<String>? = arrayListOf(),
    val match : ArrayList<String>? = arrayListOf()
)