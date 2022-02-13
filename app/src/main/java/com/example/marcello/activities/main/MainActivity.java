package com.example.marcello.activities.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.marcello.core.BotManager;
import com.example.marcello.models.ChatMessage;
import com.example.marcello.activities.main.adapters.ChatMessagesListAdapter;
import com.example.marcello.R;
import com.example.marcello.utils.TimeHandler;

import java.sql.Time;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private final int ALARM_PERMISSION_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChatMessagesListAdapter adapter = new ChatMessagesListAdapter();
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view_chat);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // dummy data
        ArrayList<ChatMessage> arr = new ArrayList<>();
        arr.add(new ChatMessage("Hello there! I am User", 1));
        arr.add(new ChatMessage("HI User! I am your BOT", 2));
        arr.add(new ChatMessage("Nice meeting you BOT",1));
        arr.add(new ChatMessage("You too User!",2));
        adapter.setList(arr);
        EditText editTextMessage =  findViewById(R.id.edit_text_message);
        ImageButton btnSend =  findViewById(R.id.btn_send_message);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userMsg = editTextMessage.getText().toString();
                if(userMsg.equals(""))
                    return ;

                arr.add(new ChatMessage(userMsg, 1));
                editTextMessage.setText("");

                BotManager botManager = BotManager.getInstance();
                final String botMsg = botManager.dealWith(getApplicationContext() , userMsg);
                arr.add(new ChatMessage(botMsg, 2));
                Log.d(TAG, "BotManager: " + botManager.dealWith(getApplicationContext() , userMsg));
                adapter.setList(arr);

            }
        });

    }

}