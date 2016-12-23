package com.tetsuyanh.esandroid;

import com.tetsuyanh.esandroid.esa.EsaWeb;
import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.service.BookmarkService;
import com.tetsuyanh.esandroid.service.HistoryService;
import com.tetsuyanh.esandroid.service.UrlService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.support.annotation.NonNull;
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

    private final String TAG = MainActivity.class.getSimpleName();

    private String mCurrentTeam;
    private UrlService mUrlService;
    private BookmarkService mBookmarkService;
    private HistoryService mHistoryService;

    private WebView mWebView;
    private View mLoadingSpinner;
    private MenuItem menuBookmark;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "----- onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // services
        Context context = getApplicationContext();
        mUrlService = new UrlService(context);
        mBookmarkService = new BookmarkService(context);
        mHistoryService = new HistoryService(context);

        // views
        mLoadingSpinner = findViewById(R.id.loading_spinner);

        // drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // webView
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        String url = mUrlService.GetLatestUrl();
        if (url == null) {
            url = EsaWeb.URL_ROOT;
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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menuBookmark = menu.findItem(R.id.action_bookmark);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bookmark) {
            if (item.getIcon().getAlpha() == 128) {
                Integer postId = EsaWeb.getPostId(mWebView.getUrl());
                String title = EsaWeb.getPostTitle(mWebView.getTitle());
                if (postId != null) {
                    if (mBookmarkService.Push(mCurrentTeam, new Post(postId, title))) {
                        updateDrawerMenu();
                        showToast("(\\( ⁰⊖⁰ )/)");
                        item.getIcon().setAlpha(255);
                    } else {
                        showToast("failed");
                    }
                }
            } else {
                Integer postId = EsaWeb.getPostId(mWebView.getUrl());
                if (postId != null) {
                    if (mBookmarkService.Pop(mCurrentTeam, postId)) {
                        updateDrawerMenu();
                        showToast("──=≡=͟͟͞͞(\\( ⁰⊖⁰)/)");
                        item.getIcon().setAlpha(128);
                    } else {
                        showToast("failed");
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            String title = (String) item.getTitle();
            String[] parts = title.split(":");
            mWebView.loadUrl(EsaWeb.getPostUrl(mCurrentTeam, Integer.parseInt(parts[0])));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

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

        List<Post> bookmarks = mBookmarkService.GetList(mCurrentTeam);
        if (bookmarks != null && bookmarks.size() > 0) {
            Menu menuBookmarks = menu.addSubMenu(R.string.drawer_bookmarks);
            for (Post p : bookmarks) {
                menuBookmarks.add(p.GetId() + ":" + p.GetTitle());
            }
        }

        List<Post> histories = mHistoryService.GetList(mCurrentTeam);
        Menu menuHistories = menu.addSubMenu(R.string.drawer_histories);
        for(Post p: histories) {
            menuHistories.add(p.GetId() + ":" + p.GetTitle());
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /** WebViewClientクラス */
    private WebViewClient mWebViewClient = new WebViewClient() {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return urlLoading(url);
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return urlLoading(request.getUrl().toString());
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            mLoadingSpinner.setVisibility(View.VISIBLE);

            if (menuBookmark != null) menuBookmark.setVisible(false);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            mLoadingSpinner.setVisibility(View.GONE);

            // update under team domain
            String team = EsaWeb.getTeam(url);
            if (team != null) {
                mUrlService.SetLatestUrl(url);

                boolean shouldUpdateMenu = false;
                if (mCurrentTeam == null || !mCurrentTeam.equals(team)) {
                    mCurrentTeam = team;
                    updateDrawerTitle(mCurrentTeam);
                    shouldUpdateMenu = true;
                }

                Integer postId = EsaWeb.getPostId(url);
                if (postId != null) {
                    Boolean isBookMarked = mBookmarkService.Has(team, postId);
                    menuBookmark.setVisible(true);
                    menuBookmark.getIcon().setAlpha(isBookMarked? 255 : 128);

                    String title = EsaWeb.getPostTitle(mWebView.getTitle());
                    mHistoryService.Push(mCurrentTeam, new Post(postId, title));
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

        private boolean urlLoading(String url) {
            if (!EsaWeb.isHost(url)) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
                return true;
            }
            return false;
        }
    };

}
