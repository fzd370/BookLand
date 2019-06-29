package com.example.bookland;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BooksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>>{

    private static final String TAG = BooksActivity.class.getName();

    private EditText mBookInput;
    private BookAdapter mAdapter;
    private ImageButton mButton;
    private static final int INITIAL_LOADER_ID = 1;
    /** URL used to fetch latest published books (max 10) as default list view items */
    private static final String INITIAL_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=2018&orderBy=newest&langRestrict=en&maxResults=10";

    /** Initial part of the url used when a search query is entered */
    private static final String SEARCH_QUERY_URL = "https://www.googleapis.com/books/v1/volumes?";


    /** This variable will contain the url that the loader will pass to asyncTask */
    String QUERY;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        mBookInput= findViewById(R.id.search_query);
        mButton= findViewById(R.id.search_btn);
        LoaderManager.getInstance(this).initLoader(INITIAL_LOADER_ID, null, this);
        // Initialize the loader.
        QUERY = INITIAL_REQUEST_URL;
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Uri uriBuilder = Uri.parse(SEARCH_QUERY_URL).buildUpon()
                            .appendQueryParameter("q", mBookInput.getText().toString())
                            .appendQueryParameter("maxResults", "10")
                            .appendQueryParameter("printType", "books")
                            .build();
                    QUERY = uriBuilder.toString();
                // Hide the keyboard when the button is pushed.
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                 LoaderManager.getInstance(BooksActivity.this).restartLoader(INITIAL_LOADER_ID, null, BooksActivity.this);

            }
        });

        ListView booksview = findViewById(R.id.booksList);

        mAdapter= new BookAdapter(this, new ArrayList<Book>());

        booksview.setAdapter(mAdapter);

        booksview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentbook=mAdapter.getItem(position);
                Intent websiteIntent=new Intent(Intent.ACTION_VIEW,currentbook.getmInfoLink());
                startActivity(websiteIntent);
            }
        });


    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, QUERY);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setQueryHint("Search Google Books");
        searchView.setIconifiedByDefault(true);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        // To display icon on overflow menu
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        // searchView.setOnQueryTextListener(searchViewOnQueryTextListener);

        return true;
    }

    }
