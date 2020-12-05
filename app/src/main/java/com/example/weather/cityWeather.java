package com.example.weather;

import java.io.Serializable;

public class cityWeather implements Serializable {
    //实现序列化

    private String weather;
    private String temperature;
    private String winddirection;
    private String windpower;
    private String humidity;
    private String reporttime;
    private String province;
    private String cityName;
    private String adcode;

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public cityWeather(String weather, String temperature, String winddirection, String windpower, String humidity, String reporttime, String province, String cityName) {
        this.weather = weather;
        this.temperature = temperature;
        this.winddirection = winddirection;
        this.windpower = windpower;
        this.humidity = humidity;
        this.reporttime = reporttime;
        this.province = province;
        this.cityName = cityName;
    }

    public cityWeather() {
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWinddirection() {
        return winddirection;
    }

    public void setWinddirection(String winddirection) {
        this.winddirection = winddirection;
    }

    public String getWindpower() {
        return windpower;
    }

    public void setWindpower(String windpower) {
        this.windpower = windpower;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getReporttime() {
        return reporttime;
    }

    public void setReporttime(String reporttime) {
        this.reporttime = reporttime;
    }

    public String toStringCity(){
        String str="";
        str=str+this.province+" "+this.cityName;
        return str;
    }
    public String toStringWeather(){
        String str="";
        str=str+"天气:"+this.weather
                +"\n气温:"+this.temperature+"°C"
                +"\n风向:"+this.winddirection
                +"\n风力:"+this.windpower+"级"
                +"\n湿度:"+this.humidity+"%"
                +"\n数据更新时间:"+this.reporttime;
        return str;
    }
}
