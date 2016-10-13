package com.greenbok.androbok;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
download image from url
 */

public class Asynctask_imagedownloader extends AsyncTask<String, Integer, Bitmap> {
    MainActivity M;
    protected Bitmap doInBackground(String... urls) {




        try {
            android.util.Log.e("thomas","starting image download");
            URL url = new URL("http://mytrade.mu/MMS.png");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            android.util.Log.e("thomas","image download completed");

           return myBitmap;
        } catch (IOException e) {
            // Log exception

            android.util.Log.e("thomas","image download failed",e);


            return null;
        }


    }

    protected void onProgressUpdate(Integer... progress) {
    android.util.Log.e("thomas","progress at at"+progress.toString());
    }

    protected void onPostExecute(Bitmap result) {
        android.util.Log.e("thomas","post execute ok");
        M.downloaded_image=result;
        M.sendMessage();

    }


}
