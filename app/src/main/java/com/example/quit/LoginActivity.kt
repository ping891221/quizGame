package com.example.quit

import UserDataRepository
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quit.databinding.ActivityLoginBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import android.app.Dialog
import android.view.Window
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity() {
    //step.1 設定binding
    //step.2 設定firebase的database
    //step.3 當使用者按下login鈕時會發生:避免選項為空以及尋找帳號是否成功，成功後會導到Home
    //step.4 當使用者按下signup text後會發生:連結到SignupActivity

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // 登入按鈕點擊
        binding.loginButton.setOnClickListener{
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            //loginDatabase(loginAccount,loginPassword)

            showPressBar()

            // 檢查帳號與密碼是否為空
            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        // 登入成功，取用當前使用者之uid
                        val uid = firebaseAuth.currentUser?.uid
                        badgeStat.nowUserUid = uid
                        if (uid != null){
                            database = FirebaseDatabase.getInstance().getReference("Users")//有此節點的話便引用，沒有便創建
                            database.child(uid).get().addOnSuccessListener { dataSnapshot ->
                                if(dataSnapshot.exists()){
                                    database.child(uid).addValueEventListener(object:
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            UserDataRepository.updateUserData(snapshot.getValue(UserData::class.java))
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(this@LoginActivity,"抓不到資料",Toast.LENGTH_SHORT).show()
                                        }

                                    })

                                    // 如果資料存在，跳轉到主頁面
                                    hideProgressBar()
                                    Toast.makeText(this,"成功登入'", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    //如果不存在，則初始化數值
                                    val user = UserData(password,"https://firebasestorage.googleapis.com/v0/b/quit-f4d36.appspot.com/o/sticker%2Finitial.png?alt=media&token=77ab52cd-0dac-4b49-980f-85b538cd091f",0,0,3,0,0,0,0,0,
                                        0,0,0,0,0,0,0,0,0,0,0,0,
                                        0,0,0,0,0,0,0,0,0,0,0,0,
                                        0,0,0,0,0,0,0,0,0,0,0,
                                        0,0,0,0,0,0,0,0,0,0,0,
                                        0,0,0,0,0,0,0,0,0,0,0,
                                        0,0,0,0,0,0,0,0,0,0,0,
                                        0,0,0,0,0,0,0,0,0,0,0,
                                        0,0,0,0,0,0,0,0,0,0,0,
                                        0,0,0,0,0,0,0,0,0,0,0,0
                                    )
                                    database.child(uid).setValue(user).addOnCompleteListener { dataTask ->
                                        if(dataTask.isSuccessful){
                                            database.child(uid).addValueEventListener(object:
                                                ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    UserDataRepository.updateUserData(snapshot.getValue(UserData::class.java))
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    Toast.makeText(this@LoginActivity,"抓不到資料",Toast.LENGTH_SHORT).show()
                                                }

                                            })
                                            hideProgressBar()
                                            Toast.makeText(this,"登入成功'", Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }else{
                                            hideProgressBar()
                                            Toast.makeText(this,"初始化資料失敗",Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }.addOnFailureListener{
                                hideProgressBar()
                                Toast.makeText(this,"check資料失敗",Toast.LENGTH_SHORT).show()
                            }
                        }

                    }else{
                        hideProgressBar()
                        Toast.makeText(this,"登入失敗${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.d("LoginActivity", "登入失敗${task.exception?.message}")
                    }
                }
            } else {
                hideProgressBar()
                Toast.makeText(this, "請輸入帳號和密碼", Toast.LENGTH_SHORT).show()
            }
        }
        binding.signupText.setOnClickListener {
            startActivity(Intent(/* packageContext = */ this, /* cls = */ SignupActivity::class.java))
            finish()
        }

    }
    private fun showPressBar(){
        dialog = Dialog(this@LoginActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

    }
    private fun hideProgressBar(){
        dialog.dismiss()
    }


}


    //使用firebase realtime database 進行帳號密碼驗證
    /*private fun loginDatabase(account:String, password: String){
        val userRef = database.child("users").child(account)
        //三種可能性:帳號名稱存不存在(因為適用帳號名稱到database找的)、密碼是否正確、其他意外錯誤
        // 取得該用戶的資料
        userRef.get().addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val dataSnapshot = task.result
                if (dataSnapshot.exists()) {
                    val dbPassword = dataSnapshot.child("password").value.toString()

                    // 檢查密碼是否正確
                    if (dbPassword == password) {

                        Toast.makeText(this, "登入成功", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)

                        startActivity(intent)
                        finish()  // 結束當前 Activity
                    } else {
                        Toast.makeText(this, "密碼錯誤", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "帳號不存在", Toast.LENGTH_SHORT).show()
                }
            } else {
                // 讀取資料失敗
                Toast.makeText(this, "登入失敗，請稍後再試", Toast.LENGTH_SHORT).show()
            }
        })
        }
    }*/

