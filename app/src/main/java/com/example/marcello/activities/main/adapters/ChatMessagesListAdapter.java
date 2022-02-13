package com.example.marcello.activities.main.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marcello.models.ChatMessage;
import com.example.marcello.R;

import java.util.ArrayList;

public class ChatMessagesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
    private final int VIEW_TYPE_USER_MESSAGE = 1;
    private final int VIEW_TYPE_BOT_MESSAGE = 2;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_USER_MESSAGE){
            return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_user_side,parent,false));
        }else if(viewType == VIEW_TYPE_BOT_MESSAGE){
            return new BotViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_bot_side,parent,false));
        }else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(chatMessages.get(position).getId() == VIEW_TYPE_USER_MESSAGE){
            UserViewHolder userViewHolder = (UserViewHolder) holder;
            userViewHolder.textMessage.setText(chatMessages.get(position).getMessage());
        }else if(chatMessages.get(position).getId() == VIEW_TYPE_BOT_MESSAGE){
            BotViewHolder botViewHolder = (BotViewHolder) holder;
            botViewHolder.textMessage.setText(chatMessages.get(position).getMessage());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void setList(ArrayList<ChatMessage> chatMessages){
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
    public class UserViewHolder extends RecyclerView.ViewHolder{
        TextView textMessage;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }
}
