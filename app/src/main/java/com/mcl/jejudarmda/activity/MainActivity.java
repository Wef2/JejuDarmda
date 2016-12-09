package com.mcl.jejudarmda.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.mcl.jejudarmda.JejuDarmda;
import com.mcl.jejudarmda.LoginStatus;
import com.mcl.jejudarmda.R;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import net.daum.mf.oauth.MobileOAuthLibrary;
import net.daum.mf.oauth.OAuthError;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String NAVER = "Naver";
    private final String KAKAO = "Kakao";
    private final String DAUM = "Daum";
    private final String FACEBOOK = "Facebook";

    private final String RGB_RED = "#C0392B";
    private final String RGB_BLUE = "#3498DB";

    private RelativeLayout facebookLayout, daumLayout, kakaostoryLayout, naverblogLayout;
    private TextView facebookStatus, daumStatus, kakaostoryStatus, naverblogStatus;

    private AccessToken accessToken;

    // Delegate
    private com.facebook.login.widget.LoginButton facebookLoginDelegate;
    private LoginButton kakaoLoginDelegate;

    private MobileOAuthLibrary.OAuthListener oAuthListener = new MobileOAuthLibrary.OAuthListener() {
        @Override
        public void onAuthorizeSuccess() {
            showToast("로그인 성공");
            LoginStatus.setDaum(true);
            checkAllStatus();
        }

        @Override
        public void onAuthorizeFail(OAuthError.OAuthErrorCodes errorCode, String errorMessage) {
            showToast("onAuthorizeFail : " + errorMessage);
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
            showToast("onRequestResourceSuccess : " + response);
            // 결과 피싱은 앱에서 담당한다.
        }

        @Override
        public void onRequestResourceFail(OAuthError.OAuthErrorCodes errorCode, String errorMessage) {
            showToast("onRequestResourceFail : " + errorMessage);
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

    // Naver
    private OAuthLogin oAuthLogin;

    private OAuthLoginHandler oAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                showToast("네이버 로그인 성공");
                LoginStatus.setNaverblog(true);
                checkAllStatus();
            } else {
                String errorCode = oAuthLogin.getLastErrorCode(MainActivity.this).getCode();
                String errorDesc = oAuthLogin.getLastErrorDesc(MainActivity.this);
                showToast("errorCode:" + errorCode + ", errorDesc:");
            }
        }
    };

    // Kakao
    private LogoutResponseCallback kakaoLogoutCallback = new LogoutResponseCallback() {
        @Override
        public void onCompleteLogout() {
            showToast("로그아웃");
            LoginStatus.setKakaostory(false);
            checkAllStatus();
        }
    };

    private SessionCallback callback;

    private CallbackManager facebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kakaoLoginDelegate = (LoginButton) findViewById(R.id.kakao_login_delegate);
        facebookLoginDelegate = (com.facebook.login.widget.LoginButton) findViewById(R.id.facebook_login_delegate);
        facebookLoginDelegate.setReadPermissions("email");

        facebookCallbackManager = CallbackManager.Factory.create();
        facebookLoginDelegate.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LoginStatus.setFacebook(true);
                checkAllStatus();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_home) {
                } else if (tabId == R.id.tab_posting) {
                    Intent intent = new Intent(MainActivity.this, PostingActivity.class);
                    startActivity(intent);
                }
            }
        });
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_home) {
                } else if (tabId == R.id.tab_posting) {
                    Intent intent = new Intent(MainActivity.this, PostingActivity.class);
                    startActivity(intent);
                }
            }
        });

        facebookLayout = (RelativeLayout) findViewById(R.id.facebook_layout);
        daumLayout = (RelativeLayout) findViewById(R.id.daum_layout);
        kakaostoryLayout = (RelativeLayout) findViewById(R.id.kakaostory_layout);
        naverblogLayout = (RelativeLayout) findViewById(R.id.naver_blog_layout);

        facebookLayout.setOnClickListener(this);
        daumLayout.setOnClickListener(this);
        kakaostoryLayout.setOnClickListener(this);
        naverblogLayout.setOnClickListener(this);

        facebookStatus = (TextView) findViewById(R.id.facebook_status_text);
        daumStatus = (TextView) findViewById(R.id.daum_status_text);
        kakaostoryStatus = (TextView) findViewById(R.id.kakaostory_status_text);
        naverblogStatus = (TextView) findViewById(R.id.naver_blog_status_text);

        // Facebook Login Check
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            LoginStatus.setFacebook(true);
        }
        
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

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }

    @Override
    public void onResume() {
        super.onResume();

        kakaoRequestMe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
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
        setTextByStatus(LoginStatus.getDaum(), daumStatus);
        setTextByStatus(LoginStatus.getKakaostory(), kakaostoryStatus);
        setTextByStatus(LoginStatus.getNaverblog(), naverblogStatus);
        if (!LoginStatus.hasConnection()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(facebookLayout)) {
            buildAndShowDialog(FACEBOOK, LoginStatus.getFacebook());
        } else if (v.equals(daumLayout)) {
            buildAndShowDialog(DAUM, LoginStatus.getDaum());
        } else if (v.equals(kakaostoryLayout)) {
            buildAndShowDialog(KAKAO, LoginStatus.getKakaostory());
        } else if (v.equals(naverblogLayout)) {
            buildAndShowDialog(NAVER, LoginStatus.getNaverblog());
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
            case FACEBOOK:
                facebookLoginDelegate.performClick();
                break;
            case NAVER:
                oAuthLogin.startOauthLoginActivity(this, oAuthLoginHandler);
                break;
            case KAKAO:
                kakaoLoginDelegate.performClick();
                break;
            case DAUM:
                MobileOAuthLibrary.getInstance().authorize(this, oAuthListener);
                break;
        }
    }

    public void logout(String channel) {
        switch (channel) {
            case FACEBOOK:
                LoginManager.getInstance().logOut();
                LoginStatus.setFacebook(false);
                break;
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
                    showToast("로그아웃 실패");
                } else {
                    LoginStatus.setDaum(false);
                    showToast("로그아웃 성공");
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
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
