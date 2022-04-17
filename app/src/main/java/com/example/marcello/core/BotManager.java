package com.example.marcello.core;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.marcello.api.Command;
import com.example.marcello.providers.AlarmClockManager;
import com.example.marcello.providers.CalendarManager;
import com.example.marcello.providers.ContactManager;
import com.example.marcello.providers.WebSearchManager;
import com.example.marcello.utils.SimpleTime;
import com.example.marcello.utils.TimeHandler;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.N)
public class BotManager {

    private static final String TAG = "BotManager";

    private static BotManager instance =  new BotManager();
    private final AlarmClockManager alarmClockManager =  AlarmClockManager.getInstance();
    private final CalendarManager calendarManager = CalendarManager.getInstance();
    private final ContactManager contactManager = ContactManager.getInstance();
    private final WebSearchManager webSearchManager = WebSearchManager.getInstance();

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
    public void dealWith(Context context, Command command) throws ParseException {
        process(context, command);
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
        }else if(command.getIntent().equals("call contact")){
            result = contactManager.makeACall(context, command);
        }else if(command.getIntent().equals("web search")){
            result = webSearchManager.doSearch(context, command);
        }
        mCommandExecution.onCommandExecutionFinished(result);
        return result;
    }

    public interface ICommandExecution{
        void onCommandExecutionFinished(String message);
    }

}
