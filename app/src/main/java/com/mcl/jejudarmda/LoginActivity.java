package com.mcl.jejudarmda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final int KAKAO_LOGIN_SUCCESS = 100;

    private final String NAVER = "Naver";
    private final String YOUTUBE = "Youtube";
    private final String KAKAO = "Kakao";
    private final String INSTAGRAM = "Instagram";
    private final String FACEBOOK = "Facebook";

    private final String RGB_RED = "#C0392B";
    private final String RGB_BLUE = "#3498DB";

    private RelativeLayout facebookLayout, instagarmLayout, youtubeLayout, kakaostoryLayout, naverblogLayout;
    private TextView facebookStatus, instagramStatus, youtubeStatus, kakaostoryStatus, naverblogStatus;

    private FloatingActionButton writeButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        facebookLayout = (RelativeLayout) findViewById(R.id.facebook_layout);
        instagarmLayout = (RelativeLayout) findViewById(R.id.instagram_layout);
        youtubeLayout = (RelativeLayout) findViewById(R.id.youtube_layout);
        kakaostoryLayout = (RelativeLayout) findViewById(R.id.kakaostory_layout);
        naverblogLayout = (RelativeLayout) findViewById(R.id.naver_blog_layout);

        facebookLayout.setOnClickListener(this);
        instagarmLayout.setOnClickListener(this);
        youtubeLayout.setOnClickListener(this);
        kakaostoryLayout.setOnClickListener(this);
        naverblogLayout.setOnClickListener(this);

        facebookStatus = (TextView) findViewById(R.id.facebook_status_text);
        instagramStatus = (TextView) findViewById(R.id.instagram_status_text);
        youtubeStatus = (TextView) findViewById(R.id.youtube_status_text);
        kakaostoryStatus = (TextView) findViewById(R.id.kakaostory_status_text);
        naverblogStatus = (TextView) findViewById(R.id.naver_blog_status_text);

        writeButton = (FloatingActionButton) findViewById(R.id.write_button);
        writeButton.setOnClickListener(this);

        checkAllStatus();

        oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(
                this,
                JejuDarmda.NAVER_OAUTH_CLIENT_ID,
                JejuDarmda.NAVER_OAUTH_CLENT_SECRET,
                JejuDarmda.NAVER_OAUTH_CLIENT_NAME
        );
    }

    @Override
    public void onResume(){
        super.onResume();

        kakaoRequestMe();
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
        setTextByStatus(LoginStatus.getYoutube(), youtubeStatus);
        setTextByStatus(LoginStatus.getKakaostory(), kakaostoryStatus);
        setTextByStatus(LoginStatus.getNaverblog(), naverblogStatus);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(facebookLayout)) {
            buildAndShowDialog(FACEBOOK, LoginStatus.getFacebook());
        } else if (v.equals(instagarmLayout)) {
            buildAndShowDialog(INSTAGRAM, LoginStatus.getInstagram());
        } else if (v.equals(youtubeLayout)) {
            buildAndShowDialog(YOUTUBE, LoginStatus.getYoutube());
        } else if (v.equals(kakaostoryLayout)) {
            buildAndShowDialog(KAKAO, LoginStatus.getKakaostory());
        } else if (v.equals(naverblogLayout)) {
            buildAndShowDialog(NAVER, LoginStatus.getNaverblog());
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
        }
    }

    public void logout(String channel) {
        switch (channel) {
            case NAVER:
                oAuthLogin.logout(this);
                LoginStatus.setNaverblog(false);
                break;
            case KAKAO:

                break;
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
