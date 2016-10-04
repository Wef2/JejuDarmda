package com.mcl.jejudarmda;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by BK on 2016-10-04.
 */
public class VolleyRequest {

    private static Context mContext;
    private static RequestQueue requestQueue;

    private static Gson gson;

    public static void init(Context context) {
        mContext = context;
        requestQueue = Volley.newRequestQueue(mContext);
        gson = new Gson();
    }

    public static void request(int method, String url, JsonObject jsonRequest, Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {
        GsonJsonRequest gsonJsonRequest = new GsonJsonRequest(method, url, jsonRequest, listener, errorListener);
        requestQueue.add(gsonJsonRequest);
    }

    public static void post(String url, JsonObject jsonRequest, Response.Listener<JsonObject> listener, Response.ErrorListener errorListener) {
        request(Request.Method.POST, url, jsonRequest, listener, errorListener);
    }

    public static class GsonJsonRequest extends JsonRequest<JsonObject> {

        public GsonJsonRequest(int method, String url, JsonObject jsonRequest,
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
    }

}
