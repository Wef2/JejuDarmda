package com.mcl.jejudarmda;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

/**
 * Created by BK on 2016-10-04.
 */
public class JejuDarmda extends Application {

    public static String NAVER_OAUTH_CLIENT_ID;
    public static String NAVER_OAUTH_CLENT_SECRET;
    public static String NAVER_OAUTH_CLIENT_NAME;

    private static volatile JejuDarmda instance = null;
    private static volatile Activity currentActivity = null;

    public static JejuDarmda getGlobalApplicationContext() {
        if (instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        JejuDarmda.currentActivity = currentActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        NAVER_OAUTH_CLIENT_ID = getResources().getString(R.string.naver_oauth_client_id);
        NAVER_OAUTH_CLENT_SECRET = getResources().getString(R.string.naver_oauth_client_secret);
        NAVER_OAUTH_CLIENT_NAME = getResources().getString(R.string.naver_oauth_client_name);

        KakaoSDK.init(new KakaoSDKAdapter());

        VolleyRequest.init(getApplicationContext());
        LoginStatus.init();
    }

    private static class KakaoSDKAdapter extends KakaoAdapter {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         *
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Activity getTopActivity() {
                    return JejuDarmda.getCurrentActivity();
                }

                @Override
                public Context getApplicationContext() {
                    return JejuDarmda.getGlobalApplicationContext();
                }
            };
        }
    }

}
