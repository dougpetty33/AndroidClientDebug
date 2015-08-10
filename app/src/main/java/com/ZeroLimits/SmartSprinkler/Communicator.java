package com.ZeroLimits.SmartSprinkler;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.HttpURLConnection;
/**
 * Created by dpetty on 8/4/2015.
 */
public class Communicator {

    //region Hearder_Strings
    public static final String NEWLINE = "\r\n";
    public static final int PORT = 1900;
    public static final String HOST = "Host: 239.225.255.250:1900";
    public static final String ST = "ST: ";
    public static final String NTS_DISCOVER = "MAN: \"ssdp:discover\"";
    public static final String MX = "MX: 5";
    public static final String SL_MSEARCH = "M-SEARCH * HTTP/1.1";
    public static final String ST_ContentDirectory = "zerolimits:sprinkler";
    public static final String USER_AGENT = "User-Agent: stuff";
    public static final String CONNECTION = "Connection: close";

    // public static final String SL_OK = "HTTP/1.1 200 OK";
    // public static final String LOCATION = "LOCATION";
    // public static final String NT = "NT";
    // public static final String NTS = "NTS";
    // public static final String ADDRESS = "239.225.255.250";
    // public static final String SL_NOTIFY = "NOTIFY * HTTP/1.1";
    // public static final String NTS_ALIVE = "ssdp:alive";
    // public static final String NTS_BYEBYE = "ssdp:byebye";
    // public static final String NTS_UPDATE = "ssdp:update";
    //endregion

    private static class SearchDevice implements Runnable {
        @Override
        public void run() {
            MulticastSocket msock;
            String query = SL_MSEARCH + NEWLINE + MX + NEWLINE + ST + ST_ContentDirectory + NEWLINE + NTS_DISCOVER + NEWLINE + USER_AGENT + NEWLINE + CONNECTION + NEWLINE + HOST + NEWLINE + NEWLINE;
            try {
                msock = new MulticastSocket(PORT);
                msock.setTimeToLive(5);
                msock.joinGroup(InetAddress.getByName("239.255.255.250"));
                msock.send(new DatagramPacket(query.getBytes(), query.getBytes().length, InetAddress.getByName("239.255.255.250"), PORT));
                msock.setTimeToLive(5);
                Log.v("Sprinkler", "Sent the Search");
                msock.leaveGroup(InetAddress.getByName("239.255.255.250"));
                //todo search the buffer for the appropriate response from the arduino
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    private static class ListenDevice implements Runnable{
        public String dev_ip;
        @Override
        public void run(){
            byte[] buf = new byte[1024];
            try {
                MulticastSocket clientMultiSock = new MulticastSocket(PORT);
                clientMultiSock.joinGroup(InetAddress.getByName("239.255.255.250"));
                while (true){
                    DatagramPacket recv = new DatagramPacket(buf, 0, buf.length);
                    clientMultiSock.receive(recv);
                    final String msg = new String(recv.getData());
                    if( msg.contains("HTTP/1.1 200 OK") && msg.contains("ST: zerolimits:sprinkler")) {
                        final String clientAddress = recv.getAddress().getHostAddress();
                        dev_ip = clientAddress;
                        clientMultiSock.disconnect();
                        break;
                    }
                }
            } catch (IOException e) {
                Log.e("Sprinkler", "Could not send the packet");
            }
        }

        public String getaddy() throws UnknownHostException {
            return this.dev_ip;
        }
    }
   //todo Fix unicast messaging
    private static class sendMessage implements Runnable {

        @Override
        public void run() {
            Socket sock;
            try {
                final String serverIP = "";
                sock = new Socket(InetAddress.getByName(serverIP), 80);
                DataOutputStream clientStream = new DataOutputStream(sock.getOutputStream());
                clientStream.flush();
                clientStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void send_unicast(InetAddress addy, String message)
    {

    }
    public static String find_device() throws UnknownHostException, InterruptedException {

        /*SearchDevice srch = new SearchDevice();
        ListenDevice lst = new ListenDevice();

        srch.run();
        lst.run();
        return lst.getaddy();*/
        ListenDevice lst = new ListenDevice();
        Thread s = new Thread(new SearchDevice());
        Thread l = new Thread(lst);
        s.start();
        while(s.isAlive())
        {
            s.join();
        }

        l.start();

        while(l.isAlive())
        {
            l.join();
        }

        return lst.getaddy();
    }


}
