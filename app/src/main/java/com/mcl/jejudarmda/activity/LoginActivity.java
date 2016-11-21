package com.mcl.jejudarmda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mcl.jejudarmda.JejuDarmda;
import com.mcl.jejudarmda.LoginStatus;
import com.mcl.jejudarmda.R;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

public class LoginActivity extends AppCompatActivity {

    private ImageView naverLoginButton, kakaoLoginButton;

    // Naver
    private OAuthLogin oAuthLogin;
    private OAuthLoginHandler oAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                Toast.makeText(LoginActivity.this, "네이버 로그인 성공", Toast.LENGTH_SHORT).show();
                LoginStatus.setNaverblog(true);
                loginSuccess();
            } else {
                String errorCode = oAuthLogin.getLastErrorCode(LoginActivity.this).getCode();
                String errorDesc = oAuthLogin.getLastErrorDesc(LoginActivity.this);
                Toast.makeText(LoginActivity.this, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        naverLoginButton = (ImageView) findViewById(R.id.naver_login_button);
        naverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oAuthLogin.startOauthLoginActivity(LoginActivity.this, oAuthLoginHandler);
            }
        });
        kakaoLoginButton = (ImageView) findViewById(R.id.kakao_login_button);

        oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(
                this,
                JejuDarmda.NAVER_OAUTH_CLIENT_ID,
                JejuDarmda.NAVER_OAUTH_CLENT_SECRET,
                JejuDarmda.NAVER_OAUTH_CLIENT_NAME
        );

    }

    private void loginSuccess() {
        Intent intent = new Intent(LoginActivity.this, LoginStatusActivity.class);
        startActivity(intent);
    }
}
