package com.tetsuyanh.esandroid;

import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.entity.Url;
import com.tetsuyanh.esandroid.esa.EsaWeb;
import com.tetsuyanh.esandroid.fragment.AuthDialogFragment;
import com.tetsuyanh.esandroid.fragment.PostListFragment;
import com.tetsuyanh.esandroid.fragment.WebFragment;
import com.tetsuyanh.esandroid.service.FingerprintService;
import com.tetsuyanh.esandroid.service.LockService;

import android.content.Context;
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
        PostListFragment.OnPostListFragmentInteractionListener,
        AuthDialogFragment.OnAuthDialogFragmentInteractionListener {

    private final String TAG = MainActivity.class.getSimpleName();

    private WebFragment mWebFragment;
    private PostListFragment mPostListFragment;
    private AuthDialogFragment mAuthDialogFragment;

    private LockService mLockService;
    private FingerprintService mFingerprintService;

    private MenuItem menuBookmark;
    private MenuItem menuLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "----- onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Context context = getApplicationContext();
        mLockService = new LockService(context);
        mFingerprintService = new FingerprintService(context);

        // drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuthDialogFragment = new AuthDialogFragment();

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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        menuBookmark = menu.findItem(R.id.action_bookmark);
        menuLock = menu.findItem(R.id.action_lock);

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
        } else if (id == R.id.action_lock) {
            if (!mFingerprintService.isUsable()) {
                showToast(R.string.toast_fingerprint_not_unavailable);
            } else if (!mFingerprintService.isEnrolled()) {
                showToast(R.string.toast_fingerprint_not_enrolled);
            } else {
                AuthDialogFragment.State state = item.getIcon().getAlpha() == alphaOff ? AuthDialogFragment.State.LOCK : AuthDialogFragment.State.UNLOCK;
                mAuthDialogFragment.initialize(state, mWebFragment.getUrl(), mFingerprintService.getCryptObject());
                mAuthDialogFragment.show(getSupportFragmentManager(), TAG);
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
                case R.id.nav_lock:
                    mPostListFragment.setKind(PostListFragment.Kind.KIND_LOCK);
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

    private void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onChangeTeam(String teamName) {
        Log.d(TAG, "onChangeTeam");
        TextView text = (TextView) findViewById(R.id.nav_header_label);
        text.setText(teamName);
        text.invalidate();
    }

    public void onNeedAuth(String url) {
        mAuthDialogFragment.initialize(AuthDialogFragment.State.UPDATE, url, mFingerprintService.getCryptObject());
        mAuthDialogFragment.show(getSupportFragmentManager(), TAG);
    }

    @Override
    public void onUpdateBookmark(boolean visibility, boolean isBookMarked) {
        Log.d(TAG, "onUpdateBookmark");
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

    @Override
    public void onUpdateLock(boolean visibility, boolean isLocked) {
        Log.d(TAG, "onUpdateLock");
        if (menuLock != null) {
            menuLock.setVisible(visibility);

            if (visibility) {
                int alphaOn = getResources().getInteger(R.integer.state_alpha_on);
                int alphaOff = getResources().getInteger(R.integer.state_alpha_off);
                menuLock.getIcon().setAlpha(isLocked ? alphaOn : alphaOff);
            }
        }
    }

    @Override
    public void onAuthSucceeded(AuthDialogFragment.State state, String url) {
        Log.d(TAG, "onAuthSucceeded state:" + state + ", url:" + url);
        String teamName = EsaWeb.getTeam(url);
        Integer postId = EsaWeb.getPostId(url);

        switch (state) {
            case LOCK:
                String title = mWebFragment.getTitle();
                if (title != null && mLockService.push(teamName, new Post(postId, title))) {
                    onUpdateLock(true, true);
                    showToast(R.string.toast_esa_fine);
                    return;
                }
                break;
            case UNLOCK:
                if (mLockService.pop(teamName, postId)) {
                    onUpdateLock(true, false);
                    showToast(R.string.toast_esa_leave);
                    return;
                }
                break;
            case UPDATE:
                if (mLockService.update(teamName, postId)) {
                    mWebFragment.load(url);
                    return;
                }
                break;
            default:
                throw new RuntimeException("must implement onAuthSucceeded state: " + state.toString());
        }
        showToast(R.string.toast_failed);
    }

    @Override
    public void onAuthFailed() {
        Log.d(TAG, "onAuthFailed");
        showToast(R.string.toast_failed);

    }

    @Override
    public void onAuthHelpMsg(String msg) {
        Log.d(TAG, "onAuthHelpMsg: " + msg);
        showToast(msg);

    }
}
