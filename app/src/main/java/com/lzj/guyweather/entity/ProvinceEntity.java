package com.lzj.guyweather.entity;

/**
 * Created by Administrator on 1/18 0018.
 * Province实体类
 */
public class ProvinceEntity {

    private int provinceId;
    private String provinceName;
    private String provinceCode;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
