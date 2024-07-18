package com.hp028.portpilot.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hp028.portpilot.R;
import com.hp028.portpilot.api.chat.dto.ChatMessage;
import com.hp028.portpilot.api.chat.dto.SenderType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messages;

    public ChatMessageAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SenderType.USER.ordinal()) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_server_message, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);
        return message.getSenderType().ordinal();
    }

    public void updateMessages(List<ChatMessage> newMessages) {
        messages = newMessages;
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private TextView timeText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.ChatMessage);
            timeText = itemView.findViewById(R.id.TimeStamp);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getChatMessage());
            timeText.setText(formatTimestamp(message.getTimestamp()));
        }

        private String formatTimestamp(LocalDateTime timestamp) {
            DateTimeFormatter formatter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("HH:mm");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return timestamp.format(formatter);
            }
            return "시간 에러";
        }
    }
}