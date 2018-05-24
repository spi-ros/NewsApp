package com.example.android.newsapp;

public class News {

    private String mAuthor;

    private String mSectionName;

    private String mTitle;

    private String mDateTime;

    private String mUrl;

    News(String author, String sectionName, String title, String dateTime, String url) {
        mAuthor = author;
        mSectionName = sectionName;
        mTitle = title;
        mDateTime = dateTime;
        mUrl = url;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getTitle() {
        return mTitle;
    }

    public String  getDateTime() {
        return mDateTime;
    }

    public String getUrl() {
        return mUrl;
    }
}
