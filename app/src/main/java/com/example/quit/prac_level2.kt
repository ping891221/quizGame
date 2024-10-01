package com.example.quit
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quit.databinding.ActivityLoginBinding
import com.example.quit.databinding.ActivityPracLevel2Binding
import com.example.quit.databinding.ActivityPracQuestion2Binding

class prac_level2 : AppCompatActivity() {
    private lateinit var binding: ActivityPracLevel2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracLevel2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reButton.setOnClickListener {
            finish()
        }

        binding.level1.setOnClickListener {
            val intent = Intent(this, prac_question2::class.java)
            intent.putExtra("QUESTION_ID", 1)  // 傳遞第二關的 ID 或內容
            startActivity(intent)
        }
        binding.level2.setOnClickListener {
            val intent = Intent(this, prac_question2::class.java)
            intent.putExtra("QUESTION_ID", 1)  // 傳遞第二關的 ID 或內容
            startActivity(intent)
        }
    }
}