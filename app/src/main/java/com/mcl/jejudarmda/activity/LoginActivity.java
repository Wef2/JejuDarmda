package com.mcl.jejudarmda.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.mcl.jejudarmda.JejuDarmda;
import com.mcl.jejudarmda.LoginStatus;
import com.mcl.jejudarmda.R;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import net.daum.mf.oauth.MobileOAuthLibrary;
import net.daum.mf.oauth.OAuthError;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String NAVER = "Naver";
    private final String KAKAO = "Kakao";
    private final String DAUM = "Daum";
    private final String INSTAGRAM = "Instagram";
    private final String FACEBOOK = "Facebook";

    private final String RGB_RED = "#C0392B";
    private final String RGB_BLUE = "#3498DB";

    private RelativeLayout facebookLayout, instagarmLayout, daumLayout, kakaostoryLayout, naverblogLayout;
    private TextView facebookStatus, instagramStatus, daumStatus, kakaostoryStatus, naverblogStatus;
    private Button settingsButton;

    private MobileOAuthLibrary.OAuthListener oAuthListener = new MobileOAuthLibrary.OAuthListener() {
        @Override
        public void onAuthorizeSuccess() {
            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
            LoginStatus.setDaum(true);
            checkAllStatus();
        }

        @Override
        public void onAuthorizeFail(OAuthError.OAuthErrorCodes errorCode, String errorMessage) {
            Toast.makeText(LoginActivity.this, "onAuthorizeFail : " + errorMessage, Toast.LENGTH_SHORT).show();
            if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorInvalidAuthorizationRequest)) {
                // 파라미터를 잘못 사용한 경우.
            } else if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorUnauthorizedClient)) {
                // 승인되지 않은 Client ID 를 사용한 경우         } else if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorAccessDenied)) {
                // 사용자가 승인 페이지에서 "취소"를 누른 경우         } else if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorUnsupportedResponseType)) {
                // 지원되지 않는 인증방식을 사용한 경우         } else if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorInvalidScope)) {
                // 유효한 권한 요청이 아닌 경우.
            }
        }

        @Override
        public void onRequestResourceSuccess(String response) {
            Toast.makeText(LoginActivity.this, "onRequestResourceSuccess : " + response, Toast.LENGTH_SHORT).show();
            // 결과 피싱은 앱에서 담당한다.
        }

        @Override
        public void onRequestResourceFail(OAuthError.OAuthErrorCodes errorCode, String errorMessage) {
            Toast.makeText(LoginActivity.this, "onRequestResourceFail : " + errorMessage, Toast.LENGTH_SHORT).show();
            if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorInvalidToken)) {
                // access token 이 없거나 만료처리된 경우 or 401 에러             // authorize() 를 통해 다시 access token을 발급 받아야함.
            } else if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorInvalidResourceRequest)) {
                // 서버와 통신중 400 에러가 발생한 경우         } else if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorInsufficientScope)) {
                // 서버와 통신중 403 에러가 발생한 경우         } else if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorServiceNotFound)) {
                // 서버와 통신중 404 에러가 발생한 경우         } else if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorNetwork)) {
                // 현재 휴대폰의 네트워크를 이용할 수 없는 경우         } else if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorServer)) {
                // 서버쪽에서 에러가 발생하는 경우             // 서버 페이지에 문제가 있는 경우이므로 api 담당자와 얘기해야함.
            } else if (errorCode.equals(OAuthError.OAuthErrorCodes.OAuthErrorUnknown)) {
                // 서버와 통신중 그 외 알수 없는 에러가 발생한 경우.
            }
        }
    };
    private FloatingActionButton writeButton;

    // Naver
    private OAuthLogin oAuthLogin;

    private OAuthLoginHandler oAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                Toast.makeText(LoginActivity.this, "네이버 로그인 성공", Toast.LENGTH_SHORT).show();
                LoginStatus.setNaverblog(true);
                checkAllStatus();
            } else {
                String errorCode = oAuthLogin.getLastErrorCode(LoginActivity.this).getCode();
                String errorDesc = oAuthLogin.getLastErrorDesc(LoginActivity.this);
                Toast.makeText(LoginActivity.this, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }
    };

    // Kakao
    private LogoutResponseCallback kakaoLogoutCallback = new LogoutResponseCallback() {
        @Override
        public void onCompleteLogout() {
            Toast.makeText(LoginActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
            LoginStatus.setKakaostory(false);
            checkAllStatus();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        facebookLayout = (RelativeLayout) findViewById(R.id.facebook_layout);
        instagarmLayout = (RelativeLayout) findViewById(R.id.instagram_layout);
        daumLayout = (RelativeLayout) findViewById(R.id.daum_layout);
        kakaostoryLayout = (RelativeLayout) findViewById(R.id.kakaostory_layout);
        naverblogLayout = (RelativeLayout) findViewById(R.id.naver_blog_layout);

        facebookLayout.setOnClickListener(this);
        instagarmLayout.setOnClickListener(this);
        daumLayout.setOnClickListener(this);
        kakaostoryLayout.setOnClickListener(this);
        naverblogLayout.setOnClickListener(this);

        facebookStatus = (TextView) findViewById(R.id.facebook_status_text);
        instagramStatus = (TextView) findViewById(R.id.instagram_status_text);
        daumStatus = (TextView) findViewById(R.id.daum_status_text);
        kakaostoryStatus = (TextView) findViewById(R.id.kakaostory_status_text);
        naverblogStatus = (TextView) findViewById(R.id.naver_blog_status_text);

        settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(this);

        writeButton = (FloatingActionButton) findViewById(R.id.write_button);
        writeButton.setOnClickListener(this);

        checkAllStatus();

        // Daum
        String DAUM_CLIENT_ID = getResources().getString(R.string.daum_client_id);
        MobileOAuthLibrary.getInstance().initialize(this, DAUM_CLIENT_ID);

        // Naver
        oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(
                this,
                JejuDarmda.NAVER_OAUTH_CLIENT_ID,
                JejuDarmda.NAVER_OAUTH_CLENT_SECRET,
                JejuDarmda.NAVER_OAUTH_CLIENT_NAME
        );

    }

    @Override
    public void onResume() {
        super.onResume();

        kakaoRequestMe();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null) {
            // authorize() 호출 후에 url scheme을 통해 callback이 들어온다.
            MobileOAuthLibrary.getInstance().handleUrlScheme(uri);
        }
    }

    private void setTextByStatus(boolean status, TextView textView) {
        if (status) {
            textView.setText("ON");
            textView.setTextColor(Color.parseColor(RGB_BLUE));
        } else {
            textView.setText("OFF");
            textView.setTextColor(Color.parseColor(RGB_RED));
        }
    }

    private void checkAllStatus() {
        setTextByStatus(LoginStatus.getFacebook(), facebookStatus);
        setTextByStatus(LoginStatus.getInstagram(), instagramStatus);
        setTextByStatus(LoginStatus.getDaum(), daumStatus);
        setTextByStatus(LoginStatus.getKakaostory(), kakaostoryStatus);
        setTextByStatus(LoginStatus.getNaverblog(), naverblogStatus);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(facebookLayout)) {
            buildAndShowDialog(FACEBOOK, LoginStatus.getFacebook());
        } else if (v.equals(instagarmLayout)) {
            buildAndShowDialog(INSTAGRAM, LoginStatus.getInstagram());
        } else if (v.equals(daumLayout)) {
            buildAndShowDialog(DAUM, LoginStatus.getDaum());
        } else if (v.equals(kakaostoryLayout)) {
            buildAndShowDialog(KAKAO, LoginStatus.getKakaostory());
        } else if (v.equals(naverblogLayout)) {
            buildAndShowDialog(NAVER, LoginStatus.getNaverblog());
        } else if (v.equals(settingsButton)) {
            startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
        } else if (v.equals(writeButton)) {
            startActivity(new Intent(LoginActivity.this, WritingActivity.class));
        }
    }

    private void buildAndShowDialog(final String channel, final boolean status) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this).setTitle(channel);
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (status) {
                    logout(channel);
                } else {
                    login(channel);
                }
            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        if (status) {
            alertDialogBuilder.setMessage("로그아웃 하시겠습니까?");
        } else {
            alertDialogBuilder.setMessage("로그인 하시겠습니까?");
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void login(String channel) {
        switch (channel) {
            case NAVER:
                oAuthLogin.startOauthLoginActivity(this, oAuthLoginHandler);
                break;
            case KAKAO:
                startActivity(new Intent(LoginActivity.this, KakaoLoginActivity.class));
                break;
            case DAUM:
                MobileOAuthLibrary.getInstance().authorize(LoginActivity.this, oAuthListener);
                break;
        }
    }

    public void logout(String channel) {
        switch (channel) {
            case NAVER:
                oAuthLogin.logout(this);
                LoginStatus.setNaverblog(false);
                break;
            case KAKAO:
                UserManagement.requestLogout(kakaoLogoutCallback);
                LoginStatus.setKakaostory(false);
                break;
            case DAUM:
                MobileOAuthLibrary.getInstance().expireAuthorization();
                if (MobileOAuthLibrary.getInstance().isAuthorized()) {
                    Toast.makeText(LoginActivity.this, "로그아웃 실패", Toast.LENGTH_SHORT).show();
                } else {
                    LoginStatus.setDaum(false);
                    Toast.makeText(LoginActivity.this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
                }
        }
        checkAllStatus();
    }


    protected void kakaoRequestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {

                } else {

                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                LoginStatus.setKakaostory(false);
                checkAllStatus();
            }

            @Override
            public void onNotSignedUp() {
                LoginStatus.setKakaostory(false);
                checkAllStatus();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                LoginStatus.setKakaostory(true);
                checkAllStatus();
            }
        });
    }

}
