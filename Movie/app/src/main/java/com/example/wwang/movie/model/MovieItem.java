package com.example.wwang.movie.model;

public class MovieItem {

    public int mId;
    public String mTitle;
    public String mYear;
    public String mImdbId;
    public String mType;


    public MovieItem(int id, String title, String year, String imdbId, String type){
        this.mId = id;
        this.mTitle = title;
        this.mYear = year;
        this.mImdbId = imdbId;
        this.mType = type;
    }

    public int getmId() { return mId; }

    public String getmTitle() { return mTitle; }

    public String getmYear() { return mYear; }

    public String getmImdbId() { return mImdbId; }

    public String getmType() { return mType; }
}
