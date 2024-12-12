package com.example.quit
import android.util.Log
import kotlinx.coroutines.*
import PhotosList
import UserDataRepository
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.quit.databinding.ActivityGameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.*
import java.io.Serializable
import kotlin.random.Random
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlinx.coroutines.launch


class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    //private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dialog: Dialog
    private lateinit var mediaPlayer: MediaPlayer

    private val questions = arrayOf("2+7=?","2*2=?","4+1=?")
    private val options = arrayOf(arrayOf("7","8","9"), arrayOf("4","6","8"), arrayOf("4","5","6"))
    private val correctAnswers = arrayOf(2,0,1)

    private var database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://quit-f4d36-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private var currentQuestionIndex = 0

    private var myScore = 0
    private var opponentScore = 0

    private var playerUniqueId = "0"
    private var opponentUniqueId = "0"
    private var opponentFound = false
    //這個房間的狀態是matching還是waiting
    private var status = "matching"
    private var playerFound = false
    //玩家加入房間的id??
    private var roomId = ""
    private var mychoose = ""
    private var opponentchoose = ""
    private var mySelectAnswer = "none"
    //private var photosLists: MutableList<PhotosList> = ArrayList()
    private var cListPosition: ArrayList<String> = arrayListOf()
    private var qPhotos = mutableMapOf<String, String?>()
    private var qResponses = mutableMapOf<String, String?>()
    private var qConversations = mutableMapOf<String, String?>()
    private var qTalks = mutableMapOf<String, String?>()

    private var startTime: Long = 0L
    private var timeRunning = false
    //在遊戲裡就初始化的值
    private var myPlayerTime = 0L
    private var opponentTime = 0L
    private var myTorF : Boolean? = null
    private var opponentTorF : Boolean? = null

    private var roundNumber = 0
    private var random1or2 = 1
    private var myselfResult = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)


        room()
        /*
        // 設定遊戲監聽器
        setupGameListener()
        displayQuestion()
        */
    }

    private fun room(){//但是要檢查對方level多少，if +-3才可以加(還沒寫) else 滾
        showPressBar()
        playerUniqueId = System.currentTimeMillis().toString()
        binding.myselfName.setText(UserDataRepository.userData?.name)
        Glide.with(this).load(UserDataRepository.userData?.sticker).into(binding.myselfHeader)

        database.child("connections").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                Log.d("Firebase", "Snapshot exists: ${snapshot.exists()}")
                if (!snapshot.exists()) {
                    // 如果節點不存在，建立節點
                    //創立房間的id
                    val roomUniqueId = System.currentTimeMillis().toString()
                    //新增第一個玩家的資料在房間並等待其他玩家
                    //ref用來進行讀取和寫入資料
                    val playerData = mapOf(
                        "player_name" to UserDataRepository.userData?.name,
                        "player_level" to UserDataRepository.userData?.level,
                        "player_sticker" to UserDataRepository.userData?.sticker
                    )
                    database.child("connections").child(roomUniqueId).child(playerUniqueId).ref.setValue(playerData)
                    status = "waiting"
                    Log.d("Firebase", "connections 節點不存在，已建立新節點")
                }
                //檢查是否有對手
                if(!opponentFound){
                    //檢查connections是否有節點
                    if(snapshot.hasChildren()){
                        //有
                        for(roms in snapshot.children){
                            //獲得所有房間的唯一辨識符(和roomUniqueId不一樣)// conId 會是 Long?，即可以為 null。但她其實不能是null，如果有null代表前面寫得有問題
                            //roomUniqueId 是你自己生成的 ID，通常在創建新房間時使用。
                            //conId 是從 Firebase 資料庫中取得的 ID，這個 ID 代表 Firebase 內部的房間 ID。
                            val conId = roms.key

                            //if getPlayersCount是1代表也有其他人在等著進入遊戲
                            //elif getPlayersCount是2代表這個房間已經滿了
                            val getPlayersCount = roms.childrenCount.toInt()
                            //檢查有沒有人創房間
                            //如果自己的狀態是waiting->代表自己已經創建一個新房間後等待其他玩家加入
                            if(status.equals("waiting")){
                                //如果是2代表已經有人加入可以開始遊戲
                                if(getPlayersCount == 2){
                                    //playgame()
                                    playerFound = false
                                    for (players in roms.children){
                                        val getPlayerUniqueId = players.key
                                        if(getPlayerUniqueId.equals(playerUniqueId)){
                                            playerFound = true
                                        }else if (playerFound){
                                            val getOpponentPlayerName = players.child("player_name").getValue(String::class.java)
                                            val getOpponentPlayerSticker = players.child("player_sticker").getValue(String::class.java)
                                            Log.d("Debug", "player_name: ${players.child("player_name").value}")
                                            Log.d("Debug", "player_sticker: ${players.child("player_sticker").value}")

                                            //如果有問題是NullPointerException就是這個值是null
                                            opponentUniqueId = players.key!!
                                            binding.opponentName.setText(getOpponentPlayerName)
                                            Glide.with(this@GameActivity).load(getOpponentPlayerSticker).into(binding.opponentHeader)
                                            roomId = conId.toString()
                                            opponentFound = true

                                            //只是啟動監聽器，有沒有執行要等內部的onChange有沒有被觸發
                                            //database.child("won").child(roomId).addValueEventListener(wonEventListener)

                                            getData()//之後再看跟上面怎麼合

                                            hideProgressBar()

                                            //連接一次後就移除這個房間的listener
                                            database.child("connections").removeEventListener(this)

                                            //開始遊戲
                                            gamestart()
                                        }
                                    }
                                }
                            }
                            //加入其他人創建的房間
                            else {
                                //檢查該連線是否已有 1 位玩家且還需要 1 位玩家來開始比賽，如果是這樣，則加入此連線
                                if (getPlayersCount == 1){
                                    for (players in roms.children) {
                                        val getOpponentPlayerLevel = players.child("player_level").getValue(Int::class.java)!!
                                        val myselfLevel = UserDataRepository.userData!!.level
                                        if(kotlin.math.abs(getOpponentPlayerLevel - myselfLevel)<=3){
                                            roomId = conId.toString()
                                            val playerData = mapOf(
                                                "player_name" to UserDataRepository.userData?.name,
                                                "player_level" to UserDataRepository.userData?.level,
                                                "player_sticker" to UserDataRepository.userData?.sticker
                                            )
                                            database.child("connections").child(roomId).child(playerUniqueId).ref.setValue(playerData)
                                            val getOpponentPlayerName = players.child("player_name").getValue(String::class.java)
                                            val getOpponentPlayerSticker = players.child("player_sticker").getValue(String::class.java)
                                            //如果有問題是NullPointerException就是這個值是null
                                            opponentUniqueId = players.key!!
                                            binding.opponentName.setText(getOpponentPlayerName)
                                            Glide.with(this@GameActivity).load(getOpponentPlayerSticker).into(binding.opponentHeader)
                                            opponentFound = true

                                            //只是啟動監聽器，有沒有執行要等內部的onChange有沒有被觸發
                                            //database.child("won").child(roomId).addValueEventListener(wonEventListener)

                                            hideProgressBar()

                                            //連接一次後就移除這個房間的listener
                                            database.child("connections").removeEventListener(this)

                                            break
                                        }else{
                                            room()
                                        }
                                    }
                                }
                            }
                        }
                        //沒有發現對手而我自己也沒有創建房間，就創一下房間(工三小)
                        if(!opponentFound&& !status.equals("waiting")){
                            //創立房間的id
                            val roomUniqueId = System.currentTimeMillis().toString()
                            //新增第一個玩家的資料在房間並等待其他玩家
                            //ref用來進行讀取和寫入資料
                            val playerData = mapOf(
                                "player_name" to UserDataRepository.userData?.name,
                                "player_level" to UserDataRepository.userData?.level,
                                "player_sticker" to UserDataRepository.userData?.sticker
                            )
                            database.child("connections").child(roomUniqueId).child(playerUniqueId).ref.setValue(playerData)

                            status = "waiting"
                        }
                    }
                    //發現沒有connections節點，所以要創一個
                    //他其實跟創立新房間並等待其他玩家加入的意思一樣
                    else{
                        //創立房間的id
                        val roomUniqueId = System.currentTimeMillis().toString()
                        //新增第一個玩家的資料在房間並等待其他玩家
                        //ref用來進行讀取和寫入資料
                        val playerData = mapOf(
                            "player_name" to UserDataRepository.userData?.name,
                            "player_level" to UserDataRepository.userData?.level,
                            "player_sticker" to UserDataRepository.userData?.sticker
                        )
                        database.child("connections").child(roomUniqueId).child(playerUniqueId).ref.setValue(playerData)
                        status = "waiting"
                    }
                }
            }
            override fun onCancelled(error: DatabaseError){

                    Toast.makeText(this@GameActivity,"配對出了點小問題！！！",Toast.LENGTH_SHORT).show()
                    Log.e("配對出了點小問題:",error.toString())

            }

        })
    }

    private fun showPressBar(){
        dialog = Dialog(this@GameActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

    }
    private fun hideProgressBar(){
        dialog.dismiss()
    }

    /*流程啦 但因為音訊處理是非同步所以不能這樣寫*/
    private fun gamestart(){
        lifecycleScope.launch {
            roundNumber = 1
            selectPhoto()
            playAudioWithDelay(qPhotos["photoAudioUrl"], qPhotos["photoAnswer"])
            selectResponse()
            playAudioWithDelay(qResponses["respAudioUrl"], qPhotos["respAnswer"])
            selectConver()
            playAudioWithDelay(qConversations["converAudioUrl"], qPhotos["converAnswer"])
            selectTalk()
            playAudioWithDelay(qTalks["talkAudioUrl"], qPhotos["talkAnswer"])
            finishQuiz()
        }
    }

    private fun getData(){ //如果他真的載入很慢我再把它弄出去
        val listPosition = listOf(Random.nextInt(0, 10),Random.nextInt(0, 10),Random.nextInt(0, 10),Random.nextInt(0, 10),Random.nextInt(0, 10))
        for (t in listPosition){
            val photoNumber = when (t) {
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
            cListPosition.add(photoNumber)
            Log.d("ramdon完的值：",photoNumber)
        }
        getPhoto()
        getResponse()
        getConversation()
        getTalk()
    }
    private fun getPhoto(){
        database.child("Photos").child(cListPosition[0]).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 在這裡處理數據
                val time = 120
                qPhotos["photoAnswer"] = snapshot.child("answer").getValue(String::class.java)
                qPhotos["photoAudioUrl"] = snapshot.child("audioUrl").getValue(String::class.java)
                qPhotos["photoOption1"] = snapshot.child("option1").getValue(String::class.java)
                qPhotos["photoOption2"] = snapshot.child("option2").getValue(String::class.java)
                qPhotos["photoOption3"] = snapshot.child("option3").getValue(String::class.java)
                qPhotos["photoOption4"] = snapshot.child("option4").getValue(String::class.java)
                qPhotos["photoPhotoUrl"] = snapshot.child("photoUrl").getValue(String::class.java)
                Log.d("拿到photoAudioUrl的值：",qPhotos["photoAudioUrl"]!!)
                /*for( photo in snapshot.child("Photos").children){
                    val photoAnswer = photo.child("answer").getValue(String::class.java)
                    val photoAudioUrl = photo.child("audioUrl").getValue(String::class.java)
                    val photoChinese = photo.child("chinese").getValue(String::class.java)
                    val photoEnglish = photo.child("english").getValue(String::class.java)
                    val photoOption1 = photo.child("option1").getValue(String::class.java)
                    val photoOption2 = photo.child("option2").getValue(String::class.java)
                    val photoOption3 = photo.child("option3").getValue(String::class.java)
                    val photoOption4 = photo.child("option4").getValue(String::class.java)
                    val photoPhotoUrl = photo.child("photoUrl").getValue(String::class.java)

                    photosLists.add(PhotosList(photoAnswer!!, photoAudioUrl!!, photoChinese!!, photoEnglish!!, photoOption1!!, photoOption2!!, photoOption3!!, photoOption4!! ,photoPhotoUrl!!))
                }
                //拿答案
                val photoAllAnswers: List<String> = photosLists.map { it.answer }
                val photosListPosition = Random.nextInt(0, 10) // 生成 0 到 9 的隨機數字*/
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GameActivity,"從firebase獲得photo資料錯誤！！！",Toast.LENGTH_SHORT).show()
                Log.e("photo教材資料錯誤:",error.toString())
            }
        })
    }

    private fun getResponse(){
        database.child("Responses").child(cListPosition[1]).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 在這裡處理數據
                val time = 120
                qResponses["respAnswer"] = snapshot.child("answer").getValue(String::class.java)
                qResponses["respAudioUrl"] = snapshot.child("audioUrl").getValue(String::class.java)
                Log.d("拿到respAudioUrl的值：",qResponses["respAudioUrl"]!!)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GameActivity,"從firebase獲得response資料錯誤！！！",Toast.LENGTH_SHORT).show()
                Log.e("response教材資料錯誤:",error.toString())
            }
        })
    }

    private fun getConversation(){
        database.child("Conversation").child(cListPosition[2]).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 在這裡處理數據
                val time = 120
                qConversations["converAnswer"] = snapshot.child("answer").getValue(String::class.java)
                qConversations["converAudioUrl"] = snapshot.child("audioUrl").getValue(String::class.java)
                qConversations["converOption1"] = snapshot.child("option1").getValue(String::class.java)
                qConversations["converOption2"] = snapshot.child("option2").getValue(String::class.java)
                qConversations["converOption3"] = snapshot.child("option3").getValue(String::class.java)
                qConversations["converOption4"] = snapshot.child("option4").getValue(String::class.java)
                qConversations["converTopic"] = snapshot.child("topic").getValue(String::class.java)
                Log.d("拿到converTopic的值：",qConversations["converTopic"]!!)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GameActivity,"從firebase獲得conversation資料錯誤！！！",Toast.LENGTH_SHORT).show()
                Log.e("conversation教材資料錯誤:",error.toString())
            }
        })
    }

    private fun getTalk(){
        database.child("Talks").child(cListPosition[3]).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 在這裡處理數據
                val time = 120
                qTalks["talkAnswer"] = snapshot.child("answer").getValue(String::class.java)
                qTalks["talkAudioUrl"] = snapshot.child("audioUrl").getValue(String::class.java)
                qTalks["talkOption1"] = snapshot.child("option1").getValue(String::class.java)
                qTalks["talkOption2"] = snapshot.child("option2").getValue(String::class.java)
                qTalks["talkOption3"] = snapshot.child("option3").getValue(String::class.java)
                qTalks["talkOption4"] = snapshot.child("option4").getValue(String::class.java)
                qTalks["talkTopic"] = snapshot.child("topic").getValue(String::class.java)
                Log.d("拿到talkTopic的值：",qTalks["talkTopic"]!!)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GameActivity,"從firebase獲得talk資料錯誤！！！",Toast.LENGTH_SHORT).show()
                Log.e("talk教材資料錯誤:",error.toString())
            }
        })
    }

    /*private fun finishQuiz(){
        //要跳到QuizResult
        // 在 GameActivity 中傳遞資料
        val intent = Intent(this@GameActivity, QuizResult::class.java)
        intent.putParcelableArrayListExtra("Photos", ArrayList(photosLists))  // 轉為 ArrayList 並傳遞
        startActivity(intent)
        finish()

    }*/

    private fun finishQuiz(){
        database.child("won").child(roomId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                myselfResult = snapshot.child(playerUniqueId).getValue(String::class.java)?:"null"
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GameActivity,"從firebase獲得won資料錯誤！！！",Toast.LENGTH_SHORT).show()
                Log.e("won資料錯誤:",error.toString())
            }
        })
        //要跳到QuizResult
        // 在 GameActivity 中傳遞資料 要看一下教學
        val intent = Intent(this@GameActivity, QuizResult::class.java)
        //我其實不知道他到底要傳甚麼intent.putParcelableArrayListExtra("Photos", )  // 轉為 ArrayList 並傳遞
        intent.putExtra("result",myselfResult)
        startActivity(intent)
        finish()

    }

    private fun selectPhoto(){//可能要改
        resetButtonColors()
        Log.d("設定第1題中","設定第1題中")
        Glide.with(this@GameActivity).load(qPhotos["photoPhotoUrl"]).into(binding.questionImage)
        binding.option1.setText(qPhotos["photoOption1"])
        binding.option2.setText(qPhotos["photoOption2"])
        binding.option3.setText(qPhotos["photoOption3"])
        binding.option4.setText(qPhotos["photoOption4"])
    }

    private fun selectResponse(){//可能要改
        resetButtonColors()
        Log.d("設定第2題中","設定第2題中")
        binding.option1.setText("A")
        binding.option2.setText("B")
        binding.option3.setText("C")
        binding.option4.setText("D")
    }

    private fun selectConver(){//可能要改
        resetButtonColors()
        Log.d("設定第3題中","設定第3題中")
        binding.option1.setText(qConversations["converOption1"])
        binding.option2.setText(qConversations["converOption2"])
        binding.option3.setText(qConversations["converOption3"])
        binding.option4.setText(qConversations["converOption4"])
        binding.questionText.setText(qConversations["converTopic"])
    }

    private fun selectTalk(){//可能要改
        resetButtonColors()
        Log.d("設定第4題中","設定第4題中")
        binding.option1.setText(qTalks["talkOption1"])
        binding.option2.setText(qTalks["talkOption2"])
        binding.option3.setText(qTalks["talkOption3"])
        binding.option4.setText(qTalks["talkOption4"])
        binding.questionText.setText(qTalks["talkTopic"])
    }

    private fun startTimer(){
        startTime = System.currentTimeMillis()
        timeRunning = true
    }
    private fun stopTimer():Long{
        val allTime = System.currentTimeMillis() - startTime
        timeRunning = false
        return allTime
    }

    private fun enableOptionButtons(enable:Boolean){
        listOf(binding.option1,binding.option2,binding.option3,binding.option4).forEachIndexed { index, button ->
            button.isEnabled=enable
            if (enable){
                button.setOnClickListener{
                    mySelectAnswer = "option${index+1}"
                    putTimeData()
                    button.setBackgroundColor(Color.parseColor("#304f00e3"))
                    enableOptionButtons(false)
                }
            }else{
                button.setOnClickListener(null)
            }

        }


    }

    suspend fun playAudioWithDelay(audioUrl: String?, answer: String?) {
        delay(3000) // 延迟 3 秒开始播放音频
        Log.d("播放音频", "开始播放")
        if (audioUrl.isNullOrEmpty()) {
            Log.e("GameActivity", "audioUrl is null or empty!")
        }
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioUrl)
            prepare() // 准备播放
            start()   // 开始播放
        }

        enableOptionButtons(true)

        // 挂起协程直到音频播放完成
        suspendCancellableCoroutine<Unit> { continuation ->
            mediaPlayer.setOnCompletionListener {
                Log.d("播放音频", "播放完成")
                continuation.resume(Unit) // 恢复协程
            }
        }

        // 延迟 8 秒后检查答案
        delay(8000)
        enableOptionButtons(false)
        checkAnswer(mySelectAnswer, answer) // 检查答案
    }

    private fun putTimeData(){
        val allTime = stopTimer()
        database.child("time").child(roomId).child(playerUniqueId).child(roundNumber.toString()).child("player_time").ref.setValue(allTime)//計時器啦
        startTime = 0L
    }

    private fun checkAnswer(selectAns: String, answer:String?){
        if(selectAns == answer){
            mycorrectButtonColors(selectAns)
            myTorF = true
            database.child("time").child(roomId).child(playerUniqueId).child(roundNumber.toString()).child("TorF").ref.setValue(myTorF)
            getTimeDB()
            if (opponentTorF!!){
                if(myPlayerTime < opponentTime){
                    myScore += 10
                }else if (myPlayerTime > opponentTime){
                    myScore += 5
                }else{
                    myScore += 7
                }
            }else{
                myScore += 15
            }
            database.child("score").child(roomId).child(playerUniqueId).child(roundNumber.toString()).child("player_score").ref.setValue(myScore)
        }else{
            wrongButtonColors(selectAns)
            mycorrectButtonColors(answer)
            myTorF = false
            database.child("time").child(roomId).child(playerUniqueId).child(roundNumber.toString()).child("TorF").ref.setValue(myTorF)
        }
        getScoreDB()
        binding.myselfScore.setText(myScore)
        binding.opponentScore.setText(opponentScore)
        roundNumber++
        /*if(currentQuestionIndex < questions.size-1){
            currentQuestionIndex++
            binding.questionText.postDelayed({displayQuestion()},1000)
        }else{
            showResults()
        }*/
    }
    //還少對方答案是否正確的UI
    private fun mycorrectButtonColors(buttonIndex: String?){
        when(buttonIndex){
            "option1" -> {
                binding.option1.setBackgroundColor(Color.parseColor("#3000e34f"))
                // 設定 option1 按鈕的右邊圖示(左上右下)
                binding.option1.setCompoundDrawablesWithIntrinsicBounds(0,  0, R.drawable.tick,0)
            }
            "option2" -> {
                binding.option2.setBackgroundColor(Color.parseColor("#3000e34f"))
                // 設定 option1 按鈕的右邊圖示(左上右下)
                binding.option2.setCompoundDrawablesWithIntrinsicBounds(0,  0, R.drawable.tick,0)
            }
            "option3" -> {
                binding.option3.setBackgroundColor(Color.parseColor("#3000e34f"))
                // 設定 option1 按鈕的右邊圖示(左上右下)
                binding.option3.setCompoundDrawablesWithIntrinsicBounds(0,  0, R.drawable.tick,0)
            }
            "option4" -> {
                binding.option4.setBackgroundColor(Color.parseColor("#3000e34f"))
                // 設定 option1 按鈕的右邊圖示(左上右下)
                binding.option4.setCompoundDrawablesWithIntrinsicBounds(0,  0, R.drawable.tick,0)
            }
        }
    }

    private fun wrongButtonColors(buttonIndex: String){
        when(buttonIndex){
            "option1" -> {
                binding.option1.setBackgroundColor(Color.parseColor("#30e30000"))
                // 設定 option1 按鈕的右邊圖示(左上右下)
                binding.option1.setCompoundDrawablesWithIntrinsicBounds(0,  0, R.drawable.cross,0)
            }
            "option2" -> {
                binding.option2.setBackgroundColor(Color.parseColor("#30e30000"))
                // 設定 option1 按鈕的右邊圖示(左上右下)
                binding.option2.setCompoundDrawablesWithIntrinsicBounds(0,  0, R.drawable.cross,0)
            }
            "option3" -> {
                binding.option3.setBackgroundColor(Color.parseColor("#30e30000"))
                // 設定 option1 按鈕的右邊圖示(左上右下)
                binding.option3.setCompoundDrawablesWithIntrinsicBounds(0,  0, R.drawable.cross,0)
            }
            "option4" -> {
                binding.option4.setBackgroundColor(Color.parseColor("#30e30000"))
                // 設定 option1 按鈕的右邊圖示(左上右下)
                binding.option4.setCompoundDrawablesWithIntrinsicBounds(0,  0, R.drawable.cross,0)
            }
        }
    }

    private fun resetButtonColors(){ //除了顏色圖案外我好像是要reset所有東西 還缺把題目和文字都清空
        binding.option1.setBackgroundColor(Color.TRANSPARENT)
        binding.option2.setBackgroundColor(Color.TRANSPARENT)
        binding.option3.setBackgroundColor(Color.TRANSPARENT)
        binding.option4.setBackgroundColor(Color.TRANSPARENT)
        binding.option1.setCompoundDrawablesWithIntrinsicBounds(0,  0, 0,0)
        binding.option2.setCompoundDrawablesWithIntrinsicBounds(0,  0, 0,0)
        binding.option3.setCompoundDrawablesWithIntrinsicBounds(0,  0, 0,0)
        binding.option4.setCompoundDrawablesWithIntrinsicBounds(0,  0, 0,0)

        myPlayerTime = 0L
        opponentTime = 0L
        myTorF = null
        opponentTorF = null

        binding.questionImage.setImageDrawable(null)
        binding.option1.setText("")
        binding.option2.setText("")
        binding.option3.setText("")
        binding.option4.setText("")
        binding.questionText.setText("")
    }

    private fun getTimeDB(){
        showPressBar()
        database.child("time").child(roomId).addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // 到時候用for鎖定roomId拿player_time數據
            val getPlayersCount = snapshot.childrenCount.toInt()
            if(getPlayersCount == 2){
                for(players in snapshot.children){
                    if(players.key == playerUniqueId){
                        val getTimeCount = snapshot.child(playerUniqueId).childrenCount.toInt()
                        if(getTimeCount == roundNumber){
                            myPlayerTime = players.child(roundNumber.toString()).child("player_time").getValue(Long::class.java) ?: 0L
                        }

                    }else{
                        val getTimeCount = snapshot.child(playerUniqueId).childrenCount.toInt()
                        if(getTimeCount == roundNumber) {
                            opponentTime = players.child(roundNumber.toString()).child("player_time").getValue(Long::class.java) ?: 0L
                            opponentTorF = players.child(roundNumber.toString()).child("TorF").getValue(Boolean::class.java)!!//布林值解包
                        }
                    }

                }
                // 如果三個數據都已經獲取完成
                if (myPlayerTime != 0L && opponentTime != 0L && opponentTorF != null) {
                    hideProgressBar()
                    database.child("time").child(roomId).removeEventListener(this)
                    Log.d("Firebase", "Listener removed after data retrieval")
                }
            }
            // 因為數字會隨時被更新所以要想一下怎麼宣告
        }

        override fun onCancelled(error: DatabaseError) {
            // 處理取消操作的邏輯
            Toast.makeText(this@GameActivity,"time資料庫有點小問題！！！",Toast.LENGTH_SHORT).show()
            Log.e("time資料庫錯誤:",error.toString())
        }
    })
}

    private fun getScoreDB(){
        database.child("score").child(roomId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 到時候用for鎖定roomId拿player_time數據
                val getPlayersCount = snapshot.childrenCount.toInt()
                if(getPlayersCount == 2){
                    for(players in snapshot.children){
                        if(players.key == playerUniqueId) {
                            val getRoundCount = snapshot.child(playerUniqueId).childrenCount.toInt()
                            if (getRoundCount == roundNumber) {
                                opponentScore =
                                    players.child(roundNumber.toString()).child("player_score")
                                        .getValue(Int::class.java) ?: 0
                                if(getRoundCount == 5){
                                    if(opponentScore < myScore){
                                        database.child("won").child(roomId).child(playerUniqueId).ref.setValue("win")
                                    }else if(opponentScore > myScore){
                                        database.child("won").child(roomId).child(playerUniqueId).ref.setValue("lose")
                                    }else{
                                        database.child("won").child(roomId).child(playerUniqueId).ref.setValue("tie")
                                    }
                                }
                                database.child("time").child(roomId).removeEventListener(this)
                            }
                        }
                    }
                }
                // 因為數字會隨時被更新所以要想一下怎麼宣告
            }

            override fun onCancelled(error: DatabaseError) {
                // 處理取消操作的邏輯
                Toast.makeText(this@GameActivity,"time資料庫有點小問題！！！",Toast.LENGTH_SHORT).show()
                Log.e("time資料庫錯誤:",error.toString())
            }
        })
    }

    /*val wonEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // 處理數據變更的邏輯
        }

        override fun onCancelled(error: DatabaseError) {
            // 處理取消操作的邏輯
        }
    }*/

    override fun onDestroy() { //這個函式是 Activity 的生命週期函式： 當 Activity 被銷毀時（例如用戶退出或系統回收資源），它會被自動調用。
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()  // 確保 MediaPlayer 已初始化且釋放資源
        }
    }
    // 從 SQLite 獲取已登入用戶的 ID
    /*private fun getUserIdFromSQLite():String{
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
    }*/

}