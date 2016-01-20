package com.lzj.guyweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzj.guyweather.R;
import com.lzj.guyweather.helper.JsonHelper;
import com.lzj.guyweather.util.HttpCallbackListener;
import com.lzj.guyweather.util.HttpUtil;

/**
 * Created by Administrator on 1/20 0020.
 */
public class WeatherActivity extends Activity {

    private LinearLayout mLinWeatherInfo;
    /**
     * 分别是：城市名，发布时间，天气信息，温度1，温度2，当前日期
     */
    private TextView mTxvCityName, mTxvPublish, mTxvWeatherDesp, mTxvTemp1, mTxvTemp2, mTxvCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather);
        initUI();
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)){
            //有县级代号就去查询天气
            mTxvPublish.setText("同步中。。。");
            mLinWeatherInfo.setVisibility(View.INVISIBLE);
            mTxvCityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else {
            //没有县级代号时直接显示本地天气
            showWeather();
        }
    }

    //初始化UI
    public void initUI(){
        mLinWeatherInfo = (LinearLayout)findViewById(R.id.lin_weather_info);
        mTxvCityName = (TextView)findViewById(R.id.txv_city_name);
        mTxvPublish = (TextView)findViewById(R.id.txv_publish);
        mTxvWeatherDesp = (TextView)findViewById(R.id.txv_weather_desp);
        mTxvTemp1= (TextView)findViewById(R.id.txv_temp1);
        mTxvTemp2= (TextView)findViewById(R.id.txv_temp2);
        mTxvCurrentDate = (TextView)findViewById(R.id.txv_current_date);
    }

    /**
     * 查询县级代号所对应的天气代号
     */
    private void queryWeatherCode(String countyCode){
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    /**
     * 查询天气代号所对应的天气信息
     */
    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
     * @param address
     * @param type
     */
    private void queryFromServer(final String address, final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onResponseSuccess(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            //再查询天气信息
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    //处理服务器返回的天气信息
                    JsonHelper.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onResponseError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTxvPublish.setText("同步失败");
                    }
                });
            }
        });
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
     */
    private void showWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mTxvCityName.setText(preferences.getString("city_name", ""));
        mTxvTemp1.setText(preferences.getString("temp1", ""));
        mTxvTemp2.setText(preferences.getString("temp2", ""));
        mTxvWeatherDesp.setText(preferences.getString("weather_desp", ""));
        mTxvPublish.setText("今天" + preferences.getString("publish_time", "") + "发布");
        mTxvCurrentDate.setText(preferences.getString("current_date", ""));
        mLinWeatherInfo.setVisibility(View.VISIBLE);
        mTxvCityName.setVisibility(View.VISIBLE);
    }
}
