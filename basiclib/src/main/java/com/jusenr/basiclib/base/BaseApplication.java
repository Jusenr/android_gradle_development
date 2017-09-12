package com.jusenr.basiclib.base;

import android.app.Activity;
import android.app.Application;

import com.jusenr.basiclib.utils.Utils;
import com.jusenr.toolslibrary.AndroidTools;

import java.util.Stack;

/**
 * 要想使用BaseApplication，必须在组件中实现自己的Application，并且继承BaseApplication；
 * 组件中实现的Application必须在debug包中的AndroidManifest.xml中注册，否则无法使用；
 * 组件的Application需置于java/debug文件夹中，不得放于主代码；
 * 组件中获取Context的方法必须为:Utils.getContext()，不允许其他写法；
 * BaseApplication主要用来管理全局Activity;
 *
 * @author 2016/12/2 17:02
 * @version V1.0.0
 * @name BaseApplication
 */
public class BaseApplication extends Application {

    private static BaseApplication sInstance;

    private Stack<Activity> activityStack;

    public static BaseApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        Utils.init(this);

        AndroidTools.init(getApplicationContext(), "putao_paiband");
    }


}
