package com.hp028.portpilot.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hp028.portpilot.adapter.ChatMessageAdapter;
import com.hp028.portpilot.api.RetrofitClient;
import com.hp028.portpilot.api.RetrofitService;
import com.hp028.portpilot.api.chat.dto.SendChatMessageRequest;
import com.hp028.portpilot.databinding.FragmentChatBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private RetrofitService service;

    private long chatRoomId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        service = RetrofitClient.getApiService(getContext());

        Bundle args = getArguments();
        if (args != null) {
            chatRoomId = args.getLong("chatRoomId", -1);
        }

        if (chatRoomId == -1) {
            Toast.makeText(getContext(), "채팅방 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            getActivity().finish();  // chatRoomId가 없으면 액티비티 종료
            return binding.getRoot();
        }

        setupRecyclerView();
        setupSendButton();

        return binding.getRoot();
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
//                sendMessage(message, chatRoomId);
            }
        });
    }

//    private void sendMessage(String message, long chatRoomId) {
//        SendChatMessageRequest request = new SendChatMessageRequest(message, chatRoomId);
//        service.sendAndReceiveMessage(request).enqueue(new Callback<ApiResponse<SendChatMessageResponse>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<SendChatMessageResponse>> call, Response<ApiResponse<SendChatMessageResponse>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    SendChatMessageResponse sendChatMessageResponse = response.body().getData();
//                    // 메시지 전송 성공 처리
//                    chatMessageAdapter.addMessage(sendChatMessageResponse.getUserMessage());
//                    chatMessageAdapter.addMessage(sendChatMessageResponse.getServerMessage());
//                    binding.etMessageInput.setText("");
//                } else {
//                    Toast.makeText(getContext(), "메시지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<SendChatMessageResponse>> call, Throwable t) {
//                Toast.makeText(getContext(), "메시지 전송 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
