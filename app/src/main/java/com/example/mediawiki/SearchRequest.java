package com.example.mediawiki;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchRequest {
    public static final String TAG = "SearchRequest";
    public static final String SEARCH_TAG = "WikiSearch";

    private RequestQueue requestQueue;
    private ArrayList<SearchModel> searchList;
    private RecyclerView.Adapter adapter;

    public SearchRequest(Context ctx, ArrayList<SearchModel> searchList, RecyclerView.Adapter adapter) {
        requestQueue = Volley.newRequestQueue(ctx);
        this.searchList = searchList;
        this.adapter = adapter;
    }

    //creates url extension by splitting string by space character and adding seperator
    public String createUrl(String searchText,String seperator,String base) {
        String [] searchQueryText = searchText.split(" ");
        String url = base;
        for(int i=0;i<searchQueryText.length;i++) {
            url += searchQueryText[i];
            if(i<searchQueryText.length-1) {
                url += seperator;
            }
        }

        return url;
    }

    //send HTTP request to mediawiki API with url
    public void sendHTTPRequest(String url) {

        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (null != response) {
                                searchList.clear();
                                Log.d(TAG, response.toString());
                                ArrayList<JSONObject> tempList = getSortedArrayModel(response);
                                for(int i=0;i<tempList.size();i++) {
                                    SearchModel searchModel = new SearchModel();

                                    searchModel.setTitle(tempList.get(i).getString("title"));
                                    if(tempList.get(i).has("terms")) {
                                        searchModel.setDescription(tempList.get(i).getJSONObject("terms").getJSONArray("description").getString(0));
                                    } else {
                                        searchModel.setDescription("");
                                    }
                                    if(tempList.get(i).has("thumbnail")) {
                                        searchModel.setSearchImageUrl(tempList.get(i).getJSONObject("thumbnail").getString("source"));
                                    } else {
                                        searchModel.setSearchImageUrl("");
                                    }
                                    searchList.add(searchModel);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,error.toString());
            }
        }) {
            //caching the response using volley library
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
        request.setTag(SEARCH_TAG);
        requestQueue.cancelAll(SEARCH_TAG);
        requestQueue.add(request);
    }

    // the search results are sorted as per the index value of JSON response
    private ArrayList<JSONObject> getSortedArrayModel (JSONObject response) {
        ArrayList<JSONObject> unsortedList = new ArrayList<>();
            try {
                JSONArray query = response.getJSONObject("query").getJSONArray("pages");

                for(int i=0;i<query.length();i++) {
                    unsortedList.add(query.getJSONObject(i));
                }

                Collections.sort(unsortedList, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject o1, JSONObject o2) {
                        int index1 = 0;
                        int index2 = 1;
                        try {
                            index1 = (Integer) o1.get("index");
                            index2 = (Integer) o2.get("index");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return index1-index2;
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

        return unsortedList;
    }
}
