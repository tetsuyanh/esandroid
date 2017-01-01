package com.tetsuyanh.esandroid.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tetsuyanh.esandroid.R;
import com.tetsuyanh.esandroid.entity.Post;
import com.tetsuyanh.esandroid.fragment.PostListFragment.OnPostListFragmentInteractionListener;

import java.util.List;

class PostListRecyclerViewAdapter extends RecyclerView.Adapter<PostListRecyclerViewAdapter.ViewHolder> {
    private final List<Post> mValues;
    private final OnPostListFragmentInteractionListener mListener;

    PostListRecyclerViewAdapter(List<Post> items, OnPostListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Post post = mValues.get(position);
        holder.mItem = post;
        holder.mId.setText(String.valueOf(post.getId()));
        String path = "";
        String title = post.getTitle();
        int idx = title.lastIndexOf("/");
        if (idx != -1) {
            path = title.substring(0, idx+1);
            title = title.substring(idx+1, title.length());
        }
        holder.mPath.setText(path);
        holder.mTitle.setText(title);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onSelectPost(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mId;
        final TextView mPath;
        final TextView mTitle;
        Post mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mId = (TextView) view.findViewById(R.id.id);
            mPath = (TextView) view.findViewById(R.id.path);
            mTitle = (TextView) view.findViewById(R.id.title);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPath.getText() + "/" + mTitle.getText() + "'";
        }
    }
}
