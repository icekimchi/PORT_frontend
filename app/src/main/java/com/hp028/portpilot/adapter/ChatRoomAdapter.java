package com.hp028.portpilot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hp028.portpilot.databinding.ItemChatRoomBinding;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    private List<String> chatRoomList;

    public ChatRoomAdapter(List<String> chatRoomList) {
        this.chatRoomList = chatRoomList;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemChatRoomBinding binding = ItemChatRoomBinding.inflate(inflater, parent, false);
        return new ChatRoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        String chatRoomName = chatRoomList.get(position);
        holder.binding.chatRoomTitle.setText(chatRoomName);
        holder.binding.chatRoomDescription.setText("채팅 설명"); // 설명은 임의로 설정하세요
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        ItemChatRoomBinding binding;

        public ChatRoomViewHolder(@NonNull ItemChatRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
