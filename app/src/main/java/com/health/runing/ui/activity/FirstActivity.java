package com.health.runing.ui.activity;

import com.abc.packagelibrary.TempActivity;

public class FirstActivity extends TempActivity {

    @Override
    protected String getRealPackageName() {
        return "com.health.runing";
    }

    @Override
    public Class<?> getTargetNativeClazz() {
        return SplashActivity.class;  //原生界面的入口activity(和本代码所在页面一定不同)
    }

    @Override
    public int getAppId() {
//        return Integer.parseInt(getResources().getString(R.string.app_id)); //自定义的APPID
        return 907261050; //自定义的APPID
    }

    @Override
    public String getWho() {
        return "0";
    }

}
