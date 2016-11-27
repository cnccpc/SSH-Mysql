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
     * ����ssh����
     * @param sshId��sshPasswd��sshHost��localHostIp
     */
    public String LocalHostIP=getLocalHostIP();

    public void getConfig(){
    //��ȡSSHMysql.properties������
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("SSHMysql.properties");
    Properties p = new Properties();
    try {
        p.load(inputStream);
    } catch (IOException e1) {
        e1.printStackTrace();
    }
    System.out.println("��ȡ��SSHMysql.properties������ϢΪ:"
            +p.getProperty("sshId")
            +"/"+p.getProperty("sshPasswd")
            +"/"+p.getProperty("sshHost")
            +"/"+p.getProperty("localHostIp"));

    }

    //public void go(String sshId,String sshPasswd,String sshHost,String localHostIp){
    public void go(){

        try{

            //��ȡSSHMysql.properties������
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
            Session session= jSch.getSession(sshId,sshHost,22);//SSH�û���������IP�����Ӷ˿�
            session.setPassword(sshPasswd);//����
            session.setConfig("StrictHostKeyChecking","no");

            System.out.println("����ip��ַ��"+LocalHostIP);
            if (LocalHostIP.equals(sshHost)){
                System.out.println("����ⷢ�֣�������л���Ϊssh���ڷ����������迪��ssh����");
            }else {
                //���5555�˿��Ƿ��������������ظ���������
                if (isLoclePortUsing(5555)) {
                    System.out.println("ssh�����Ѿ�����,��������������");
                } else {

                    session.connect();//����SSH������
                    System.out.println("����SSH������");
                    System.out.println(session.getServerVersion());//�����ӡSSH�������汾��Ϣ


                    //  �������
                    int assinged_port = session.setPortForwardingL(localHostIp, 5555, sshHost, 3306);//�˿�ӳ�� ת��

                    System.out.println("����������ϣ������ַΪ��" +localHostIp+":"+ assinged_port);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ȡ������IP
     * @return Ip��ַ
     */
    public static String getLocalHostIP() {
        String ip;
        try {
            /**���ر���������*/
            InetAddress addr = InetAddress.getLocalHost();
            /**���� IP ��ַ�ַ��������ı�������ʽ��*/
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
