package com.example.wwang.movie;

public class MovieItem {

    public String mId;
    public String mTitle;
    public String mYear;
    public String mImdbId;
    public String mType;


    public MovieItem(String id, String title, String year, String imdbId, String type){
        this.mId = id;
        this.mTitle = title;
        this.mYear = year;
        this.mImdbId = imdbId;
        this.mType = type;
    }

    public String getmId() { return mId; }

    public String getmTitle() { return mTitle; }

    public String getmYear() { return mYear; }

    public String getmImdbId() { return mImdbId; }

    public String getmType() { return mType; }
}
