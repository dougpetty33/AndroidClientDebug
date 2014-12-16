package com.ZeroLimits.SmartSprinkler;


import android.app.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;


public class Debug extends Activity {

    public static final String NEWLINE = "\r\n";
    public static final int PORT = 1900;
    public static final String HOST = "Host: 239.225.255.250:1900";
    public static final String ADDRESS = "239.225.255.250";
    public static final String ST = "ST";
    public static final String LOCATION = "LOCATION";
    public static final String NT = "NT";
    public static final String NTS = "NTS";
    public static final String SL_NOTIFY = "NOTIFY * HTTP/1.1";
    public static final String SL_MSEARCH = "M-SEARCH * HTTP/1.1";
    public static final String SL_OK = "HTTP/1.1 200 OK";
    public static final String ST_ContentDirectory = " upnp:rootdevice";
    public static final String USER_AGENT = "User-Agent: UPnP/1.0 DLNADOC/1.50 Platinum/1.0.4.11";
    public static final String CONNECTION = "Connection: close";

   // public static final String NTS_ALIVE = "ssdp:alive";
  //  public static final String NTS_BYEBYE = "ssdp:byebye";
  //  public static final String NTS_UPDATE = "ssdp:update";
    public static final String NTS_DISCOVER = "MAN: \"ssdp:discover\"";
    public static final String MX = "MX: 5";

    private PrintWriter pw;
    private EditText rfield;
    private EditText sfield, sIP;
    private Button sbutton;
    private Button id_button;
    private RadioButton uni;
    private RadioButton multi;
    private String cmd;
    private Socket sock;

    private class SearchDevice implements Runnable {

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






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_debug);
        Intent mainAct = getIntent();
        sbutton = (Button) findViewById(R.id.s_button_widg);
        id_button = (Button) findViewById(R.id.ID_button);
        rfield = (EditText) findViewById(R.id.r_field_widg);
        sfield = (EditText) findViewById(R.id.s_field_widg);
        uni = (RadioButton) findViewById(R.id.ucast);
        multi = (RadioButton) findViewById(R.id.mcast);
        sIP = (EditText) findViewById(R.id.send_ip);
        final String serverIP = null;


         class ListenDevice implements Runnable{

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
                        if( msg != null){
                            final String clientAddress = recv.getAddress().toString();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    rfield.setText(msg);
                                    sIP.setText(clientAddress);
                                }
                            });

                            clientMultiSock.disconnect();
                            break;
                        }

                    }

                } catch (IOException e) {
                    Log.e("Sprinkler", "Could not send the packet");
                }

            }
        }

        class sendMessage implements Runnable {

            @Override
            public void run() {
                Socket sock;
                try {
                    sock = new Socket(InetAddress.getByName(serverIP), 80);
                    DataOutputStream clientStream = new DataOutputStream(sock.getOutputStream());
                    clientStream.flush();
                    clientStream.close();
                } catch (IOException e) {

                }

            }
        }

        id_button.setOnClickListener(new View.OnClickListener() {
                 public void onClick(View v) {
                   Thread s = new Thread(new SearchDevice());
                   Thread r = new Thread(new ListenDevice());

                   try {
                       s.start();
                       while (s.isAlive()) {
                           s.join();
                       }
                       r.start();
                       while (r.isAlive()){
                        r.join();
                       }


                   } catch (InterruptedException e){
                        Log.e("Sprinkler", "The Thread was interrupted");
                   }
                     Log.d("Sprinkler", "Finished Id button");
                 }
        });


        //todo need to setup to catch intent so we have a reference to the main activity.

        sbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    cmd = sfield.getText().toString();
                   final String serverIP = sIP.getText().toString();
                    if (!uni.isChecked()) {

                    } else {
                        Log.d("Sprinkler", "Trying to send command");
                        Log.d("Sprinkler", sIP.getText().toString());
                        if(serverIP == "") {
                            Context context = getApplicationContext();
                            CharSequence text = "Need to Enter a valid IP address to continue";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }

                       else {
                            try {
                                Thread s = new Thread(new sendMessage());
                                s.start();
                                while (s.isAlive()) {
                                    s.join();
                                }
                            } catch (InterruptedException e) {

                            }
                        }

                    }
                    sfield.setText("");
                }
        });




    }

}
