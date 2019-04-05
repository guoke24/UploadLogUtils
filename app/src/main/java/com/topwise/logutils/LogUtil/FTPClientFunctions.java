package com.topwise.logutils.LogUtil;





import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;




/**
 * Created by topwise on 19-3-27.
 */

public class FTPClientFunctions {

    private static final String TAG = "FTPClientFunctions";
    // 加密密钥
    private static final  byte[] logKey ="1234567890QWERT@".getBytes();
    private static final String Algorithm = "DESede/ECB/PKCS5Padding";
    private FTPClient ftpClient = null; // FTP客户端

    /**
     * 连接到FTP服务器
     *
     * @param host     ftp服务器域名
     * @param username 访问用户名
     * @param password 访问密码
     * @param port     端口
     * @return 是否连接成功
     */
    public boolean ftpConnect(String host, String username, String password, int port) {
        try {
            ftpClient = new FTPClient();
            LogUtil.d("connecting to the ftp server " + host + " ：" + port);
            ftpClient.connect(host);
            // 根据返回的状态码，判断链接是否建立成功
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                LogUtil.d( "login to the ftp server");
                boolean status = ftpClient.login(username, password);
                /*
                 * 设置文件传输模式
                 * 避免一些可能会出现的问题，在这里必须要设定文件的传输格式。
                 * 在这里我们使用BINARY_FILE_TYPE来传输文本、图像和压缩文件。
                 */
//                ftpClient.changeWorkingDirectory("/share");
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                // 使用被动模式设为默认
                ftpClient.enterLocalPassiveMode();
                // 设置模式
//                ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
//                ftpClient.enterLocalPassiveMode();

//                ftpClient.setControlEncoding("UTF-8");
//                ftpClient.enterLocalPassiveMode();

                return status;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("Error: could not connect to host " + host);
        }
        return false;
    }

    /**
     * 断开ftp服务器连接
     *
     * @return 断开结果
     */
    public boolean ftpDisconnect() {
        // 判断空指针
        if (ftpClient == null) {
            return true;
        }

        // 断开ftp服务器连接
        try {
            ftpClient.logout();
            ftpClient.disconnect();
            return true;
        } catch (Exception e) {
            LogUtil.d("Error occurred while disconnecting from ftp server.");
        }
        return false;
    }

    /**
     * ftp 文件上传
     *
     * @param srcFilePath  源文件目录
     * @param desFileName  文件名称
     * @param desDirectory 目标文件
     * @return 文件上传结果
     */
    public boolean ftpUpload(String srcFilePath, String desFileName, String desDirectory) {
        boolean status = false;
        try {
            File localfile =new File(srcFilePath);
            FileInputStream srcFileStream = new FileInputStream(localfile);
            String filename = localfile.getName();
            status = ftpClient.storeFile(localfile.getName(), srcFileStream);
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d( "upload failed: " + e.getLocalizedMessage());
        }
        return status;
    }
    /**
     * ftp 文件上传
     *  默认日志目录上传
     * @return 文件上传结果
     */
    public boolean ftpUpload() {
        boolean status = false;
        try {
            File localfile = LogUtil.getLogFile() ;
            FileInputStream srcFileStream = new FileInputStream(localfile);
            String filename = localfile.getName();
            status = ftpClient.storeFile(localfile.getName(), srcFileStream);
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d( "upload failed: " + e.getLocalizedMessage());
        }
        return status;
    }

    /**
     * ftp 文件上传,根据 day 选择上传哪一天
     *  默认日志目录上传
     * @return 文件上传结果
     */
    public int ftpUpload(int day) {
        boolean status = false;
        try {
            File localfile = LogUtil.getLogFile(day) ;
            if(!localfile.exists()){
                LogUtil.w( "log文件:"+localfile.getName()+",不存在!");
                return 2;
            }
            FileInputStream srcFileStream = new FileInputStream(localfile);
            // 想改变上传的文件的名字，在这里直接传进去就好了。
            //status = ftpClient.storeFile(localfile.getName(), srcFileStream);
            status = ftpClient.storeFile(LogUtil.getUpLoadLogFileName(day), srcFileStream);
            srcFileStream.close();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d( "upload failed: " + e.getLocalizedMessage());
        }
        return 1;
    }



    /**
     * ftp 更改目录
     *
     * @param path 更改的路径
     * @return 更改是否成功
     */
    public boolean ftpChangeDir(String path) {
        boolean status = false;
        try {
            status = ftpClient.changeWorkingDirectory(path);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("change directory failed: " + e.getLocalizedMessage());
        }
        return status;
    }


    public static String decrypt3DES(byte[] data) throws Exception {
        byte[] buf ;
        // 恢复密钥
        if(data == null || data.length ==0){
            return "";

        }

        SecretKey secretKey = new SecretKeySpec(logKey, Algorithm);

        // Cipher完成解密

        Cipher cipher = Cipher.getInstance(Algorithm);

        // 初始化cipher

        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] plain = cipher.doFinal(data);

        String hexData = ConvertUtil.bytesToHexString(plain);
        return hexData;


    }



    public static String encrypt3DES(byte[] data) throws Exception {


        if(data == null || data.length ==0){
            return "";

        }

        SecretKey secretKey = new SecretKeySpec(logKey, Algorithm);

        // Cipher完成加密

        Cipher cipher = Cipher.getInstance(Algorithm);

        // cipher初始化

        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encrypt = cipher.doFinal(data);

        //转码 base64转码
        String hexData = ConvertUtil.bytesToHexString(encrypt);
        return hexData;

    }
}
