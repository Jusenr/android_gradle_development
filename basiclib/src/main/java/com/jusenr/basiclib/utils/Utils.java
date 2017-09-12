package com.jusenr.basiclib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;

/**
 * Utils Initialization correlation.
 */
public class Utils {

    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Initialization tool class.
     *
     * @param context context
     */
    public static void init(Context context) {
        Utils.context = context.getApplicationContext();
    }

    /**
     * Get ApplicationContext.
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) return context;
        throw new NullPointerException("u should init first");
    }

    /**
     * View tool for getting Activity.
     *
     * @param view view
     * @return Activity
     */
    public static
    @NonNull
    Activity getActivity(View view) {
        Context context = view.getContext();

        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }

        throw new IllegalStateException("View " + view + " is not attached to an Activity");
    }

    /**
     * The method of global access to String.
     *
     * @param id Resource Id
     * @return String
     */
    public static String getString(@StringRes int id) {
        return context.getResources().getString(id);
    }

    /**
     * Determine whether the App is a Debug version.
     *
     * @return {@code true}: Yes<br>{@code false}: No
     */
    public static boolean isAppDebug() {
        if (isSpace(context.getPackageName())) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Determine whether the string is null or all spaces.
     *
     * @param s String to be checked
     * @return {@code true}: Null or full space<br> {@code false}: Not null and not full space
     */
    private static boolean isSpace(String s) {
        return (s == null || s.trim().length() == 0);
    }

}