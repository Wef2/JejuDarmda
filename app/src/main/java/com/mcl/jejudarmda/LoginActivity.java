package com.mcl.jejudarmda;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String RGB_RED = "#C0392B";
    private final String RGB_BLUE = "#3498DB";

    private RelativeLayout facebookLayout, instagarmLayout, youtubeLayout, kakaostoryLayout, naverblogLayout;
    private TextView facebookStatus, instagramStatus, youtubeStatus, kakaostoryStatus, naverblogStatus;

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

        checkAllStatus();
    }

    private void setColorByStatus(boolean status, TextView textView) {
        if (status) {
            textView.setText("ON");
            textView.setTextColor(Color.parseColor(RGB_BLUE));
        } else {
            textView.setText("OFF");
            textView.setTextColor(Color.parseColor(RGB_RED));
        }
    }

    private void checkAllStatus() {
        setColorByStatus(LoginStatus.getFacebook(), facebookStatus);
        setColorByStatus(LoginStatus.getInstagram(), instagramStatus);
        setColorByStatus(LoginStatus.getYoutube(), youtubeStatus);
        setColorByStatus(LoginStatus.getKakaostory(), kakaostoryStatus);
        setColorByStatus(LoginStatus.getNaverblog(), naverblogStatus);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(facebookLayout)){
            buildAndShowDialog("Facebook", LoginStatus.getFacebook());
        }
        else if(v.equals(instagarmLayout)){
            buildAndShowDialog("Instagram", LoginStatus.getInstagram());
        }
        else if(v.equals(youtubeLayout)){
            buildAndShowDialog("Youtube", LoginStatus.getYoutube());
        }
        else if(v.equals(kakaostoryLayout)){
            buildAndShowDialog("Kakaostory", LoginStatus.getKakaostory());
        }
        else if(v.equals(naverblogLayout)){
            buildAndShowDialog("Naver Blog", LoginStatus.getNaverblog());
        }
    }

    private void buildAndShowDialog(String channel, boolean status){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this).setTitle(channel);
        alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        if(status){
            alertDialogBuilder.setMessage("로그아웃 하시겠습니까?");
        }
        else{
            alertDialogBuilder.setMessage("로그인 하시겠습니까?");
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
