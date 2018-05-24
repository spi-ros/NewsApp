package com.example.android.newsapp;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getName();


    /**
     * Query the GUARDIAN dataSet and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl, Context context) {
        Log.e(LOG_TAG, "fetchNewsData()");
        // Create URL object
        URL url = createUrl(requestUrl, context);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, context);
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.problem_http_request), e);
        }

        // Return the list of {@link News}s
        return extractFeatureFromJson(jsonResponse, context);
    }

    private static URL createUrl(String stringUrl, Context context) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, context.getString(R.string.error_with_creating_url), e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url, Context context) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(20000 /* milliseconds */);
            urlConnection.setConnectTimeout(25000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, context.getString(R.string.error_response_code) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, context.getString(R.string.problem_retrieving_the_news_JSON_results), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON, Context context) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<News> newest = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONObject associated with the key called "response".
            JSONObject newsObject = baseJsonResponse.getJSONObject(context.getString(R.string.response));

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (news articles).
            JSONArray newsArray = newsObject.getJSONArray(context.getString(R.string.results));

            // For each article in the newsArray, create a {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {
                // Get a single article at position i within the list of articles.
                JSONObject currentNews = newsArray.getJSONObject(i);

                String author;

                JSONArray tagsArray = currentNews.getJSONArray(context.getString(R.string.tags));
                
                if (tagsArray.length() > 0) {
                    JSONObject currentTag = tagsArray.getJSONObject(0);
                    author = currentTag.getString(context.getString(R.string.web_title));
                } else author = context.getString(R.string.no_author);

                // Extract the value for the key called "sectionName"
                String section = currentNews.getString(context.getString(R.string.section_name));

                // Extract the value for the key called "webTitle"
                String title = currentNews.getString(context.getString(R.string.web_title));

                // Extract the value for the key called "time"
                String time = currentNews.getString(context.getString(R.string.web_publication_date));

                // Extract the value for the key called "url"
                String url = currentNews.getString(context.getString(R.string.web_url));

                // Create a new {@link News} object with the section, title, time,
                // and url from the JSON response.
                News news = new News(author, section, title, time, url);

                // Add the new {@link News} to the list of articles.
                newest.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, context.getString(R.string.problem_parsing), e);
        }
        // Return the list of articles
        return newest;
    }
}
