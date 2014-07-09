package com.patrickmckinnon.homeaudio;

import android.util.Log;

/**
 * Created by prm on 1/21/14.
 */
public class Logger implements LoggerInterface {
    private static final String LOG_PREFIX = "BF_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;
    private final String TAG;
    private Logger(String tag) {
        TAG = tag;
    }

    public Logger(Class<?> classOfT) {
        this(makeLogTag(classOfT));
    }

    private static String makeLogTag(Class<?> classOfT) {
        if(BuildConfig.DEBUG) {
            return classOfT.getName();
        }
        else {
            String str = classOfT.getSimpleName();
            if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
                return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
            }

            return LOG_PREFIX + str;
        }
    }

    private static Logger tmpLog;

    // Global log interface used for printf debugging (code should not be checked-in using this interface)
    public static void TMP(String message) {
        if(BuildConfig.DEBUG) {
            if(tmpLog == null) {
                tmpLog = new Logger("BloomfireTemporaryLog");
            }

            tmpLog.e(message);
        }
    }
    @Override
    public boolean debugEnabled() {
        return BuildConfig.DEBUG;
    }

    @Override
    public void e(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void e(String message, Throwable cause) {
        Log.e(TAG, message, cause);
    }

    @Override
    public void w(String message) {
        Log.w(TAG, message);
    }

    @Override
    public void w(String message, Throwable cause) {
        Log.w(TAG, message, cause);
    }

    @Override
    public void i(String message) {
        Log.i(TAG, message);
    }

    @Override
    public void i(String message, Throwable cause) {
        Log.i(TAG, message, cause);
    }

    @Override
    public void d(String message) {
        if (BuildConfig.DEBUG || Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, message);
        }
    }

    @Override
    public void d(String message, Throwable cause) {
        if (BuildConfig.DEBUG || Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, message, cause);
        }
    }

    @Override
    public void v(String message) {
        if (BuildConfig.DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, message);
        }
    }

    @Override
    public void v(String message, Throwable cause) {
        if (BuildConfig.DEBUG || Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, message, cause);
        }
    }
}
