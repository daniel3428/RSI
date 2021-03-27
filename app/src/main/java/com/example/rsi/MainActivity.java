package com.example.rsi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String[] ChId = {"ch3","ch4"};
    private int ch_index = 0;

    private void setNotification(String message) {
        Intent intent= new Intent();
        intent.setClass(this, MainActivity.class);
        //intent.setAction(MyService.ACTION1);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
        | Intent.FLAG_ACTIVITY_NEW_TASK);
        //PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.pika)
                .setWhen(System.currentTimeMillis())
                .setContentText("Go")
                //.setContentIntent(pendingIntent)
                .setChannelId("2")
                .setContentInfo("3");

        builder.setContentTitle("Hi");
        //builder.addAction(111,"ACTION1",pendingIntent);
        //builder.setVibrate(new long[] { 1000, 3000, 1000});


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel;
            channel = new NotificationChannel(ChId[ch_index], "Todo list", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{500});

            if (!TextUtils.isEmpty(ChId[ch_index])) {
                //先删除之前的channelId对应的消息通道.
                manager.deleteNotificationChannel(ChId[ch_index]);
                Log.i("wangshu", String.valueOf(ch_index));
            }
            if (!TextUtils.isEmpty(ChId[ch_index])) {
                Log.i("wangshu", String.valueOf(ch_index));
            }
            ch_index = (ch_index + 1) % 2;
            channel = new NotificationChannel(ChId[ch_index], "Todo list", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{500});

            manager.createNotificationChannel(channel);
            builder.setChannelId(ChId[ch_index]);
        }
        Notification notification = builder.build();
        //notification.defaults |= Notification.DEFAULT_VIBRATE;

        manager.notify((int)(Math.random()*55446), notification);
        //Log.i("wangshu", String.valueOf(Build.VERSION.SDK_INT));
    }

    static Handler handler; //宣告成static讓service可以直接使用

    //設定HTTP Get & Post要連線的Url
    private String getUrl = "https://pchome.megatime.com.tw/stock/sto0/ock3/sid3010.html";
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

        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    // 當收到的Message的代號為我們剛剛訂的代號就做下面的動作。
                    case R.integer.sentToMain:
                        String ss_msg = (String)msg.obj;
                        //Log.i("wangshu", ss_msg);
                        setNotification(ss_msg);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void setVibrate(int time){
        Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(time);
    }

    private Button.OnClickListener btn1Listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.http_get_btn:
                    //Log.i("123","321");
                    for (int i = 0; i < 10000; i++) {

                        //final int test_index;
                        //test_index = i;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                HG.Get(getUrl);
                            }
                        }, 10000 * i);
                    }
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