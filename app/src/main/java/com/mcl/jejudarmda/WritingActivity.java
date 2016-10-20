package com.mcl.jejudarmda;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.kakao.kakaostory.api.KakaoStoryApi;
import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.request.PostRequest;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WritingActivity extends AppCompatActivity {

    private final String NAVER_URL = "https://openapi.naver.com/blog/writePost.json";
    private EditText titleEdit, contentsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        titleEdit = (EditText) findViewById(R.id.title_edit);
        contentsEdit = (EditText) findViewById(R.id.contents_edit);

        requestKakaoAccessTokenInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.writing_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                try {
                    String title = URLEncoder.encode(titleEdit.getText().toString(), "utf8");
                    String contents = URLEncoder.encode(contentsEdit.getText().toString(), "utf8");
                    writePost(title, contents);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void writePost(String title, String contents) {
        naverPost(title, contents);
        kakaoPost(contents);
    }

    public void naverPost(String title, String contents) {
        String url = NAVER_URL;
        url = url + "?title=" + title;
        url = url + "&contents=" + contents;
        VolleyRequest.get(url, null, new Response.Listener<JsonObject>() {
            @Override
            public void onResponse(JsonObject response) {
                Toast.makeText(WritingActivity.this, response.toString(), Toast.LENGTH_SHORT);
            }
        });
    }

    public void kakaoPost(String contents)  {
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
            }, contents, PostRequest.StoryPermission.ONLY_ME, true, "test", null, null, null);
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
                Toast.makeText(WritingActivity.this, accessTokenInfoResponse.toString(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(WritingActivity.this, result.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}
