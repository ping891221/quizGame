package com.example.quit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.quit.databinding.ActivityPracLevel2Binding
import com.example.quit.databinding.ActivityPracLevelBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class prac_level : AppCompatActivity() {
    private lateinit var binding: ActivityPracLevelBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val levelViews = listOf(
            binding.level1,
            binding.level2,
            binding.level3,
            binding.level4,
            binding.level5,
            binding.level6,
            binding.level7,
            binding.level8,
            binding.level9,
            binding.level10
        )

        for(i in 0..9){
            Log.d("第幾個", i.toString())
            Log.d("到底有沒有迴圈到", badgeStat.okPhoto[i].ok.toString())
            if(badgeStat.okPhoto[i].ok){
                levelViews[i].setImageResource(R.drawable.lock1_ok)
                Log.d("true的", levelViews[i].toString())
            }
        }

        binding.reButton.setOnClickListener {
            val intent = Intent(this, prac_type::class.java)
            startActivity(intent)
        }
        //要進資料庫看這題的pracSuccess是不是true，不是的話圖片要用黑白的

        binding.level1.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "one")  // 傳遞第一關的 ID 或內容
            intent.putExtra("hintState", 0)
            startActivity(intent)
        }
        binding.level2.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "two")  // 傳遞第一關的 ID 或內容
            intent.putExtra("hintState", 1)
            startActivity(intent)
        }
        binding.level3.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "three")  // 傳遞第一關的 ID 或內容
            intent.putExtra("hintState", 2)
            startActivity(intent)
        }
        binding.level4.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "four")  // 傳遞第一關的 ID 或內容
            intent.putExtra("hintState", 3)
            startActivity(intent)
        }
        binding.level5.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "five")  // 傳遞第一關的 ID 或內容
            intent.putExtra("hintState", 4)
            startActivity(intent)
        }
        binding.level6.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "six")  // 傳遞第一關的 ID 或內容
            intent.putExtra("hintState", 5)
            startActivity(intent)
        }
        binding.level7.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "seven")  // 傳遞第一關的 ID 或內容
            intent.putExtra("hintState", 6)
            startActivity(intent)
        }
        binding.level8.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "eight")  // 傳遞第一關的 ID 或內容
            intent.putExtra("hintState", 7)
            startActivity(intent)
        }
        binding.level9.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "nine")  // 傳遞第一關的 ID 或內容
            intent.putExtra("hintState", 8)
            startActivity(intent)
        }
        binding.level10.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "ten")  // 傳遞第一關的 ID 或內容
            intent.putExtra("hintState", 9)
            startActivity(intent)
        }
    }
}