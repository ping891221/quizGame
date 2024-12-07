package com.example.quit

import PhotosList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase

class QuizResult : AppCompatActivity() {
    //private var photosLists: ArrayList<PhotosList>? = null
    private var database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://quit-f4d36-default-rtdb.asia-southeast1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)

        /*@Suppress("DEPRECATION")//一直棄用到底想怎樣
        photosLists = intent.getParcelableArrayListExtra("Photos")*/

        val res = intent.getStringExtra("result")

        //如果贏了就換贏的圖，輸了換輸的圖，這樣就只要傳一個值就好
    }
}