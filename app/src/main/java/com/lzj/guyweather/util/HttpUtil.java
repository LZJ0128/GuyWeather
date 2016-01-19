package com.lzj.guyweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 1/19 0019.
 * 与服务端交互的类
 */
public class HttpUtil {

    /**
     * 发送数据请求
     * @param address 请求的服务器地址
     * @param listener  数据回调监听器
     */
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();//打开url
                    connection.setRequestMethod("GET");//请求方式
                    connection.setConnectTimeout(8000);//连接超时
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if (listener != null){
                        //回调onFinish()方法
                        listener.onResponseSuccess(response.toString());
                    }
                }catch (Exception e){
                    if (listener != null){
                        //回调onError()方法
                        listener.onResponseError(e);
                    }
                }finally {
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
