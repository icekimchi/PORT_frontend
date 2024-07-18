package com.hp028.portpilot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.hp028.portpilot.databinding.ActivityChatBinding;
import com.hp028.portpilot.databinding.ToolbarBinding;
import com.hp028.portpilot.fragment.ChatFragment;

public class ChatActivity extends AppCompatActivity {

    private ToolbarBinding toolbar;
    private boolean showMenu;
    private ActivityChatBinding binding;
    private Long roomId;
//    private String roomName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        roomId = intent.getLongExtra("roomId", -1);
        setupToolbar("채팅방", true); // 툴바 설정
//        roomName = intent.getStringExtra("roomName");

        if (roomId == -1) {
            finish();  // roomId가 없으면 액티비티 종료
            return;
        }

//        setTitle(roomName);

        if (savedInstanceState == null) {
            ChatFragment chatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("chatRoomId", roomId);
//            bundle.putString("chatRoomName", roomName);
            chatFragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, chatFragment);
            transaction.commit();
        }
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
