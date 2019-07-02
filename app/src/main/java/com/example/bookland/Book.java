package com.example.bookland;

import android.net.Uri;

public class Book {

    private String mAuthor;
    private String mPubDate;
    private Uri mImageUrl;
    private Uri mInfoLink;

    public Book(String author,String pubdate, String imageurl, String infolink){
        mAuthor=author;
        mPubDate=pubdate;
        mImageUrl=Uri.parse(imageurl);
        mInfoLink=Uri.parse(infolink);
    }


    public String getmAuthor(){
        return mAuthor;
    }

    public String getmPubDate(){
        return mPubDate;
    }

    public Uri getmImageUrl(){
        return mImageUrl;
    }

    public Uri getmInfoLink(){
        return mInfoLink;
    }
}
