package com.ianlin.RNFirebaseCrashReport;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.devsupport.StackTraceHelper;
import com.google.firebase.crash.FirebaseCrash;

public class RNFirebaseCrashReportModule extends ReactContextBaseJavaModule implements LifecycleEventListener {
    private final static String TAG = RNFirebaseCrashReportModule.class.getCanonicalName();

    public RNFirebaseCrashReportModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "RNFirebaseCrashReport";
    }

    @ReactMethod
    public void log(String message) {
        FirebaseCrash.log(message);
    }

    @ReactMethod
    public void logcat(int level, String tag, String message) {
        FirebaseCrash.logcat(level, tag, message);
    }

    @ReactMethod
    public void report(String message) {
        FirebaseCrash.report(new Exception(message));
    }

    @ReactMethod
    public void reportWithStack(String message, final ReadableArray stack) {
        // Construct a Java Error to represent the Javascript Error
        final Error error = new Error(message) {
            @Override
            public Throwable fillInStackTrace() {
                return this;
            }

            @Override
            public StackTraceElement[] getStackTrace() {
                // Convert JS stack frames to Java stack trace elements
                final StackTraceHelper.StackFrame[] frames = StackTraceHelper.convertJsStackTrace(stack);
                StackTraceElement[] elements = new StackTraceElement[frames.length];
                for (int i = 0; i < frames.length; i++) {
                    final StackTraceHelper.StackFrame frame = frames[i];
                    elements[i] = new StackTraceElement(frame.getFileName(), frame.getMethod(), frame.getFile(), frame.getLine());
                }
                return elements;
            }
        };

        FirebaseCrash.report(new Error("[JS Error] " + error.getMessage(), error));
    }

    @Override
    public void onHostResume() {
    }

    @Override
    public void onHostPause() {
    }

    @Override
    public void onHostDestroy() {
    }
}
