package com.example.wwang.movie;

public class MovieDetails {

    public String mId;
    public String mTitle;
    public String mYear;
    public String mImdbId;
    public String mType;
    public String mPlot;
    public String mPoster;


    public MovieDetails(String id, String title, String year, String imdbId, String type, String plot, String poster){
        this.mId = id;
        this.mTitle = title;
        this.mYear = year;
        this.mImdbId = imdbId;
        this.mType = type;
        this.mPlot = plot;
        this.mPoster = poster;
    }

    public String getmId() { return mId; }

    public String getmTitle() { return mTitle; }

    public String getmYear() { return mYear; }

    public String getmImdbId() { return mImdbId; }

    public String getmType() { return mType; }

    public String getmPlot() { return mPlot; }

    public String getmPoster() { return mPoster; }
}
