package com.mcl.jejudarmda;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nhn.android.naverlogin.OAuthLogin;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BK on 2016-10-04.
 */
public class VolleyRequest {

    private static Context mContext;
    private static RequestQueue requestQueue;

    private static Gson gson;

    private static Response.ErrorListener errorListener;

    public static void init(Context context) {
        mContext = context;
        requestQueue = Volley.newRequestQueue(mContext);
        gson = new Gson();

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT);
            }
        };
    }

    public static void naverRequest(int method, String url, JsonObject jsonRequest, Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {
        NaverJsonRequest naverRequest = new NaverJsonRequest(method, url, jsonRequest, listener, errorListener);
        requestQueue.add(naverRequest);
    }

    public static void naverPost(String url, JsonObject jsonRequest, Response.Listener<JsonObject> listener) {
        naverRequest(Request.Method.POST, url, jsonRequest, listener, errorListener);
    }

    public static void naverGet(String url, JsonObject jsonRequest, Response.Listener<JsonObject> listener) {
        naverRequest(Request.Method.GET, url, jsonRequest, listener, errorListener);
    }


    public static class NaverJsonRequest extends JsonRequest<JsonObject> {

        public NaverJsonRequest(int method, String url, JsonObject jsonRequest,
                               Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {
            super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
        }

        @Override
        protected Response<JsonObject> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + OAuthLogin.getInstance().getAccessToken(mContext.getApplicationContext()));
            headers.put("X-Naver-Client-Id", JejuDarmda.NAVER_OAUTH_CLIENT_ID);
            headers.put("X-Naver-Client-Secret", JejuDarmda.NAVER_OAUTH_CLENT_SECRET);
            return headers;
        }
    }

}
