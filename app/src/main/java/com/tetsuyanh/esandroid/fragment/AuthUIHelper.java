package com.tetsuyanh.esandroid.fragment;

import com.tetsuyanh.esandroid.R;

import android.support.v4.os.CancellationSignal;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;

public class AuthUIHelper extends FingerprintManagerCompat.AuthenticationCallback {
    private static final String TAG = AuthUIHelper.class.getSimpleName();

    static final long ERROR_TIMEOUT_MILLIS = 1600;
    static final long SUCCESS_DELAY_MILLIS = 1300;

    private final FingerprintManagerCompat mFPMgr;
    private final Callback mCallback;
    private CancellationSignal mCancellationSignal;
    private boolean mIsCancelled;

    public static class Builder {
        private final FingerprintManagerCompat mFPMgr;

        public Builder(FingerprintManagerCompat fpMgr) {
            mFPMgr = fpMgr;
        }

        public AuthUIHelper build(Callback callback) {
            return new AuthUIHelper(mFPMgr, callback);
        }
    }

    private AuthUIHelper(FingerprintManagerCompat fPMgr, Callback callback) {
        mFPMgr = fPMgr;
        mCallback = callback;
    }

    public boolean isFingerprintAuthAvailable() {
        return mFPMgr.isHardwareDetected() && mFPMgr.hasEnrolledFingerprints();
    }

    public void startAuth(FingerprintManagerCompat.CryptoObject cryptoObject) {
        if (!isFingerprintAuthAvailable()) {
            return;
        }
        mCancellationSignal = new CancellationSignal();
        mIsCancelled = false;
        mFPMgr.authenticate(cryptoObject, 0, mCancellationSignal, this, null);
    }

    public void stopAuth() {
        if (mCancellationSignal != null) {
            mIsCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Log.d(TAG, "onAuthenticated");
        if (!mIsCancelled) {
            mCallback.onAuthFailed();
        }
    }

    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Log.d(TAG, "onAuthenticationHelp: " + helpMsgId);
        mCallback.onAuthHelp(helpString.toString());
    }

    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        Log.d(TAG, "onAuthenticationSucceeded");
        mCallback.onAuthSucceeded();
    }

    public void onAuthenticationFailed() {
        Log.e(TAG, "onAuthenticationFailed");
        mCallback.onAuthFailed();
    }

    public interface Callback {
        void onAuthSucceeded();
        void onAuthFailed();
        void onAuthHelp(String msg);
    }
}
