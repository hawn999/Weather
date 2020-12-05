package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Button search1,search2;
    private EditText editText;
    private String adcode="";//传入weather的adcode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //直接输入adcode
        search1=(Button)findViewById(R.id.search1);
        editText=(EditText)findViewById(R.id.editText);
        search1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adcode=editText.getText().toString();
                Intent intent=new Intent(SearchActivity.this,WeatherActivity.class);
                intent.putExtra("adcode",adcode);
                startActivity(intent);
            }
        });
//        search2=(Button)findViewById(R.id.search2);

    }

}