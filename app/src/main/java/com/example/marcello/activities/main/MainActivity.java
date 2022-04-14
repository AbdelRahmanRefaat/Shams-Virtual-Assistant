package com.example.marcello.activities.main;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
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

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BotManager.ICommandExecution {

    private final String TAG = "MainActivity";
    private final int READ_CALENDAR_REQUEST_CODE = 123;
    private final int WRITE_CALENDAR_REQUEST_CODE = 124;
    private final int READ_CONTACT_REQUEST_CODE = 125;
    private final int WRITE_CONTACT_REQUEST_CODE = 126;

    private final String [] PERMISSIONS = new String[]{
      Manifest.permission.READ_CALENDAR,
      Manifest.permission.WRITE_CALENDAR,
      Manifest.permission.READ_CONTACTS,
      Manifest.permission.WRITE_CONTACTS,
      Manifest.permission.CALL_PHONE
    };

    private BotManager botManager;

    private ActivityResultLauncher<String[]>  permissionsLauncher;


    // widgets
    private ChatMessagesListAdapter messagesAdapter;
    private RecyclerView messagesRecycler;

    // chat array
    private ArrayList<ChatMessage> chatList;


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
        // checking user permissions for Contacts Read & Write !important
//        checkPermission(Manifest.permission.READ_CONTACTS, READ_CONTACT_REQUEST_CODE);
//        checkPermission(Manifest.permission.WRITE_CONTACTS, WRITE_CONTACT_REQUEST_CODE);

        // checking user permissions for Calendar Read & Write !important
//        checkPermission(Manifest.permission.READ_CALENDAR, READ_CALENDAR_REQUEST_CODE);
//        checkPermission(Manifest.permission.WRITE_CALENDAR, WRITE_CALENDAR_REQUEST_CODE);



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
                chatList.add(new ChatMessage(userMsg, 1));
                editTextMessage.setText("");
                try {
                    botManager.dealWith(getApplicationContext() , userMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

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
    public void onCommandExecutionFinished(String message) {
        chatList.add(new ChatMessage(message, 2));
        Log.d(TAG, "BotManager: " + message);
        messagesAdapter.setList(chatList);
    }
//    public void checkPermission(String permission, int requestCode ){
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED){
//            String [] permArray = new String[] {permission};
//            ActivityCompat.requestPermissions(this, permArray, requestCode);
//        }else{
//            Toast.makeText(this, "Permission already Granted", Toast.LENGTH_SHORT).show();
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == READ_CALENDAR_REQUEST_CODE){
//            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(MainActivity.this, "Calendar Read Permission Granted", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(MainActivity.this, "Calendar Read Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }else if(requestCode == WRITE_CALENDAR_REQUEST_CODE){
//            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(MainActivity.this, "Calendar Write Permission Granted", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(MainActivity.this, "Calendar Write Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }else if(requestCode == READ_CONTACT_REQUEST_CODE){
//            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(MainActivity.this, "Contact Read Permission Granted", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(MainActivity.this, "Contact Read Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }else if(requestCode == WRITE_CONTACT_REQUEST_CODE){
//            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(MainActivity.this, "Contact Write Permission Granted", Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(MainActivity.this, "Contact Write Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}