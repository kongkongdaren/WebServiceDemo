package com.wen.asyl.webservicedemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private TextView mTvContent;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                mTvContent.setText("解析结果如下：\n"+ (String)msg.obj.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvContent = (TextView) findViewById(R.id.tv_content);

    }
    public void downLoadOnClick(View view){
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("byProvinceName", "北京");
        //如果有请求字段则写properties,否则将properties至为null即可
        WebServiceUtils.callWebService(WebServiceUtils.WEB_SERVER_URL, "getSupportCity", properties, new WebServiceUtils.WebServiceCallBack() {
            @Override
            public void callBack(SoapObject result) {
                if (result != null) {
                    Log.e("result",result.toString());
                    mTvContent.setText("解析结果如下：\n"+ result);
                }
            }
        });
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                String data =WebserverDownData.downData("北京", "getSupportCity");
//                Message msg=Message.obtain();
//                msg.what=1;
//                msg.obj=data;
//                handler.sendMessage(msg);
//            }
//        }.start();


    }
}

