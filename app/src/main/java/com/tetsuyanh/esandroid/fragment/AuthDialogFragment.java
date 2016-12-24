package com.tetsuyanh.esandroid.fragment;

import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tetsuyanh.esandroid.R;
import com.tetsuyanh.esandroid.entity.Url;

public class AuthDialogFragment extends DialogFragment implements AuthUIHelper.Callback{
    public enum State {
        LOCK,
        UNLOCK,
        UPDATE
    }

    private static final String TAG = AuthDialogFragment.class.getSimpleName();

    private AuthUIHelper mAuthUIHelper;
    private State mState;
    private String mUrl;
    private FingerprintManagerCompat.CryptoObject mCryptoObject;
    private AuthDialogFragment.OnAuthDialogFragmentInteractionListener mListener;

    public AuthDialogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        getDialog().setTitle("Authentication");
        View v = inflater.inflate(R.layout.fragment_auth, container, false);
        Button mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        FingerprintManagerCompat fpMgr  = FingerprintManagerCompat.from(getActivity().getApplicationContext());
        mAuthUIHelper = new AuthUIHelper.Builder(fpMgr).build(this);

        return v;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mAuthUIHelper.startAuth(mCryptoObject);
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        mAuthUIHelper.stopAuth();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof OnAuthDialogFragmentInteractionListener) {
            mListener = (OnAuthDialogFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFingerprintDialogFragmentInteractionListener");
        }
    }

    public void initialize(State state, String url, FingerprintManagerCompat.CryptoObject cryptoObject) {
        mState = state;
        mUrl = url;
        mCryptoObject = cryptoObject;
    }

    @Override
    public void onAuthSucceeded() {
        Log.d(TAG, "onAuthSucceeded");
        mListener.onAuthSucceeded(mState, mUrl);
        dismiss();
    }

    @Override
    public void onAuthFailed() {
        Log.d(TAG, "onAuthFailed");
        mListener.onAuthFailed();
        dismiss();
    }

    @Override
    public void onAuthHelp(String msg) {
        Log.d(TAG, "onAuthHelp");
        mListener.onAuthHelpMsg(msg);
        dismiss();
    }

    public interface OnAuthDialogFragmentInteractionListener {
        void onAuthSucceeded(State state, String url);
        void onAuthFailed();
        void onAuthHelpMsg(String msg);
    }

}
