package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String NO_AUTHOR = "no author";

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context of the app
     * @param newest  is the list of earthquakes, which is the data source of the adapter
     */
    NewsAdapter(@NonNull Context context, List<News> newest) {
        super(context, 0, newest);
    }

    /**
     * Returns a list item view that displays a news article at the given position
     * in the list of news.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Find the news article at the given position in the list of news.
        News currentNews = getItem(position);

        TextView authorTextView = listItemView.findViewById(R.id.author_text_view);
        if (currentNews != null) {
            if (currentNews.getAuthor().equals(NO_AUTHOR)) {
                authorTextView.setText(R.string.author_unknown);
            } else {
                authorTextView.setText(currentNews.getAuthor());
            }
        }

        // Find the TextView with view ID section_text_view
        TextView sectionTextView = listItemView.findViewById(R.id.section_text_view);
        // Display the section of the current news article in that TextView
        if (currentNews != null) {
            sectionTextView.setText(currentNews.getSectionName());
        }

        // Find the TextView with view ID title_text_view
        TextView titleTextView = listItemView.findViewById(R.id.title_text_view);
        // Display the title of the current news article in that TextView
        if (currentNews != null) {
            titleTextView.setText(currentNews.getTitle());
        }

        // Create a String with the pattern that the Date and Time are coming in.
        String patternIncome = getContext().getString(R.string.dateTime_pattern_income);

        SimpleDateFormat dateFormat = new SimpleDateFormat(patternIncome, Locale.UK);

        // Change the date to our preferable pattern -> "EEE, d MMM, yyyy"
        Date myDate = null;
        try {
            if (currentNews != null) {
                myDate = dateFormat.parse(currentNews.getDateTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String datePatternOutcome = getContext().getString(R.string.date_pattern_outcome);
        SimpleDateFormat newDateFormat = new SimpleDateFormat(datePatternOutcome, Locale.UK);
        String outcomeDate = newDateFormat.format(myDate);

        // Find the TextView with view ID date
        TextView dateView = listItemView.findViewById(R.id.date);
        // Give the TextView dateView our preferable pattern
        dateView.setText(outcomeDate);

        // Change the time to our preferable pattern -> "kk:mm:ss"
        Date myTime = null;
        try {
            if (currentNews != null) {
                myTime = dateFormat.parse(currentNews.getDateTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String timePatternOutcome = getContext().getString(R.string.time_pattern_outcome);
        SimpleDateFormat newTimeFormat = new SimpleDateFormat(timePatternOutcome, Locale.UK);
        String outcomeTime = newTimeFormat.format(myTime);

        // Find the TextView with view ID time
        TextView timeView = listItemView.findViewById(R.id.time);
        // Give the TextView timeView our preferable pattern
        timeView.setText(outcomeTime);

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}
