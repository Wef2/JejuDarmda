package com.mcl.jejudarmda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.JsonObject;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.kakaostory.KakaoStoryService;
import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.request.PostRequest;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.network.ErrorResult;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;
import com.mcl.jejudarmda.R;
import com.mcl.jejudarmda.TokenManager;
import com.mcl.jejudarmda.VolleyRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PostingActivity extends AppCompatActivity {

    private String DAUM_URL = "https://apis.daum.net/blog/v1/";
    private final String NAVER_URL = "https://openapi.naver.com/blog/writePost.json";

    private String daumBlogName;
    private EditText titleEdit, contentsEdit;

    private boolean facebook, daum, kakao, naver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        Intent intent = getIntent();
        facebook = intent.getBooleanExtra("facebook", false);
        daum = intent.getBooleanExtra("daum", false);
        kakao = intent.getBooleanExtra("kakao", false);
        naver = intent.getBooleanExtra("naver", false);

        titleEdit = (EditText) findViewById(R.id.title_edit);
        contentsEdit = (EditText) findViewById(R.id.contents_edit);

        // Load
        if (daum) {
            requestDaumBlogName();
        }
        if (kakao) {
            requestKakaoAccessTokenInfo();
        }

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
                try {
                    String title = URLEncoder.encode(titleEdit.getText().toString(), "utf8");
                    String contents = URLEncoder.encode(contentsEdit.getText().toString(), "utf8");
                    oneClickPost(title, contents);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestDaumBlogName() {
        String testUrl = DAUM_URL + "info.json?access_token=" + TokenManager.getDaumToken();
        VolleyRequest.get(testUrl, null, new Response.Listener<JsonObject>() {
            @Override
            public void onResponse(JsonObject response) {
                Log.i("Blog Info", response.toString());
                JsonObject daumBlog = response.getAsJsonObject("channel");
                daumBlogName = daumBlog.get("name").getAsString();
                makeDaumApiUrl(daumBlogName);
            }
        });

    }

    public void makeDaumApiUrl(String daumBlogName) {
        DAUM_URL = DAUM_URL + daumBlogName + "/write.json?access_token=" + TokenManager.getDaumToken();
    }

    public void oneClickPost(String title, String contents) {
        if (facebook) {

        }
        if (daum) {
            daumPost(title, contents);
        }
        if (naver) {
            naverPost(title, contents);
        }
        if (kakao) {
            kakaoPost(contents);
        }
    }

    public void facebookPost(String contents){

    }

    public void daumPost(String title, String contents) {
        String url = DAUM_URL;
        url = url + "&title=" + title;
        url = url + "&content=" + contents;
        url = url + "&tag=" + title;
        VolleyRequest.daumRequest(url, new Response.Listener<JsonObject>() {
            @Override
            public void onResponse(JsonObject response) {
                Toast.makeText(PostingActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void naverPost(String title, String contents) {
        String url = NAVER_URL;
        url = url + "?title=" + title;
        url = url + "&contents=" + contents;
        VolleyRequest.get(url, null, new Response.Listener<JsonObject>() {
            @Override
            public void onResponse(JsonObject response) {
                Toast.makeText(PostingActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void kakaoPost(String contents) {
        try {
            KakaoStoryService.requestPostNote(new StoryResponseCallback<MyStoryInfo>() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                }

                @Override
                public void onNotSignedUp() {

                }

                @Override
                public void onNotKakaoStoryUser() {

                }

                @Override
                public void onSuccess(MyStoryInfo result) {
                    Logger.d(result.toString());
                }
            }, contents, PostRequest.StoryPermission.PUBLIC, true, "test", null, null, null);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }

    }

    private void requestKakaoAccessTokenInfo() {
        AuthService.requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onFailure(ErrorResult errorResult) {
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                Toast.makeText(PostingActivity.this, accessTokenInfoResponse.toString(), Toast.LENGTH_SHORT).show();
                KakaoStoryService.requestIsStoryUser(new StoryResponseCallback<Boolean>() {
                    @Override
                    public void onNotKakaoStoryUser() {

                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {

                    }

                    @Override
                    public void onNotSignedUp() {

                    }

                    @Override
                    public void onSuccess(Boolean result) {
                        Toast.makeText(PostingActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}
