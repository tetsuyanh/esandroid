package com.tetsuyanh.esandroid.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tetsuyanh.esandroid.R;
import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.entity.Url;
import com.tetsuyanh.esandroid.esa.EsaWeb;
import com.tetsuyanh.esandroid.service.BookmarkService;
import com.tetsuyanh.esandroid.service.HistoryService;
import com.tetsuyanh.esandroid.service.UrlService;

public class WebFragment extends Fragment {
    private final String TAG = WebFragment.class.getSimpleName();

    private OnWebFragmentInteractionListener mListener;

    private UrlService mUrlService;
    private BookmarkService mBookmarkService;
    private HistoryService mHistoryService;

    private WebView mWebView;
    private View mLoadingSpinner;
    private String mCurrentTeam;

    public WebFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getActivity().getApplicationContext();
        mUrlService = new UrlService(context);
        mBookmarkService = new BookmarkService(context);
        mHistoryService = new HistoryService(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_web, container, false);

        // views
        mLoadingSpinner = v.findViewById(R.id.loading_spinner);

        // webView
        mWebView = (WebView) v.findViewById(R.id.webview);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        Url url = mUrlService.getLatestUrl(null);
        if (url != null) {
            mWebView.loadUrl(url.getUrl());
        } else {
            mWebView.loadUrl(EsaWeb.URL_ROOT);
        }

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWebFragmentInteractionListener) {
            mListener = (OnWebFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWebFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnWebFragmentInteractionListener {
        void onChangeTeam(String teamName);
        void onUpdateBookmark(boolean visibility, boolean isBookMarked);
    }

    public boolean didBacked() {
        if (mWebView != null && mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return false;
    }

    public boolean addBookmark() {
        Integer postId = EsaWeb.getPostId(mWebView.getUrl());
        String title = EsaWeb.getPostTitle(mWebView.getTitle());
        return postId != null && mBookmarkService.push(mCurrentTeam, new Post(postId, title));
    }

    public boolean removeBookmark() {
        Integer postId = EsaWeb.getPostId(mWebView.getUrl());
        return postId != null && mBookmarkService.pop(mCurrentTeam, postId);
    }

    public void load(Post post) {
        mWebView.loadUrl(EsaWeb.getPostUrl(mCurrentTeam, post.getId()));
    }

    public String getTeam() {
        return mCurrentTeam;
    }

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

            mListener.onUpdateBookmark(false, false);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            mLoadingSpinner.setVisibility(View.GONE);

            String team = EsaWeb.getTeam(url);
            if (team != null) {
                mUrlService.setLatestUrl(team, url);

                if (!team.equals(mCurrentTeam)) {
                    mCurrentTeam = team;
                    mListener.onChangeTeam(mCurrentTeam);
                }

                Integer postId = EsaWeb.getPostId(url);
                if (postId != null) {
                    mListener.onUpdateBookmark(true, mBookmarkService.has(team, postId));

                    String title = EsaWeb.getPostTitle(mWebView.getTitle());
                    mHistoryService.push(team, new Post(postId, title));
                }
            }
        }

        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.d(TAG, "----- onReceivedError");
        }

        private boolean urlLoading(String url) {
            // open external web page in browser app
            if (!EsaWeb.isInternal(url)) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
                return true;
            }
            // reload next team latest url if it existed and not root
            String team = EsaWeb.getTeam(url);
            if (team != null && !team.equals(mCurrentTeam) && EsaWeb.isPathRoot(url)) {
                Url urlLatest = mUrlService.getLatestUrl(team);
                if (urlLatest != null && !EsaWeb.isPathRoot(urlLatest.getUrl())) {
                    mWebView.loadUrl(urlLatest.getUrl());
                    return true;
                }
            }
            return false;
        }
    };

}
