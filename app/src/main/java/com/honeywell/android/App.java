package com.honeywell.android;

import android.app.Application;
import com.honeywell.android.login.HoneyHelper;
import com.honeywell.obd.bluetooth.SppService;
import com.honeywell.obd.net.HoneySDK;

/**
 * Created by ycmedia_imac
 * on 2018/4/28.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HoneySDK.getInstance().init(this);
        SppService.getInstance().init(this);
        //HoneySDK.getInstance().initSesionId("d36de7b2d3e78886a40913d6e42824b8");//d36de7b2d3e78886a40913d6e42824b8   //40dc6588a9cdc56edfeb10f76da287eb
        //HoneyHelper.login();
    }
}
