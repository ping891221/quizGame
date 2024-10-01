package com.example.quit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quit.databinding.ActivityPracQuestion2Binding

class prac_question2 : AppCompatActivity() {
    private lateinit var binding: ActivityPracQuestion2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPracQuestion2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // 接收從 Intent 傳來的題目 ID
        val questionId = intent.getIntExtra("QUESTION_ID", -1)

        // 根據題目 ID 顯示對應的題目內容
        /*when (questionId) {
            1 -> {
                // 顯示第一關的題目
                binding.questionText.text = "這是第一關的題目"
                //選項1、2、3，這關不用但其他關要
                /*binding.option1.text="A"
                binding.option2.text="B"
                binding.option3.text="C"*/
            }
            2 -> {
                // 顯示第二關的題目
                questionTextView.text = "這是第二關的題目"
            }
            else -> {
                // 預設的情況
                questionTextView.text = "未知的關卡"
            }
        }*/
    }
}