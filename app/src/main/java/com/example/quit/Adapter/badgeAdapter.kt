package com.example.quit.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quit.Domain.badgeDomain
import com.example.quit.R

class badgeAdapter : RecyclerView.Adapter<badgeAdapter.ViewHolder>() {

    //獎章清單??
    private val badgeList = ArrayList<badgeDomain>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //實現在哪裡
        val inflator = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.badge_list,parent,false)
        return ViewHolder(inflator)
    }

    override fun getItemCount(): Int {
        return badgeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentitem = badgeList[position]

        holder.title.text = currentitem.title
        holder.describe.text = currentitem.describe
        holder.gold_number.text = currentitem.goldNumber
        holder.silver_number.text = currentitem.silverNumber
        holder.copper_number.text = currentitem.copperNumber
    }

    fun updateBadgeList(badgeList: List<badgeDomain>){
        this.badgeList.clear()
        this.badgeList.addAll(badgeList)
        notifyDataSetChanged()
    }

    //連結示圖中要顯示的地方
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val gold_number : TextView = itemView.findViewById(R.id.gold_number_1);
        val silver_number : TextView = itemView.findViewById(R.id.silver_number_1);
        val copper_number : TextView = itemView.findViewById(R.id.copper_number_1);
        val title : TextView = itemView.findViewById(R.id.title);
        val describe : TextView = itemView.findViewById(R.id.describe_1);
    }
}