package com.example.quit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quit.Adapter.badgeAdapter
import com.example.quit.Domain.badgeViewModel
import com.example.quit.Domain.badgeDomain

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Badge.newInstance] factory method to
 * create an instance of this fragment.
 */
class Badge : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    /*private lateinit var adapterBadgeList: RecyclerView.Adapter<*>*/
    private lateinit var adapterBadgeList: badgeAdapter
    private lateinit var recyclerViewBadge: RecyclerView
    private lateinit var viewModel: badgeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_badge, container, false)
        /*initRecyclerView(view)*/
        return view
    }

    /*private fun initRecyclerView(view: View) {
        val items = ArrayList<badgeDomain>()
        items.add(badgeDomain("","","", "ABC", "aaaaabbbbbbbbbccccccccccdddddddd"))
        items.add(badgeDomain("","","", "DEF", "deeeeeeeeeffffff"))
        items.add(badgeDomain("","","", "GHI", "ggggghhhhhhhiiiiiii"))
        items.add(badgeDomain("","","", "", ""))
        items.add(badgeDomain("","","", "", ""))
        items.add(badgeDomain("","","", "", ""))
        items.add(badgeDomain("","","", "", ""))
        items.add(badgeDomain("","","", "", ""))
        items.add(badgeDomain("","","", "", ""))

        /*FirebaseRecyclerOptions<badgeDomain> options =
                new FirebaseRecyclerOptions.Builder<badgeDomain>()
                    .setQuery(com.google.firebase.database.FirebaseDatabase.getInstance().getReference().child("Badges"),badgeDomain.class)
                    .build();*/

        recyclerViewBadge = view.findViewById(R.id.view)
        recyclerViewBadge.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapterBadgeList = badgeAdapter(items)
        recyclerViewBadge.adapter = adapterBadgeList
    }*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Badge.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Badge().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //引用視圖
        recyclerViewBadge = view.findViewById(R.id.view)
        recyclerViewBadge.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewBadge.setHasFixedSize(true)
        adapterBadgeList = badgeAdapter()
        recyclerViewBadge.adapter = adapterBadgeList
        //創建物件
        viewModel = ViewModelProvider(this).get(badgeViewModel::class.java)

        viewModel.allBadges.observe(viewLifecycleOwner, Observer {
            adapterBadgeList.updateBadgeList(it)
        })
    }
}