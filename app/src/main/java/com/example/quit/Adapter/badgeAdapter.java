package com.example.quit.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quit.Domain.badgeDomain;
import com.example.quit.R;

import java.util.ArrayList;

public class badgeAdapter extends RecyclerView.Adapter<badgeAdapter.Viewholder> {
    ArrayList<badgeDomain> items;
    Context context;

    public badgeAdapter(ArrayList<badgeDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public badgeAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflator = LayoutInflater.from(parent.getContext()).inflate(R.layout.badge_list,parent,false);
        context = parent.getContext();
        return new Viewholder(inflator);
    }

    @Override
    public void onBindViewHolder(@NonNull badgeAdapter.Viewholder holder, int position) {
        holder.gold_number.setText(items.get(position).getGoldNumber());
        holder.silver_number.setText(items.get(position).getSilverNumber());
        holder.copper_number.setText(items.get(position).getCopperNumber());
        holder.describe.setText(items.get(position).getTitle());
        holder.describe.setText(items.get(position).getDescribe());



        holder.layout.setBackgroundResource(R.drawable.badge_background1);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        TextView gold_number, silver_number, copper_number, title, describe;
        ConstraintLayout layout;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            gold_number = itemView.findViewById(R.id.gold_number_1);
            silver_number = itemView.findViewById(R.id.silver_number_1);
            copper_number = itemView.findViewById(R.id.copper_number_1);
            title = itemView.findViewById(R.id.title);
            describe = itemView.findViewById(R.id.describe_1);

            layout = itemView.findViewById(R.id.badge_constraintlayout);
        }
    }
}
