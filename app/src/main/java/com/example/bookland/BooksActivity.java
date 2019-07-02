package com.example.bookland;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class BooksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String TAG = BooksActivity.class.getName();
    private static final int INITIAL_LOADER_ID = 1;
    /**
     * Initial part of the url used when a search query is entered
     */
    private static final String SEARCH_QUERY_URL = "https://www.googleapis.com/books/v1/volumes?";
    SearchView searchView;
    /**
     * This variable will contain the url that the loader will pass to asyncTask
     */
    String QUERY;
    private BookAdapter mAdapter;
    private final SearchView.OnQueryTextListener searchViewOnQueryTextListener =
            new SearchView.OnQueryTextListener() {

                /**
                 * Called when the user submits the search query. Returns early if the search
                 * query hasn't changed since the last submission. Clears the static
                 * {@link BooksActivity} and (re)starts the loader that fetches the
                 * search results using the
                 * <a href="https://developers.google.com/books/docs/overview"> Google Books
                 * API</a>.
                 *
                 * @see BooksActivity
                 * @return true, since the query has already been handled
                 */
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Uri baseUri = Uri.parse(SEARCH_QUERY_URL);
                    Uri.Builder uriBuilder = baseUri.buildUpon();
                    uriBuilder.appendQueryParameter("q", query);
                    uriBuilder.appendQueryParameter("orderBy", "newest");

                    QUERY = uriBuilder.toString();
                    Log.e(TAG,"Complete url for entered query is "+QUERY);
                    LoaderManager.getInstance(BooksActivity.this).restartLoader(INITIAL_LOADER_ID, null, BooksActivity.this);
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        LoaderManager.getInstance(this).initLoader(INITIAL_LOADER_ID, null, this);

        GridView booksview = findViewById(R.id.booksList);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        booksview.setAdapter(mAdapter);

    }

    public void show_toast(View v){
                int duration= LENGTH_SHORT;
                Context context=getApplicationContext();
                Toast.makeText(context,"Click on learn more to see more details",duration).show();
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
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search Google Books");
        searchView.setIconifiedByDefault(true);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        searchView.setSubmitButtonEnabled(true);
        // To display icon on overflow menu
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        searchView.setOnQueryTextListener(searchViewOnQueryTextListener);
        return true;
    }

}
