package com.tetsuyanh.esandroid;

import com.tetsuyanh.esandroid.esa.EsaWeb;
import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.db.Data;
import com.tetsuyanh.esandroid.db.DataSQLite;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final String TAG = "MainActivity";

    private WebView mWebView;
    private View mLoadingSpinner;

    private String mCurrentTeam;

    private Data mData;

    private FloatingActionButton mFabAdd;
    private FloatingActionButton mFabRemove;
    private TextView mNavText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "----- onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // data
        mData = new DataSQLite(getApplicationContext());

        // fab
        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer postId = EsaWeb.GetPostId(mWebView.getUrl());
                String title = EsaWeb.GetTitle(mWebView.getTitle());
                if (postId != null) {
                    if (mData.PushBookmark(mCurrentTeam, new Post(postId, title))) {
                        mFabAdd.setVisibility(View.GONE);
                        mFabRemove.setVisibility(View.VISIBLE);
                        updateDrawerMenu();
                        showToast("(\\( ⁰⊖⁰ )/)");
                    } else {
                        showToast("failed");
                    }
                }
            }
        });
        FloatingActionButton fab_remove = (FloatingActionButton) findViewById(R.id.fab_remove);
        fab_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer postId = EsaWeb.GetPostId(mWebView.getUrl());
                if (postId != null) {
                    if (mData.PopBookmark(mCurrentTeam, postId)) {
                        mFabRemove.setVisibility(View.GONE);
                        mFabAdd.setVisibility(View.VISIBLE);
                        updateDrawerMenu();
                        showToast("──=≡=͟͟͞͞(\\( ⁰⊖⁰)/)");
                    } else {
                        showToast("failed");
                    }
                }
            }
        });

        // drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLoadingSpinner = findViewById(R.id.loading_spinner);
        mFabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        mFabRemove = (FloatingActionButton) findViewById(R.id.fab_remove);
        mNavText = (TextView)findViewById(R.id.nav_header_label);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        String url = mData.GetLatestUrl();
        if (url == null) {
            url = EsaWeb.URL_TOP;
        }
        mWebView.loadUrl(url);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "----- onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "----- onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "----- onStop");
        super.onStop();
        getDelegate().onStop();
    }

    /** WebViewClientクラス */
    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            mLoadingSpinner.setVisibility(View.VISIBLE);
            mFabAdd.setVisibility(View.GONE);
            mFabRemove.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            mLoadingSpinner.setVisibility(View.GONE);

            // チームドメインのみ管理対象
            String team = EsaWeb.GetTeam(url);
            if (team != null) {
                mData.SetLatestUrl(url);

                boolean shouldUpdateMenu = false;
                if (mCurrentTeam == null || !mCurrentTeam.equals(team)) {
                    mCurrentTeam = team;
                    updateDrawerTitle(mCurrentTeam);
                    shouldUpdateMenu = true;
                }

                // 記事のみfabを表示
                Integer postId = EsaWeb.GetPostId(url);
                if (postId != null) {
                    Boolean isBookMarked = mData.HasBookmark(team, postId);
                    mFabAdd.setVisibility(isBookMarked ? View.GONE : View.VISIBLE);
                    mFabRemove.setVisibility(!isBookMarked ? View.GONE : View.VISIBLE);

                    String title = EsaWeb.GetTitle(mWebView.getTitle());
                    //mData.PushHistory(mCurrentTeam, new Post(postId, title));
                    shouldUpdateMenu = true;
                }

                if (shouldUpdateMenu) {
                    updateDrawerMenu();
                }
            }
        }

        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.d(TAG, "----- onReceivedError");
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload) {
            mWebView.reload();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String title = (String)item.getTitle();
        String[] parts = title.split(":");

        mWebView.loadUrl(EsaWeb.GetPost(mCurrentTeam, parts[0]));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateDrawerTitle(String team) {

        TextView text = (TextView)findViewById(R.id.nav_header_label);
        text.setText(team);
        text.invalidate();
    }

    private void updateDrawerMenu() {
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();

        menu.clear();

        List<Post> bookmarks = mData.GetBookmarks(mCurrentTeam);
        if (bookmarks != null && bookmarks.size() > 0) {
            Menu menuBookmarks = menu.addSubMenu(R.string.drawer_bookmarks);
            for (Post p : bookmarks) {
                menuBookmarks.add(p.GetId() + ":" + p.GetTitle());
            }
        }

        /*List<Post> histories = mData.GetHistories(mCurrentTeam);
        Menu menuHistories = menu.addSubMenu(R.string.drawer_histories);
        for(Post p: histories) {
            menuHistories.add(p.GetId() + ":" + p.GetTitle());
        }*/

        navView.invalidate();
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}
