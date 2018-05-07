package com.honeywell.android.login;

/**
 * Created by melo on 2018/5/2.
 */

public interface IBindOBD {
    void doing();
    void onFailed(String msg);
    void onSuccess(String resut);
}
