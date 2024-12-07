package com.example.quit

import android.app.Dialog
import android.os.Handler
import android.media.MediaPlayer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.example.quit.databinding.ActivityPracQuestion2Binding
import com.example.quit.databinding.ActivityPracQuestionBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class prac_question : AppCompatActivity() {
    private lateinit var binding: ActivityPracQuestionBinding
    private lateinit var database: DatabaseReference
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var dialog: Dialog
    private lateinit var runnable: Runnable
    private var handler = Handler()
    var questionId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPracQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showPressBar()
        database = FirebaseDatabase.getInstance().getReference("Photos")
        questionId = intent.getStringExtra("QUESTION_ID")!!
        loadDataFromFirebase()



    }
    private fun loadDataFromFirebase(){
        database.child(questionId).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val photoUrl = snapshot.child("photoUrl").getValue(String::class.java)
                val audioUrl = snapshot.child("audioUrl").getValue(String::class.java)
                val answer = snapshot.child("answer").getValue(String::class.java)
                val chinese = snapshot.child("chinese").getValue(String::class.java)
                val english = snapshot.child("english").getValue(String::class.java)
                val hintCon = snapshot.child("hintCon").getValue(Boolean::class.java)
                val hintTrans = snapshot.child("hintTrans").getValue(Boolean::class.java)
                Glide.with(this@prac_question).load(photoUrl).into(binding.photoTopic)
                hideProgressBar()
                binding.voice.setOnClickListener {
                    playAudio(audioUrl)
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    //對或錯
    //道具
    private fun showPressBar(){
        dialog = Dialog(this@prac_question)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

    }
    private fun hideProgressBar(){
        dialog.dismiss()
    }
    private fun playAudio(audioUrl: String?) {
        try {
            // 初始化 MediaPlayer（如果尚未初始化）
            if (!::mediaPlayer.isInitialized) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioUrl) // 設置音訊 URL
                    prepare() // 準備播放
                }

                // 設置完成監聽器，播放結束後重置按鈕
                mediaPlayer.setOnCompletionListener {
                    Log.d("AudioPlayer", "播放完成")
                    binding.voice.setImageResource(R.drawable.play)
                    binding.seekbar.progress = 0
                    handler.removeCallbacks(runnable) // 停止更新 SeekBar
                }

                // 初始化 SeekBar 最大值
                binding.seekbar.max = mediaPlayer.duration

                // 設置 SeekBar 監聽器
                binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            mediaPlayer.seekTo(progress) // 用戶拖動滑桿時調整播放進度
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        handler.removeCallbacks(runnable) // 暫停更新 SeekBar
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        handler.post(runnable) // 恢復更新 SeekBar
                    }
                })

                // 定期更新 SeekBar
                runnable = object : Runnable {
                    override fun run() {
                        binding.seekbar.progress = mediaPlayer.currentPosition
                        handler.postDelayed(this, 1000)
                    }
                }
            }

            // 控制播放/暫停
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.voice.setImageResource(R.drawable.play)
                handler.removeCallbacks(runnable) // 停止更新 SeekBar
            } else {
                mediaPlayer.start()
                binding.voice.setImageResource(R.drawable.pause)
                handler.post(runnable) // 開始更新 SeekBar
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "播放音訊時出錯", e)
        }
    }
    private fun releaseMediaPlayer() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer() // 避免記憶體洩漏
    }
}