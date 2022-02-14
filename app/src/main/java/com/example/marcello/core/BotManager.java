package com.example.marcello.core;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.marcello.providers.AlarmClockManager;
import com.example.marcello.utils.SimpleTime;
import com.example.marcello.utils.TimeHandler;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotManager {

    private static final String TAG = "BotManager";

    private static BotManager instance =  new BotManager();
    private final AlarmClockManager alarmClockManager =  AlarmClockManager.getInstance();

    private final String REGEX_MATCH_TIME = "(?<hours>\\d{1,2})(:(?<minutes>\\d{1,2}))?\\s*(?<format>[A|P]M)?";

    private BotManager(){}


    public static BotManager getInstance(){
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String dealWith(Context context, String query){

        String result = "امر غير صحيح";
        if(query.contains("منبة") || query.contains("منبه")){
              Pattern pat = Pattern.compile(REGEX_MATCH_TIME, Pattern.CASE_INSENSITIVE);
              Matcher matcher = pat.matcher(query);

              if(!matcher.find()){
                return "امر غير صحيح.";
              }

              SimpleTime simpleTime = TimeHandler.handle(
                      matcher.group("hours"),
                      matcher.group("minutes"),
                      matcher.group("format"));

              if(simpleTime == null){
                  return "برجاء اختيار وقت صحيح.";
              }

              result = alarmClockManager.createAlarmClock(context,
                      simpleTime.getHours(),
                      simpleTime.getMinutes());
        }
        return result;
    }


}
