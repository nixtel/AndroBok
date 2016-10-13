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

       //strings for 3 input variables from url: to -> html file of numbers,image-->url of image,msg->the message
        String to_afterDecode="";//url of recipients list
        String image_afterDecode="";//url of image
        String msg_afterDecode="";//message as URLEncoded string

        //Sample CSV : www.mytrade.mu%2Fcampaign.html
        //Sample msg : Bonjour%2C+j%27ai+une+r%C3%A9v%C3%A9lation+%C3%A0+vous+faire+au+sujet+de+votre+avenir+professionnel+%21+Appelez-moi+vite+au+0372600108
        //Sample image : www.mytrade.mu%2FMMS.png OR www.journaldelavoyance.com%2Fwp-content%2Fuploads%2F2013%2F12%2Flogo_scan_5.png
        try {
            to_afterDecode = URLDecoder.decode("http://"+parms.get("to"), "UTF-8");
            msg_afterDecode = URLDecoder.decode(parms.get("msg"), "UTF-8");
            image_afterDecode=URLDecoder.decode("http://"+parms.get("image"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        //M.J_sendnum=parms.get("to");
        M.J_sendmsg=msg_afterDecode;
        android.util.Log.e("thomas", "line 90 android webserver TO:"+to_afterDecode+" Message:"+msg_afterDecode+" Image:"+image_afterDecode);

        String output= "<html><body><h1>Results</h1><hr/>Time: "+new Date(System.currentTimeMillis())+"<br/>"+"To: "+to_afterDecode+"<br/>MSG: "+msg_afterDecode+"<br/>Image: "+image_afterDecode+"<hr/></body></html> \n";

        //**********************************************************
        android.util.Log.e("thomas", "Asynctask request bcc Androidwebserver.response() line115");
        //start asynctask to get bcc/image
       // String url1 = "http://www.mytrade.mu/campaign.html";
        Asynctask_httprequest AS= new Asynctask_httprequest();
        AS.M=this.M;
        AS.url_of_image=image_afterDecode;//pass the url of the image as it will then be passed down to asynctask image downloader later directly from asynctaskhttprequest;
        AS.execute(to_afterDecode);//from here asynctask_httprequest will query server for numbers and pictures and start sending
        //************************************************************

        return newFixedLengthResponse(output);

    }



}