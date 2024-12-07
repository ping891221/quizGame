package com.example.quit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quit.databinding.ActivityPracLevel2Binding
import com.example.quit.databinding.ActivityPracLevelBinding

class prac_level : AppCompatActivity() {
    private lateinit var binding: ActivityPracLevelBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reButton.setOnClickListener {
            finish()
        }
        //要進資料庫看這題的pracSuccess是不是true，不是的話圖片要用黑白的

        binding.level1.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "one")  // 傳遞第一關的 ID 或內容
            startActivity(intent)
        }
        binding.level2.setOnClickListener {
            val intent = Intent(this, prac_question::class.java)
            intent.putExtra("QUESTION_ID", "two")  // 傳遞第一關的 ID 或內容
            startActivity(intent)
        }
    }
}