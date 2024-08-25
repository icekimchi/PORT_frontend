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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private static final String TAG = "ChatFragment";
    private RetrofitService service;
    private long chatRoomId;
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
        }

        if (chatRoomId == -1) {
            Toast.makeText(getContext(), "채팅방 ID가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            getActivity().finish();  // chatRoomId가 없으면 액티비티 종료
            return binding.getRoot();
        }

        // 채팅 메시지 리스트 초기화
        chatMessages = new ArrayList<>();
        chatMessageAdapter = new ChatMessageAdapter(chatMessages);

        // RecyclerView 설정
        setupRecyclerView();

        // 전송 버튼 설정
        setupSendButton();

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

                    for (ChatMessageDto list : dtoList) {
                        Log.d(TAG, list.getChatMessage());
                    }

                    // 어댑터에 변경사항 알리기
                    chatMessageAdapter.notifyDataSetChanged();
                    binding.rvChatMessages.scrollToPosition(chatMessageAdapter.getItemCount() - 1);
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
        binding.etMessageInput.setText("");
        // 현재 시간을 가져와서 문자열로 변환 (yyyy-MM-ddTHH:mm:ss 형식 사용)
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String formattedTime = dateFormatter.format(new Date());

        // SendChatMessageRequest 객체 생성 및 요청 설정
        SendChatMessageRequest request = new SendChatMessageRequest();
        request.setChatMessage(message);
        request.setChatRoomId(chatRoomId);

        // 사용자가 보낸 메시지를 채팅에 추가
        ChatMessageDto userMessage = new ChatMessageDto(
                message,
                SenderType.USER,
                formattedTime); // 포맷된 시간을 사용
        chatMessageAdapter.addMessage(userMessage);

        // "..." 표시를 서버 응답 전까지 보여줌
        ChatMessageDto waitingMessage = new ChatMessageDto(
                "...",
                SenderType.SERVER,
                formattedTime); // Date를 포맷한 시간을 사용
        chatMessageAdapter.addMessage(waitingMessage);

        // 리사이클러뷰를 최신 메시지로 스크롤
        binding.rvChatMessages.scrollToPosition(chatMessageAdapter.getItemCount() - 1);

        // 서버에 메시지 전송 요청
        service.sendChatMessage(request).enqueue(new Callback<ApiResponse<SendChatMessageResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SendChatMessageResponse>> call, Response<ApiResponse<SendChatMessageResponse>> response) {
                chatMessageAdapter.removeMessage(waitingMessage);

                if (response.isSuccessful() && response.body() != null) {
                    SendChatMessageResponse sendChatMessageResponse = response.body().getBody();
                    Log.d(TAG, "응답 :" + sendChatMessageResponse);

                    // 메시지 전송 성공 처리
                    ChatMessageDto serverMessage = new ChatMessageDto(
                            sendChatMessageResponse.getServerMessage(),
                            SenderType.SERVER,
                            sendChatMessageResponse.getServerMessageTime());
                    chatMessageAdapter.addMessage(serverMessage);

                    // 입력창 초기화

                    // 리사이클러뷰를 최신 메시지로 스크롤
                    binding.rvChatMessages.scrollToPosition(chatMessages.size() - 1);
                } else {
                    Toast.makeText(getContext(), "메시지 전송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SendChatMessageResponse>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                chatMessageAdapter.removeMessage(waitingMessage);
                binding.etMessageInput.setText("");
                Toast.makeText(getContext(), "메시지 전송 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupRecyclerView() {
        binding.rvChatMessages.setAdapter(chatMessageAdapter);  // chatMessageAdapter를 RecyclerView에 연결
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
}
