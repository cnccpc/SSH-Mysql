package com.cnccpc.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;


/**
 * Created by cnccpc on 2016/11/15.
 */
public class SSHMysql2 {


    /**
     * 创建ssh代理
     * @param sshId，sshPasswd，sshHost，localHostIp
     */
    public String LocalHostIP=getLocalHostIP();

    public void getConfig(){
    //获取SSHMysql.properties的设置
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("SSHMysql.properties");
    Properties p = new Properties();
    try {
        p.load(inputStream);
    } catch (IOException e1) {
        e1.printStackTrace();
    }
    System.out.println("获取到SSHMysql.properties配置信息为:"
            +p.getProperty("sshId")
            +"/"+p.getProperty("sshPasswd")
            +"/"+p.getProperty("sshHost")
            +"/"+p.getProperty("localHostIp"));

    }

    //public void go(String sshId,String sshPasswd,String sshHost,String localHostIp){
    public void go(){

        try{

            //获取SSHMysql.properties的设置
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("SSHMysql.properties");
            Properties p = new Properties();
            try {
                p.load(inputStream);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            String sshId=p.getProperty("sshId");
            String sshHost=p.getProperty("sshHost");
            String sshPasswd=p.getProperty("sshPasswd");
            String localHostIp=p.getProperty("localHostIp");

            JSch jSch=new JSch();
            Session session= jSch.getSession(sshId,sshHost,22);//SSH用户名，连接IP，连接端口
            session.setPassword(sshPasswd);//密码
            session.setConfig("StrictHostKeyChecking","no");

            System.out.println("本地ip地址："+LocalHostIP);
            if (LocalHostIP.equals(sshHost)){
                System.out.println("经检测发现，你的运行环境为ssh所在服务器，无需开启ssh代理！");
            }else {
                //检测5555端口是否开启，开启则不再重复建立连接
                if (isLoclePortUsing(5555)) {
                    System.out.println("ssh代理已经开启,无需重新启动！");
                } else {

                    session.connect();//连接SSH服务器
                    System.out.println("连接SSH服务器");
                    System.out.println(session.getServerVersion());//这里打印SSH服务器版本信息


                    //  正向代理
                    int assinged_port = session.setPortForwardingL(localHostIp, 5555, sshHost, 3306);//端口映射 转发

                    System.out.println("创建代理完毕，代理地址为：" +localHostIp+":"+ assinged_port);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取本机的IP
     * @return Ip地址
     */
    public static String getLocalHostIP() {
        String ip;
        try {
            /**返回本地主机。*/
            InetAddress addr = InetAddress.getLocalHost();
            /**返回 IP 地址字符串（以文本表现形式）*/
            ip = addr.getHostAddress();
        } catch(Exception ex) {
            ip = "";
        }

        return ip;
    }

    /***
     *  true:already in using  false:not using
     * @param port
     */
    public static boolean isLoclePortUsing(int port){
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception e) {
        }
        return flag;
    }

    /***
     *  true:already in using  false:not using
     * @param host
     * @param port
     * @throws UnknownHostException
     */
    public static boolean isPortUsing(String host,int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(theAddress,port);
            flag = true;
        } catch (IOException e) {

        }
        return flag;
    }


}
