package com.wen.asyl.webservicedemo;




import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;




public class WebserverDownData {

    public static String downData(String json, String methodName) {
        // 命名空间
        String nameSpace = "http://WebXml.com.cn/";
        // 调用的方法名称
        String action=nameSpace+methodName;
        String wsdlUrl = "http://www.webxml.com.cn/webservices/weatherwebservice.asmx";
        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);
        // 设置需调用WebService接口需要传入的参数
        rpc.addProperty("byProvinceName", json);
        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;  //true是.net false是java
        envelope.setOutputSoapObject(rpc);
        HttpTransportSE transport = new HttpTransportSE(wsdlUrl);
        transport.debug = true;
        try {
            // 调用WebService
            transport.call(action, envelope);
            // 获取返回的数据
            if (envelope.getResponse()!=null){
                SoapObject   object = (SoapObject) envelope.getResponse();
                return object.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
