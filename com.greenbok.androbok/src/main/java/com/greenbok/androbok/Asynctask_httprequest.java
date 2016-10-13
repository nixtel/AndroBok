package com.greenbok.androbok;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by DELL PC on 08/10/2016.
 */

public class Asynctask_httprequest extends AsyncTask<String, Integer, String> {
    MainActivity M;
    protected String doInBackground(String... urls) {
        httprequest Jhttprequest=new httprequest();
        String response = null;
        try {
            response = Jhttprequest.run(urls[0]);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(String result) {

        android.util.Log.e("thomas", "Asynctask_httprequest.onpostexecute.line32");
        android.util.Log.e("thomas", result);
        String resultarray[]=result.split("\n");
        android.util.Log.e("thomas", "length of array"+String.valueOf(resultarray.length));
        M.J_bccnumarray=resultarray;
        M.J_numarray=resultarray;
        M.ESVbcc_num_array=M.convert_to_encodedarray(resultarray);
        //M.sendMessage();
        for (int i = 0; i<M.J_bccnumarray.length ; i++) {
            android.util.Log.e("thomas", "J_bccnumarray"+M.J_bccnumarray[i]);
        }


         /*image download test*/
        //daisy chained http reuqest followed by image request

        Asynctask_imagedownloader AIM=new Asynctask_imagedownloader();
        AIM.M=this.M;
        AIM.execute("test");
        //image download test end

    }


}
