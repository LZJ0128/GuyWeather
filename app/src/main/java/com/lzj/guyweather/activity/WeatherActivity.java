package com.lzj.guyweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzj.guyweather.R;
import com.lzj.guyweather.helper.JsonHelper;
import com.lzj.guyweather.service.AutoUpdateService;
import com.lzj.guyweather.util.HttpCallbackListener;
import com.lzj.guyweather.util.HttpUtil;

/**
 * Created by Administrator on 1/20 0020.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{

    private LinearLayout mLinWeatherInfo;

    /**
     * 分别是：城市名，发布时间，天气信息，温度1，温度2，当前日期
     */
    private TextView mTxvCityName, mTxvPublish, mTxvWeatherDesp, mTxvTemp1, mTxvTemp2, mTxvCurrentDate;
    private ImageView mImgWeather;
    /**
     * 按钮：切换城市，手动刷新天气
     */
    private Button mBtnSwitchCity, mBtnRefreshWeather;

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
        mBtnSwitchCity = (Button)findViewById(R.id.btn_switch_city);
        mBtnRefreshWeather = (Button)findViewById(R.id.btn_refresh_weather);
        mBtnSwitchCity.setOnClickListener(this);
        mBtnRefreshWeather.setOnClickListener(this);

        mImgWeather = (ImageView)findViewById(R.id.img_weather);

    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);//标志位（若直接跳转则又会跳转回来）
                startActivity(intent);
                finish();
                break;

            case R.id.btn_refresh_weather:
                mTxvPublish.setText("同步中。。。");
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = pref.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)){
                    //查询天气
                    queryWeatherCode(weatherCode);
                    mTxvPublish.setText("今天" + pref.getString("publish_time", "") + ":00发布");
                }
                break;

            default:
                break;
        }
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
                        /**
                         * 服务其返回的数据类似：190404|101190404 前者为县级代号，后者为该县级的天气代号
                         */
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
        showWeatherIcon(preferences.getString("weather_desp", ""));
        mTxvCityName.setText(preferences.getString("city_name", ""));
        mTxvTemp1.setText(preferences.getString("temp2", ""));
        mTxvTemp2.setText(preferences.getString("temp1", ""));
        mTxvWeatherDesp.setText(preferences.getString("weather_desp", ""));
        mTxvPublish.setText("今天" + preferences.getString("publish_time", "") + ":00发布");
        mTxvCurrentDate.setText(preferences.getString("current_date", ""));
        mLinWeatherInfo.setVisibility(View.VISIBLE);
        mTxvCityName.setVisibility(View.VISIBLE);
        //激活服务.8小时更新一次
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void showWeatherIcon(String weatherDesp){
        switch (weatherDesp){
            case "小雨":
                mImgWeather.setImageResource(R.drawable.xiaoyu);
                break;
            case "多云":
            case "阴转多云":
            case "晴转多云":
                mImgWeather.setImageResource(R.drawable.duoyun);
                break;
            case "阴":
                mImgWeather.setImageResource(R.drawable.yintian);
                break;
            case "晴":
                mImgWeather.setImageResource(R.drawable.qingtian);
                break;
            default:
                mImgWeather.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
