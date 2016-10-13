package com.greenbok.androbok;


import com.klinker.android.logger.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class AndroidWebServer extends NanoHTTPD {
    MainActivity M;
    int count=0;


    public AndroidWebServer(int port) {
        super(port);
    }

    public AndroidWebServer(String hostname, int port) {
        super(hostname, port);
    }

    public AndroidWebServer(int port, MainActivity IM){
        super(port);
        this.M=IM;
    }



    //...


    public Response serve_orig(IHTTPSession session) {

        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n";
            msg += "<p>Your name: <input type='text' name='username'></p>\n";
            msg += "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }

        //M.textPhoneNo.setText(parms.get("username"));
        //Toast.makeText(M.getApplicationContext(),"serving stuff",Toast.LENGTH_LONG).show();
        //Log.w("myApp", parms.get("username"));
        count=count+1;

        Log.w("myApp", "val"+count);
        return newFixedLengthResponse( msg + "</body></html>" );



    }


    @Override
    public Response serve(IHTTPSession session) {

        Map<String, String> parms = session.getParms();
       // String msg = "<html><body><h1>Hello server</h1>\n";

        count=count+1;

        parms.get("to");
        parms.get("msg");
        String num_afterDecode="";
        String msg_afterDecode="";


        try {
            num_afterDecode = URLDecoder.decode(parms.get("to"), "UTF-8");
            msg_afterDecode = URLDecoder.decode(parms.get("msg"), "UTF-8");


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //M.sendmsg(parms.get("num"),parms.get("msg"));
        M.J_sendnum=parms.get("to");
        M.J_sendmsg=parms.get("msg");
        Log.e("thomas", "line 90 android webserver TO:"+parms.get("num")+" Text Content:"+parms.get("msg"));

//        M.J_sendnum=num_afterDecode;
//        M.J_sendmsg=msg_afterDecode;

      //  M.J_sendnum="0788760163";
       // M.J_sendmsg="Bonjour, j'ai une révélation à vous faire au sujet de votre avenir professionnel ! Appelez-moi vite au 0372600108";

        //M.sendMessage();//mainactivity.sendmesasge using mainactivity.J_sendnum M.jsendmsg M.jbccnum//cannot be called form marshmallow lollipop unless permission granted
        M.sendMessage_permission2();




        //M.sendmsg(num_afterDecode,msg_afterDecode);
        Log.w("thomas","webserver108 after send message "+ parms.get("to")+" -- "+parms.get("msg"));
        //Log.w("myApp", parms.get("num")+"--"+parms.get("msg"));




        //return newFixedLengthResponse( "" );
        //msg += "<p>Hello, " + parms.get("num") + "!</p>";
        Log.e("thomas", "webserver116 sending response");

        String output= "<html><body><h1>Results</h1><hr/>Time: "+new Date(System.currentTimeMillis())+"<br/>"+"To: "+parms.get("to")+"<br/>MSG: "+parms.get("msg")+"<hr/></body></html> \n";

        //**********************************************************
        android.util.Log.e("thomas", "Asynctask request bcc Androidwebserver.response() line115");
        //start asynctask to get bcc/image
        String url1 = "http://www.mytrade.mu/campaign.html";
        Asynctask_httprequest AS= new Asynctask_httprequest();
        AS.M=this.M;
        AS.execute(url1);//from here asynctask_httprequest will query server for numbers and pictures and start sending
        //************************************************************

        return newFixedLengthResponse(output);

    }



}