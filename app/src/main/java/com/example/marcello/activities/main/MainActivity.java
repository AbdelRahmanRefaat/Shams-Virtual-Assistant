package com.example.marcello.activities.main;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;


import com.example.marcello.core.BotManager;
import com.example.marcello.core.DialogManager;
import com.example.marcello.core.RecordingManager;
import com.example.marcello.api.ApiInterface;
import com.example.marcello.api.Command;
import com.example.marcello.api.RetrofitClient;
import com.example.marcello.models.ChatMessage;
import com.example.marcello.activities.main.adapters.ChatMessagesListAdapter;
import com.example.marcello.R;
import com.example.marcello.models.Message;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BotManager.ICommandExecution,
        DialogManager.IDialogStatus {

    private final String TAG = "MainActivity";

    private final String [] PERMISSIONS = new String[]{
      Manifest.permission.READ_CALENDAR,
      Manifest.permission.WRITE_CALENDAR,
      Manifest.permission.READ_CONTACTS,
      Manifest.permission.WRITE_CONTACTS,
      Manifest.permission.CALL_PHONE,
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE
    };


    private boolean isOpenDialog = false;

    private BotManager botManager;
    private DialogManager dialogManager;
    private ActivityResultLauncher<String[]>  permissionsLauncher;


    // widgets
    private ChatMessagesListAdapter messagesAdapter;
    private RecyclerView messagesRecycler;

    // chat array
    private ArrayList<ChatMessage> chatList;
    // password: 01150581050


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ActivityResultLauncher for requesting multiple permissions
        permissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if(isGranted.containsValue(false)){
                permissionsLauncher.launch(PERMISSIONS);
            }
        });
        askPermissions(permissionsLauncher);

        dialogManager.getInstance().setIDialogStatus(this);

        findViewById(R.id.record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "RecordingManager:  started recording...");
                RecordingManager.getInstance().startRecording();

            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Log.d(TAG, "RecordingManager: stopped recording.");
                RecordingManager.getInstance().stopRecording();
                uploadRecordToBeProcessed();
            }
        });

        // setUp BotManager
        botManager = BotManager.getInstance();
        botManager.setICommandExecution(this);
        // --- ---- --- -- -- --- -
        messagesAdapter = new ChatMessagesListAdapter();
        messagesRecycler =  findViewById(R.id.recycler_view_chat);
        messagesRecycler.setAdapter(messagesAdapter);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(this));

        // init convo with greeting
        chatList= new ArrayList<>();
        chatList.add(new ChatMessage("مرحبا! انا مساعدك الافتراضى.", 2));
        chatList.add(new ChatMessage("سوف احاول تلبيه طلباتك.", 2));
        messagesAdapter.setList(chatList);

        EditText editTextMessage =  findViewById(R.id.edit_text_message);
        ImageButton btnSend =  findViewById(R.id.btn_send_message);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                final String userMsg = editTextMessage.getText().toString();
                if(userMsg.equals(""))
                    return ;
                chatList.add(new ChatMessage(userMsg, Message.MESSAGE_SENDER_USER));
                editTextMessage.setText("");
                uploadTextQueryToBeProcessed(userMsg);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadRecordToBeProcessed(){
        File audioFile = new File(RecordingManager.STORAGE_EXTERNAL_CACHE_DIR + RecordingManager.AUDIO_FILE_NAME);
        // decode audio file to Base64
        byte[] bytes = null;
        try {
          bytes = FileUtils.readFileToByteArray(audioFile);
        }catch (IOException e){
            Log.d(TAG, "uploadRecordToBeProcessed: " + e.getMessage() );
        }
        if(bytes == null) return;
        String encoded = Base64.encodeToString(bytes, 0);
        try {
            botManager.dealWith(getApplicationContext(), encoded, BotManager.QUERY_TYPE_AUDIO);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadTextQueryToBeProcessed(String query){
        try{
            if(isOpenDialog){
                  dialogManager.getInstance().sendMessage(query);
//                botManager.dealWith(getApplicationContext(), query, BotManager.QUERY_TYPE_FILLING_REQUIREMENTS);
            }else{
                botManager.dealWith(getApplicationContext(), query, BotManager.QUERY_TYPE_TEXT);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onCommandExecutionFinished(String message) {
        chatList.add(new ChatMessage(message, 2));
        Log.d(TAG, "BotManager: " + message);
        messagesAdapter.setList(chatList);
    }

    private void askPermissions(ActivityResultLauncher<String[]> permissionsLauncher){
        if(!hasPermissions(PERMISSIONS)) {
            Log.d(TAG, "askPermissions: Some Permissions needs to be granted first.");
            permissionsLauncher.launch(PERMISSIONS);
        }else{
            Log.d(TAG, "askPermissions: All necessary permissions are granted.");
        }
    }
    private boolean hasPermissions(String ... permissions){
        if(permissions != null){
            for(String permission : permissions){
                if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onDialogStarted() {
        this.isOpenDialog = true;
    }

    @Override
    public void onDialogEnded() {
        this.isOpenDialog = false;
        dialogManager = null;
    }

    @Override
    public void onMessageReceived(String message) {
        chatList.add(new ChatMessage(message, Message.MESSAGE_SENDER_BOT));
        messagesAdapter.setList(chatList);
    }


}

