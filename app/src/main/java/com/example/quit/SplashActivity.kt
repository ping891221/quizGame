package com.example.quit

import androidx.appcompat.app.AppCompatActivity
//使用Intent啟動另一個activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.quit.databinding.ActivityMainBinding
import com.example.quit.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    //使用延遲初始化的方式定義 binding 變數，它將用來訪問 activity_splash.xml 中的視圖。
    private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //初始化 binding，使用資料綁定來膨脹佈局(將XML格式布局文件轉換成相應的View對象)。
        binding = ActivitySplashBinding.inflate(layoutInflater)
        //設定活動的內容視圖為 binding.root，即 activity_splash.xml 的根視圖。
        setContentView(binding.root)

        //設定延遲時間3秒
        Handler(Looper.getMainLooper()).postDelayed({
            //跳轉到LoginActivity(啟動另一個activity)
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            //結束SpladhActivity
            finish()
        },3000)
    }
}