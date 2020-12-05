package com.example.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private List<String> followCity;
    private String key="3e61b2b982b12e07952be39cd784d86a";
    private Button search;
    private List<cityWeather> ls=new ArrayList<>();//关注城市列表

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final ListView lsView=(ListView)findViewById(R.id.list_view);

        search=(Button)findViewById(R.id.main_search) ;
        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
        //读取关注城市并显示在ListView
        SharedPreferences prefs=getSharedPreferences("followCity",MODE_PRIVATE);

        lsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cityWeather temp=ls.get(i);
                Intent intent=new Intent(MainActivity.this,WeatherActivity.class);
                intent.putExtra("adcode",temp.getAdcode());
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        ls.clear();

        //读取sp中关注的城市
        SharedPreferences spf=getSharedPreferences("followCity",MODE_PRIVATE);
        //使用Map获取所有键值对
        Map<String,?> key_value=(Map<String,?>)spf.getAll();
        //所有键（即adcode）
        Set<String> key=key_value.keySet();
        for (String adcode:key){
            //对应城市的天气类
            Log.d("hwan",adcode);
            String str_url="https://restapi.amap.com/v3/weather/weatherInfo?"
                    +"city="+adcode
                    +"&key=3e61b2b982b12e07952be39cd784d86a"
                    +"&extensions=base"
                    +"&output=JSON";
            cityWeather temp=searchWeather(str_url);
            Log.d("hwan",temp.toStringCity());
            ls.add(temp);
        }
        Adapter adapter=new Adapter(MainActivity.this,R.layout.item,ls);
        ListView lsView=(ListView)findViewById(R.id.list_view);

        lsView.setAdapter(adapter);
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
class Adapter extends ArrayAdapter<cityWeather> {
    private int resourceId;
    public Adapter(@NonNull Context context, int resource, List<cityWeather> objects) {
        super(context, resource,objects);
        resourceId=resource;
    }
    @Override
    //修改getView，提高性能
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        cityWeather city_weather=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView item_loca=(TextView)view.findViewById(R.id.item_location);
        TextView item_adcode=(TextView)view.findViewById(R.id.item_adcode);
        TextView item_temp=(TextView)view.findViewById(R.id.item_temp);
        TextView item_wea=(TextView)view.findViewById(R.id.item_wea);
        item_loca.setText(city_weather.toStringCity());
        item_adcode.setText("地区编码:"+city_weather.getAdcode());
        item_temp.setText(city_weather.getTemperature()+"°C");
        item_wea.setText(city_weather.getWeather());
        return view;
    }
}