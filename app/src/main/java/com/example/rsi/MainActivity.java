package com.example.rsi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //設定HTTP Get & Post要連線的Url
    private String getUrl = "http://pchome.megatime.com.tw/stock/sto0/ock1/sid3010.html";
    Http_Get HG;

    ViewPager pager;
    ArrayList<View> pagerList;
    private Button getBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HG = new Http_Get();

        pager = findViewById(R.id.pager);
        LayoutInflater li = getLayoutInflater().from(this);
        View v1 = li.inflate(R.layout.initial_layout,null);
        pagerList = new ArrayList<View>();
        pagerList.add(v1);
        pager.setAdapter(new MyViewPagerAdapter(pagerList));
        pager.setCurrentItem(0);

        getBtn = (Button) v1.findViewById(R.id.http_get_btn);
        getBtn.setOnClickListener(btn1Listener);


    }

    private Button.OnClickListener btn1Listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.http_get_btn:
                    //Log.i("123","321");
                    HG.Get(getUrl);
                    break;
                default:
                    break;

            }
        }
    };

    //依照按下的按鈕去做相對應的任務
    public void onClick(View v){
    }

}