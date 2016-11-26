package com.mcl.jejudarmda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.mcl.jejudarmda.JejuDarmda;
import com.mcl.jejudarmda.LoginStatus;
import com.mcl.jejudarmda.R;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

public class LoginActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 100;

    private LoginButton kakaoLoginDelegate;
    private ImageView naverLoginButton, kakaoLoginButton;

    // Kakao
    private SessionCallback kakaoCallback;

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

        oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(
                this,
                JejuDarmda.NAVER_OAUTH_CLIENT_ID,
                JejuDarmda.NAVER_OAUTH_CLENT_SECRET,
                JejuDarmda.NAVER_OAUTH_CLIENT_NAME
        );

        kakaoLoginButton = (ImageView) findViewById(R.id.kakao_login_button);
        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kakaoLoginDelegate.performClick();
                Log.i("Click", "Click");
            }
        });
        kakaoLoginDelegate = (LoginButton) findViewById(R.id.kakao_login_delegate);

        kakaoCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(kakaoCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        kakaoRequestMe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(kakaoCallback);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loginSuccess() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, REQUEST_CODE);
    }

    protected void kakaoRequestMe() {
        Log.i("TEST", "REQUEST");
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.i("ERROR", errorResult.toString());
                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                } else {
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.i("CLOSE", errorResult.toString());
                LoginStatus.setKakaostory(false);
            }

            @Override
            public void onNotSignedUp() {
                LoginStatus.setKakaostory(false);
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                LoginStatus.setKakaostory(true);
                loginSuccess();
            }
        });
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
            }
        }
    }
}
