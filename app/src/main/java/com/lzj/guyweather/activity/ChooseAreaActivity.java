package com.lzj.guyweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzj.guyweather.R;
import com.lzj.guyweather.entity.CityEntity;
import com.lzj.guyweather.entity.CountyEntity;
import com.lzj.guyweather.entity.ProvinceEntity;
import com.lzj.guyweather.helper.DBOperationHelper;
import com.lzj.guyweather.util.HttpCallbackListener;
import com.lzj.guyweather.util.HttpUtil;
import com.lzj.guyweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 1/19 0019.
 */
public class ChooseAreaActivity extends Activity {

    /**
     * province，city， county等级常量
     */
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    //进度条
    private ProgressDialog mProgressDialog;

    //布局元素
    private TextView mTxvTitle;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<String> dataList = new ArrayList<String>();

    //数据库操作帮助类
    private DBOperationHelper mDbOperationHelper;

    /**
     * province,city,county列表
     */
    private List<ProvinceEntity> mProvinceList;
    private List<CityEntity> mCityList;
    private List<CountyEntity> mCountyList;

    /**
     * 选中的province,city,level
     */
    private ProvinceEntity mSelectedProvince;
    private CityEntity mSelectedCity;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        initUI();
        queryProvinces();
    }

    /**
     * 初始化视图
     */
    public void initUI(){

        mListView = (ListView)findViewById(R.id.lsv_view);
        mTxvTitle = (TextView)findViewById(R.id.txv_title);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(mAdapter);
        mDbOperationHelper = DBOperationHelper.getInstance(this);
        mListView.setOnItemClickListener(mOnItemClick);
    }

    /**
     * ListView的Item点击事件
     */
    private AdapterView.OnItemClickListener mOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (currentLevel == LEVEL_PROVINCE){
                mSelectedProvince = mProvinceList.get(position);
                queryCities();
            }else if (currentLevel == LEVEL_CITY){
                mSelectedCity = mCityList.get(position);
                queryCounties();
            }
        }
    };

    /**
     * 查询所有的Province,优先从数据库查询，若没有则再去服务器上查询
     */
    private void queryProvinces(){
        mProvinceList = mDbOperationHelper.loadProvinces();
        if (mProvinceList.size()>0){
            dataList.clear();
            for (ProvinceEntity province : mProvinceList){
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();//刷新适配器更新数据
            mListView.setSelection(0);
            mTxvTitle.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询所有的City,优先从数据库查询，若没有则再去服务器上查询
     */
    private void queryCities(){
        mCityList = mDbOperationHelper.loadCities(mSelectedProvince.getProvinceId());
         if (mCityList.size()>0){
            dataList.clear();
            for (CityEntity city : mCityList){
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTxvTitle.setText(mSelectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(mSelectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询所有的County,优先从数据库查询，若没有则再去服务器上查询
     */
    private void queryCounties(){
        mCountyList = mDbOperationHelper.loadCounties(mSelectedCity.getCityId());
        if (mCountyList.size()>0){
            dataList.clear();
            for (CountyEntity county : mCountyList){
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTxvTitle.setText(mSelectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(mSelectedCity.getCityCode(), "county");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询Province，city,county数据
     */
    private void queryFromServer(final String code, final String type){
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(mDbOperationHelper, response);
                } else if ("city".equals(type)){
                    result = Utility.handleCityResponse(mDbOperationHelper, response, mSelectedProvince.getProvinceId());
                } else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(mDbOperationHelper, response, mSelectedCity.getCityId());
                }

                if (result){
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUIThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("正在加载。。。");
//            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    /**
     * 捕获back按键，根据当前的级别来判断返回的页面或者退出
     */
    @Override
    public void onBackPressed(){
        if (currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }

}
