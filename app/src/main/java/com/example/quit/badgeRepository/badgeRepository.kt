package com.example.quit.badgeRepository

import androidx.lifecycle.MutableLiveData
import com.example.quit.Domain.badgeDomain
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class badgeRepository {
    private val databaseReference : DatabaseReference=FirebaseDatabase.getInstance().getReference("Badges")

    //確保這裡面只有一個實例instance
    @Volatile private var INSTANCE : badgeRepository ?= null

    fun getInstance() : badgeRepository{
        return INSTANCE ?: synchronized(this){
            val instance = badgeRepository()
            INSTANCE = instance
            instance
        }
    }

    fun loadBadges(badgeList : MutableLiveData<List<badgeDomain>>){
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try{
                    val _badgeList : List<badgeDomain> = snapshot.children.map{dataSnapshot ->
                        dataSnapshot.getValue(badgeDomain::class.java)!!
                    }
                    badgeList.postValue(_badgeList)
                }catch (e:Exception){

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}