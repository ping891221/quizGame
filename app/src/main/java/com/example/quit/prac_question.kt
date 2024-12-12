package com.example.quit

import UserDataRepository
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.media.MediaPlayer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.SeekBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.quit.databinding.ActivityPracQuestionBinding
import com.example.quit.databinding.PracendAlertdialogBinding
import com.example.quit.databinding.PrachintAlertdialogBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuff
import kotlinx.coroutines.awaitAll

class prac_question : AppCompatActivity() {
    private lateinit var binding: ActivityPracQuestionBinding
    private lateinit var hintBinding1 : PrachintAlertdialogBinding
    private lateinit var endBinding : PracendAlertdialogBinding
    private lateinit var database: DatabaseReference
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var dialog: Dialog
    private lateinit var runnable: Runnable
    private var handler = Handler()
    var questionId = ""
    var hintnum = 0
    var hintConState = false
    var hintTransState = false
    var english = ""
    var chinese = ""
    var mySelectAnswer = ""
    var currentHintContentNum = UserDataRepository.userData!!.hintContent
    var currentHintTranslaNum = UserDataRepository.userData!!.hintTransla
    var answer = ""
    var ok = false
    var heart = UserDataRepository.userData!!.heart
    var money = UserDataRepository.userData!!.money
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPracQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showPressBar()
        binding.contextToolNumber.text = UserDataRepository.userData?.hintContent.toString()
        binding.translationToolNumber.text = UserDataRepository.userData?.hintTransla.toString()
        questionId = intent.getStringExtra("QUESTION_ID")!!
        hintnum = intent.getIntExtra("hintState",-1)
        ok = badgeStat.okPhoto[hintnum].ok
        hintConState = badgeStat.hintPhotoStates[hintnum].con
        hintTransState = badgeStat.hintPhotoStates[hintnum].trans
        loadDataFromFirebase()

    }
    private fun loadDataFromFirebase(){
        database = FirebaseDatabase.getInstance().getReference("Photos")
        database.child(questionId).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val photoUrl = snapshot.child("photoUrl").getValue(String::class.java)
                val audioUrl = snapshot.child("audioUrl").getValue(String::class.java)
                answer = snapshot.child("answer").getValue(String::class.java)!!
                chinese = snapshot.child("chinese").getValue(String::class.java)!!
                english = snapshot.child("english").getValue(String::class.java)!!
                Glide.with(this@prac_question).load(photoUrl).into(binding.photoTopic)
                hideProgressBar()
                binding.voice.setOnClickListener {
                    playAudio(audioUrl)
                }
                //題目文字提示
                binding.contextTool.setOnClickListener {
                    if(!hintConState){
                        if(currentHintContentNum>0){
                            currentHintContentNum-=1
                            binding.contextToolNumber.text = currentHintContentNum.toString()
                            changeDB("hintContent",currentHintContentNum)
                            badgeStat.hintPhotoStates[hintnum].con = true //以後的數據都改
                            hintConState = true //沒監聽所以當下的也要改
                            binding.contextTool.setOnClickListener(null)
                        }else{
                            Toast.makeText(this@prac_question, "你沒有該道具！請至商店購買", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        binding.contextTool.setOnClickListener(null)
                    }

                }
                binding.contextToolText.setOnClickListener {
                    showHint1AlertDialog()
                    Log.d("題目文字的鍵", "有點到")
                }
                //翻譯提示
                binding.translationTool.setOnClickListener {
                    if(!hintTransState){
                        if(currentHintTranslaNum>0){
                            currentHintTranslaNum-=1
                            binding.translationToolNumber.text = currentHintTranslaNum.toString()
                            changeDB("hintTransla",currentHintTranslaNum)
                            badgeStat.hintPhotoStates[hintnum].trans = true //以後的數據都改
                            hintTransState = true //沒監聽所以當下的也要改
                            binding.translationTool.setOnClickListener(null)
                        }else{
                            Toast.makeText(this@prac_question, "你沒有該道具！請至商店購買", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        binding.translationTool.setOnClickListener(null)
                    }

                }
                binding.translationToolText.setOnClickListener {
                    showHint2AlertDialog()
                    Log.d("中文翻譯的鍵", "有點到")
                }
                binding.option1.setOnClickListener {
                    resetButtonColors()
                    mySelectAnswer = "option1"
                    binding.option1.setBackgroundResource(R.drawable.option_border1)
                    binding.option2.setBackgroundResource(R.drawable.option_border_wb)
                    binding.option3.setBackgroundResource(R.drawable.option_border_wb)
                    binding.option4.setBackgroundResource(R.drawable.option_border_wb)
                }
                binding.option2.setOnClickListener {
                    resetButtonColors()
                    mySelectAnswer = "option2"
                    binding.option1.setBackgroundResource(R.drawable.option_border_wb)
                    binding.option2.setBackgroundResource(R.drawable.option_border1)
                    binding.option3.setBackgroundResource(R.drawable.option_border_wb)
                    binding.option4.setBackgroundResource(R.drawable.option_border_wb)
                }
                binding.option3.setOnClickListener {
                    resetButtonColors()
                    mySelectAnswer = "option3"
                    binding.option1.setBackgroundResource(R.drawable.option_border_wb)
                    binding.option2.setBackgroundResource(R.drawable.option_border_wb)
                    binding.option3.setBackgroundResource(R.drawable.option_border1)
                    binding.option4.setBackgroundResource(R.drawable.option_border_wb)
                }
                binding.option4.setOnClickListener {
                    resetButtonColors()
                    mySelectAnswer = "option4"
                    binding.option1.setBackgroundResource(R.drawable.option_border_wb)
                    binding.option2.setBackgroundResource(R.drawable.option_border_wb)
                    binding.option3.setBackgroundResource(R.drawable.option_border_wb)
                    binding.option4.setBackgroundResource(R.drawable.option_border1)
                }
                binding.checkAnswer.setOnClickListener {
                    if(mySelectAnswer == answer){
                        if (!ok){
                            badgeStat.okPhoto[hintnum].ok = true
                            Log.d("確定有沒有改到",badgeStat.okPhoto[hintnum].ok.toString())
                            showEndAlertDialog("答對了！",0)
                        }else{
                            //跳個甚麼複習成功(heart+1)
                            showEndAlertDialog("複習成功！",2)
                        }
                    }else{
                        showEndAlertDialog("答錯了",1)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    private fun resetButtonColors() {
        binding.option1.setBackgroundColor(Color.TRANSPARENT)
        binding.option2.setBackgroundColor(Color.TRANSPARENT)
        binding.option3.setBackgroundColor(Color.TRANSPARENT)
        binding.option4.setBackgroundColor(Color.TRANSPARENT)
    }
    private fun changeDB(position:String,value:Int){
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(badgeStat.nowUserUid!!).child(position).ref.setValue(value)
    }
    private fun showEndAlertDialog(title:String,success:Int){
        endBinding = PracendAlertdialogBinding.inflate(layoutInflater)
        endBinding.title.text = title
        database = FirebaseDatabase.getInstance().getReference("Users")
        if(success == 1){
            endBinding.heartAdd.text = "再接再厲！"
            endBinding.nextLevel.setImageResource(R.drawable.tryagain)
        }else{
            if(heart<3){
                heart+=1
                database.child(badgeStat.nowUserUid!!).child("heart").ref.setValue(heart)
            }
            if (success == 0){
                money+=5
                database.child(badgeStat.nowUserUid!!).child("money").ref.setValue(money)
                endBinding.moneyAdd.text = "\uD83E\uDE99 +10"
                endBinding.heartAdd.text = "❤\uFE0F +1"
            }else{
                endBinding.heartAdd.text = "❤ +1"
            }
        }
        val dialogend = Dialog(this@prac_question).apply {
            setContentView(endBinding.root)
            window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            window?.setBackgroundDrawable(getDrawable(R.drawable.alertdialog_bg))
            setCancelable(false)
        }
        if(success == 1){//變tryagain 重新載入
            endBinding.nextLevel.setOnClickListener {
                dialogend.dismiss()
                onPause()
                val intent = intent // 結束當前的 Activity
                startActivity(intent)
            }
        }else{
            endBinding.nextLevel.setOnClickListener {
                dialogend.dismiss()
                onPause()
                if (hintnum == 9){
                    val intent =  Intent(this, prac_question::class.java)
                    intent.putExtra("QUESTION_ID", "one")  // 傳遞第一關的 ID 或內容
                    intent.putExtra("hintState", 0)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }else{
                    Log.d("原本", hintnum.toString())
                    hintnum+=1
                    val hintEnglish = when (hintnum) {
                        0 -> "one"
                        1 -> "two"
                        2 -> "three"
                        3 -> "four"
                        4 -> "five"
                        5 -> "six"
                        6 -> "seven"
                        7 -> "eight"
                        8 -> "nine"
                        9 -> "ten"
                        else -> ""
                    }
                    Log.d("確認按下一關時有沒有", hintnum.toString())
                    val intent =  Intent(this, prac_question::class.java)
                    intent.putExtra("QUESTION_ID", hintEnglish)
                    intent.putExtra("hintState", hintnum)
                    Log.d("傳遞資料", "QUESTION_ID: $hintEnglish, hintState: $hintnum")
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }
        //選項要改成下一關和回選單
        endBinding.backToMenu.setOnClickListener {
            dialogend.dismiss()
            onPause()
            val intent = Intent(this, prac_level::class.java)
            startActivity(intent)
        }
        dialogend.show()
    }
    private fun showHint1AlertDialog(){
        hintBinding1 = PrachintAlertdialogBinding.inflate(layoutInflater)
        hintBinding1.title.text = "題目文字"
        val dialoghint1 = Dialog(this@prac_question).apply {
            if(hintConState){
                hintBinding1.english.text = english
            }else{
                hintBinding1.english.text = "使用題目文字道具解鎖"
            }
            setContentView(hintBinding1.root)
            window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            window?.setBackgroundDrawable(getDrawable(R.drawable.alertdialog_bg))
            setCancelable(false)
        }
        hintBinding1.close.setOnClickListener {
            dialoghint1.dismiss()
        }
        dialoghint1.show()
    }
    private fun showHint2AlertDialog(){
        hintBinding1 = PrachintAlertdialogBinding.inflate(layoutInflater)
        hintBinding1.title.text = "中文翻譯"
        val dialoghint2 = Dialog(this@prac_question).apply {
            if(hintTransState){
                hintBinding1.english.text = chinese
            }else{
                hintBinding1.english.text = "使用中文翻譯道具解鎖"
            }
            setContentView(hintBinding1.root)
            window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            window?.setBackgroundDrawable(getDrawable(R.drawable.alertdialog_bg))
            setCancelable(false)
        }
        hintBinding1.close.setOnClickListener {
            dialoghint2.dismiss()
        }
        dialoghint2.show()
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

    override fun onPause() {
        super.onPause()
        try {
            stopAndReleaseMediaPlayer()
        } catch (e: IllegalStateException) {
            Log.e("prac_question", "MediaPlayer state is invalid: ${e.message}")
        }
    }


    private fun stopAndReleaseMediaPlayer() {
        if (::mediaPlayer.isInitialized) { // 確保MediaPlayer已初始化
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop() // 停止播放
            }
            mediaPlayer.release() // 釋放資源
            binding.seekbar.progress = 0 // 重置進度條
            handler.removeCallbacks(runnable) // 停止更新進度條
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        releaseMediaPlayer() // 避免記憶體洩漏
    }
}