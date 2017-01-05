package com.tetsuyanh.esandroid;

import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.fragment.PostListFragment;
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
        WebFragment.OnWebFragmentInteractionListener,
        PostListFragment.OnPostListFragmentInteractionListener {

    private final String TAG = MainActivity.class.getSimpleName();

    private WebFragment mWebFragment;
    private PostListFragment mPostListFragment;

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
        if (mWebFragment == null) {
            mWebFragment = new WebFragment();
            FragmentManager fragMgr = getSupportFragmentManager();
            FragmentTransaction trans = fragMgr.beginTransaction();
            trans.add(R.id.web_container, mWebFragment);
            trans.commit();
        }
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
        } else if (getSupportFragmentManager().popBackStackImmediate()) {
            // nothing to do
        } else if (mWebFragment != null && mWebFragment.didBacked()) {
            // nothing to do
        } else{
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
        int alphaOn = getResources().getInteger(R.integer.state_alpha_on);
        int alphaOff = getResources().getInteger(R.integer.state_alpha_off);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bookmark) {
            if (item.getIcon().getAlpha() == alphaOff) {
                if (mWebFragment.addBookmark()) {
                    showToast(R.string.toast_esa_fine);
                    item.getIcon().setAlpha(alphaOn);
                } else {
                    showToast(R.string.toast_failed);
                }
            } else {
                if (mWebFragment.removeBookmark()) {
                    showToast(R.string.toast_esa_leave);
                    item.getIcon().setAlpha(alphaOff);
                } else {
                    showToast(R.string.toast_failed);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // fragment
        String team = mWebFragment.getTeam();
        if (team != null) {
            if (mPostListFragment == null) {
                mPostListFragment = new PostListFragment();
            }
            mPostListFragment.setTeam(team);
            switch (item.getItemId()) {
                case R.id.nav_bookmark:
                    mPostListFragment.setKind(PostListFragment.Kind.BOOKMARK);
                    break;
                case R.id.nav_history:
                default:
                    mPostListFragment.setKind(PostListFragment.Kind.HISTORY);
                    break;
            }

            FragmentManager fragMgr = getSupportFragmentManager();
            FragmentTransaction trans = fragMgr.beginTransaction();
            trans.add(R.id.postlist_container, mPostListFragment);
            trans.addToBackStack(null);
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            trans.commit();
        } else {
            showToast(R.string.toast_no_team);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showToast(int resId) {
        Toast toast = Toast.makeText(this, getResources().getString(resId), Toast.LENGTH_SHORT);
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
                int alphaOn = getResources().getInteger(R.integer.state_alpha_on);
                int alphaOff = getResources().getInteger(R.integer.state_alpha_off);
                menuBookmark.getIcon().setAlpha(isBookMarked ? alphaOn : alphaOff);
            }
        }
    }

    @Override
    public void onSelectPost(Post post) {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            Log.d(TAG, "popBackStack");
            mWebFragment.load(post);
        } else {
            Log.d(TAG, "failed to popBackStack");
        }
    }
}
