package com.example.bookland;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    /**
     * Query URL
     */
    private String mUrl;

    // Constructor of the class which initialises the mUrl with the URL passed to the constructor
    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    //Starts to load loader
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This method performs the httprequest on the background thread so that the request doesn't block
     * the UI thread and stops the functioning of the app momentarily. This method calls the
     * fetchBooksData method of the {@link QueryUtils} and returns the list of books.
     */
    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Book> books = QueryUtils.fetchEarthquakeData(mUrl);
        return books;
    }
}
