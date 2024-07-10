package com.hp028.portpilot;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.hp028.portpilot.api.RetrofitService;
import com.hp028.portpilot.api.member.dto.LoginRequestDto;
import com.hp028.portpilot.api.member.dto.LoginResponseDto;
import com.hp028.portpilot.databinding.ActivityEmailLoginBinding;
import com.hp028.portpilot.databinding.ToolbarBinding;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailLoginActivity extends AppCompatActivity {

    private ActivityEmailLoginBinding binding;
    private ToolbarBinding toolbar;
    private TextInputLayout til_user_name, til_user_email, til_user_password;
    private EditText ed_user_name, ed_user_email, ed_user_password;
    private TextView btn_start;
    private boolean showMenu;
    private RetrofitService service;

    private static final String NAME_PATTERN = "^[가-힣]{2,10}$"; // 이름 패턴 (한글만, 2글자 이상 10글자 이하)
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$"; // 이메일 패턴
    private static final String PASSWORD_PATTERN = "^.{8,20}$";// 비밀번호 패턴 (8글자 이상 20글자 이하)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar("회원가입", false); //툴바 설정

        til_user_name = binding.tilUserName;
        til_user_email = binding.tilUserEmail;
        til_user_password = binding.tilUserPassword;

        ed_user_name = binding.edUserName;
        ed_user_email = binding.edUserEmail;
        ed_user_password = binding.edUserPassword;


        btn_start = binding.loginButton;
        btn_start.setOnClickListener(v -> validateAndLogin());
    }

    private void validateAndLogin() {
        boolean isValid = true;

        if (!validateName()) {
            isValid = false;
            ed_user_name.setActivated(true);
        } else {
            ed_user_name.setActivated(false);
        }

        if (!validateEmail()) {
            isValid = false;
            ed_user_email.setActivated(true);
        } else {
            ed_user_email.setActivated(false);
        }

        if (!validatePassword()) {
            isValid = false;
            ed_user_password.setActivated(true);
        } else {
            ed_user_password.setActivated(false);
        }

        if (isValid) {
            // 모든 유효성 검사를 통과했을 때 로그인 또는 다음 단계로 진행
            // TODO: 로그인 로직 구현
        }
    }

    private void Login(LoginRequestDto data){
        service.memberLogin(data).enqueue(new Callback<LoginResponseDto>() {
            @Override
            public void onResponse(Call<LoginResponseDto> call, Response<LoginResponseDto> response) {
                LoginResponseDto result = response.body();
            }

            @Override
            public void onFailure(Call<LoginResponseDto> call, Throwable t) {
                Toast.makeText(EmailLoginActivity.this, "회원가입 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 에러 발생", t.getMessage());
            }
        });
    }

    private boolean validateName() {
        String name = ed_user_name.getText().toString().trim();
        if (name.isEmpty()) {
            til_user_name.setError("이름을 입력해주세요.");
            return false;
        } else if (!Pattern.matches(NAME_PATTERN, name)) {
            til_user_name.setError("이름은 2글자 이상 10글자 이하의 한글만 입력해야 합니다.");
            return false;
        }
        til_user_name.setError(null);
        return true;
    }

    private boolean validateEmail() {
        String email = ed_user_email.getText().toString().trim();
        if (email.isEmpty()) {
            til_user_email.setError("이메일을 입력해주세요.");
            return false;
        } else if (!Pattern.matches(EMAIL_PATTERN, email)) {
            til_user_email.setError("올바른 이메일 형식이 아닙니다.");
            return false;
        }
        til_user_email.setError(null);
        return true;
    }

    private boolean validatePassword() {
        String password = ed_user_password.getText().toString();
        if (password.isEmpty()) {
            til_user_password.setError("비밀번호를 입력해주세요.");
            return false;
        } else if (!Pattern.matches(PASSWORD_PATTERN, password)) {
            til_user_password.setError("비밀번호는 8글자 이상 20글자 이하여야 합니다.");
            return false;
        }
        til_user_password.setError(null);
        return true;
    }

    protected void setupToolbar(String title, boolean showMenu) {
        toolbar = binding.signupToolbar;
        if (toolbar != null) {
            setSupportActionBar(toolbar.toolbar);
            TextView toolbarTitle = binding.signupToolbar.barFindText;

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}