package com.mcl.jejudarmda;

/**
 * Created by BK on 2016-10-04.
 */
public class LoginStatus {

    private static boolean facebook;
    private static boolean instagram;
    private static boolean youtube;
    private static boolean kakaostory;
    private static boolean naverblog;

    public static void init() {
        facebook = false;
        instagram = false;
        youtube = false;
        kakaostory = false;
        naverblog = false;
    }

    public static boolean getFacebook() {
        return facebook;
    }

    public static void setFacebook(boolean status) {
        facebook = status;
    }

    public static boolean getInstagram() {
        return instagram;
    }

    public static void setInstagram(boolean status) {
        instagram = status;
    }

    public static boolean getYoutube() {
        return youtube;
    }

    public static void setYoutube(boolean status) {
        youtube = status;
    }

    public static boolean getKakaostory() {
        return kakaostory;
    }

    public static void setKakaostory(boolean status) {
        kakaostory = status;
    }

    public static boolean getNaverblog() {
        return naverblog;
    }

    public static  void setNaverblog(boolean status) {
        naverblog = status;
    }
}