package com.tetsuyanh.esandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tetsuyanh.esandroid.R;
import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.service.BookmarkService;
import com.tetsuyanh.esandroid.service.HistoryService;

import java.util.List;

public class PostListFragment extends Fragment {
    private static final String TAG = PostListFragment.class.getSimpleName();
    public enum Kind {
        UNDEFINED(0),
        BOOKMARK(1),
        HISTORY(2);

        private final int id;
        Kind(final int id) {
            this.id = id;
        }
        public int getInt() {
            return this.id;
        }
    }
    public static Kind getKind(final int id) {
        Kind[] kinds = Kind.values();
        for (Kind kind : kinds) {
            if (kind.getInt() == id) {
                return kind;
            }
        }
        return null;
    }

    private String mTeam;
    private Kind mKind;
    private OnPostListFragmentInteractionListener mListener;

    private BookmarkService mBookmarkService;
    private HistoryService mHistoryService;
    private PostListDivider mDivider;

    public PostListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "//////// onCreate:");
        super.onCreate(savedInstanceState);

        Context context = getActivity().getApplicationContext();
        mBookmarkService = new BookmarkService(context);
        mHistoryService = new HistoryService(context);
        mDivider = new PostListDivider(context);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "//////// onCreateView:");
        View view = inflater.inflate(R.layout.fragment_postlist, container, false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.postlist);
        TextView emptyText = (TextView)view.findViewById(R.id.empty);

        List<Post> list = null;
        switch (mKind) {
            case BOOKMARK:
                list = mBookmarkService.getList(mTeam);
                break;
            case HISTORY:
                list = mHistoryService.getList(mTeam);
                break;
        }

        if (mKind != Kind.UNDEFINED && recyclerView != null && list != null && list.size() > 0) {
            Context context = recyclerView.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new PostListRecyclerViewAdapter(list, mListener));
            recyclerView.addItemDecoration(mDivider);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "//////// onAttach:");
        super.onAttach(context);
        if (context instanceof OnPostListFragmentInteractionListener) {
            mListener = (OnPostListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "//////// onDetach:");
        super.onDetach();
        mListener = null;
    }

    public void setTeam(String team) {
        mTeam = team;
    }

    public void setKind(Kind kind) {
        mKind = kind;
    }


    public interface OnPostListFragmentInteractionListener {
        void onSelectPost(Post post);
    }
}
