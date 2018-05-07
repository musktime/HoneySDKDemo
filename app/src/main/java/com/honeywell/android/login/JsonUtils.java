package com.honeywell.android.login;

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
            JSONObject object=new JSONObject(dataSource);
            JSONArray array=object.optJSONArray("data");
            JSONObject jsonObject = array.optJSONObject(1);
            String Evaluation=jsonObject.optString("Evaluation");
            String Grade=jsonObject.optString("Grade");
            String Score=jsonObject.optString("Score");
            String Evaluation0=jsonObject.optString("V0 Evaluation");
            String Evaluation4=jsonObject.optString("V4 Evaluation");
            return new BatteryInfo(Evaluation,Grade,Score,Evaluation0,Evaluation4);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
