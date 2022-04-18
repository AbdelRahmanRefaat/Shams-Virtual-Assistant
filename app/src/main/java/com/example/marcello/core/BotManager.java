package com.example.marcello.core;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.marcello.api.ApiInterface;
import com.example.marcello.api.Command;
import com.example.marcello.api.RetrofitClient;
import com.example.marcello.providers.AlarmClockManager;
import com.example.marcello.providers.CalendarManager;
import com.example.marcello.providers.ContactManager;
import com.example.marcello.providers.Requirements.ContactRequirements;
import com.example.marcello.providers.Requirements.WebSearchRequirements;
import com.example.marcello.providers.WebSearchManager;
import com.example.marcello.utils.SimpleTime;
import com.example.marcello.utils.TimeHandler;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.N)
public class BotManager implements DialogManager.IDialogResult {

    private static final String TAG = "BotManager";
    public static final int QUERY_TYPE_TEXT = 0;
    public static final int QUERY_TYPE_AUDIO = 1;
    public static final int QUERY_TYPE_FILLING_REQUIREMENTS = 2;


    private static BotManager instance =  new BotManager();
    private final AlarmClockManager alarmClockManager =  AlarmClockManager.getInstance();
    private final CalendarManager calendarManager = CalendarManager.getInstance();
    private final ContactManager contactManager = ContactManager.getInstance();
    private final WebSearchManager webSearchManager = WebSearchManager.getInstance();
    private final DialogManager dialogManager = DialogManager.getInstance();

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
    public void dealWith(Context context,String message, int messageType) {
        ApiInterface client = RetrofitClient.getInstance().create(ApiInterface.class);
        HashMap<Object, Object> payload = new HashMap<>();
        Call<HashMap<Object, Object>> call = null;
        payload.put("data", message);
        switch (messageType){
            case QUERY_TYPE_TEXT:
                call = client.makeCall();
                break;
            case QUERY_TYPE_AUDIO:
                call = client.uploadAudio(payload);
                break;

        }

        assert call != null;
        call.enqueue(new Callback<HashMap<Object, Object>>() {
            @Override
            public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                Log.d(TAG, "upload is success.");
                try {
                    process(context, response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {
                Log.d(TAG, "upload failed due to: " + t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String process(Context context, HashMap<Object, Object> command) throws ParseException {

        dialogManager.setIDialogResult(BotManager.this);
        switch (command.get("intent").toString()){
            case "call contact":
                dialogManager.start(context, command,
                        ContactRequirements.CallContact.REQUIREMENTS,
                        ContactRequirements.CallContact.MESSAGES);
                break;
            case "add contact":
                dialogManager.start(context, command,
                        ContactRequirements.AddContact.REQUIREMENTS,
                        ContactRequirements.AddContact.MESSAGES);
                break;
            case "web search":
                dialogManager.start(context,command,
                        WebSearchRequirements.WebSearch.REQUIREMENTS,
                        WebSearchRequirements.WebSearch.MESSAGES);
                break;
        }


//        String query = "", result = "failed";
//        if(query.contains("منبة") || query.contains("منبه")){
//            Pattern pat = Pattern.compile(REGEX_MATCH_TIME, Pattern.CASE_INSENSITIVE);
//            Matcher matcher = pat.matcher(query);
//
//            if(!matcher.find()){
//                return "امر غير صحيح.";
//            }
//            SimpleTime simpleTime = TimeHandler.handle(
//                    matcher.group("hours"),
//                    matcher.group("minutes"),
//                    matcher.group("format"));
//
//            if(simpleTime == null){
//                return "برجاء اختيار وقت صحيح.";
//            }
//
//            result = alarmClockManager.createAlarmClock(context,
//                    simpleTime.getHours(),
//                    simpleTime.getMinutes());
//        }
////        else if(command.get("intent").equals("create_calendar")){
////            result =  calendarManager.insertCalendar(context, command);
////        }else if command.get("intent").equals("update_calendar")) {
////            result = calendarManager.updateCalenderEvent(context, command);
////        } else if(command.get("intent").equals("read_calendar")){
////            calendarManager.getEventsOfCalender(context, command);
////        }else if(command.get("intent").equals("read_contacts")){
////            result = contactManager.readContacts(context);
////        }
//        else if(command.get("intent").equals("add_contact")){
//            result = contactManager.addContact(context, command);
//        }else if(command.get("intent").equals("delete_contact")){
//            result = contactManager.deleteContact(context, command);
//        }else if(command.get("intent").equals("call contact")){
//            result = contactManager.makeACall(context, command);
//        }else if(command.get("intent").equals("web search")){
//            result = webSearchManager.doSearch(context, command);
//        }
//        mCommandExecution.onCommandExecutionFinished(result);
        return "d";
    }

    @Override
    public void onDialogResults(Context context, HashMap<Object, Object> result) {

        if(result.get("intent").equals("add contact")){
              contactManager.addContact(context, result);
        }else if(result.get("intent").equals("delete_contact")){
              contactManager.deleteContact(context, result);
        }else if(result.get("intent").equals("call contact")){
            contactManager.makeACall(context, result);
        }else if(result.get("intent").equals("web search")){
            webSearchManager.doSearch(context, result);
        }
        mCommandExecution.onCommandExecutionFinished("done");
    }

    public interface ICommandExecution{
        void onCommandExecutionFinished(String message);
    }

}
