package com.example.quit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quit.databinding.ActivitySignupBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.util.Log
import com.google.firebase.auth.FirebaseAuth


class SignupActivity : AppCompatActivity() {
    //step.1 設定binding
    //step.2 設定firebase的database
    //step.3 當使用者按下signup鈕時會發生:避免選項為空以及帳號創建是否成功
    //step.4 當使用者按下login text後會發生:連結到LoginActivity
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    //private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        //database = FirebaseDatabase.getInstance().reference

        // 註冊按鈕
        binding.signupButton.setOnClickListener {//獲取輸入
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()

            // 檢查帳號與密碼是否為空
            if (email.isNotEmpty() && password.isNotEmpty()) {
                //signupDatabase(signupAccount, signupPassword)
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        // 註冊成功，跳轉到登入頁面
                        Toast.makeText(this,"Signup Successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this,"Signup Unsuccessful", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "請輸入帳號和密碼", Toast.LENGTH_SHORT).show()
            }
        }

        // 按login字後跳轉到登入頁面
        binding.loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // 檢查帳號是否存在並進行註冊
    /*private fun signupDatabase(account: String, password: String) {
        checkUsernameExists(account) { exists ->
            if (!exists) {
                val userId = database.push().key  // 自動生成一個唯一的 userId
                val userData = mapOf(
                    "username" to account,
                    "password" to password,  // 密碼可以在這裡加密處理
                    "userId" to userId,
                    "level" to 1 ,
                    "money" to 1,
                    "heart" to 3// 初始化分數或其他需要存儲的用戶資料
                )

                // 將用戶資料寫入 Firebase Database
                database.child("users").child(account).setValue(userData)
                    .addOnSuccessListener {
                        // 註冊成功，跳轉到登入頁面
                        Toast.makeText(this, "註冊成功", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()  // 結束當前 Activity
                    }
                    .addOnFailureListener {
                        // 註冊失敗，顯示錯誤訊息
                        Toast.makeText(this, "註冊失敗", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // 帳號已經存在，顯示提示訊息
                Toast.makeText(this, "帳號名稱已被使用", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 檢查帳號名稱是否已存在於 Firebase Database 中
    private fun checkUsernameExists(username: String, onResult: (Boolean) -> Unit) {
        val usernameRef = database.child("users").child(username)
        usernameRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                onResult(dataSnapshot.exists())
            } else {
                Log.w("TAG", "Failed to read username", task.exception)
                onResult(false)
            }
        }
    }*/
}