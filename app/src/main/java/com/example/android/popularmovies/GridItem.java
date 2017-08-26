package com.example.android.popularmovies;

/**
 * Created by anuj on 4/3/17.
 */

public class GridItem {

    private String mImageUrlSuffix;
    private String mTitle;
    private String mOverview;
    private double mRating;
    private String mReleaseDate;
    private String mMovieId;

    public GridItem(String mImageUrlSuffix, String mTitle, String mOverview, double mRating, String mReleaseDate, String mMovieId) {
        this.mImageUrlSuffix = mImageUrlSuffix;
        this.mTitle = mTitle;
        this.mOverview = mOverview;
        this.mRating = mRating;
        this.mReleaseDate = mReleaseDate;
        this.mMovieId = mMovieId;
    }

    public String getmImageUrlSuffix() {
        return mImageUrlSuffix;
    }

    public String getmMovieId(){return mMovieId;}

    public void setmImageUrlSuffix(String mImageView_id) {
        this.mImageUrlSuffix = mImageUrlSuffix;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmOverview() {
        return mOverview;
    }

    public void setmOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public double getmRating() {
        return mRating;
    }

    public void setmRating(double mRating) {
        this.mRating = mRating;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }
}
