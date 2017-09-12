package com.jusenr.android.gradle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jusenr.basiclib.base.BaseApplication;
import com.jusenr.basiclib.utils.Utils;

/**
 * Description:
 * Copyright  : Copyright (c) 2017
 * Email      : jusenr@163.com
 * Author     : Jusenr
 * Date       : 2017/09/12
 * Time       : 19:15
 * Project    ：android_gradle_development.
 */
public class TotalApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        if (Utils.isAppDebug()) {
            //开启InstantRun之后，一定要在ARouter.init之前调用openDebug
            ARouter.openDebug();
            ARouter.openLog();
        }
        ARouter.init(this);
    }
}
