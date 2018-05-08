package com.honeywell.android.login;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.honeywell.obd.api.DetectionManager;
import com.honeywell.obd.api.FuelManager;
import com.honeywell.obd.api.callback.DetectionListening;
import com.honeywell.obd.api.callback.FuelListening;
import com.honeywell.obd.bean.CarData;
import com.honeywell.obd.net.HoneySDK;
import com.honeywell.obd.service.BtnConnectManage;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by musk on 2018/4/28.
 */

public class HoneyHelper {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String OBD_BIND_URL="http://111.231.63.125:8088/user/obd/bind";
    private static  final String LOGIN_URL="http://111.231.63.125:8088/car_robot/device/register_login";
    private static  final String REGISTER_CAR_URL="http://111.231.63.125:8088/car/register";

//    public static final String VIN="LVSHFAAL2BN165119";
//    public static final String OBD_NAME="HON11CK8Q5C3";
    private static final String DEVICE_ID="205d5401";//手机和OBD会绑定，必须保持一致

    private BtnConnectManage btnConnectManage;
    private DetectionManager detectionManager;
    private FuelManager fuelManager;

    private CarData carData;
    private String sessionId="";


    private static HoneyHelper sInstance=null;
    private HoneyHelper(){
        btnConnectManage=new BtnConnectManage();
        detectionManager=new DetectionManager();
        fuelManager=new FuelManager();
    }
    public static HoneyHelper getInstance(){
        if(sInstance==null){
            synchronized (HoneyHelper.class){
                if(sInstance==null){
                    sInstance=new HoneyHelper();
                }
            }
        }
        return sInstance;
    }

    public CarData getCarData() {
        return carData;
    }

    public void setCarData(CarData carData) {
        this.carData = carData;
    }

    public  void login(ILogin iLogin) {
        iLogin.doing();
        final OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DeviceID", DEVICE_ID);
            jsonObject.put("TenantID", "200");
        } catch (JSONException e) {
            e.printStackTrace();
            iLogin.onFailed(e.getMessage());
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        final Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    //Headers headers = response.headers();
                    Log.d("musk", "==login=="+response);
                    if (!response.headers("Set-Cookie").isEmpty()) {
                        String cookies = response.header("Set-Cookie");
                        String[] sessions = cookies.split(";");
                        String session = sessions[0].split("=")[1];
                        Log.d("musk","session=="+session);

                        HoneySDK.getInstance().initSesionId(session);
                        iLogin.onSuccess(session);
                        sessionId=session;
                        //bindObd(session);
                        /*if(!isRegistered){
                            registerCar(session);
                            isRegistered=true;
                        }*/
                    }
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                    iLogin.onFailed(e.getMessage());
                }
            }
        }).start();
    }
    public  void bindObd(String obdName,IBindOBD iBindOBD) {
        if(TextUtils.isEmpty(sessionId)){
            iBindOBD.onFailed("用户未注册~");
            return;
        }
        if(TextUtils.isEmpty(obdName)){
            iBindOBD.onFailed("OBD设备名称为空~");
            return;
        }

        iBindOBD.doing();
        final OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ObdName", obdName);
        } catch (JSONException e) {
            e.printStackTrace();
            iBindOBD.onFailed(e.getMessage());
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        final Request request = new Request.Builder()
                .url(OBD_BIND_URL)
                .post(body)
                .addHeader("Cookie", String.format("beegosessionID=%s", sessionId))
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    Log.i("musk", "==bindObd==" + response);
                    iBindOBD.onSuccess(response.toString());
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                    iBindOBD.onFailed(e.getMessage());
                }
            }
        }).start();
    }

//             "Vin": "", // vin码
//            "PlatNumber":"", // 车牌号
//            "EngineNumber":"", // 发动机编号
//            "PL": "", //排量
//            "EngineType":"", // 0 or 1  0:自然吸气 1:涡轮增压


    //----------------------------------------------------------------------
//            "Vin": "", // vin码
//            "PlatNumber":"", // 车牌号
//            "EngineNumber":"", // 发动机编号
//            "CarBrand": "", //车牌
//            "CarType":"", //车型
//            "Ssnf":"", //上市年份
//            "PL": "", //排量
//            "EngineType":"", // 0 or 1  0:自然吸气 1:涡轮增压

    public  void registerCar(String vin,String plateNumber,String engineNumber,String pailiang,IRegisterCar iRegisterCar) {
        if(TextUtils.isEmpty(sessionId)){
            iRegisterCar.onFailed("用户未注册~");
            return;
        }
        if(TextUtils.isEmpty(vin)){
            iRegisterCar.onFailed("车架号为空");
            return;
        }
        if(TextUtils.isEmpty(plateNumber)){
            iRegisterCar.onFailed("车牌号为空");
            return;
        }
        if(TextUtils.isEmpty(engineNumber)){
            iRegisterCar.onFailed("发动机号为空");
            return;
        }
        if(TextUtils.isEmpty(pailiang)){
            iRegisterCar.onFailed("排量信息为空");
            return;
        }
        iRegisterCar.doing();
        final OkHttpClient okHttpClient = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Vin", vin);
            jsonObject.put("PlateNumber", plateNumber);
            jsonObject.put("EngineNumber", engineNumber);
            jsonObject.put("CarBrand", "京A8888");
            jsonObject.put("CarType", "yueye");
            //jsonObject.put("Ssnf", "2018");
            jsonObject.put("PL", pailiang);
            jsonObject.put("EngineType", "1");
            Log.i("musk","==jsonObject.toString()=="+jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            iRegisterCar.onFailed(e.getMessage());
        }
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        final Request request = new Request.Builder()
                .url(REGISTER_CAR_URL)
                .post(body)
                .addHeader("Cookie", String.format("beegosessionID=%s", sessionId))
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    Log.i("musk", "==registerCar==" + response);
                    iRegisterCar.onSuccess(response.toString());
                    response.body().close();
                } catch (IOException e) {
                    e.printStackTrace();
                    iRegisterCar.onFailed(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 连接OBD
     * @param context
     * @param obdName
     * @param callback
     */
    public void connectOBD(Context context,String obdName,BtnConnectManage.Callback callback){
        if(TextUtils.isEmpty(obdName)){
            return;
        }
        btnConnectManage.connect(context, obdName, callback);
    }

    /**
     * 检测模式
     * @param context
     * @param listener
     */
    public void startDection(Activity context,DetectionListening listener){
        if(carData==null)
            return;
        detectionManager.startDetection(context,carData,listener);
    }

    /**
     * 监控模式
     * @param context
     * @param listener
     */
    public void startFuel(Activity context,FuelListening listener){
        if(carData==null)
            return;
        fuelManager.startFuel(context,carData,listener);
    }
}