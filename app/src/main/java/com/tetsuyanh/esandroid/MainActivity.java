package com.tetsuyanh.esandroid;

import com.tetsuyanh.esandroid.esa.EsaWeb;
import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.service.BookmarkService;
import com.tetsuyanh.esandroid.service.HistoryService;
import com.tetsuyanh.esandroid.service.UrlService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

    private final String TAG = MainActivity.class.getSimpleName();

    private String mCurrentTeam;
    private UrlService mUrlService;
    private BookmarkService mBookmarkService;
    private HistoryService mHistoryService;

    private WebView mWebView;
    private View mLoadingSpinner;
    private FloatingActionButton mFabAdd;
    private FloatingActionButton mFabRemove;
    private View mNavLayount;
    private TextView mNavTitle;
    private GradientDrawable mNavBg;

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
        mFabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        mFabRemove = (FloatingActionButton) findViewById(R.id.fab_remove);

        // fab
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer postId = EsaWeb.GetPostId(mWebView.getUrl());
                String title = EsaWeb.GetTitle(mWebView.getTitle());
                if (postId != null) {
                    if (mBookmarkService.Push(mCurrentTeam, new Post(postId, title))) {
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
        mFabRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer postId = EsaWeb.GetPostId(mWebView.getUrl());
                if (postId != null) {
                    if (mBookmarkService.Pop(mCurrentTeam, postId)) {
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

        // webView
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        String url = mUrlService.GetLatestUrl();
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

            // update under team domain
            String team = EsaWeb.GetTeam(url);
            if (team != null) {
                mUrlService.SetLatestUrl(url);

                boolean shouldUpdateMenu = false;
                if (mCurrentTeam == null || !mCurrentTeam.equals(team)) {
                    mCurrentTeam = team;
                    updateDrawerHeader(mCurrentTeam);
                    shouldUpdateMenu = true;
                }

                Integer postId = EsaWeb.GetPostId(url);
                if (postId != null) {
                    Boolean isBookMarked = mBookmarkService.Has(team, postId);
                    mFabAdd.setVisibility(isBookMarked ? View.GONE : View.VISIBLE);
                    mFabRemove.setVisibility(!isBookMarked ? View.GONE : View.VISIBLE);

                    String title = EsaWeb.GetTitle(mWebView.getTitle());
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String title = (String)item.getTitle();
        String[] parts = title.split(":");

        mWebView.loadUrl(EsaWeb.GetPost(mCurrentTeam, parts[0]));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateDrawerHeader(String teamName) {
        Log.d(TAG, "teamName : " + teamName);
        if (mNavTitle == null) {
            mNavTitle = (TextView)findViewById(R.id.nav_header_label);
        }
        if (mNavLayount == null) {
            mNavLayount = (View)findViewById(R.id.nav_layout);
        }

        mNavTitle.setText(teamName);
        mNavTitle.invalidate();

        // background colors depend on title string like random
        mNavBg = (GradientDrawable)mNavLayount.getBackground();
        int hash = teamName.hashCode();
        int hashRed = (hash & 0x00FF0000) >> 16;
        int hashGreen = (hash & 0x0000FF00) >> 8;
        int hashBlue  = hash & 0x000000FF;
        int colorStart = Color.rgb(128 + 64 * hashRed / 256, 128 + 64 * hashGreen / 256, 128 + 64 * hashBlue / 256);
        int colorCenter = Color.rgb(80 + 96 * hashRed / 256, 80 + 96 * hashGreen / 256, 80 + 96 * hashBlue / 256);
        int colorEnd = Color.rgb(48 + 80 * hashRed / 256, 48 + 80 * hashGreen / 256, 48 + 80  * hashBlue / 256);
        int[] colors = {colorStart, colorCenter, colorEnd};
        mNavBg.setColors(colors);
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

}
