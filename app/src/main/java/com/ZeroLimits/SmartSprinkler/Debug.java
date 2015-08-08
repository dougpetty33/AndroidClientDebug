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
import java.net.UnknownHostException;

import com.ZeroLimits.SmartSprinkler.Communicator;


public class Debug extends Activity {

    //region widgets
    private PrintWriter pw;
    private EditText rfield;
    private EditText sfield, sIP;
    private Button sbutton;
    private Button id_button;
    private RadioButton uni;
    private RadioButton multi;
    //endregion

    //region variables
    private String cmd;
    private Socket sock;
    //endregion

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

        sbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String msg = sfield.getText().toString();
                    if(sIP.getText().toString().matches("([0-9]{1,3}:)[0-9]{1,3}")) {
                        Communicator.send_unicast(InetAddress.getByName(sIP.getText().toString()), msg);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });

        id_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    try {

                        sIP.setText(Communicator.find_device());

                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        });
    }//end on create
}//end debug class
