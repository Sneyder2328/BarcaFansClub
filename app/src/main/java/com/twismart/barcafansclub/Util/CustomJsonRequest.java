package com.twismart.barcafansclub.Util;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by sneyd on 2/1/2017.
 **/

public class CustomJsonRequest extends Request<JSONArray> {
    Map<String, String> params;
    private Response.Listener listener;
    public CustomJsonRequest(int requestMethod, String url, Map<String, String> params, Response.Listener responseListener, Response.ErrorListener errorListener) {
        super(requestMethod, url, errorListener);
        this.params = params;
        this.listener = responseListener;
    }
    @Override
    protected void deliverResponse(JSONArray response) {
        listener.onResponse(response);
    }
    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONArray(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}