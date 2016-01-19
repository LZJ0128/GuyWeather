package com.lzj.guyweather.util;

/**
 * Created by Administrator on 1/19 0019.
 * 服务端回调接口
 */
public interface HttpCallbackListener {

    void onResponseSuccess(String response);

    void onResponseError(Exception e);
}
