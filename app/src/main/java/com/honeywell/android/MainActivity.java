package com.honeywell.android;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.honeywell.android.login.HoneyHelper;
import com.honeywell.android.login.IBindOBD;
import com.honeywell.android.login.ILogin;
import com.honeywell.android.login.IRegisterCar;
import com.honeywell.android.login.ShareSave;
import com.honeywell.obd.bean.CarData;

public class MainActivity extends AppCompatActivity {

    TextView userTip,carTip,obdTip;
    Button registerUser,registerCar,bindObd,goTest;
    EditText inputOBD;
    //共计8个参数
    EditText et_vin,et_plate_number,et_engine_number,et_car_pl,et_engine_type;

    //CarData carData = new CarData("", "audi", "a8", "1.8", "2012", "18237177660");

    public static final String TAG = "MainActivity";
    private HoneyHelper honeyHelper;
    private CarData carData;
    private String obdName;
    private boolean userRegisterSuccess,carRegisterSuccess,obdBindSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userTip=findViewById(R.id.tv_user_tip);
        carTip=findViewById(R.id.tv_car_tip);
        obdTip=findViewById(R.id.tv_obd_tip);

        et_vin=findViewById(R.id.et_vin);
        et_vin.setText(ShareSave.getString(this,"vin"));
        et_plate_number=findViewById(R.id.et_plate_number);
        et_plate_number.setText(ShareSave.getString(this,"plateNumber"));
        et_engine_number=findViewById(R.id.et_engine_number);
        et_engine_number.setText(ShareSave.getString(this,"engineNumber"));
        et_car_pl=findViewById(R.id.et_car_pl);
        et_car_pl.setText(ShareSave.getString(this,"pl"));
        et_engine_type=findViewById(R.id.et_engine_type);
        et_engine_type.setText(ShareSave.getString(this,"engine_type"));


        inputOBD=findViewById(R.id.et_obd);
        inputOBD.setText(ShareSave.getString(this,"obdName"));
        registerUser=findViewById(R.id.register_user);
        registerCar=findViewById(R.id.register_car);
        bindObd=findViewById(R.id.bind_obd);
        goTest=findViewById(R.id.go_test);

        honeyHelper=HoneyHelper.getInstance();

        registerUser.setOnClickListener(v -> {
            honeyHelper.login(new ILogin() {
                @Override
                public void doing() {
                v.setClickable(false);
                    showInfo(userTip,"用户注册登录中...");
                }

                @Override
                public void onFailed(String msg) {
                    v.setClickable(true);
                    userRegisterSuccess=false;
                    showInfo(userTip,"用户注册登录失败，原因是："+msg);
                }

                @Override
                public void onSuccess(String resut) {
                    v.setClickable(true);
                    userRegisterSuccess=true;
                    showInfo(userTip,"用户注册登录成功！");
                }
            });
        });
        registerCar.setOnClickListener(v->{
            String plateNumber=et_plate_number.getText().toString();
            String engineNumber=et_engine_number.getText().toString();

            String vin=et_vin.getText().toString();
            String user= Build.SERIAL;
            String pl=et_car_pl.getText().toString();
            String engine_type=et_engine_type.getText().toString();

            ShareSave.setString(MainActivity.this,"vin",vin);
            ShareSave.setString(MainActivity.this,"plateNumber",plateNumber);
            ShareSave.setString(MainActivity.this,"engineNumber",engineNumber);
            ShareSave.setString(MainActivity.this,"pl",pl);
            ShareSave.setString(MainActivity.this,"engine_type",engine_type);

            carData=new CarData(vin,"xx","xx",pl,"xxxx",user);
            honeyHelper.registerCar(vin, plateNumber,engineNumber,pl,new IRegisterCar() {
                @Override
                public void doing() {
                    v.setClickable(false);
                    showInfo(carTip,"车辆注册中...");
                }

                @Override
                public void onFailed(String msg) {
                    v.setClickable(true);
                    carRegisterSuccess=false;
                    showInfo(carTip,"车辆注册失败，原因是："+msg);
                }

                @Override
                public void onSuccess(String resut) {
                    v.setClickable(true);
                    carRegisterSuccess=true;
                    showInfo(carTip,"车辆注册成功！");
                }
            });
        });
        bindObd.setOnClickListener(v ->{
            obdName=inputOBD.getText().toString();
            if(TextUtils.isEmpty(obdName)){
                showInfo(obdTip,"OBD设备名称为空！");
                return;
            }
            ShareSave.setString(MainActivity.this,"obdName",obdName);
            honeyHelper.bindObd(inputOBD.getText().toString(), new IBindOBD() {
                @Override
                public void doing() {
                    v.setClickable(false);
                    showInfo(obdTip,"OBD设备绑定中...");
                }

                @Override
                public void onFailed(String msg) {
                    v.setClickable(true);
                    obdBindSuccess=false;
                    showInfo(obdTip,"OBD设备绑定失败，原因是："+msg);
                }

                @Override
                public void onSuccess(String resut) {
                    v.setClickable(true);
                    obdBindSuccess=true;
                    showInfo(obdTip,"OBD设备绑定成功！");
                }
            });
        });

        goTest.setOnClickListener(v -> {
            goCarActivity();
        });

    }
    private void goCarActivity(){
        if(userRegisterSuccess && carRegisterSuccess && obdBindSuccess){
            Intent intent = new Intent(this,CarActivity.class);
            intent.putExtra("carInfo",carData);
            intent.putExtra("obd",obdName);
            startActivity(intent);
        }
    }
    private void showInfo(TextView tv,String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(msg);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //initConnect();
    }

}