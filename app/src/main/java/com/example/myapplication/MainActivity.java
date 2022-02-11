package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChatMessagesListAdapter adapter = new ChatMessagesListAdapter();
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view_chat);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        // dummy data
        ArrayList<ChatMessageModel> arr = new ArrayList<>();
        arr.add(new ChatMessageModel("Hello there! I am User Chan", 1));
        arr.add(new ChatMessageModel("HI User Chan! I am your BOT Chan", 2));
        arr.add(new ChatMessageModel("Nice meeting you BOT Chan",1));
        arr.add(new ChatMessageModel("You too User Chan!",2));
        adapter.setList(arr);
    }
}