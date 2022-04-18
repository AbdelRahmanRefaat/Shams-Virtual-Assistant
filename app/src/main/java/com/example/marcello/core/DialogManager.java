package com.example.marcello.core;


import android.annotation.SuppressLint;
import android.content.Context;

import com.example.marcello.providers.Requirements.ContactRequirements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialogManager {

    private static final String TAG = "DialogManager";
    private final ArrayList<String> omitList = new ArrayList<String>()
    {
        {
            add("intent");
        }
    };

    private ArrayList<String> requiredData;
    private ArrayList<String> requiredMessages;
    private HashMap<Object, Object> mData;
    private IDialogStatus iDialogStatus;
    private Context mContext;

    private IDialogResult dialogResult;

    @SuppressLint("StaticFieldLeak")
    private static DialogManager instance = null;

    private DialogManager(){}
    public static synchronized DialogManager getInstance(){
        if(instance == null){
            instance = new DialogManager();
        }
        return instance;
    }

    /*
    * @Params: Json Object to extract the basic
    * requirements to finish a certain task
    * */
    private void prepare(HashMap<Object, Object> data) {


        for(Map.Entry<Object, Object> i : data.entrySet()){
            if(omitList.contains(i.getKey().toString())) {
                mData.put(i.getKey(), i.getValue());
                continue;
            }
            if(requiredData.contains(i.getKey().toString()) && i.getValue() != null){
                mData.put(i.getKey(), i.getValue());
                int index = requiredData.indexOf(i.getKey().toString());
                requiredData.remove(index);
                requiredMessages.remove(index);
            }
        }
    }

    /*
    * checks if there still base requirements that haven't been fulfilled and asks them
    * */
    private void requestRequiredData(){
        if(allFulfilled()){
            finish(mContext, mData);
            return ;
        }
        iDialogStatus.onMessageReceived("Please provide " + requiredMessages.get(0));
    }
    /*
    * a way for the user to fulfill the requirements with the dialog manager
    * @logic:
    * -add user's message as it fulfilled a certain requirement
    * -remove that requirement and ask again
    * */
    public void sendMessage(String message){
        mData.put(requiredData.get(0), message);
        requiredData.remove(0);
        requiredMessages.remove(0);
        requestRequiredData();
    }
    /*
    * @Context: for sending it back to the finish method to send results to the BotManager to execute task
    * @Data: the Json data retrieved from server
    * */
    public void start(Context context, HashMap<Object,Object> data,
                      ArrayList<String> REQUIREMENTS, ArrayList<String> MESSAGES){
        requiredData = new ArrayList<>(REQUIREMENTS);
        requiredMessages = new ArrayList<>(MESSAGES);
        this.iDialogStatus.onDialogStarted();
        this.mData = new HashMap<>();
        this.mContext = context;
        prepare(data);
        requestRequiredData();
    }
    /*
    * @Usage: alert that the user has done with this dialog and sends the dialog results
    * to BotManager to execute it
    * */
    private void finish(Context context, HashMap<Object, Object> data){
        this.iDialogStatus.onDialogEnded(); // alert that this dialog has ended
        this.dialogResult.onDialogResults(context, data); // send results to execute the command

    }
    private void askForConfirmation(){}
    private void cancel(){
        this.iDialogStatus.onDialogEnded();
    }
    private boolean allFulfilled(){
        return requiredData.size() == 0;
    }

    public void setIDialogStatus(IDialogStatus dialogStatus){
        this.iDialogStatus = dialogStatus;
    }
    public void setIDialogResult(IDialogResult dialogResult){
        this.dialogResult = dialogResult;
    }
    public interface IDialogStatus{
         void onDialogStarted();
         void onDialogEnded();
         void onMessageReceived(String message);
    }
    public interface IDialogResult{
        void onDialogResults(Context context, HashMap<Object, Object> result);
    }
}
