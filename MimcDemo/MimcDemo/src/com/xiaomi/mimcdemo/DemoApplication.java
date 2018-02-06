package com.xiaomi.mimcdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.xiaomi.mimc.MIMCClient;
import com.xiaomi.mimc.MIMCConstant;
import com.xiaomi.mimc.MIMCException;
import com.xiaomi.mimc.MIMCLogger;
import com.xiaomi.mimc.MIMCUser;
import com.xiaomi.mimcdemo.common.UserManager;

public class DemoApplication extends Application {
    private int mCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化SDK
        MIMCClient.initialize(this);

        // 启用记录日志文件，位于：包名（应用安装路径）/files/MiPushLog/log*.txt
        MIMCLogger.enableMIMCLog(getApplicationContext(), true);
        MIMCLogger.setLogLevel(MIMCLogger.INFO);
        MIMCLogger.i("App start...");

        // 建议，从后台切换到前台时，登录一下
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                mCount++;
                // 切换到前台
                if (mCount == 1) {
                    MIMCUser user = UserManager.getInstance().getUser();
                    if (user != null) {
                        try {
                            user.login();
                            // 建议，拉一下数据
                            if (UserManager.getInstance().getStatus() == MIMCConstant.STATUS_LOGIN_SUCCESS) {
                                user.pull();
                            }
                        } catch (MIMCException e) {
                            e.printStackTrace();
                        }
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
                // 切换到后台
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
