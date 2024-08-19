package com.hp028.portpilot.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hp028.portpilot.adapter.ChatMessageAdapter;
import com.hp028.portpilot.api.RetrofitClient;
import com.hp028.portpilot.api.RetrofitService;
import com.hp028.portpilot.api.chat.dto.ChatMessageDto;
import com.hp028.portpilot.api.chat.dto.GetChatMessagesResponse;
import com.hp028.portpilot.api.chat.dto.SendChatMessageRequest;
import com.hp028.portpilot.api.chat.dto.SendChatMessageResponse;
import com.hp028.portpilot.api.chat.dto.SenderType;
import com.hp028.portpilot.api.common.ApiResponse;
import com.hp028.portpilot.databinding.FragmentChatBinding;
import com.hp028.portpilot.databinding.ToolbarBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {


    private FragmentChatBinding binding;
    private static final String TAG = "ChatFragment";
    private RetrofitService service;
    private long chatRoomId;
    private String chatRoomName;
    private ToolbarBinding toolbar;
    private List<ChatMessageDto> chatMessages;
    private ChatMessageAdapter chatMessageAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        service = RetrofitClient.getApiService(getContext());

        Bundle args = getArguments();
        if (args != null) {
            chatRoomId = args.getLong("chatRoomId", -1);
            chatRoomName = args.getString("chatRoomName", "채팅방");
        }

        if (chatRoomId == -1) {
            Toast.makeText(getContext(), "채팅방 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            getActivity().finish();  // chatRoomId가 없으면 액티비티 종료
            return binding.getRoot();
        }

        setupToolbar();
        setupRecyclerView();
        setupSendButton();

        // Initialize the chatMessages list
        chatMessages = new ArrayList<>();
        chatMessageAdapter = new ChatMessageAdapter(chatMessages);

        // 채팅 기록 불러오기
        loadChatMessages();

        return binding.getRoot();
    }

    private void loadChatMessages() {
        // 서버에서 채팅 기록을 불러오는 로직
        service.getChatMessages(chatRoomId).enqueue(new Callback<GetChatMessagesResponse>() {
            @Override
            public void onResponse(Call<GetChatMessagesResponse> call, Response<GetChatMessagesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    // 기존 채팅 메시지 목록을 지우고 새로 불러온 메시지를 추가
                    chatMessages.clear();

                    // ChatMessageDto를 바로 추가
                    List<ChatMessageDto> dtoList = response.body().getBody();
                    if (dtoList != null) {
                        chatMessages.addAll(dtoList);
                    }

                    // 어댑터에 변경사항 알리기
                    chatMessageAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "채팅 기록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetChatMessagesResponse> call, Throwable t) {
                Log.e("ChatFragment", "Request Failed: " + t.getMessage(), t);
                Toast.makeText(getContext(), "서버와의 통신에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String message, long chatRoomId) {
        // SendChatMessageRequest 객체 생성 및 요청 설정
        SendChatMessageRequest request = new SendChatMessageRequest();
        request.setChatMessage(message);
        request.setChatRoomId(chatRoomId);

        // 서버에 메시지 전송 요청
        service.sendChatMessage(request).enqueue(new Callback<ApiResponse<SendChatMessageResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SendChatMessageResponse>> call, Response<ApiResponse<SendChatMessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SendChatMessageResponse sendChatMessageResponse = response.body().getBody();
                    Log.d(TAG, "응답 :" + sendChatMessageResponse);

                    // 메시지 전송 성공 처리
                    // 서버로부터 반환된 메시지 처리
                    ChatMessageDto userMessage = new ChatMessageDto(
                            sendChatMessageResponse.getUserMessage(),
                            SenderType.USER,
                            sendChatMessageResponse.getUserMessageTime());
                    ChatMessageDto serverMessage = new ChatMessageDto(
                            sendChatMessageResponse.getServerMessage(),
                            SenderType.SERVER,
                            sendChatMessageResponse.getServerMessageTime());

                    if (userMessage != null) {
                        chatMessages.add(userMessage);
                        chatMessageAdapter.notifyItemInserted(chatMessages.size() - 1);
                    }
                    if (serverMessage != null) {
                        chatMessages.add(serverMessage);
                        chatMessageAdapter.notifyItemInserted(chatMessages.size() - 1);
                    }

                    // 입력창 초기화
                    binding.etMessageInput.setText("");

                    // 리사이클러뷰를 최신 메시지로 스크롤
                    binding.rvChatMessages.scrollToPosition(chatMessages.size() - 1);
                } else {
                    Toast.makeText(getContext(), "메시지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SendChatMessageResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "메시지 전송 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupRecyclerView() {
        ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(new ArrayList<>());
        binding.rvChatMessages.setAdapter(chatMessageAdapter);
        binding.rvChatMessages.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupSendButton() {
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.etMessageInput.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(getContext(), "메시지를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getContext(), "메시지 전송", Toast.LENGTH_SHORT).show();
                sendMessage(message, chatRoomId);
            }
        });
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            toolbar = binding.chatroomToolbar;

            activity.setSupportActionBar(toolbar.toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
            TextView toolbarTitle = binding.chatroomToolbar.barFindText;
            if (toolbarTitle != null) {
                toolbarTitle.setText(chatRoomName);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
