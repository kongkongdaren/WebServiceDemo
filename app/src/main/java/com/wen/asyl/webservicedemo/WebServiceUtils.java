package com.wen.asyl.webservicedemo;

import android.os.Handler;
import android.os.Message;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServiceUtils {
    public static final String WEB_SERVER_URL = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx";
    // 含有3个线程的线程池
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);//限制线程池大小为3的线程池
    // 命名空间
    private static final String NAMESPACE = "http://WebXml.com.cn/";

    public static void callWebService(String url, final String methodName, HashMap<String,String> properties, final WebServiceCallBack webServiceCallBack){
        //创建HttpTransportSE对象，传递WebService服务器地址
        final HttpTransportSE httpTransportSE = new HttpTransportSE(url);
        //创建SoapObject对象
        final SoapObject soapObject = new SoapObject(NAMESPACE,methodName);
        //SoapObject添加参数
        if (properties != null){
            for (Iterator<Map.Entry<String,String>> it = properties.entrySet().iterator(); it.hasNext();){
                Map.Entry<String,String> entry = it.next();
                soapObject.addProperty(entry.getKey(),entry.getValue());
            }
        }
        //实例化SoapSerializationEnvelope,传入WebService的SOAP协议的版本号
        final SoapSerializationEnvelope soapSerializationEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        //设置是否调用的是.NET开发的WebService
        soapSerializationEnvelope.setOutputSoapObject(soapObject);
        soapSerializationEnvelope.dotNet = true;
        httpTransportSE.debug = true;

        //用于子线程与主线程通信的Handler
        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //将返回值回调到callBack的参数中
                webServiceCallBack.callBack((SoapObject) msg.obj);
            }
        };
        //开启线程去访问WebService
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                SoapObject resultSoapObject = null;
                try {
                    httpTransportSE.call(NAMESPACE + methodName,soapSerializationEnvelope);
                    if (soapSerializationEnvelope.getResponse() != null){
                        //获取服务器响应返回的SoapObject
                        resultSoapObject = (SoapObject) soapSerializationEnvelope.bodyIn;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }finally {
                    //将获取的消息通过handler发到主线程
                    mHandler.sendMessage(mHandler.obtainMessage(0,resultSoapObject));
                }
            }
        });
    }

    public interface WebServiceCallBack{
        public void callBack(SoapObject result);
    }
}