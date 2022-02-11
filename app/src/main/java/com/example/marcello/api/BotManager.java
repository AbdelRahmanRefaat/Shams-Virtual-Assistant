package com.example.marcello.api;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BotManager {

    private static Context context;
    private static BotManager instance =  new BotManager();

    private BotManager(){}

    public static BotManager getInstance( Context c){
        context = c;
        return instance;
    }

    public String dealWith(String query){
        String [] params = query.split(" ");
        String result = "Failed";
        if(params[0].equals("alarm")){
            Intent intent = new Intent(context, AlertReciever.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent,0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + 20000,pendingIntent);
            Toast.makeText(context, "Alarm set int 3 secs", Toast.LENGTH_SHORT).show();
            result = "Success";
        }
        return result;
    }
}
