package com.restress.demo;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {


    private List<TestDemo> testDemos = new ArrayList<>();

    private TestDemo[] testDemos1  = new TestDemo[3];

    private TestDemo testDemo1;

    private Gson gson;
    private GsonBuilder builder;
    private String JsonTest;

    // 命名空间
    public static final String NAMESPACE = "http://tempuri.org/";

    // 调用的方法名称
    public static final String methodName = "ToAndriod";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //新创建一个水果list类
        initFruit();
        attmptConnect();
    }

    public void attmptConnect(){

        //TODO 这里需要成对应的
         String WEB_SERVER_URL = "host/Demo.asmx";

        // 含有3个线程的线程池
        ExecutorService executorService = Executors
                .newFixedThreadPool(3);


        // 创建HttpTransportSE对象，传递WebService服务器地址
        final HttpTransportSE httpTransportSE = new HttpTransportSE(WEB_SERVER_URL);
        // 创建SoapObject对象
        SoapObject soapObject = new SoapObject(NAMESPACE,methodName);

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        soapObject.addProperty("HH",JsonTest);


        // 实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
        final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER12);
        // 设置是否调用的是.Net开发的WebService
        soapEnvelope.setOutputSoapObject(soapObject);
        soapEnvelope.dotNet = true;
        httpTransportSE.debug = true;


        // 用于子线程与主线程通信的Handler
        final Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                SoapObject result = (SoapObject) msg.obj;
               /* SoapObject provinceSoapObject = (SoapObject) result.getProperty("registerResponse");*/
                String resultFinal = result.getProperty("FromAndriodResult").toString();

                Toast toast = Toast.makeText(MainActivity.this,resultFinal, Toast.LENGTH_SHORT);
                toast.show();


                // 将返回值回调到callBack的参数中
                //webServiceCallBack.callBack((SoapObject) msg.obj);
            }

        };

        // 开启线程去访问WebService
        executorService.submit(new Runnable() {

            @Override
            public void run() {
                SoapObject resultSoapObject = null;
                try {

                   httpTransportSE.call(NAMESPACE + methodName, soapEnvelope);
                    if (soapEnvelope.getResponse() != null) {
                        // 获取服务器响应返回的SoapObject
                        //TODO
                        resultSoapObject = (SoapObject) soapEnvelope.bodyIn;
                    }
                } catch (HttpResponseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } finally {
                    // 将获取的消息利用Handler发送到主线程
                    mHandler.sendMessage(mHandler.obtainMessage(0,
                            resultSoapObject));
                }
            }
        });
    }



public void initFruit(){


        testDemo1 = new TestDemo();
        testDemo1.setA(1);
        testDemo1.setB(3);
        testDemo1.setC(6);
        testDemos.add(testDemo1);
        testDemos1[0] = testDemo1;


        TestDemo testDemo2 = new TestDemo();
        testDemo2.setA(10);
        testDemo2.setB(30);
        testDemo2.setC(60);
        testDemos.add(testDemo2);
        testDemos1[1] = testDemo2;

        //这两句代码必须的，为的是初始化出来gson这个对象，才能拿来用
        builder=new GsonBuilder();
        gson=builder.create();
        JsonTest = gson.toJson(testDemos);

       /* TestDemo testDemo3 = new TestDemo();
        testDemo3.setA(100);
        testDemo3.setB(300);
        testDemo3.setC(600);
        testDemos.add(testDemo3);*/

    }

}
