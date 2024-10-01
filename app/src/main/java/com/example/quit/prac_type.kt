package com.example.quit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.quit.databinding.ActivityPracTypeBinding

class prac_type : AppCompatActivity() {
    private lateinit var binding:ActivityPracTypeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPracTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reButton.setOnClickListener {
            finish()
        }

        binding.topic2.setOnClickListener {
            val intent = Intent(this, prac_level2::class.java)
            startActivity(intent)

        }
    }

}