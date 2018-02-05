package com.xiaomi.mimcdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import com.xiaomi.mimc.MIMCClient;
import com.xiaomi.mimc.MIMCConstant;
import com.xiaomi.mimc.MIMCException;
import com.xiaomi.mimc.MIMCLogger;
import com.xiaomi.mimc.MIMCLoggerInterface;
import com.xiaomi.mimc.MIMCUser;
import com.xiaomi.mimcdemo.common.SystemUtils;
import com.xiaomi.mimcdemo.common.UserManager;

public class DemoApplication extends Application {
    public static final String TAG = "com.xiaomi.MimcDemo";
    private int mCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();


        MIMCLoggerInterface newLogger = new MIMCLoggerInterface() {
            @Override
            public void setTag(String s) {

            }

            @Override
            public void log(String s) {
                Log.d(TAG, s);
            }

            @Override
            public void log(String s, Throwable throwable) {
                Log.d(TAG, s, throwable);
            }
        };
        MIMCLogger.setLogger(getApplicationContext(), newLogger);
        MIMCLogger.setLogLevel(MIMCLogger.INFO);

        SystemUtils.initialize(this);
        MIMCClient.initialize(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mCount++;
                // Switch to the foreground
                if (mCount == 1) {
                    MIMCUser user = UserManager.getInstance().getUser();
                    if (user != null) try {
                        user.login();
                        if (UserManager.getInstance().getStatus() == MIMCConstant.STATUS_LOGIN_SUCCESS) {
                            user.pull();
                        }
                    } catch (MIMCException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mCount--;
                // Switch to the background
                if (mCount == 0) {
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }
}
