package com.example.quit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.quit.Adapter.badgeAdapter;
import com.example.quit.Domain.badgeDomain;
import com.google.android.material.badge.BadgeDrawable;

import java.util.ArrayList;

public class BadgeListActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapterBadgeList;
    private RecyclerView recyclerViewBadge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_list);

        initRecyclerView();
    }

    private void initRecyclerView() {
        ArrayList<badgeDomain> items = new ArrayList<>();
        items.add(new badgeDomain(1,2,3,"ABC","aaaaabbbbbbbbbccccccccccdddddddd"));
        items.add(new badgeDomain(1,2,3,"DEF","deeeeeeeeeffffff"));
        items.add(new badgeDomain(1,2,3,"GHI","ggggghhhhhhhiiiiiii"));
        items.add(new badgeDomain(1,2,3,"",""));
        items.add(new badgeDomain(1,2,3,"",""));
        items.add(new badgeDomain(1,2,3,"",""));
        items.add(new badgeDomain(1,2,3,"",""));
        items.add(new badgeDomain(1,2,3,"",""));
        items.add(new badgeDomain(1,2,3,"",""));

        recyclerViewBadge = findViewById(R.id.view);
        recyclerViewBadge.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        adapterBadgeList = new badgeAdapter(items);
        recyclerViewBadge.setAdapter(adapterBadgeList);
    }
}