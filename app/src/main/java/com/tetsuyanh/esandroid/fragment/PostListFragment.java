package com.tetsuyanh.esandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tetsuyanh.esandroid.R;
import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.service.BookmarkService;
import com.tetsuyanh.esandroid.service.HistoryService;

import java.util.List;

public class PostListFragment extends Fragment {
    public enum Kind {
        KIND_BOOKMARK(1),
        KIND_HISTORY(2);

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

    public PostListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getActivity().getApplicationContext();
        mBookmarkService = new BookmarkService(context);
        mHistoryService = new HistoryService(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postlist, container, false);

        // Set the adapter
        List<Post> list;
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            switch (mKind) {
                case KIND_BOOKMARK:
                    list = mBookmarkService.getList(mTeam);
                    break;
                case KIND_HISTORY:
                default:
                    list = mHistoryService.getList(mTeam);
                    break;
            }
            recyclerView.setAdapter(new PostListRecyclerViewAdapter(list, mListener));
            recyclerView.addItemDecoration(new PostListDivider(getActivity().getApplicationContext()));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
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
        super.onDetach();
        mListener = null;
    }

    public String getTeam() {
        return mTeam;
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
