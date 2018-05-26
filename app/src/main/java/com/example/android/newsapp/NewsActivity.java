package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = NewsActivity.class.getName();
    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    /**
     * Adapter for the list of news
     */
    private NewsAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mySwipeRefreshLayout = findViewById(R.id.swipeRefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateList();
                    }
                });

        mEmptyStateTextView = findViewById(R.id.empty_text_view);

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news articles as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Find the current news article that was clicked on
                News currentNews = mAdapter.getItem(i);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = null;
                if (currentNews != null) {
                    newsUri = Uri.parse(currentNews.getUrl());
                }

                // Create a new intent to view the article's URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            Log.e(LOG_TAG, "initLoader()");
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle bundle) {
        //URL for news data from the Guardian
        String GUARDIAN_REQUEST_URL = this.getString(R.string.request_url);
        return new NewsLoader(this, GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newest) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        if (connectivityManager != null) {
            // Set empty state text to display "No internet connection."
            mEmptyStateTextView.setText(R.string.no_internet);
        }
        // Set empty state text to display "No news found."
        mEmptyStateTextView.setText(R.string.no_news);

        // Clear the adapter of previous news articles data
        mAdapter.clear();

        // If there is a valid list of {@link News}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newest != null && !newest.isEmpty()) {
            mAdapter.addAll(newest);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    private void updateList() {
        finish();
        startActivity(getIntent());
        mySwipeRefreshLayout.setRefreshing(false); // Disables the refresh icon
    }

    /*
     * Listen for option item selections so that we receive a notification
     * when the user requests a refresh by selecting the refresh action bar item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Check if user triggered a refresh:
            case R.id.menu_refresh:
                Log.i(LOG_TAG, "Refresh menu item selected");

                // Signal SwipeRefreshLayout to start the progress indicator
                mySwipeRefreshLayout.setRefreshing(true);

                // Start the refresh background task.
                // This method calls setRefreshing(false) when it's finished.
                updateList();
                return true;
        }
        // User didn't trigger a refresh, let the superclass handle this action
        return super.onOptionsItemSelected(item);
    }
}
