package com.mcl.jejudarmda;

import android.app.Application;

/**
 * Created by BK on 2016-10-04.
 */
public class JejuDarmda extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        VolleyRequest.init(getApplicationContext());
        LoginStatus.init();
    }
}
