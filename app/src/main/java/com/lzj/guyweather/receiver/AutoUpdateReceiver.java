package com.lzj.guyweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzj.guyweather.service.AutoUpdateService;

/**
 * Created by Administrator on 1/20 0020.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {

    /**
     * 在这个方法中再次启动AutoUpdateService,就可以实现后台定时更新的功能
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent){
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
