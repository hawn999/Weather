package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;

public class WeatherActivity extends AppCompatActivity {

    private String adcode;//用于接受传入的adcode
    private cityWeather city_weather;//该城市的天气类
    private String key="3e61b2b982b12e07952be39cd784d86a";//API_Key
    private TextView resText,location;
    private Button follow,refresh;
    private boolean isSuccess=true;//记录查询是否成功
    private String t="";//用以记录缓存中的key

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent=getIntent();
        adcode=intent.getStringExtra("adcode");
//        Log.d("hwan","adcode="+adcode);
        //显示查询结果
        resText=(TextView)findViewById(R.id.result);
        location=(TextView)findViewById(R.id.location);
        follow=(Button)findViewById(R.id.follow);
        refresh=(Button)findViewById(R.id.refresh);


        final String str_url="https://restapi.amap.com/v3/weather/weatherInfo?"
                +"city="+adcode
                +"&key=3e61b2b982b12e07952be39cd784d86a"
                +"&extensions=base"
                +"&output=JSON";
//        String str_url="https://restapi.amap.com/v3/weather/weatherInfo?city=110101&key=3e61b2b982b12e07952be39cd784d86a";

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityWeather city_weather=searchWeather(str_url);

                if (city_weather.getCityName()!=null) {//查询成功
                    isSuccess=true;
                }else {
                    isSuccess=false;
                }
                if(isSuccess) {
                    //使用SPUtil将该城市加入SharedPreferences中
                    SPUtil.setObject(WeatherActivity.this, adcode, city_weather, "followCity");
                    Toast.makeText(WeatherActivity.this, "关注成功！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(WeatherActivity.this,"查询失败！不能关注，请重新查询！",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //读取缓存的三条历史记录
        SharedPreferences spf=getSharedPreferences("history",MODE_PRIVATE);
        Map<String,?> key_value=(Map<String,?>)spf.getAll();

        if (containsKey(adcode)){//要查询的城市在缓存中
            cityWeather wea=SPUtil.getObject(WeatherActivity.this,t,"history");
            String searchtime=new Date().toString();
            resText.setText(wea.toStringWeather()+"\n查询时间:"+searchtime);
            location.setText(wea.toStringCity());
            Toast.makeText(WeatherActivity.this,"直接从缓存中读取的数据",Toast.LENGTH_SHORT).show();
            Log.d("hwan","直接从缓存中读取的数据");
        }else{
            showResult(str_url);
            Toast.makeText(WeatherActivity.this,"未直接从缓存中读取的数据",Toast.LENGTH_SHORT).show();
            Log.d("hwan","未直接从缓存中读取的数据");
        }

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResult(str_url);
                Toast.makeText(WeatherActivity.this,"刷新成功！",Toast.LENGTH_SHORT).show();
            }
        });

    }

    boolean containsKey(String str){
        SharedPreferences spf=getSharedPreferences("history",MODE_PRIVATE);
        Map<String,?> key_value=(Map<String,?>)spf.getAll();
        int n=key_value.size();
        boolean flag=false;
        Log.d("hwan","开始循环");
        for (int i=0;i<n;i++){
            Log.d("hwan","i="+i);
            cityWeather spW=SPUtil.getObject(WeatherActivity.this,String.valueOf(i),"history");
            String ad="";
            ad=spW.getAdcode();
            Log.d("hwan", spW.getAdcode());
            Log.d("hwan",adcode);
            if (ad.equals(adcode)){
                flag=true;
                t=String.valueOf(i);
                break;
            }
        }
        return flag;
    }

    void showResult(String str_url){

        final cityWeather city_weather=searchWeather(str_url);

        if (city_weather.getCityName()!=null){//查询成功
            String searchtime=new Date().toString();
            resText.setText(city_weather.toStringWeather()+"\n查询时间:"+searchtime);
            location.setText(city_weather.toStringCity());
            //使用SPUtil将存储查询记录放入SharedPreferences中
            SharedPreferences spf=getSharedPreferences("history",MODE_PRIVATE);
            //使用Map获取所有键值对
            Map<String,?> key_value=(Map<String,?>)spf.getAll();
            SharedPreferences.Editor editor=spf.edit();
            //保障只保存3条
            if (key_value.size()==0){
                SPUtil.setObject(WeatherActivity.this,"0",city_weather,"history");
            }
            else if (key_value.size()==1){
                SPUtil.setObject(WeatherActivity.this,"1",city_weather,"history");
            }
            else if (key_value.size()==2){
                SPUtil.setObject(WeatherActivity.this,"2",city_weather,"history");
            }
            else if (key_value.size()==3){
                //读取存储的三个天气类
                cityWeather wea1=SPUtil.getObject(WeatherActivity.this,"0","history"),
                        wea2=SPUtil.getObject(WeatherActivity.this,"1","history"),
                        wea3=SPUtil.getObject(WeatherActivity.this,"2","history");
//                Log.d("hwan",wea1.getAdcode()+" "+wea2.getAdcode()+" "+wea3.getAdcode());
                //重新设置
                SPUtil.setObject(WeatherActivity.this,"0",wea2,"history");
                SPUtil.setObject(WeatherActivity.this,"1",wea3,"history");
                SPUtil.setObject(WeatherActivity.this,"2",city_weather,"history");
            }
        }else{//查询不成功
            isSuccess=false;
            location.setText("抱歉！");
            resText.setText("您输入的城市ID有误，请重新输入");
        }
    }

    cityWeather searchWeather(String str_url){
        cityWeather city_weather=new cityWeather();
        try {
            StringBuffer stringBuffer=new StringBuffer();
            URL url=new URL(str_url);
            HttpURLConnection con=null;
            con=(HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setReadTimeout(5000);
            con.setConnectTimeout(5000);
            con.setDoInput(true);
            con.setUseCaches(false);
            InputStream inputStream=null;
            BufferedReader reader=null;

            int code=con.getResponseCode();
            if(code==200){
                inputStream=con.getInputStream();
            }else{
                inputStream=con.getErrorStream();
            }
            reader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String temp=null;
            //循环读取
            while ((temp=reader.readLine())!=null){
                stringBuffer.append(temp);
            }

            reader.close();
            inputStream.close();
            con.disconnect();

            JSONObject object=new JSONObject(stringBuffer.toString());
            //获取lives数组
            JSONArray lives=object.getJSONArray("lives");
            //取出数组中的第一项,即天气信息
            JSONObject weatherInfo=lives.getJSONObject(0);
            city_weather.setProvince(weatherInfo.getString("province"));
            city_weather.setCityName(weatherInfo.getString("city"));
            city_weather.setWeather(weatherInfo.getString("weather"));
            city_weather.setTemperature(weatherInfo.getString("temperature"));
            city_weather.setWinddirection(weatherInfo.getString("winddirection"));
            city_weather.setWindpower(weatherInfo.getString("windpower"));
            city_weather.setHumidity(weatherInfo.getString("humidity"));
            city_weather.setReporttime(weatherInfo.getString("reporttime"));
            city_weather.setAdcode(weatherInfo.getString("adcode"));
//            Log.d("hwan",weatherInfo.getString("temperature"));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return city_weather;
    }

}
