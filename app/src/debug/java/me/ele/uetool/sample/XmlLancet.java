package me.ele.uetool.sample;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import me.ele.lancet.base.Origin;
import me.ele.lancet.base.annotations.Proxy;
import me.ele.lancet.base.annotations.TargetClass;

//编译器AOP
public class XmlLancet {
   // @Proxy   将使用新的方法替换代码里存在的原有的目标方法.
    @Proxy("inflate")  // @Proxy 指定了将要被织入代码目标方法 inflate.
    @TargetClass(value = "android.view.LayoutInflater")  //@TargetClass 指定了将要被织入代码目标类
    public View inflate(int resourceId, ViewGroup root) {
        View view = (View) Origin.call();  // Origin.call() 代表了 LayoutInflater.inflate() 这个目标方法.
        traverse(view, getResourceName(view, resourceId));
        return view;
    }

    @Proxy("inflate")
    @TargetClass(value = "android.view.LayoutInflater")
    public View inflate(int resourceId, ViewGroup root, boolean attachToRoot) {
        View view = (View) Origin.call();
        traverse(view, getResourceName(view, resourceId));
        return view;
    }

    @Proxy("inflate")
    @TargetClass(value = "android.view.View")
    public static View inflate(Context context, int resourceId, ViewGroup root) {
        View view = (View) Origin.call();
        traverse(view, getResourceName(view, resourceId));
        return view;
    }

    private static void traverse(View view, String name) {
        if (view.getTag(R.id.uetool_xml) == null) {
            view.setTag(R.id.uetool_xml, name);
        }
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            for (int i = 0; i < parent.getChildCount(); i++) {
                traverse(parent.getChildAt(i), name);
            }
        }
    }

    private static String getResourceName(View view, int resourceId) {
        String resourceName = view.getResources().getResourceName(resourceId) + ".xml";
        String[] splits = resourceName.split("/");
        if (splits.length == 2) {
            return splits[1];
        }
        return resourceName;
    }

}
