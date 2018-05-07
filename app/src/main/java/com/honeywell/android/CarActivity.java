package com.honeywell.android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.android.login.HoneyHelper;
import com.honeywell.android.login.JsonUtils;
import com.honeywell.obd.api.DetectionManager;
import com.honeywell.obd.api.FuelManager;
import com.honeywell.obd.api.callback.DetectionListening;
import com.honeywell.obd.api.callback.FuelListening;
import com.honeywell.obd.bean.CarData;
import com.honeywell.obd.bean.FuelParams;
import com.honeywell.obd.bluetooth.SppService;
import com.honeywell.obd.service.BtnConnectManage;

import java.lang.ref.WeakReference;

/**
 * Created by musk on 2018/5/2.
 */

public class CarActivity extends Activity {
    Button connect, detection, fuel;
    TextView connectTip;
    DetectionManager detectionManager;
    FuelManager fuelManager;

    private CarData carData;
    private String obdName;

    private static final int UPDATE_DECT=1;
    private static final int UPDATE_FUEL=2;

    private final Handler handler=new CarHandler(this);
    private static class CarHandler extends Handler{

        private WeakReference<CarActivity>mActivity;

        public CarHandler(CarActivity activity){
            mActivity=new WeakReference<CarActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mActivity.get()==null){
                return;
            }
            CarActivity carActivity=mActivity.get();
            switch(msg.what){
                case UPDATE_FUEL:
                    FuelParams params= (FuelParams) msg.obj;
                    StringBuilder builder=new StringBuilder();
                    builder.append("Totalgas:\t"+params.getTotalgas()+"\n")
                            .append("Speed:\t"+params.getSpeed()+"\n")
                            .append("Instantgas:\t"+params.getInstantgas()+"\n")
                            .append("Rotate:\t"+params.getRotate()+"\n")
                            .append("Unit:\t"+params.getUnit()+"\n");
                    carActivity.connectTip.setText(builder.toString());
                    break;
                case UPDATE_DECT:
                    carActivity.connectTip.setText("检测进度："+msg.arg1+"%");
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);
        connect = findViewById(R.id.connect);
        fuel = findViewById(R.id.fuel);
        detection = findViewById(R.id.detection);
        connectTip=findViewById(R.id.tv_connect_tip);

        detectionManager = new DetectionManager();
        fuelManager = new FuelManager();

        Intent intent=getIntent();
        carData=intent.getParcelableExtra("carInfo");
        obdName=intent.getStringExtra("obd");

        connect.setOnClickListener(v -> {
            connect.setClickable(false);
            initConnect();
        });
        detection.setOnClickListener(v -> {
            detection.setClickable(false);
            initDetection();
        });
        fuel.setOnClickListener(v -> {
            fuel.setClickable(false);
            initFuel();
        });
    }

    private void initFuel() {
        if (!SppService.getInstance().isIsConnecting()) {
            fuel.setClickable(true);
            connectTip.setText("设备未连接");
            return;
        }
        if(carData==null){
            connectTip.setText("车辆信息为空");
            return;
        }
        detection.setClickable(true);
        fuelManager.startFuel(this, carData, new FuelListening() {
            @Override
            public void isIgnition(boolean isIgnitionEd) {
                // isIgnitionEd = false  提示用户点火 会多次调起
                // isIgnitionEd = true  关闭提示
                Log.i("musk","==isIgnition=="+isIgnitionEd);
            }

            @Override
            public void error(int i) {
                fuel.setClickable(true);
                Log.i("musk","==error=="+i);
                // 错误回调 油耗测试已停止
                switch (i) {
                    case FuelManager.ErrorCode.ConnectError:
                        //“不支持”;
                        break;
                    case FuelManager.ErrorCode.InvalidError:
                        //“内部错误”;
                        break;
                    case FuelManager.ErrorCode.NETError:
                        // "网络连接失败";
                        break;
                    default:
                        // "内部错误,分屏已经停止";
                        break;
                }
            }

            @Override
            public void uploadFuel(FuelParams fuelParams) {
                // 油耗实时更新数据
                Log.i("musk","==uploadFuel=="+fuelParams.getSpeed());
                Message msg=new Message();
                msg.obj=fuelParams;
                msg.what=UPDATE_FUEL;
                handler.sendMessage(msg);
            }

            @Override
            public void endFuel(FuelParams fuelParams, String s) {
                // 油耗测试最终结果数据
                fuel.setClickable(true);
                Log.i("musk","==endFuel=="+s);
            }
        });
    }

    private void initDetection() {
        if (!SppService.getInstance().isIsConnecting()) {
            detection.setClickable(true);
             connectTip.setText("设备未连接");
            return;
        }
        if(carData==null){
            connectTip.setText("车辆信息为空");
            return;
        }
        fuel.setClickable(true);
        detectionManager.startDetection(this, carData, new DetectionListening() {
            @Override
            public void ignitionHeat(boolean ignition) {
                // ignition = true  提示用户点火 会多次调起
                // ignition = false  关闭提示
            }

            @Override
            public void initError(int i) {
                // 错误回调 检测停止
                detection.setClickable(true);
                switch (i) {
                    case DetectionManager.ErrorCode.connectError:
                        // 设备未连接
                        break;
                    case DetectionManager.ErrorCode.startHot:
                        //  点火超时
                        break;
                    case DetectionManager.ErrorCode.invalidError:
                        // 不支持车辆
                        break;
                    case DetectionManager.ErrorCode.vinError:
                        // vin码不正确
                        break;
                    default:
                        // 内部错误
                        break;
                }
            }

            @Override
            public void initProgress(int i) {
                // 进度
                Log.i("musk","---Progress--"+i);
                Message msg=new Message();
                msg.what=UPDATE_DECT;
                msg.arg1=i;
                handler.sendMessage(msg);
            }

            @Override
            public void detectionOver(String s) {
                // 检测最终结果
                detection.setClickable(true);
                Log.i("musk","---detectionOver--"+s);
                connectTip.setText(s);
                JsonUtils.parseJSons(s);
            }

            @Override
            public void temperature() {
                // 提示用户水温过高  结果可能不精确
            }
        });
    }

    /**
     * 设备连接 参数 设备名称 连接前 确保已经获取到用户  ACCESS_COARSE_LOCATION 权限
     */
    private void initConnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            }
        }
        if(TextUtils.isEmpty(obdName)){
            connectTip.setText("obd名称为空");
            return;
        }

        BtnConnectManage manage = new BtnConnectManage();
        manage.connect(getApplicationContext(), obdName, new BtnConnectManage.Callback() {
            @Override
            public void onSuccess() {
                connect.setClickable(true);
                connectTip.setText("连接成功");
            }

            @Override
            public void onError() {
                connect.setClickable(true);
                connectTip.setText("连接失败");
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        detectionManager.stop();
        fuelManager.stopFuel(this);
        if (handler != null) {
            handler.removeCallbacks(null);
        }
    }

    public void toast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
