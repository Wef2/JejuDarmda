package com.mcl.jejudarmda.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;

import com.mcl.jejudarmda.R;
import com.mcl.jejudarmda.fragment.HomeFragment;
import com.mcl.jejudarmda.fragment.PostingFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import net.daum.mf.oauth.MobileOAuthLibrary;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_home) {
                    getFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
                } else if (tabId == R.id.tab_posting) {
                    getFragmentManager().beginTransaction().replace(R.id.frame_layout, new PostingFragment()).commit();
                } else if (tabId == R.id.tab_graph) {

                }
            }
        });
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

}
