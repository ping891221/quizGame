package com.example.quit

import UserDataRepository
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.quit.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storageReference: StorageReference
    private lateinit var dialog: Dialog
    private lateinit var uid:String
    private lateinit var user:UserData

    // 声明 View Binding 变量
    private var _binding: FragmentProfileBinding? = null
    // 仅在访问视图的生命周期时使用
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        UserDataRepository.userDataLiveData.observe(this) { userData ->
            if (userData != null) {
                Glide.with(this).load(UserDataRepository.userData?.sticker).into(binding.profileImage)
                binding.profileName.setText(UserDataRepository.userData?.name)
                binding.goldNumber.setText(UserDataRepository.userData?.bgGAll.toString())
                binding.silverNumber.setText(UserDataRepository.userData?.bgSAll.toString())
                binding.copperNumber.setText(UserDataRepository.userData?.bgCAll.toString())
            } else {
                Toast.makeText(requireContext(), "使用者資料載入失敗", Toast.LENGTH_SHORT).show()
            }
        }
        /*auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()


        if(uid.isNotEmpty()){
            getUserData()
        }*/
    }

    /*private fun getUserData(){
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(uid).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(UserData::class.java)!!
                binding.profileName.setText(UserDataRepository.userData?.name)


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"抓不到資料", Toast.LENGTH_SHORT).show()
            }

        })
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 清除 binding 对象以防止内存泄漏
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}