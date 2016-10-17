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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WritingActivity extends AppCompatActivity {

    private final String NAVER_URL = "https://openapi.naver.com/blog/writePost.json";
    private EditText titleEdit, contentsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        titleEdit = (EditText) findViewById(R.id.title_edit);
        contentsEdit = (EditText) findViewById(R.id.contents_edit);
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
}
