package com.hp028.portpilot;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hp028.portpilot.adapter.ChatRoomAdapter;
import com.hp028.portpilot.api.RetrofitClient;
import com.hp028.portpilot.api.RetrofitService;
import com.hp028.portpilot.api.chat.dto.ChatMessageDto;
import com.hp028.portpilot.api.chat.dto.CreateChatRoomResponse;
import com.hp028.portpilot.api.chat.dto.ChatRoomWithLastMessageResponse;
import com.hp028.portpilot.api.chat.dto.GetChatRoomResponse;
import com.hp028.portpilot.api.common.ApiResponse;
import com.hp028.portpilot.databinding.ActivityChatroomBinding;
import com.hp028.portpilot.databinding.DialogAddChatRoomBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomActivity extends ToolBarActivity {
    private ActivityChatroomBinding binding;
    private static final String TAG = "ChatRoomActivity";
    private List<ChatRoomWithLastMessageResponse.ChatRoomResponse> chatRoomList;
    private List<ChatMessageDto> lastMessagelist;
    private ChatRoomAdapter adapter;
    private RetrofitService service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatroomBinding.inflate(getLayoutInflater());
        service = RetrofitClient.getApiService(this);
        setContentView(binding.getRoot());
        Log.d(TAG, "> ChatRoomActivity");

        setupToolbar("채팅방", false); // 툴바 설정


        // 상태 바만 투명하게 설정
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // 상태 바만 투명하게 설정하고 내비게이션 바는 그대로 유지
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        chatRoomList = new ArrayList<>();
        lastMessagelist = new ArrayList<>();
        adapter = new ChatRoomAdapter(chatRoomList, lastMessagelist, this::onChatRoomSelected);
        adapter.setOnChatRoomLongClickListener(this::onChatRoomLongClick);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // VerticalSpaceItemDecoration 추가
        int spaceHeight = getResources().getDimensionPixelSize(R.dimen.recycler_view_item_space); // dimens.xml에서 값 가져오기
        binding.recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(spaceHeight));

        binding.addChatRoomButton.setOnClickListener(v -> showAddChatRoomDialog());

        // 채팅방 목록을 서버에서 가져와서 설정하는 함수 호출
        fetchChatRooms();
    }

    private void showAddChatRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogAddChatRoomBinding dialogBinding = DialogAddChatRoomBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        AlertDialog dialog = builder.create();

        dialogBinding.checkButton.setOnClickListener(v -> {
            String chatRoomName = dialogBinding.edtTitle.getText().toString().trim();
            if (chatRoomName.isEmpty()) {
                chatRoomName = "새 채팅방";
            }
            createChatRoom(chatRoomName);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void createChatRoom(String chatRoomName) {
        service.createChatRoom(chatRoomName).enqueue(new Callback<CreateChatRoomResponse>() {
            @Override
            public void onResponse(Call<CreateChatRoomResponse> call, Response<CreateChatRoomResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreateChatRoomResponse result = response.body();
                    if (result.getStatus() == 201) {
                        // 새로 생성된 채팅방을 리스트에 추가
                        CreateChatRoomResponse.ChatRoomResponseBody newChatRoom = result.getBody();

                        // GetChatRoomResponseBody와 호환되는 형태로 변환
                        ChatRoomWithLastMessageResponse.ChatRoomResponse newChatRoomResponse = new ChatRoomWithLastMessageResponse.ChatRoomResponse();
                        newChatRoomResponse.setId(newChatRoom.getId());
                        newChatRoomResponse.setRoomName(newChatRoom.getRoomName());

                        chatRoomList.add(newChatRoomResponse);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateChatRoomResponse> call, Throwable t) {
                Log.e("채팅방 생성 오류 발생", t.getMessage());
            }
        });
    }

    private void fetchChatRooms() {
        service.getChatRoom().enqueue(new Callback<ApiResponse<GetChatRoomResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<GetChatRoomResponse>> call, Response<ApiResponse<GetChatRoomResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    GetChatRoomResponse chatRoomResponse = response.body().getBody();
                    lastMessagelist.clear();
                    chatRoomList.clear();
                    for (ChatRoomWithLastMessageResponse chatRoomWithLastMessage : chatRoomResponse.getChatRooms()) {
                        chatRoomList.add(chatRoomWithLastMessage.getChatRoom());
                        lastMessagelist.add(chatRoomWithLastMessage.getLastMessage());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GetChatRoomResponse>> call, Throwable t) {
                Log.e(TAG, "채팅방 목록 불러오기 오류 발생");
            }
        });
    }


    // 채팅방 클릭 시 해당 채팅방의 roomId를 사용하여 ChatActivity로 이동
    private void onChatRoomSelected(ChatRoomWithLastMessageResponse.ChatRoomResponse chatRoom) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("roomId", chatRoom.getId());
        intent.putExtra("roomName", chatRoom.getRoomName());
        startActivity(intent);
    }

    private boolean onChatRoomLongClick(ChatRoomWithLastMessageResponse.ChatRoomResponse chatRoom, int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_chat_summary, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextView summaryTitle = bottomSheetView.findViewById(R.id.summaryTitle);
        TextView summaryContent = bottomSheetView.findViewById(R.id.summaryContent);

        summaryTitle.setText(chatRoom.getRoomName() + " 요약");
        summaryContent.setText("여기에 채팅방 요약 내용이 들어갑니다.");

        bottomSheetDialog.show();
        return true;
    }
}
