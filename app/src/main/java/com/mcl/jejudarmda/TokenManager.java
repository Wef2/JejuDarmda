package com.mcl.jejudarmda;

/**
 * Created by kakao on 2016. 12. 12..
 */

public class TokenManager {

    private static String daumToken;

    public static String getDaumToken() {
        return daumToken;
    }

    public static void setDaumToken(String daumToken) {
        TokenManager.daumToken = daumToken;
    }
}
