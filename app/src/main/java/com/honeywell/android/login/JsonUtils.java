package com.honeywell.android.login;

import android.text.TextUtils;

import com.honeywell.android.bean.BatteryInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by melo on 2018/5/2.
 */

public class JsonUtils {

    public static BatteryInfo parseJSons(String dataSource){
        try{
            if(TextUtils.isEmpty(dataSource)){
                return null;
            }
            JSONObject object=new JSONObject(dataSource);
            JSONObject rootObj=object.optJSONObject("Data");
            JSONObject batteryObj = rootObj.optJSONObject("Battery");
            JSONObject coolantObj= rootObj.optJSONObject("Coolant");
            JSONObject faultCodeObj= rootObj.optJSONObject("FaultCode");
            JSONObject integratedObj= rootObj.optJSONObject("Integrated");
            JSONObject missfireObj= rootObj.optJSONObject("Missfire");
            JSONObject oxygenSensorObj= rootObj.optJSONObject("OxygenSensor");
            String Evaluation=batteryObj.optString("Evaluation");
            String Grade=batteryObj.optString("Grade");
            String Score=batteryObj.optString("Score");
            String Evaluation0=batteryObj.optString("V0 Evaluation");
            String Evaluation4=batteryObj.optString("V4 Evaluation");
            return new BatteryInfo(Evaluation,Grade,Score,Evaluation0,Evaluation4);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
