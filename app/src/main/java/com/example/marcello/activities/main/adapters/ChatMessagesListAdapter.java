package com.example.marcello.activities.main.adapters;

import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marcello.models.ChatMessage;
import com.example.marcello.R;
import com.example.marcello.models.Message;

import java.util.ArrayList;
import com.example.marcello.models.MessageType;

public class ChatMessagesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Message> chatMessages = new ArrayList<Message>();
    private final int VIEW_TYPE_USER_MESSAGE = 1;
    private final int VIEW_TYPE_BOT_MESSAGE = 2;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewTypeSenderId) {
        int senderId = viewTypeSenderId % 10;
        int viewType = viewTypeSenderId / 10;
        if(senderId == VIEW_TYPE_USER_MESSAGE){
            return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_user_side,parent,false));
        }else if(senderId == VIEW_TYPE_BOT_MESSAGE){
            switch (viewType){
                case MessageType.CONTACT_ADD:
                    return new ContactAddViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.add_contact,parent,false));
                case MessageType.CALENDAR_NEW:
                    return new CalendarAddViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar, parent, false));
                default:
                    return new BotViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_bot_side,parent,false));

            }
        }else {
            return null;
        }

//        if(viewType == VIEW_TYPE_USER_MESSAGE){
//            return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_user_side,parent,false));
//        }else if(viewType == VIEW_TYPE_BOT_MESSAGE){
//            return new BotViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_bot_side,parent,false));
//        }else{
//            return null;
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int senderId = chatMessages.get(position).getMessageSender();
        if(senderId == VIEW_TYPE_USER_MESSAGE){
            UserViewHolder userViewHolder = (UserViewHolder) holder;
            userViewHolder.textMessage.setText(chatMessages.get(position).getMessageText());
        }else if(senderId == VIEW_TYPE_BOT_MESSAGE){
            switch (chatMessages.get(position).getMessageType()){
                case MessageType.CONTACT_ADD:
                    ContactAddViewHolder contactAddViewHolder = (ContactAddViewHolder) holder;
                    contactAddViewHolder.addedName.setText("ahmed added");
                    break;
                case MessageType.CALENDAR_NEW:
                    CalendarAddViewHolder calendarAddViewHolder = (CalendarAddViewHolder) holder;
                    calendarAddViewHolder.eventName.setText("Bola Bola Event");
                    calendarAddViewHolder.eventDate.setText("2022-7-3");
                    calendarAddViewHolder.eventTime.setText("06:00 PM");
                    break;
                default:
                    BotViewHolder botViewHolder = (BotViewHolder) holder;
                    botViewHolder.textMessage.setText(chatMessages.get(position).getMessageText());
                    break;

            }

        }

    }

    @Override
    public int getItemViewType(int position) {
        int senderId = chatMessages.get(position).getMessageSender();
        return (chatMessages.get(position).getMessageType() * 10 + senderId);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void setList(ArrayList<Message> chatMessages){
        this.chatMessages = chatMessages;
        notifyDataSetChanged();
    }
    public class BotViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }
    public class CalendarAddViewHolder extends RecyclerView.ViewHolder{
        TextView eventName, eventDate, eventTime;
        public CalendarAddViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
            eventTime = itemView.findViewById(R.id.event_time);
        }
    }
    public class ContactAddViewHolder extends RecyclerView.ViewHolder{
        TextView addedName;
        public ContactAddViewHolder(@NonNull View itemView) {
            super(itemView);
            addedName = itemView.findViewById(R.id.addContactTv);
        }
    }
    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView textMessage;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }
}
