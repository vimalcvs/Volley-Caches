package com.vimalcvs.volleycaches;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.color.MaterialColors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class NotificationActivity extends AppCompatActivity {

    private List<NotificationModel> list;
    public NotificationAdapter adapter;
    ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int progressBackgroundColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorBackgroundFloating, Color.WHITE);
        int progressIndicatorColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorPrimary, Color.BLACK);


        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(progressBackgroundColor);
        swipeRefreshLayout.setColorSchemeColors(progressIndicatorColor);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getData();
            showRecyclerView();
        });
        showRecyclerView();
        getData();
    }

    private void showRecyclerView() {
        progressBar = findViewById(R.id.listProgressBar);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        list = new ArrayList<> ();
        adapter = new NotificationAdapter(getApplicationContext(), list);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Constants.PDF_URL, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    NotificationModel notificationModel = new NotificationModel();
                    notificationModel.setId(jsonObject.getInt("id"));
                    notificationModel.setTitle(jsonObject.getString("title"));
                    notificationModel.setImage(jsonObject.getString("image"));
                    notificationModel.setBody(jsonObject.getString("body"));
                    notificationModel.setDate(jsonObject.getString("date"));
                    list.add(notificationModel);
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }, error -> {
            Log.e("Volley", error.toString());
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000;
                    final long cacheExpired = 24 * 60 * 60 * 1000;
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers != null ? response.headers.get("Date") : null;
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers != null ? response.headers.get("Last-Modified") : null;
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONArray(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
}
