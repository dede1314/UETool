package me.ele.uetool.base;

import android.os.Build;
import android.util.Log;

import me.weishu.reflection.Reflection;

/**
 * 来自 weishu FreeReflection
 * https://github.com/tiann/FreeReflection
 */
public class ReflectionP {

    private static final String TAG = "Reflection";

    public static <T> T breakAndroidP(Func<T> func) {
        T result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                // TODO 这个反射库有点东西
                // https://weishu.me/2019/03/16/another-free-reflection-above-android-p/
                Reflection.unseal(Application.getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        result = func.call();
        return result;
    }

    public interface Func<T> {
        T call();
    }
}
