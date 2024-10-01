package com.example.quit

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.quit.databinding.ActivityGameBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.*

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var database: DatabaseReference

    private val questions = arrayOf("2+7=?","2*2=?","4+1=?")
    private val options = arrayOf(arrayOf("7","8","9"), arrayOf("4","6","8"), arrayOf("4","5","6"))
    private val correctAnswers = arrayOf(2,0,1)

    private var currentQuestionIndex = 0
    private var score = 0
    private var opponentScore = 0
    private var playerId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化 Firebase
        database = FirebaseDatabase.getInstance().reference

        // 獲取玩家 ID，這裡你可以使用 SQLite 獲取的用戶信息
        playerId = getUserIdFromSQLite()

        // 設定遊戲監聽器
        setupGameListener()

        displayQuestion()

        binding.option1Button.setOnClickListener{
            checkAnswer(0)
        }
        binding.option2Button.setOnClickListener{
            checkAnswer(1)
        }
        binding.option3Button.setOnClickListener{
            checkAnswer(2)
        }
        binding.restartButton.setOnClickListener {
            restartQuiz()
        }
    }

    // 從 SQLite 獲取已登入用戶的 ID
    private fun getUserIdFromSQLite():String{
        val databaseHelper = DatabaseHelper(this)
        val db = databaseHelper.readableDatabase
        var userId = ""
        val cursor = db.query("data", arrayOf("id"), null, null, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex("id")
                if (columnIndex >= 0) {
                    userId = cursor.getString(columnIndex) ?: ""
                }
            }
            cursor.close()
        }
        db.close()
        return userId
    }

    private fun setupGameListener() {
        database.child("games").child(playerId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // 讀取對方的分數
                    opponentScore = snapshot.child("opponentScore").getValue(Int::class.java) ?: 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GameActivity, "Failed to load game data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun correctButtonColors(buttonIndex: Int){
        when(buttonIndex){
            0 -> binding.option1Button.setBackgroundColor(Color.GREEN)
            1 -> binding.option2Button.setBackgroundColor(Color.GREEN)
            2 -> binding.option3Button.setBackgroundColor(Color.GREEN)
        }
    }

    private fun wrongButtonColors(buttonIndex: Int){
        when(buttonIndex){
            0 -> binding.option1Button.setBackgroundColor(Color.RED)
            1 -> binding.option2Button.setBackgroundColor(Color.RED)
            2 -> binding.option3Button.setBackgroundColor(Color.RED)
        }
    }

    private fun resetButtonColors(){
        binding.option1Button.setBackgroundColor(Color.rgb(50,59,96))
        binding.option2Button.setBackgroundColor(Color.rgb(50,59,96))
        binding.option3Button.setBackgroundColor(Color.rgb(50,59,96))
    }

    //之後要改成用一個頁面顯示結果
    //這裡的結果只是展示答題後雙方的分數，還沒有寫誰贏誰輸的問題，所以更新資料庫裡的分數才會寫在後面
    private fun showResults(){
        Toast.makeText(this,"Your score: $score out of ${questions.size}", Toast.LENGTH_LONG).show()
        Toast.makeText(this, "Opponent's score: $opponentScore", Toast.LENGTH_LONG).show()
        binding.restartButton.isEnabled = true
        updateOpponentScore()
    }

    private fun displayQuestion(){
        binding.questionText.text = questions[currentQuestionIndex]
        binding.option1Button.text = options[currentQuestionIndex][0]
        binding.option2Button.text = options[currentQuestionIndex][1]
        binding.option3Button.text = options[currentQuestionIndex][2]
        resetButtonColors()
    }

    private fun checkAnswer(selectedAnswerIndex: Int){
        val correctAnswerIndex = correctAnswers[currentQuestionIndex]
        if(selectedAnswerIndex == correctAnswerIndex){
            score++
            correctButtonColors(selectedAnswerIndex)
        }else{
            wrongButtonColors(selectedAnswerIndex)
            correctButtonColors(correctAnswerIndex)
        }
        if(currentQuestionIndex < questions.size-1){
            currentQuestionIndex++
            binding.questionText.postDelayed({displayQuestion()},1000)
        }else{
            showResults()
        }
    }

    private fun updateOpponentScore() {
        // 將當前玩家的分數更新到 Firebase
        database.child("games").child(playerId).child("opponentScore").setValue(score)
    }

    private fun restartQuiz(){
        currentQuestionIndex = 0
        score = 0
        displayQuestion()
        binding.restartButton.isEnabled = false
    }

}