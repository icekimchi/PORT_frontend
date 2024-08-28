package com.hp028.portpilot.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hp028.portpilot.api.chat.dto.ChatMessageDto;
import com.hp028.portpilot.api.chat.dto.ChatRoomWithLastMessageResponse;
import com.hp028.portpilot.databinding.ItemChatRoomBinding;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private List<ChatRoomWithLastMessageResponse.ChatRoomResponse> chatRoomList;
    private List<ChatMessageDto> lastMessageList;
    private OnChatRoomClickListener listener;
    private OnChatRoomLongClickListener longClickListener;

    public ChatRoomAdapter(List<ChatRoomWithLastMessageResponse.ChatRoomResponse> chatRoomList, List<ChatMessageDto> lastMessageList,  OnChatRoomClickListener listener) {
        this.chatRoomList = chatRoomList;
        this.lastMessageList = lastMessageList;
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
        ChatRoomWithLastMessageResponse.ChatRoomResponse chatRoom = chatRoomList.get(position);
        ChatMessageDto chatMessage = lastMessageList.get(position);

        // 채팅 메시지의 초기값을 빈 문자열로 설정
        String shortMessage = getShortMessage(chatMessage);
        String timeStamp = getTimeStamp(chatMessage);

        holder.binding.chatRoomName.setText(chatRoom.getRoomName());
        holder.binding.lastMessage.setText(shortMessage);
        holder.binding.lastMessageDate.setText(timeStamp);
        holder.itemView.setOnClickListener(v -> listener.onChatRoomClick(chatRoom));
        holder.itemView.setOnLongClickListener(v -> {
            boolean isExpanded = holder.binding.summaryLayout.getVisibility() == View.VISIBLE;
            holder.binding.summaryLayout.setVisibility(isExpanded ? View.GONE : View.VISIBLE);

            if (!isExpanded) {
                holder.binding.summaryTitle.setText(chatRoom.getRoomName() + " 요약");
                holder.binding.summaryContent.setText("여기에 채팅방 요약 내용이 들어갑니다.");
            }

            return true;
        });
    }

    @NonNull
    private static String getShortMessage(ChatMessageDto chatMessage) {
        String shortMessage = "";

        if (chatMessage != null && chatMessage.getChatMessage() != null) {
            // 채팅 메시지를 20자까지 자르고, 그 뒤에 "..."을 추가
            shortMessage = chatMessage.getChatMessage();
            if (shortMessage.length() > 20) {
                shortMessage = shortMessage.substring(0, 20) + "...";
            }
        }
        return shortMessage;
    }

    @NonNull
    private static String getTimeStamp(ChatMessageDto chatMessage) {
        String timestamp = "";

        if (chatMessage != null && chatMessage.getChatMessage() != null) {
            if (chatMessage.getTimestamp() != null) {
                timestamp = chatMessage.getTimestamp().substring(11, 16);
            }
        }
        return timestamp;
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
        void onChatRoomClick(ChatRoomWithLastMessageResponse.ChatRoomResponse chatRoom);
    }

    public interface OnChatRoomLongClickListener {
        boolean onChatRoomLongClick(ChatRoomWithLastMessageResponse.ChatRoomResponse chatRoom, int position);
    }

    public void setOnChatRoomLongClickListener(OnChatRoomLongClickListener listener) {
        this.longClickListener = listener;
    }
}
