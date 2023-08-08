package me.ele.uetool.base;

import android.content.Context;

import java.lang.reflect.Method;

public class Application {

    private static Context CONTEXT;

    private Application() {
    }

    // 假的Application，实际上支持持有Application对象，面向全局提供。
    // TODO: davidzhou 2022/8/18  为啥要搞个假的来持有全局的context
    public static Context getApplicationContext() {
        if (CONTEXT != null) {
            return CONTEXT;
        } else {
            try {
                Class activityThreadClass = Class.forName("android.app.ActivityThread");
                Method method = activityThreadClass.getMethod("currentApplication");
                CONTEXT = (Context) method.invoke(null);
                return CONTEXT;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
