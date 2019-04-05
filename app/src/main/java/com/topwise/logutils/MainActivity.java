package com.topwise.logutils;


import android.os.Bundle;
import android.view.View;

import com.topwise.logutils.LogUtil.CrashLogUtil;
import com.topwise.logutils.LogUtil.LogUtil;

public class MainActivity extends BaseTestActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        // 初始化
        CrashLogUtil.getInstance().init(this);

        // 设置log前缀名
        LogUtil.setLogFileNamePrefix("ProjectName");

        // 设置log路径，需要传进包名
        LogUtil.setLogFilePath(getPackageName());
    }

    public void test_1(View v){
        //do something
        showMessage("click test_1 bt");

        LogUtil.d("test");

        //String ss = "123";
        //ss.charAt(5);//越界错误的捕捉

    }

    private static String FTP_SERVER = "";
    private static int FTP_PORT = 0;
    private static String FTP_USERNAME = "";
    private static String FTP_PASSWORD = "";

    public void test_2(View v){
        LogUtil.setFtpServer(FTP_SERVER);
        LogUtil.setFtpPort(FTP_PORT);
        LogUtil.setFtpUsername(FTP_USERNAME);
        LogUtil.setFtpPassword(FTP_PASSWORD);
        LogUtil.startUplaod(0);
    }
}
