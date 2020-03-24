package com.example.mediawiki;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private SearchRequest searchRequest;
    private RecyclerView searchRecyclerview;
    private LinearLayoutManager layoutmanager;
    private DividerItemDecoration itemDecoration;
    private RecyclerView.Adapter searchViewAdapter;
    private ArrayList<SearchModel> searchModelDataList;
    RecyclerViewClickListener clickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchRecyclerview = findViewById(R.id.search_results_list);
        searchModelDataList = new ArrayList<>();
        clickListener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d(TAG,"clicked");
                String title = searchModelDataList.get(position).getTitle();
                String wikiTitle = searchRequest.createUrl(title,"_",Constants.wikiUrl);

                Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wikiTitle));
                startActivity(urlIntent);
            }
        };
        initializeRecyclerView();
        searchRequest = new SearchRequest(this,searchModelDataList,searchViewAdapter);

    }

    //setting the adapter, layoutmanager and item decoration for recyclerview
    private void initializeRecyclerView () {
        searchViewAdapter = new SearchListAdapter(getApplicationContext(),searchModelDataList,clickListener);
        layoutmanager = new LinearLayoutManager(this);
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        itemDecoration = new DividerItemDecoration(searchRecyclerview.getContext(),layoutmanager.getOrientation());
        searchRecyclerview.setLayoutManager(layoutmanager);
        searchRecyclerview.addItemDecoration(itemDecoration);
        searchRecyclerview.setAdapter(searchViewAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search,menu);
        MenuItem searchMenuItem = menu.findItem(R.id.search_wiki);
        final androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText!=null && !newText.isEmpty()) {
                    String searchUrl = searchRequest.createUrl(newText,"+",Constants.baseUrl);
                    Log.d(TAG,newText);
                    Constants.setQueryText(newText);
                    searchRequest.sendHTTPRequest(searchUrl);
                } else {
                    searchModelDataList.clear();
                    searchViewAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        return true;
    }

    //Item click listener interface of recyclerview
    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
