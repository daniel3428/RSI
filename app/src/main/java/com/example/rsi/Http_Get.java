package com.example.rsi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

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

public class Http_Get extends Service {

    private String getUrl;

    private void converStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;

        while ((line = reader.readLine()) != null) {
            Log.i("wangshu", line);
        }
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
                get.addHeader("Referer", "http://pchome.megatime.com.tw/stock/sto0/ock3/sid6552.html");

                try {
                    HttpResponse mHttpResponse = httpClient.execute(get);
                    HttpEntity mHttpEntity = mHttpResponse.getEntity();
                    int code = mHttpResponse.getStatusLine().getStatusCode();
                    if (null != mHttpEntity) {
                        InputStream mInputStream = mHttpEntity.getContent();
                        converStreamToString(mInputStream);
                        //Log.i("wangshu", "請求狀態碼:" + code + "\n請求結果:\n" + respose);
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
