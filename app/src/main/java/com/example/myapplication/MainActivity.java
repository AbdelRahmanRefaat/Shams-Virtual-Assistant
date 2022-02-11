package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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
        arr.add(new ChatMessageModel("Hello there! I am User", 1));
        arr.add(new ChatMessageModel("HI User! I am your BOT", 2));
        arr.add(new ChatMessageModel("Nice meeting you BOT",1));
        arr.add(new ChatMessageModel("You too User!",2));
        adapter.setList(arr);
        EditText editTextMessage = (EditText) findViewById(R.id.edit_text_message);
        ImageButton btnSend = (ImageButton) findViewById(R.id.btn_send_message);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userMsg = editTextMessage.getText().toString();
                if(userMsg.equals(""))
                    return ;
                arr.add(new ChatMessageModel(userMsg, 1));
                editTextMessage.setText("");
                final String botMsg = "Ok";
                arr.add(new ChatMessageModel(botMsg, 2));
                adapter.setList(arr);

            }
        });

    }
}