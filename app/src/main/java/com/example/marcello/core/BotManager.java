package com.example.marcello.core;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;

import com.example.marcello.providers.AlarmClockManager;
import com.example.marcello.utils.TimeHandler;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotManager {

    private static BotManager instance =  new BotManager();
    private final String REGEX_MATCH_TIME = "(?<hours>\\d{1,2}):(?<minutes>\\d{1,2})\\s*(?<format>[A|P]M)?";

    private BotManager(){}

    private static final String TAG = "BotManager";
    public static BotManager getInstance(){
        return instance;
    }

    public String dealWith(Context context, String query){

        String result = "امر غير صحيح";
        if(query.contains("منبة") || query.contains("منبه")){
              Pattern mypattern = Pattern.compile(REGEX_MATCH_TIME, Pattern.CASE_INSENSITIVE);
              Matcher mymatcher = mypattern.matcher(query);

              if(!mymatcher.find()){
                return "امر غير صحيح.";
              }

              int hours = Integer.parseInt(mymatcher.group("hours"));
              int minutes = Integer.parseInt(mymatcher.group("minutes"));
              String format = mymatcher.group("format");
              Log.d(TAG, "dealWith: " + TimeHandler.handle(hours, minutes, format));
              result = AlarmClockManager
                      .getInstance()
                      .createAlarmClock(context, hours, minutes);
        }
        return result;
    }


}
