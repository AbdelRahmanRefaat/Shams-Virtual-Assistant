package com.example.marcello.activities.main;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChatMessagesListAdapter adapter = new ChatMessagesListAdapter();
        RecyclerView recycler =  findViewById(R.id.recycler_view_chat);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // dummy data
        ArrayList<ChatMessage> arr = new ArrayList<>();
        arr.add(new ChatMessage("مرحبا! انا مساعدك الافتراضى.", 2));
        arr.add(new ChatMessage("سوف احاول تلبيه طلباتك.", 2));

        adapter.setList(arr);
        EditText editTextMessage =  findViewById(R.id.edit_text_message);
        ImageButton btnSend =  findViewById(R.id.btn_send_message);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
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