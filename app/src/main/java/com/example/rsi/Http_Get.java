package com.example.rsi;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;

public class Http_Get extends Service {

    private String getUrl;

    private ArrayList<Integer> upDownArr;
    private ArrayList<Double> buyPriceArr;
    private ArrayList<Double> sellPriceArr;
    private ArrayList<Double> dealPriceArr;
    private ArrayList<Integer> dealQuantityArr;
    private ArrayList<LocalTime> preTimestampTemp;
    private ArrayList<RSI> rsiArr;

    private void processOneLine(String inputString) {
        int i_temp=0, priceDigit=1,quantityDigit=1;

        if (inputString.indexOf('.',i_temp) > -1) {
            //Log.i("123",inputString.substring(inputString.indexOf('.',0)-2,
            //inputString.indexOf('.',0)+3));
            for (int i=1;i<=4;i++) {
                if(inputString.charAt(inputString.indexOf('.',i_temp)-i) <= '9' &&
                        inputString.charAt(inputString.indexOf('.',i_temp)-i) >= '0') {
                    priceDigit = i;
                }
                else {
                    break;
                }
            }
            if(inputString.indexOf("<!--價量明細 結束-->") < 0) {

                this.buyPriceArr.add(Double.valueOf(inputString.substring(inputString.indexOf('.',i_temp)-priceDigit,
                        inputString.indexOf('.',i_temp)+3)));
                i_temp = inputString.indexOf('.',i_temp) + 1;
                this.sellPriceArr.add(Double.valueOf(inputString.substring(inputString.indexOf('.',i_temp)-priceDigit,
                        inputString.indexOf('.',i_temp)+3)));
                i_temp = inputString.indexOf('.',i_temp) + 1;
                this.dealPriceArr.add(Double.valueOf(inputString.substring(inputString.indexOf('.',i_temp)-priceDigit,
                        inputString.indexOf('.',i_temp)+3)));
            }
            else {
                this.dealPriceArr.add(Double.valueOf(inputString.substring(inputString.indexOf('.',i_temp)-priceDigit,
                        inputString.indexOf('.',i_temp)+3)));
                i_temp = inputString.indexOf("</td><td>",i_temp) + 1;
                //i_temp = inputString.indexOf('.',i_temp) + 1;
            }
            //Log.i("123",inputString.substring(inputString.indexOf("</td><td>",i_temp)));
            for (int i=0;i<3;i++) {
                i_temp = inputString.indexOf("</td><td>",i_temp) + 1;
            }
            //Log.i("123",String.valueOf(this.dealPriceArr.size()));
            //Log.i("123",inputString.substring(inputString.indexOf("</td><td>",i_temp)-8,
            //inputString.indexOf("</td><td>",i_temp)-5));
            if (inputString.charAt(inputString.indexOf("</td><td>",i_temp)-1) <= '9' &&
                    inputString.charAt(inputString.indexOf("</td><td>",i_temp)-1) >= '0') {
                for (int i=1;i<=4;i++) {
                    if(inputString.charAt(inputString.indexOf("</td><td>",i_temp)-i) <= '9' &&
                            inputString.charAt(inputString.indexOf("</td><td>",i_temp)-i) >= '0') {
                        quantityDigit = i;
                    }
                    else {
                        break;
                    }
                }
                this.dealQuantityArr.add(Integer.valueOf(inputString.substring(inputString.indexOf("</td><td>",i_temp)-quantityDigit,
                        inputString.indexOf("</td><td>",i_temp))));

            }
            else {
                for (int i=1;i<=4;i++) {
                    //Log.i("123",inputString.substring(inputString.indexOf("</td><td>",i_temp)-7-i));
                    if(inputString.charAt(inputString.indexOf("</td><td>",i_temp)-7-i) <= '9' &&
                            inputString.charAt(inputString.indexOf("</td><td>",i_temp)-7-i) >= '0') {
                        quantityDigit = i;
                    }
                    else {
                        break;
                    }
                }
                this.dealQuantityArr.add(Integer.valueOf(inputString.substring(inputString.indexOf("</td><td>",i_temp)-7-quantityDigit,
                        inputString.indexOf("</td><td>",i_temp)-7)));
                if(inputString.charAt(inputString.indexOf("</td><td>",i_temp)-7-quantityDigit-7)== 'e') {
                    //Log.i("123",String.valueOf(inputString.charAt(inputString.indexOf("</td><td>",i_temp)-7-quantityDigit-7)));
                    upDownArr.add(1);
                }
                else if(inputString.charAt(inputString.indexOf("</td><td>",i_temp)-7-quantityDigit-7)== '3'){
                    //Log.i("123",String.valueOf(inputString.charAt(inputString.indexOf("</td><td>",i_temp)-7-quantityDigit-7)));
                    upDownArr.add(-1);
                }
                else {
                    upDownArr.add(0);
                }
                //Log.i("123",inputString.substring(inputString.indexOf("</td><td>",i_temp)-7-quantityDigit,
                //inputString.indexOf("</td><td>",i_temp)-7));
            }
            //Log.i("123",String.valueOf(dealQuantityArr.get(dealQuantityArr.size()-1)));
            //Log.i("123",String.valueOf(dealPriceArr.get(dealPriceArr.size()-1)));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void converStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        boolean rightLine;
        char c_temp;
        int i_temp;

        this.buyPriceArr = new ArrayList<>();
        this.sellPriceArr = new ArrayList<>();
        this.dealPriceArr = new ArrayList<>();
        this.dealQuantityArr = new ArrayList<>();
        this.upDownArr = new ArrayList<>();
        this.preTimestampTemp = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            if(line.compareTo("<!--價量明細 開始-->") == 0) {
                break;
            }
        }

        while(true) {
            line = String.valueOf("");
            rightLine = false;
            c_temp = (char)reader.read();
            while (c_temp != ':') {
                line = line.concat(String.valueOf(c_temp));
                c_temp = (char)reader.read();
                //Log.i("wangshu", String.valueOf(c_temp));
                //break;
            }

            for (int i=0;i<6;i++) {
                c_temp = (char)reader.read();
                //line = line.concat(String.valueOf(c_temp));


                //Log.i("123", String.valueOf(c_temp));

                if (i==2 && c_temp == ':' ) {
                    rightLine = true;
                    this.preTimestampTemp.add(LocalTime.now());
                    //Log.i("wangshu", String.valueOf(this.timestampTemp.getHour()));
                } else if(i==2 && line.indexOf("<!--價量明細 結束-->") > 0) {
                    rightLine = true;
                }
                else {
                    line = line.concat(String.valueOf(c_temp));
                }

            }

            if (rightLine) {
                //Log.i("wangshu", line.substring(line.length()-9,line.length()-1));
                if (line.indexOf("<!--價量明細 結束-->") < 0) {
                    i_temp = Integer.parseInt(line.substring(line.length()-7,line.length()-5));
                    this.preTimestampTemp.set(this.preTimestampTemp.size()-1,
                            this.preTimestampTemp.get(this.preTimestampTemp.size()-1).withHour(i_temp));
                    i_temp = Integer.parseInt(line.substring(line.length()-5,line.length()-3));
                    this.preTimestampTemp.set(this.preTimestampTemp.size()-1,
                            this.preTimestampTemp.get(this.preTimestampTemp.size()-1).withMinute(i_temp));
                    i_temp = Integer.parseInt(line.substring(line.length()-3,line.length()-1));
                    this.preTimestampTemp.set(this.preTimestampTemp.size()-1,
                            this.preTimestampTemp.get(this.preTimestampTemp.size()-1).withSecond(i_temp));
                }
                processOneLine(line);
                //Log.i("wangshu", line);
            }

            if (line.indexOf("<!--價量明細 結束-->") > 0) {
                break;
            }
        }
        //Log.i("wangshu", line);
        sentToMainActivity(R.integer.sentToMain, line);
    }

    private void sentToMainActivity (int number, String message) {

        Message msg = Message.obtain();
        //設定Message的內容
        msg.what = number;
        msg.obj = message;
        //使用MainActivity的static handler來丟Message
        MainActivity.handler.sendMessage(msg);
    }

    private void calculateRSI() {
        RSI tempRSI;
        int i;

        this.rsiArr = new ArrayList<>();
        tempRSI = new RSI();
        tempRSI.downMean = 0.2;
        tempRSI.upMean = 0.3;
        tempRSI.timeStamp = LocalTime.now();
        tempRSI.timeStamp = tempRSI.timeStamp.withHour(2);
        this.rsiArr.add(tempRSI);

        tempRSI = new RSI();
        tempRSI.downMean = 0.5;
        tempRSI.upMean = 0.7;
        tempRSI.timeStamp = LocalTime.now();
        tempRSI.timeStamp = tempRSI.timeStamp.withHour(3);
        this.rsiArr.add(tempRSI);

        tempRSI = new RSI();
        tempRSI.downMean = 0.5;
        tempRSI.upMean = 0.7;
        tempRSI.timeStamp = LocalTime.now();
        tempRSI.timeStamp = tempRSI.timeStamp.withHour(3);
        this.rsiArr.add(tempRSI);;
        /*Log.i("wangshu", String.valueOf(this.rsiArr.size()));
        for(i = 0; i < this.rsiArr.size(); i++) {
            Log.i("wangshu", String.valueOf(this.rsiArr.get(i).timeStamp));
        }*/
    }

    public void Get(String url){
        this.getUrl = url;

        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpParams mDefaultHttpParams = new BasicHttpParams();
                //设置连接超时
                HttpConnectionParams.setConnectionTimeout(mDefaultHttpParams, 15000);
                //设置请求超时
                HttpConnectionParams.setSoTimeout(mDefaultHttpParams, 15000);
                HttpConnectionParams.setTcpNoDelay(mDefaultHttpParams, true);
                HttpProtocolParams.setVersion(mDefaultHttpParams, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(mDefaultHttpParams, HTTP.UTF_8);
                //持续握手
                HttpProtocolParams.setUseExpectContinue(mDefaultHttpParams, true);
                //建立HttpClient物件
                HttpClient httpClient = new DefaultHttpClient(mDefaultHttpParams);
                //建立Http Get，並給予要連線的Url
                HttpGet get = new HttpGet(getUrl);
                //透過Get跟Http Server連線並取回傳值，並將傳值透過Log顯示出來

                get.addHeader("Connection", "Keep-Alive");
                get.addHeader("Accept-Encoding", "deflate");
                get.addHeader("Accept", "text/html");
                get.addHeader("Accept-Language", "zh-TW,zh;q=0.9,en-US;q=0.8,en;q=0.7");
                get.addHeader("Referer", "http://pchome.megatime.com.tw/stock/sto0/ock3/sid3010.html");

                try {
                    HttpResponse mHttpResponse = httpClient.execute(get);
                    HttpEntity mHttpEntity = mHttpResponse.getEntity();
                    int code = mHttpResponse.getStatusLine().getStatusCode();
                    if (null != mHttpEntity) {
                        InputStream mInputStream = mHttpEntity.getContent();
                        converStreamToString(mInputStream);
                        //Log.i("wangshu", "請求狀態碼:" + code + "\n請求結果:\n" + respose);
                        calculateRSI();
                        mInputStream.close();
                    }
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
