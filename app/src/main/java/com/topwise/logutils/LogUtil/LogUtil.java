package com.topwise.logutils.LogUtil;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Description
 * 日志工具类，用于控制日志的显示
 *
 */

public class LogUtil {

    private static final String DEFAULT_TAG = LogUtil.class.getSimpleName();
    private static final int VERBOSE = 1;
    private static final int DEBUG = VERBOSE + 1;
    private static final int INFO = DEBUG + 1;
    private static final int WARN = INFO + 1;
    private static final int ERROR = WARN + 1;
    private static final int NOTHING = ERROR + 1;
    private static int level = VERBOSE;

    private static final boolean WRITETTOFILE = true;
    private static SimpleDateFormat mLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    private static final int MAX_SIZE = 300 * 1024 * 1024;

    public static FileOutputStream mLogFileStream;
    public static File mLogFile;
    //public static String LOGFILENAME_PREFIX = "UnknowName";
    public static String LOGFILENAME_PREFIX = "";
    public final static String LOGFILENAME_SUFFIX = ".log";
    //public static String LOGFILEPATH = "/data/user/0/{包名}/"; //例如：/data/user/0/com.topwise.mofangpay/
    public static String LOGFILEPATH = "";

    public static void setLogFileNamePrefix(String value){
        LOGFILENAME_PREFIX = value;
    }

    public static String getLogFileNamePrefix(){
        return LOGFILENAME_PREFIX;
    }

    public static void setLogFilePath(String value){
        LOGFILEPATH = "/data/user/0/" + value + "/";
    }

    public static String getLogFilePath(){
        return LOGFILEPATH;
    }

    public static void v( String msg) {
        String tag=getTag();
        msg=buildMessage(msg);

        if (level <= VERBOSE) {
            Log.v(DEFAULT_TAG + " " + tag + " ", msg);
        }
        writeLog(tag, msg);
    }

    public static void d( String msg) {
        String tag=getTag();
        msg=buildMessage(msg);
        if (level <= DEBUG) {
            Log.d(DEFAULT_TAG + " " + tag + " ", msg);
        }
        writeLog(tag, msg);
    }

    public static void i( String msg) {
        String tag=getTag();
        msg=buildMessage(msg);
        if (level <= INFO) {
            Log.i(DEFAULT_TAG + " " + tag + " ", msg);
        }
        writeLog(tag, msg);
    }

    public static void w( String msg) {
        String tag=getTag();
        msg=buildMessage(msg);
        if (level <= WARN) {
            Log.w(DEFAULT_TAG + " " + tag + " ", msg);
        }
        writeLog(tag, msg);
    }

    public static void e( String msg) {
        String tag=getTag();
        msg=buildMessage(msg);
        if (level <= ERROR) {
            Log.e(DEFAULT_TAG + " " + tag + " ", msg);
        }
        writeLog(tag, msg);
    }

    //将日志记录到文件中
    public static void writeLog(String tag, String msg) {

        if (!WRITETTOFILE) {
            return;
        }

        File logFile = getLogFile();
        long length;
        length = logFile.length();
        if (length > MAX_SIZE) {
            try {
                mLogFileStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            logFile.delete();
            mLogFileStream = getmLogFileStream();
        }
        Date nowTime = new Date();
        String needWriteMessage = mLogSdf.format(nowTime) + "-" + tag + ": ---" + msg + "\n";

        try {
            if (mLogFileStream != null) {
                mLogFileStream.write(needWriteMessage.getBytes());
                mLogFileStream.flush();
            } else {
                mLogFileStream = getmLogFileStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //得到今天的log文件流
    public static FileOutputStream getmLogFileStream() {
        FileOutputStream fos = null;
        if (mLogFile == null) {
            mLogFile = getLogFile();
        }
        try {
            fos = new FileOutputStream(mLogFile, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fos;
    }

    //得到今天的log
    public static File getLogFile() {

        File logFile,logFileNdayAgo,logFile_tmp;
        logFile = new File(LOGFILEPATH, LOGFILENAME_PREFIX +"_"+"SN"+"_"+getStringDateShort()+LOGFILENAME_SUFFIX);

        // 删除 前n天的那个log
        int n = 3;
        logFileNdayAgo = new File(LOGFILEPATH, LOGFILENAME_PREFIX +"_"+"SN"+"_"+getStringDateShortNDayBefore(n)+LOGFILENAME_SUFFIX);

        if (!logFile.exists()) {

            try {
                LogUtil.d( "create file result\t" + logFile.createNewFile());
                if(logFileNdayAgo.exists()){ // 删除7天前的log
                    LogUtil.d( "guohao-uplog delete "+ n +" day age logfile result\t" + logFileNdayAgo.delete());
                }else{
                    LogUtil.d( "guohao-uplog log文件："+logFileNdayAgo+" 不存在");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return logFile;
    }

    //得到n天前的log
    public static File getLogFile(int day) {

        File logFile;
        logFile = new File(LOGFILEPATH, LOGFILENAME_PREFIX +"_"+"SN"+"_"+getStringDateShortNDayBefore(day)+LOGFILENAME_SUFFIX);

        return logFile;
    }

    /**
     * 方法名和输出信息
     *
     * @param msg 原本输出的信息
     * @return 对原本的信息进行包装加上ｉd和方法名
     */
    private static String buildMessage(String msg) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String caller = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                caller = trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, " %s: %s", caller, msg);
    }

    /**
     * 获取到调用者的类名
     *
     * @return 调用者的类名
     */
    private static String getTag() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace()
                .getStackTrace();
        String callingClass = "";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(LogUtil.class)) {
                callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass
                        .lastIndexOf('.') + 1);
                break;
            }
        }
        return callingClass;
    }

    //得到当天日期的格式化字串
    public static String getStringDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    //得到n天前的日期的格式化字串
    public static String getStringDateShortNDayBefore(int n) {
        Date currentTime = new Date();
        long onedaytTime = 24*60*60*1000;// 1天时间的毫秒
        //int n = 7;// n天前
        currentTime.setTime(currentTime.getTime() - onedaytTime*n );//设置为n天前的时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    //得到上传文件名字
    public static String getUpLoadLogFileName(int day) {
        String name = LOGFILENAME_PREFIX +"_"+/*getTopwiseSn()+*/"_"+getStringDateShortNDayBefore(day)+LOGFILENAME_SUFFIX;
        LogUtil.d(":"+name);
        return name;
    }
}
