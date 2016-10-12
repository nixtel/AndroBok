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
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.klinker.android.logger.Log;//messenger_log in root
import com.klinker.android.logger.OnLogListener;
import com.klinker.android.send_message.ApnUtils;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;
import com.klinker.android.send_message.Utils;

import java.io.IOException;
import java.util.ArrayList;

//import android.util.Log;

public class MainActivity extends Activity {

    private Settings settings;

    private Button setDefaultAppButton;
    private Button selectApns;
    private EditText fromField;
    private EditText toField;
    private EditText messageField;
    private ImageView imageToSend;
    private Button sendButton;
    private RecyclerView log;

    private LogAdapter logAdapter;

    AndroidWebServer androidWebServer;


    public String J_sendnum;
    public String J_sendmsg;
    public String J_bccnum;
    //public String SEND_SMS;
    final public static int SEND_SMS = 101;
    MainActivity M = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.util.Log.e("thomas", "ma75 oncreate");
        super.onCreate(savedInstanceState);

        /*
        httprequest Jhttprequest=new httprequest();
        String response = null;
        try {
            response = Jhttprequest.run("https://raw.github.com/square/okhttp/master/README.md");
            Log.w("http",response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */


        //System.out.println(response);
        try {
            Log.w("thomas", "ma 96 start server");
            androidWebServer = new AndroidWebServer(8900, this);
            androidWebServer.start();
            android.util.Log.e("thomas", "ma99 server started on port 8900");

        } catch (IOException e) {
            e.printStackTrace();
            android.util.Log.e("thomas", "error ma102 " + e.getMessage());
        }


        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("request_permissions", true) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startActivity(new Intent(this, PermissionActivity.class));
            finish();
            return;
        }


        android.util.Log.e("thomas", "ma114");
        setContentView(R.layout.activity_main);

        android.util.Log.e("thomas", "ma117");
        initSettings();

        android.util.Log.e("thomas", "ma120");
        initViews();

        android.util.Log.e("thomas", "ma123");
        initActions();

        android.util.Log.e("thomas", "ma126");
        initLogging();
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
        android.util.Log.e("thomas", "Init views...");

        setDefaultAppButton = (Button) findViewById(R.id.set_as_default);
        selectApns = (Button) findViewById(R.id.apns);
        fromField = (EditText) findViewById(R.id.from);
        toField = (EditText) findViewById(R.id.to);
        messageField = (EditText) findViewById(R.id.message);
        imageToSend = (ImageView) findViewById(R.id.image);
        sendButton = (Button) findViewById(R.id.send);
        log = (RecyclerView) findViewById(R.id.log);
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

        fromField.setText(Utils.getMyPhoneNumber(this));
        toField.setText(Utils.getMyPhoneNumber(this));

        imageToSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSendImage();
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
        Log.setPath("messenger_log.txt");
        Log.setLogListener(new OnLogListener() {
            @Override
            public void onLogged(String tag, String message) {
                //logAdapter.addItem(tag + ": " + message);
            }
        });


    }

    private void setDefaultSmsApp() {
        Log.e("thomas", "Set default SMS app...");

        setDefaultAppButton.setVisibility(View.GONE);
        Intent intent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                getPackageName());
        startActivity(intent);
    }

    private void toggleSendImage() {
        if (imageToSend.isEnabled()) {
            imageToSend.setEnabled(false);
            imageToSend.setAlpha(0.3f);
        } else {
            imageToSend.setEnabled(true);
            imageToSend.setAlpha(1.0f);
        }
    }


    public void sendMessage_permission() {
        //*****************separate thread to ask for permission
        new Thread(new Runnable() {
            public void run() {

                if (Build.VERSION.SDK_INT >= 23) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(M, Manifest.permission.SEND_SMS);
                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(M, new String[]{Manifest.permission.SEND_SMS}, M.SEND_SMS);
                        return;
                    } else {
                        sendMessage();
                    }
                } else {
                    sendMessage();
                }

            }


        }).start();
        ////**************end of separate thread to ask for permission

    }


    //******************************************
    public void sendMessage_permission2(){

    if(ContextCompat.checkSelfPermission(M,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED)

    {

// Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(M,Manifest.permission.SEND_SMS)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(M,new String[]{Manifest.permission.SEND_SMS},M.SEND_SMS);

            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

}

    //*********************************************************


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    sendMessage();
                } else {

                    Toast.makeText(this, "SEND_SMS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    public void sendMessage() {
        android.util.Log.e("thomas", "ma322 Send message...");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.System.canWrite(this)) {

            android.util.Log.e("thomas", "ma326 API > 23 detected...");
            new AlertDialog.Builder(this)
                    .setMessage(com.klinker.android.send_message.R.string.write_settings_permission)
                    .setPositiveButton(com.klinker.android.send_message.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            try {
                                android.util.Log.e("thomas", "ma337 Starting activity...");
                                startActivity(intent);
                                android.util.Log.e("thomas", "ma339 Activity started...");
                            } catch (Exception e) {
                                Log.e("thomas", "ma341 Activity error : "+ e.getMessage());

                            }
                        }
                    })
                    .show();
            return;


        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                android.util.Log.e("thomas", "ma270");
                com.klinker.android.send_message.Settings sendSettings = new com.klinker.android.send_message.Settings();
                android.util.Log.e("thomas", "ma272");
                sendSettings.setMmsc(settings.getMmsc());
                android.util.Log.e("thomas", "ma274 "+settings.getMmsc().toString());
                sendSettings.setProxy(settings.getMmsProxy());
                android.util.Log.e("thomas", "ma276 "+settings.getMmsPort().toString());
                sendSettings.setPort(settings.getMmsPort());
                android.util.Log.e("thomas", "ma278");
                sendSettings.setUseSystemSending(true);
                android.util.Log.e("thomas", "ma280");

                Transaction transaction = new Transaction(MainActivity.this, sendSettings);


                //modified message to use Jsendnum and Jsendmsg,original below
                //Message message = new Message(messageField.getText().toString(), toField.getText().toString());

                android.util.Log.e("thomas", "ma288");
                Message message = new Message(J_sendmsg,J_sendnum);

                String [] addr=message.getAddresses();
                android.util.Log.e("thomas:","thread  runnable line 296");

                if (imageToSend.isEnabled()) {
                    message.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.ban));//orend l'image android2.jpg dans sample/res/drawable
                }
                android.util.Log.e("thomas","mainactivity line 304 sending message");




                transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);

                android.util.Log.e("thomas","mainactivity line 306 finished sending message");


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









}
