package com.booksearch;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ProgressBar mLoadingProgress;
    private RecyclerView rvBooks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoadingProgress = (ProgressBar) findViewById(R.id.pb_loading);
        rvBooks = (RecyclerView) findViewById(R.id.rvBooks);

        Intent intent = getIntent();
        String query = intent.getExtras() != null ? intent.getExtras().get("Query").toString() : "";
        URL bookUrl = null;
        LinearLayoutManager booksLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvBooks.setLayoutManager(booksLayoutManager);
        if (query.isEmpty()) {
            bookUrl = ApiUtil.buildURL("cooking");

        } else {
            try {
                bookUrl = new URL(query);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        new BookQueryTask().execute(bookUrl);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_advanced_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                int pos = item.getItemId() + 1;
                String preferencesName = SpUtil.QUERY + String.valueOf(pos);
                String query = SpUtil.getPreferenceString(getApplicationContext(), preferencesName);
                String[] prefParams = query.split("\\,");
                String[] queryParams = new String[4];
                for (int i = 0; i < prefParams.length; i++) {
                    queryParams[i] = prefParams[i];
                }
                URL bookUrl = ApiUtil.buildURL(
                        (queryParams[0] == null) ? "" : queryParams[0],
                        (queryParams[1] == null) ? "" : queryParams[1],
                        (queryParams[2] == null) ? "" : queryParams[2],
                        (queryParams[3] == null) ? "" : queryParams[3]
                );
                new BookQueryTask().execute(bookUrl);
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        ArrayList<String> recentList = SpUtil.getQueryString(getApplicationContext());
        int itemNum = recentList.size();
        MenuItem recentMenu;
        for (int i = 0; i < itemNum; i++) {
            recentMenu = menu.add(Menu.NONE, i, Menu.NONE, recentList.get(i));
        }
        return true;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            URL bookUrl = ApiUtil.buildURL(query);
            new BookQueryTask().execute(bookUrl);
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public class BookQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL searchURL = urls[0];
            String result = null;
            try {
                result = ApiUtil.getJSON(searchURL);
            } catch (IOException e) {
                Log.d("ERROR", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            TextView tvError = (TextView) findViewById(R.id.tvError);
            mLoadingProgress.setVisibility(View.INVISIBLE);
            if (s == null) {
                rvBooks.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.VISIBLE);
            } else {
                rvBooks.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.INVISIBLE);
                ArrayList<Book> books = ApiUtil.getBooksFromJson(s);
                BooksAdapter adapter = new BooksAdapter(books);
                rvBooks.setAdapter(adapter);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }
}
