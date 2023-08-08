package me.ele.uetool;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.ele.uetool.attrdialog.AttrsDialogMultiTypePool;
import me.ele.uetool.attrdialog.binder.AddMinusEditTextItemBinder;
import me.ele.uetool.attrdialog.binder.BitmapItemBinder;
import me.ele.uetool.attrdialog.binder.BriefDescItemBinder;
import me.ele.uetool.attrdialog.binder.EditTextItemBinder;
import me.ele.uetool.attrdialog.binder.SwitchItemBinder;
import me.ele.uetool.attrdialog.binder.TextItemBinder;
import me.ele.uetool.attrdialog.binder.TitleItemBinder;
import me.ele.uetool.base.Application;
import me.ele.uetool.base.ItemViewBinder;
import me.ele.uetool.base.item.AddMinusEditItem;
import me.ele.uetool.base.item.BitmapItem;
import me.ele.uetool.base.item.BriefDescItem;
import me.ele.uetool.base.item.EditTextItem;
import me.ele.uetool.base.item.Item;
import me.ele.uetool.base.item.SwitchItem;
import me.ele.uetool.base.item.TextItem;
import me.ele.uetool.base.item.TitleItem;

public class UETool {

    private static volatile UETool instance;
    private Set<String> filterClassesSet = new HashSet<>();
    private List<String> attrsProviderSet = new ArrayList<String>() {
        {
            add(UETCore.class.getName());
            add("me.ele.uetool.fresco.UETFresco");
        }
    };
    private Activity targetActivity;
    private UETMenu uetMenu;
    private AttrsDialogMultiTypePool attrsDialogMultiTypePool = new AttrsDialogMultiTypePool();

    private UETool() {
        initAttrsDialogMultiTypePool();
    }


    // 使用单例来管理这个一个view. 没有传入context.
    static UETool getInstance() {
        if (instance == null) {
            synchronized (UETool.class) {
                if (instance == null) {
                    instance = new UETool();
                }
            }
        }
        return instance;
    }

    public static void putFilterClass(Class clazz) {
        putFilterClass(clazz.getName());
    }

    public static void putFilterClass(String className) {
        getInstance().putFilterClassName(className);
    }

    public static <T extends Item> void registerAttrDialogItemViewBinder(Class<T> clazz, ItemViewBinder<T, ?> binder) {
        getInstance().attrsDialogMultiTypePool.register(clazz, binder);
    }

    public static void putAttrsProviderClass(Class clazz) {
        putAttrsProviderClass(clazz.getName());
    }

    public static void putAttrsProviderClass(String className) {
        //添加自定义属性
        getInstance().putAttrsProviderClassName(className);
    }

    public static boolean showUETMenu() {
        return getInstance().showMenu();
    }

    public static boolean showUETMenu(int y) {
        return getInstance().showMenu(y);
    }

    public static int dismissUETMenu() {
        return getInstance().dismissMenu();
    }

    private void putFilterClassName(String className) {
        filterClassesSet.add(className);
    }

    private void putAttrsProviderClassName(String className) {
        attrsProviderSet.add(0, className);
    }

    private boolean showMenu() {
        return showMenu(10);
    }

    private boolean showMenu(int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 悬浮窗权限申请
            if (!Settings.canDrawOverlays(Application.getApplicationContext())) {
                requestPermission(Application.getApplicationContext());
                Toast.makeText(Application.getApplicationContext(), "After grant this permission, re-enable UETool", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (uetMenu == null) {
            uetMenu = new UETMenu(Application.getApplicationContext(), y);
        }
        if (!uetMenu.isShown()) {
            uetMenu.show();
            return true;
        }
        return false;
    }

    private int dismissMenu() {
        if (uetMenu != null) {
            int y = uetMenu.dismiss();
            uetMenu = null;
            return y;
        }
        return -1;
    }

    public Set<String> getFilterClasses() {
        return filterClassesSet;
    }

    public Activity getTargetActivity() {
        return targetActivity;
    }


    public void setTargetActivity(Activity targetActivity) {
        this.targetActivity = targetActivity;
    }

    public AttrsDialogMultiTypePool getAttrsDialogMultiTypePool() {
        return attrsDialogMultiTypePool;
    }

    public List<String> getAttrsProvider() {
        return attrsProviderSet;
    }

    void release() {
        targetActivity = null;
    }

    private void initAttrsDialogMultiTypePool() {
        // 右侧可以操作的类型
        //加减
        attrsDialogMultiTypePool.register(AddMinusEditItem.class, new AddMinusEditTextItemBinder());
        // 图片类型
        attrsDialogMultiTypePool.register(BitmapItem.class, new BitmapItemBinder());
        //validview开关打开后的子view
        attrsDialogMultiTypePool.register(BriefDescItem.class, new BriefDescItemBinder());
        // 可以编辑的
        attrsDialogMultiTypePool.register(EditTextItem.class, new EditTextItemBinder());
        //开关
        attrsDialogMultiTypePool.register(SwitchItem.class, new SwitchItemBinder());
        // 纯展示的
        attrsDialogMultiTypePool.register(TextItem.class, new TextItemBinder());
        //分隔符，类别
        attrsDialogMultiTypePool.register(TitleItem.class, new TitleItemBinder());
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
