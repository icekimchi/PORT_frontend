package com.hp028.portpilot;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hp028.portpilot.adapter.ChatRoomAdapter;
import com.hp028.portpilot.api.RetrofitClient;
import com.hp028.portpilot.api.RetrofitService;
import com.hp028.portpilot.api.chat.dto.ChatRoomResponse;
import com.hp028.portpilot.api.member.dto.OAuthLoginResponseDto;
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
    private List<String> chatRoomList;
    private ChatRoomAdapter adapter;
    RetrofitService service = RetrofitClient.getApiService(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatroomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d(TAG, "> ChatRoomActivity");

        setupToolbar("PortMIS", true); //툴바 설정
        chatRoomList = new ArrayList<>();
        adapter = new ChatRoomAdapter(chatRoomList);
        binding.recyclerView.setAdapter(adapter);

        chatRoomList = new ArrayList<>();
        adapter = new ChatRoomAdapter(chatRoomList);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.addChatRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddChatRoomDialog();
            }
        });
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
            //createChatRoom(chatRoomName);
            chatRoomList.add(chatRoomName);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        dialogBinding.cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void createChatRoom(String chatRoomName) {
        service.createChatRoom(chatRoomName).enqueue(new Callback<ChatRoomResponse>() {
            @Override
            public void onResponse(Call<ChatRoomResponse> call, Response<ChatRoomResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChatRoomResponse result = response.body();
                    if (result.getStatus() == 201){
                        chatRoomList.add(chatRoomName);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatRoomResponse> call, Throwable t) {
                Log.e("채팅방 생성 오류 발생", t.getMessage());
            }
        });
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
