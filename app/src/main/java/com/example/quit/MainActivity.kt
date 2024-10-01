package com.example.quit
import android.util.Log
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.example.quit.databinding.ActionBarBinding
import com.example.quit.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MainActivity : AppCompatActivity() {
    private lateinit var bottomBinding: ActivityMainBinding
    private lateinit var actionBarBinding: ActionBarBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var dialog: Dialog
    private lateinit var uid:String
    private lateinit var user:UserData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bottomBinding.root)

        actionBarBinding = ActionBarBinding.inflate(layoutInflater)
        val actionBar = supportActionBar
        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.customView = actionBarBinding.root

        replacefragment(Home())

        bottomBinding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replacefragment(Home())
                R.id.badge -> replacefragment(Badge())
                R.id.rank -> replacefragment(Rank())
                R.id.profile -> replacefragment(Profile())
                else ->{

                }
            }
            true
        }

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        database = FirebaseDatabase.getInstance().getReference("Users")
        if(uid.isNotEmpty()){
            getUserData()
        }

    }

    private fun getUserData(){
        database.child(uid).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(UserData::class.java)!!
                actionBarBinding.userName.setText(user.name)
                actionBarBinding.heartCount.setText(String.format(user.heart.toString()))
                actionBarBinding.levelNumber.setText(String.format(user.level.toString()))
                actionBarBinding.moneyCount.setText(String.format(user.money.toString()))

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"抓不到資料",Toast.LENGTH_SHORT).show()
            }

        })
    }




    private fun replacefragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }


}