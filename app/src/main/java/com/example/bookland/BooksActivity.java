package com.example.bookland;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    String QUERY, query_copy;
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
                    query_copy = query;
                    Uri baseUri = Uri.parse(SEARCH_QUERY_URL);
                    Uri.Builder uriBuilder = baseUri.buildUpon();
                    uriBuilder.appendQueryParameter("q", query);

                    QUERY = uriBuilder.toString();
                    Log.e(TAG, "Complete url for entered query is " + QUERY);
                    mAdapter.clear();
                    LoaderManager.getInstance(BooksActivity.this).restartLoader(INITIAL_LOADER_ID, null, BooksActivity.this);
                    searchView.clearFocus();

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            };

    /**
     * onCreate method is called when the activity is made. This method contains the code which inflates the gridview with the contents provided by the
     * {@link BookAdapter} . It also starts loader when the activity is created.
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        LoaderManager.getInstance(this).initLoader(INITIAL_LOADER_ID, null, this);
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        GridView booksview = findViewById(R.id.booksList);
        booksview.setEmptyView(findViewById(R.id.emptyView));
        booksview.setAdapter(mAdapter);
        if (booksview.getCount() == 0) {
                LinearLayout linearLayout = findViewById(R.id.search_first);
                linearLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when the user clicks on the cardview. This method displays a toast to tell the user to click on Learn more
     * provided on the lower side of the card to know more about the book.
     *
     */
    public void show_toast(View v) {
        int duration = LENGTH_SHORT;
        Context context = getApplicationContext();
        Toast.makeText(context, "Click on learn more to see more details", duration).show();
    }

    /**
     * CreateLoader is the first method which gets called when the loader is created.
     * This checks the network connection of the phone and shows message if the
     * phone is not connected to the internet and calls the constructor of the {@link BookLoader} class
     * and passes context and the whole URL formed. If the phone is connected to the internet
     * and the query entered in the SearchView is not null then the method
     * forms uniform resource locater(Uri) by attaching the string entered in the SearchView
     * with the URL of the Google Books API and calls the constructor of the {@link BookLoader}.
     *
     */

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()&&query_copy!=null) {
            LinearLayout linearLayout2=findViewById(R.id.noConnectionView);
            linearLayout2.setVisibility(View.GONE);
            LinearLayout linearLayout = findViewById(R.id.search_first);
            linearLayout.setVisibility(View.GONE);
            View loadingIndicator = findViewById(R.id.progress_horizontal);
            loadingIndicator.setVisibility(View.VISIBLE);
            LinearLayout linearLayout1=findViewById(R.id.emptyView);
            ImageView imageView = findViewById(R.id.image_no_books);
            TextView textView = findViewById(R.id.text_no_books);
            linearLayout1.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String maxresults = sharedPrefs.getString(getString(R.string.settings_max_results_key), getString(R.string.settings_max_results_default));
            String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));
            String lang_pref=sharedPrefs.getString(getString(R.string.settings_lang_preference_key),getString(R.string.settings_lang_preference_default));
            Uri baseUri = Uri.parse(SEARCH_QUERY_URL);
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("q", query_copy);
            uriBuilder.appendQueryParameter("orderBy", orderBy);
            uriBuilder.appendQueryParameter("maxResults", maxresults);
            uriBuilder.appendQueryParameter("langRestrict",lang_pref);

            QUERY = uriBuilder.toString();
            Log.e(TAG, "Complete url for entered query is " + QUERY);

            return new BookLoader(this, QUERY);

        }
        //If the phone is not connected to internet and the query in the SearchView is not null
        else if(networkInfo==null&&query_copy!=null)
        {
            LinearLayout linearLayout = findViewById(R.id.search_first);
            linearLayout.setVisibility(View.GONE);
            View loadingIndicator = findViewById(R.id.progress_horizontal);
            loadingIndicator.setVisibility(View.GONE);
            LinearLayout linearLayout2=findViewById(R.id.noConnectionView);
            linearLayout2.setVisibility(View.VISIBLE);
            LinearLayout linearLayout1=findViewById(R.id.emptyView);
            ImageView imageView = findViewById(R.id.image_no_books);
            TextView textView = findViewById(R.id.text_no_books);
            linearLayout1.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            return new BookLoader(this, QUERY);
        }
        else
            return new BookLoader(this, QUERY);
    }

    /**
     * Called when the the loader is about to finish. This method checks that whether any result was
     * found for the query entered and if no books were found then it displays message showing that and
     * clears the {@link BookAdapter}. If the results were found then it hides the LinearLayouts showing
     * the messages and adds the data of the books to the adapter.
     */

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        View loadingIndicator = findViewById(R.id.progress_horizontal);
        loadingIndicator.setVisibility(View.GONE);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        LinearLayout linearLayout=findViewById(R.id.emptyView);
        ImageView imageView = findViewById(R.id.image_no_books);
        TextView textView = findViewById(R.id.text_no_books);
        if (QueryUtils.numberOfResults==0&&query_copy!=null&&networkInfo!=null) {
            LinearLayout linearLayout2=findViewById(R.id.noConnectionView);
            linearLayout2.setVisibility(View.GONE);
            LinearLayout linearLayout1 = findViewById(R.id.search_first);
            linearLayout1.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.no_books);
            textView.setText(R.string.no_books);
        }

        mAdapter.clear();

        // If there is a valid list of books, then add them to the adapter's
        // data set. This will trigger the GridView to update.
        if (books != null && !books.isEmpty()) {
            LinearLayout linearLayout2=findViewById(R.id.noConnectionView);
            linearLayout2.setVisibility(View.GONE);
            LinearLayout linearLayout1 = findViewById(R.id.search_first);
            linearLayout1.setVisibility(View.GONE);
            mAdapter.addAll(books);
        }
    }

    /**
     * Called to reset the loader.
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

    /**
     * Called to show the menu items in the toolbar and define their properties
     */
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

    /**
     * Called when the user selects any of the items showed in the menu and performs required
     * action according to the id of the item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
