package com.example.marcello.providers;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;

import java.util.Calendar;

public class AlarmClockManager {

    private static AlarmClockManager instance = new AlarmClockManager();
    final int [] days = {Calendar.MONDAY, Calendar.SATURDAY, Calendar.SUNDAY, Calendar.THURSDAY, Calendar.WEDNESDAY, Calendar.TUESDAY, Calendar.FRIDAY};
    private AlarmClockManager(){
    }
    public static AlarmClockManager getInstance(){
        return instance;
    }
    public String createAlarmClock(Context context,int hour, int minute){

        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtra(AlarmClock.EXTRA_HOUR,  hour);
        alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        alarmIntent.putExtra(AlarmClock.EXTRA_DAYS, days);
        alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        String result = "تم ضبط المنبه.";
        context.startActivity(alarmIntent);
        return result;
    }
}
