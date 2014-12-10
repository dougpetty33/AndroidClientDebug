package com.ZeroLimits.SmartSprinkler;

import com.ZeroLimits.SmartSprinkler.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.RadioButton;
import android.widget.Button;
import android.widget.EditText;

import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;



 class ssdp{

     /* New line definition */
     public static final String NEWLINE = "\r\n";

     public static final String ADDRESS = "239.255.255.250";
     public static final int PORT = 1900;

     public static final String ST = "ST";
     public static final String LOCATION = "LOCATION";
     public static final String NT = "NT";
     public static final String NTS = "NTS";

     /* Definitions of start line */
     public static final String SL_NOTIFY = "NOTIFY * HTTP/1.1";
     public static final String SL_MSEARCH = "M-SEARCH * HTTP/1.1";
     public static final String SL_OK = "HTTP/1.1 200 OK";

     public static final String ST_ContentDirectory = ST;
     public static final String NTS_ALIVE = "ssdp:alive";
     public static final String NTS_BYEBYE = "ssdp:byebye";
     public static final String NTS_UPDATE = "ssdp:update";


 }

public class Debug extends Activity {

    private DatagramSocket msock;
    private PrintWriter pw;
    private EditText rfield;
    private EditText sfield;
    private Button sbutton;
    private RadioButton uni, multi;
    private String cmd;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_debug);

        sbutton = (Button) findViewById(R.id.s_button_widg);
        rfield = (EditText) findViewById(R.id.r_field_widg);
        sfield = (EditText) findViewById(R.id.s_field_widg);
        uni = (RadioButton) findViewById(R.id.ucast);
        multi = (RadioButton) findViewById(R.id.mcast);

        sbutton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    cmd = sfield.getText().toString();
                    sfield.setText("");
                    //TODO: Add logic to decide whether it is multicast or unicast.
                    if(uni.isChecked())
                    {

                    }
                    else
                    {
                        //multi must be checked
                    }
                }
        });


    }

}
