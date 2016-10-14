/*
 * Copyright 2014 Jacob Klinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.greenbok.androbok;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.os.Handler;

import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.widget.TextView;

import com.google.android.mms.pdu_alt.EncodedStringValue;
import com.klinker.android.logger.Log;
import com.klinker.android.logger.OnLogListener;
import com.klinker.android.send_message.ApnUtils;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;
import com.klinker.android.send_message.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class MainActivity extends Activity {

    private Settings settings;

    private Button setDefaultAppButton;
    private Button selectApns;
    private Button sendButton;
    private RecyclerView log;

    private Button CheckIp;
    private TextView ShowIp;
    private String IPaddress;
    private Boolean IPValue;

    private LogAdapter logAdapter;

    private AndroidWebServer androidWebServer;

    public String J_sendnum;
    public String J_sendmsg;
    public String J_bccnum;
    public String [] J_numarray;
    public String[] J_bccnumarray;
    public EncodedStringValue[] ESVbcc_num_array;
    //public String SEND_SMS;
    final public static int SEND_SMS = 101;
    MainActivity M = this;
    public Transaction Mtrans;
    Bitmap downloaded_image;
    private static final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "Oncreate started...");
        super.onCreate(savedInstanceState);


        //System.out.println(response);
        try {
            Log.v(TAG, "Launching Web server...");
            androidWebServer = new AndroidWebServer(8900, this);
            androidWebServer.start();
            Log.v(TAG, "ma105 - Web server started on localport 8900");

        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG, "Web server or LogWatcher error - ma109 " + e.getMessage());
        }


        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("request_permissions", true) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startActivity(new Intent(this, PermissionActivity.class));
            finish();
            return;
        }

        Log.v(TAG, "ma121 - Init app...");
        setContentView(R.layout.activity_main);
        initSettings();
        initViews();
        initActions();
        initLogging();
        Log.v(TAG, "Logging activated...");
        Log.v(TAG, "App initialised successfully...");
    }

    private void initSettings() {
        settings = Settings.get(this);

        if (TextUtils.isEmpty(settings.getMmsc())) {
            initApns();
        }
    }

    private void initApns() {
        ApnUtils.initDefaultApns(this, new ApnUtils.OnApnFinishedListener() {
            @Override
            public void onFinished() {
                settings = Settings.get(MainActivity.this, true);
            }
        });
    }

    private void initViews() {
        Log.v(TAG, "Init views...");
        setDefaultAppButton = (Button) findViewById(R.id.set_as_default);
        selectApns = (Button) findViewById(R.id.apns);
        sendButton = (Button) findViewById(R.id.send);
        log = (RecyclerView) findViewById(R.id.log);

        CheckIp = (Button) findViewById(R.id.checkip);
        ShowIp = (TextView) findViewById(R.id.showip);
    }

    private void initActions() {
        if (Utils.isDefaultSmsApp(this)) {
            setDefaultAppButton.setVisibility(View.GONE);
        } else {
            setDefaultAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDefaultSmsApp();
                }
            });
        }

        selectApns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initApns();
            }
        });

        CheckIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetwordDetect();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        log.setHasFixedSize(false);
        log.setLayoutManager(new LinearLayoutManager(this));
        logAdapter = new LogAdapter(new ArrayList<String>());
        log.setAdapter(logAdapter);
    }

    private void initLogging() {
        Log.setDebug(true);
        Log.setPath("androbok.log");
        Log.setLogListener(new OnLogListener() {
            @Override
            public void onLogged(String tag, String message) {
                logAdapter.addItem(tag + ": " + message);
            }
        });

        /**
         *
        Log.v(TAG, "ma106 - Launching InApp Logs...");
        // get scaledDensity
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        float scaledDensity = metrics.scaledDensity;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView text = new TextView(this);
        layout.addView(text);
        text.setText("AndroBok Log Viewer");
        // for metrics table
        ScrollView scroll = new ScrollView(this);
        TableLayout table = new TableLayout(this);
        scroll.addView(table);
        layout.addView(scroll);

        setContentView(layout);

        // log watcher
        logWatcher = new LogWatcher(handler, table);
        logWatcher.start();
        Thread th = new Thread(logWatcher);
        th.start();
        /**
         */

    }

    private void setDefaultSmsApp() {
        Log.e("thomas", "ma218 - Set default SMS app...");

        setDefaultAppButton.setVisibility(View.GONE);
        Intent intent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                getPackageName());
        startActivity(intent);
    }

    //Check the internet connection.
    private void NetwordDetect() {
        boolean WIFI = false;
        boolean MOBILE = false;
        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = CM.getAllNetworkInfo();

        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    WIFI = true;

            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected())
                    MOBILE = true;
        }

        if(WIFI == true){
            IPaddress = GetDeviceipWiFiData();
            ShowIp.setText(IPaddress);
        }

        if(MOBILE == true){
            IPaddress = GetDeviceipMobileData();
            ShowIp.setText(IPaddress);
        }
    }

    public String GetDeviceipMobileData(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface networkinterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkinterface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.v(TAG, "Current IP: "+ex.toString());
        }
        return null;
    }

    public String GetDeviceipWiFiData()
    {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        @SuppressWarnings("deprecation")
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public void sendMessage() {
        Log.v(TAG, "Start sending message...");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.System.canWrite(this)) {

            Log.v(TAG, "API > 23 detected...");
            new AlertDialog.Builder(this)
                    .setMessage(com.klinker.android.send_message.R.string.write_settings_permission)
                    .setPositiveButton(com.klinker.android.send_message.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            try {
                                Log.v(TAG, "Starting activity...");
                                startActivity(intent);
                                Log.v(TAG, "Activity started...");
                            } catch (Exception e) {
                                Log.v(TAG, "Activity error : "+ e.getMessage());
                            }
                        }
                    })
                    .show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.v(TAG, "Init Settings...");
                com.klinker.android.send_message.Settings sendSettings = new com.klinker.android.send_message.Settings();
                Log.v(TAG, "Get Mmsc / Proxy / Port");
                sendSettings.setMmsc(settings.getMmsc());
                Log.v(TAG, "MMSC : "+settings.getMmsc().toString());
                sendSettings.setProxy(settings.getMmsProxy());
                Log.v(TAG, "Proxy : "+settings.getMmsProxy().toString());
                sendSettings.setPort(settings.getMmsPort());
                Log.v(TAG, "Port : "+settings.getMmsPort().toString());
                sendSettings.setUseSystemSending(true);
                Log.v(TAG, "SetUseSystemSending = true");

                Transaction transaction = new Transaction(MainActivity.this, sendSettings);

                Log.v(TAG, "Crafting message...");
                Message message = new Message(J_sendmsg,J_numarray);
                String [] addr=message.getAddresses();
                message.setImage(downloaded_image);
                Log.v(TAG,"Trying to send message...");
                transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
                Log.v(TAG,"MainActivity finished...");
            }
        }).start();
    }

    public void sendmsg(String number,String msg){
        //petite fonction que j'avais ajoutE pour envoyer des sms d'un autre projet.il marche.

        String phoneNo = number;
        String sms = msg;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            //Toast.makeText(getApplicationContext(), "SMS Sent!",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(),"SMS faild, please try again later!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public EncodedStringValue[] convert_to_encodedarray(String [] iarray){
        //converts ordinary string array into encoded array
        EncodedStringValue[] ESVbccpn =new EncodedStringValue[iarray.length];

        for (int i = 0; i <iarray.length ; i++) {
            ESVbccpn[i]=new EncodedStringValue(iarray[i]);
        }
        return ESVbccpn;

    }


}

