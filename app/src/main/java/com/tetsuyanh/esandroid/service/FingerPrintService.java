package com.tetsuyanh.esandroid.service;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyProperties;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.util.Log;

import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

/**
 * Created by tetsuyanh on 2017/01/01.
 */

public class FingerprintService {
    private static final String TAG = FingerprintService.class.getSimpleName();

    private FingerprintManagerCompat mFPManager;
    private KeyStore mKeyStore;
    private Cipher mCipher;
    private FingerprintManagerCompat.CryptoObject mCryptoObject;
    private CancellationSignal mCancelSignal;

    public FingerprintService(Context context) {
        mFPManager = FingerprintManagerCompat.from(context);
        Log.d(TAG, "FP hardware : " + mFPManager.isHardwareDetected());
        Log.d(TAG, "FP has fp : " + mFPManager.hasEnrolledFingerprints());
    }

    public boolean isUsable() {
        return mFPManager.isHardwareDetected();
    }

    public boolean isEnrolled() {
        return mFPManager.hasEnrolledFingerprints();
    }

    public boolean initialize() {
        try{
            String keyName = "mykey";
            String keyStoreName = "AndroidKeyStore";

            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return false;
    }

    public FingerprintManagerCompat.CryptoObject getCryptObject() {
        return new FingerprintManagerCompat.CryptoObject(mCipher);
    }

}
