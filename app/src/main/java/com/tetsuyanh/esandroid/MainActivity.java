package com.tetsuyanh.esandroid;

import com.tetsuyanh.esandroid.fragment.WebFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WebFragment.OnFragmentInteractionListener {

    private final String TAG = MainActivity.class.getSimpleName();

    private WebFragment mWebFragment;

    private MenuItem menuBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "----- onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // web fragment
        mWebFragment = new WebFragment();
        FragmentManager fragMgr = getSupportFragmentManager();
        FragmentTransaction trans = fragMgr.beginTransaction();
        trans.add(R.id.fragment_container, mWebFragment);
        trans.addToBackStack(null);
        trans.commit();

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
        } else if (!mWebFragment.didBacked()) {
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
                if (mWebFragment.addBookmark()) {
                    showToast("(\\( ⁰⊖⁰ )/)");
                    item.getIcon().setAlpha(255);
                } else {
                    showToast("failed");
                }
            } else {
                if (mWebFragment.removeBookmark()) {
                    showToast("──=≡=͟͟͞͞(\\( ⁰⊖⁰)/)");
                    item.getIcon().setAlpha(128);
                } else {
                    showToast("failed");
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /*try {
            String title = (String) item.getTitle();
            String[] parts = title.split(":");
            mWebView.loadUrl(EsaWeb.getPostUrl(mCurrentTeam, Integer.parseInt(parts[0])));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }*/
        // fragment

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onChangeTeam(String teamName) {
        Log.d(TAG, "onChangeTeam");
        TextView text = (TextView)findViewById(R.id.nav_header_label);
        text.setText(teamName);
        text.invalidate();
    }

    @Override
    public void onUpdateBookmark(boolean visibility, boolean isBookMarked) {
        Log.d(TAG, "onUpdateBookmarkable");
        if (menuBookmark != null) {
            menuBookmark.setVisible(visibility);

            if (visibility) {
                menuBookmark.getIcon().setAlpha(isBookMarked ? 255 : 128);
            }
        }
    }
}
