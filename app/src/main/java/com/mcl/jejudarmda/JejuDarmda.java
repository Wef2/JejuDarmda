package com.mcl.jejudarmda;

import android.app.Application;

/**
 * Created by BK on 2016-10-04.
 */
public class JejuDarmda extends Application {

    public static String NAVER_OAUTH_CLIENT_ID;
    public static String NAVER_OAUTH_CLENT_SECRET;
    public static String NAVER_OAUTH_CLIENT_NAME;

    @Override
    public void onCreate() {
        super.onCreate();

        NAVER_OAUTH_CLIENT_ID = getResources().getString(R.string.naver_oauth_client_id);
        NAVER_OAUTH_CLENT_SECRET = getResources().getString(R.string.naver_oauth_client_secret);
        NAVER_OAUTH_CLIENT_NAME = getResources().getString(R.string.naver_oauth_client_name);

        VolleyRequest.init(getApplicationContext());
        LoginStatus.init();
    }
}
