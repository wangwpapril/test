package com.example.wwang.movie.model;

public class MovieItem {

    private int mId;
    private String mTitle;
    private String mYear;
    private String mImdbId;
    private String mType;

    public MovieItem(){

    }

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

    public void setmId(int id) {
        this.mId = id;
    }

    public void setmTitle(String title) {
        this.mTitle = title;
    }

    public void setmImdbId(String imdbId) {
        this.mImdbId = imdbId;
    }
}
