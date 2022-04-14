package com.example.marcello.providers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.marcello.dummypackage.Command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CalendarManager {

    private static final String TAG = "CalendarManager";
    long eID = 0;
    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    private static CalendarManager instance = new CalendarManager();
    private CalendarManager() {

    }
    public static synchronized CalendarManager getInstance(){
        return instance;
    }

    public String insertCalendar(Context context, Command command) throws ParseException {
        long calID = 1;
        long startMillis = 0;
        long endMillis = 0;

        // Data
        Command.Data data = command.getData();

        // Setting up calender parameters
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data.getStartDate()));
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data.getEndDate()));
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, data.getTitle());
        values.put(CalendarContract.Events.DESCRIPTION, data.getDescription());
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, data.getEventTimeZone());
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        eID = eventID;
        Toast.makeText(context, "Event Id = " + eventID, Toast.LENGTH_SHORT).show();
        //
        // ... do something with event ID
        //
        //
    return "تم يا رايق.";
    }

    public String updateCalenderEvent(Context context, Command command) throws ParseException{
        long eventID = eID;
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        Uri updateUri = null;
        values.put(CalendarContract.Events.TITLE, "Kickboxing");

        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        int rows = cr.update(CalendarContract.Events.CONTENT_URI, values,
                CalendarContract.Events.TITLE + " = ?",
                new String[] {"Jazzercise"});
        Log.d(TAG, "updateCalenderEvent: Rows Updated: " + rows);
        return "تم التعديل يا رايق";
    }
    public String deleteEvent(Context context, Command command){
        ContentResolver cr = context.getContentResolver();

        int deletedRows = cr.delete(CalendarContract.Events.CONTENT_URI,
                CalendarContract.Events.TITLE + " = ?",
                new String[] {command.getData().getTitle()});
        Log.d(TAG, "deleteEvent: rows deleted: " + deletedRows);
        return "تم المسح يا رايق";
    }
    public void getEventsOfCalender(Context context, Command command) throws ParseException {
        ContentResolver contentResolver = context.getContentResolver();
        final Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI,
                new String[]{CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME},
                null, null, null);
        Log.d(TAG, "Cals count = " + cursor.getCount());
        Command.Data data = command.getData();
        while (cursor.moveToNext()) {

            String calId = cursor.getString(0);
            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

            Calendar beginTime = Calendar.getInstance();
            beginTime.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data.getStartDate()));
            long startMills = beginTime.getTimeInMillis();

            Calendar endTime = Calendar.getInstance();
            endTime.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data.getEndDate()));
            long endMills = endTime.getTimeInMillis();

            ContentUris.appendId(builder, startMills);
            ContentUris.appendId(builder, endMills);

            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[]{
                            CalendarContract.Instances.TITLE,
                            CalendarContract.Instances.BEGIN,
                            CalendarContract.Instances.END,
                            CalendarContract.Instances.DESCRIPTION,
                            CalendarContract.Instances._ID},
                    CalendarContract.Instances.CALENDAR_ID + " = ?",
                    new String[]{calId}, null);

            Log.d(TAG, "Events Count = " + eventCursor.getCount());

            while (eventCursor.moveToNext()) {
                final String title = eventCursor.getString(0);
                final Date begin = new Date(eventCursor.getLong(1));
                final Date end = new Date(eventCursor.getLong(2));
                final String description = eventCursor.getString(3);
                final String eventID = eventCursor.getString(4);
                Log.d(TAG, "Title: " + title + "\tDescription: " + description + "\tBegin: " + begin + "\tEnd: " + end + "\tEventID: " + eventID);
            }
        }
    }

}
