package com.hp028.portpilot.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hp028.portpilot.api.chat.dto.GetChatRoomResponse;
import com.hp028.portpilot.databinding.ItemChatRoomBinding;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private List<GetChatRoomResponse.GetChatRoomResponseBody> chatRoomList;
    private OnChatRoomClickListener listener;

    public ChatRoomAdapter(List<GetChatRoomResponse.GetChatRoomResponseBody> chatRoomList, OnChatRoomClickListener listener) {
        this.chatRoomList = chatRoomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatRoomBinding binding = ItemChatRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatRoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        GetChatRoomResponse.GetChatRoomResponseBody chatRoom = chatRoomList.get(position);
        holder.binding.chatRoomName.setText(chatRoom.getRoomName());
        holder.itemView.setOnClickListener(v -> listener.onChatRoomClick(chatRoom));
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        ItemChatRoomBinding binding;

        public ChatRoomViewHolder(ItemChatRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnChatRoomClickListener {
        void onChatRoomClick(GetChatRoomResponse.GetChatRoomResponseBody chatRoom);
    }
}
