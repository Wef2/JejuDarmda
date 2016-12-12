package com.mcl.jejudarmda;

import android.net.Uri;

/**
 * Created by kakao on 2016. 12. 12..
 */

public class DaumTokenExtractor {

    public String extract(Uri uri) {
        String result = null;
        if (uri != null) {
            result = uri.toString();
            result = result.substring(55);
            result = result.split("&")[0];
        }
        return result;
    }
}
