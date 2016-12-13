package com.mcl.jejudarmda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.mcl.jejudarmda.LoginStatus;
import com.mcl.jejudarmda.R;

public class PostingOptionActivity extends AppCompatActivity {

    private RelativeLayout facebookLayout, daumLayout, kakaoLayout, naverLayout;
    private CheckBox facebookCheckBox, daumCheckBox, kakaoCheckBox, naverCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting_option);

        facebookLayout = (RelativeLayout) findViewById(R.id.facebook_layout);
        daumLayout = (RelativeLayout) findViewById(R.id.daum_layout);
        kakaoLayout = (RelativeLayout) findViewById(R.id.kakaostory_layout);
        naverLayout = (RelativeLayout) findViewById(R.id.naver_blog_layout);

        facebookCheckBox = (CheckBox) findViewById(R.id.facebook_checkbox);
        daumCheckBox = (CheckBox) findViewById(R.id.daum_checkbox);
        kakaoCheckBox = (CheckBox) findViewById(R.id.kakao_checkbox);
        naverCheckBox = (CheckBox) findViewById(R.id.naver_checkbox);

        setViewsByStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                Intent intent = new Intent(this, PostingActivity.class);
                intent.putExtra("facebook", facebookCheckBox.isChecked());
                intent.putExtra("daum", daumCheckBox.isChecked());
                intent.putExtra("kakao", kakaoCheckBox.isChecked());
                intent.putExtra("naver", naverCheckBox.isChecked());
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setViewsByStatus() {
        if (!LoginStatus.getFacebook()) {
            facebookLayout.setVisibility(View.GONE);
        }
        if (!LoginStatus.getDaum()) {
            daumLayout.setVisibility(View.GONE);
        }
        if (!LoginStatus.getKakaostory()) {
            kakaoLayout.setVisibility(View.GONE);
        }
        if (!LoginStatus.getNaverblog()) {
            naverLayout.setVisibility(View.GONE);
        }
    }
}
