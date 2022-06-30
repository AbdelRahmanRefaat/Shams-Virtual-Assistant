package com.example.marcello.activities.main;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.marcello.models.MessageType;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BotManager.ICommandExecution,
        DialogManager.IDialogStatus {

    private final String TAG = "MainActivity";

    private final String [] PERMISSIONS = new String[]{
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.READ_CALENDAR,
      Manifest.permission.WRITE_CALENDAR,
      Manifest.permission.READ_CONTACTS,
      Manifest.permission.WRITE_CONTACTS,
      Manifest.permission.CALL_PHONE,
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.READ_SMS,
      Manifest.permission.RECEIVE_SMS,
      Manifest.permission.READ_PHONE_STATE
    };


    private boolean isOpenDialog = false;
    private boolean isRecording = false;

    private BotManager botManager;
    private DialogManager dialogManager;
    private ActivityResultLauncher<String[]>  permissionsLauncher;


    // widgets
    private ChatMessagesListAdapter messagesAdapter;
    private RecyclerView messagesRecycler;

    // chat array
    private ArrayList<Message> chatList;
    // password: 01150581050


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // set logo if the app as a round image
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_sona);
        Drawable drawable = new BitmapDrawable(getResources(), createCircleBitmap(sourceBitmap));
        getSupportActionBar().setIcon(drawable);


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
        chatList.add(new Message("مرحبا! انا مساعدك الافتراضى.", Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        chatList.add(new Message("سوف احاول تلبيه طلباتك.", Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        messagesAdapter.setList(chatList);

        ImageButton btnSend =  findViewById(R.id.btn_send_message);
        EditText editTextMessage =  findViewById(R.id.edit_text_message);
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.length() == 0){
                        btnSend.setImageResource(R.drawable.ic_baseline_mic_24);
                    }else{
                        btnSend.setImageResource(R.drawable.ic_send_message);
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                    final String userMsg = editTextMessage.getText().toString();
                    if (userMsg.equals("") && !isRecording) { // start recording
                        isRecording = true;
                        btnSend.setImageResource(R.drawable.ic_baseline_stop_24);
                        Log.d(TAG, "RecordingManager:  started recording...");
                        RecordingManager.getInstance().startRecording();
                        editTextMessage.setEnabled(false);
                    }else if(userMsg.equals("") && isRecording){ // stop recording
                        Log.d(TAG, "RecordingManager: stopped recording.");
                        RecordingManager.getInstance().stopRecording();
                        btnSend.setImageResource(R.drawable.ic_baseline_mic_24);
                        editTextMessage.setEnabled(true);
                        uploadRecordToBeProcessed();
                    }else {
                        chatList.add(new Message(userMsg, Message.MESSAGE_SENDER_USER, MessageType.TEXT));
                        editTextMessage.setText("");
                        uploadTextQueryToBeProcessed(userMsg);
                        btnSend.setImageResource(R.drawable.ic_baseline_mic_24);
                    }

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
    public void onCommandExecutionFinished(Message message) {
//        chatList.add(new Message(message, Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        chatList.add(message);
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
    public void onMessageReceived(Message message) {
//        chatList.add(new Message(message, Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        chatList.add(message);
        messagesAdapter.setList(chatList);
    }


    public Bitmap createCircleBitmap(Bitmap bitmapimg){
        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                bitmapimg.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmapimg.getWidth() / 2,
                bitmapimg.getHeight() / 2, bitmapimg.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;
    }

}

