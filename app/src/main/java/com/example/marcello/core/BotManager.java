package com.example.marcello.core;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.marcello.dummypackage.ApiInterface;
import com.example.marcello.dummypackage.Command;
import com.example.marcello.providers.AlarmClockManager;
import com.example.marcello.providers.CalendarManager;
import com.example.marcello.providers.ContactManager;
import com.example.marcello.utils.SimpleTime;
import com.example.marcello.utils.TimeHandler;

import org.json.JSONException;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RequiresApi(api = Build.VERSION_CODES.N)
public class BotManager {

    private static final String TAG = "BotManager";

    private static BotManager instance =  new BotManager();
    private final AlarmClockManager alarmClockManager =  AlarmClockManager.getInstance();
    private final CalendarManager calendarManager = CalendarManager.getInstance();
    private final ContactManager contactManager = ContactManager.getInstance();

    private final String REGEX_MATCH_TIME = "(?<hours>\\d{1,2})(:(?<minutes>\\d{1,2}))?\\s*(?<format>[A|P]M)?";

    // CallBack interfaces
    private ICommandExecution mCommandExecution;
    public void setICommandExecution(ICommandExecution commandExecution){
        this.mCommandExecution = commandExecution;
    }

    private BotManager(){}


    public static synchronized BotManager getInstance(){
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void dealWith(Context context, String query) throws JSONException {

        String result = "امر غير صحيح";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.8:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
//        Call<Command> call = apiInterface.createCalendar();
//        Call<Command> call = apiInterface.getEvents();
//        Call<Command> call = apiInterface.updateEvent();
//        Call<Command> call = apiInterface.readContacts();
//        Call<Command> call = apiInterface.addContact();
//        Call<Command> call = apiInterface.deleteContact();
        Call<Command> call = apiInterface.makeCall();

        call.enqueue(new Callback<Command>() {
            @Override
            public void onResponse(Call<Command> call, Response<Command> response) {
                Log.d(TAG, "onResponse: Calling API was a success.");
                try {
                    process(context, response.body());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Command> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String process(Context context, Command command) throws ParseException {

        String query = "", result = "failed";
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
        }else if(command.getIntent().equals("create_calendar")){
            result =  calendarManager.insertCalendar(context, command);
        }else if (command.getIntent().equals("update_calendar")) {
            result = calendarManager.updateCalenderEvent(context, command);
        } else if(command.getIntent().equals("read_calendar")){
            calendarManager.getEventsOfCalender(context, command);
        }else if(command.getIntent().equals("read_contacts")){
            result = contactManager.readContacts(context);
        }else if(command.getIntent().equals("add_contact")){
            result = contactManager.addContact(context, command);
        }else if(command.getIntent().equals("delete_contact")){
            result = contactManager.deleteContact(context, command);
        }else if(command.getIntent().equals("make_call")){
            result = contactManager.makeACall(context, command);
        }
        mCommandExecution.onCommandExecutionFinished(result);
        return result;
    }

    public interface ICommandExecution{
        void onCommandExecutionFinished(String message);
    }

}
