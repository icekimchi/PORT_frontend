package com.hp028.portpilot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hp028.portpilot.adapter.ChatRoomAdapter;
import com.hp028.portpilot.api.RetrofitClient;
import com.hp028.portpilot.api.RetrofitService;
import com.hp028.portpilot.api.chat.dto.CreateChatRoomResponse;
import com.hp028.portpilot.api.chat.dto.GetChatRoomResponse;
import com.hp028.portpilot.databinding.ActivityChatroomBinding;
import com.hp028.portpilot.databinding.DialogAddChatRoomBinding;
import com.hp028.portpilot.databinding.ToolbarBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomActivity extends AppCompatActivity {
    private ActivityChatroomBinding binding;
    private static final String TAG = "ChatRoomActivity";
    private ToolbarBinding toolbar;
    private boolean showMenu;
    private List<GetChatRoomResponse.GetChatRoomResponseBody> chatRoomList;
    private ChatRoomAdapter adapter;
    private RetrofitService service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatroomBinding.inflate(getLayoutInflater());
        service = RetrofitClient.getApiService(this);
        setContentView(binding.getRoot());
        Log.d(TAG, "> ChatRoomActivity");

        setupToolbar("PortMIS", true); // 툴바 설정
        chatRoomList = new ArrayList<>();
        adapter = new ChatRoomAdapter(chatRoomList, this::onChatRoomSelected);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        dialogBinding.cancelButton.setOnClickListener(v -> dialog.dismiss());

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
                        GetChatRoomResponse.GetChatRoomResponseBody newChatRoomResponse = new GetChatRoomResponse.GetChatRoomResponseBody();
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
        service.getChatRoom().enqueue(new Callback<GetChatRoomResponse>() {
            @Override
            public void onResponse(Call<GetChatRoomResponse> call, Response<GetChatRoomResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    chatRoomList.clear();
                    chatRoomList.addAll(response.body().getBody());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GetChatRoomResponse> call, Throwable t) {
                Log.e(TAG, "채팅방 목록 불러오기 오류 발생");
            }
        });
    }


    // 채팅방 클릭 시 해당 채팅방의 roomId를 사용하여 ChatActivity로 이동
    private void onChatRoomSelected(GetChatRoomResponse.GetChatRoomResponseBody chatRoom) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("roomId", chatRoom.getId());
        intent.putExtra("roomName", chatRoom.getRoomName());
        startActivity(intent);
    }

    protected void setupToolbar(String title, boolean showMenu) {
        toolbar = binding.chatroomToolbar;
        if (toolbar != null) {
            setSupportActionBar(toolbar.toolbar);
            TextView toolbarTitle = binding.chatroomToolbar.barFindText;

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            this.showMenu = showMenu;
            if (toolbarTitle != null) {
                toolbarTitle.setText(title);
            }
        }
    }
}
