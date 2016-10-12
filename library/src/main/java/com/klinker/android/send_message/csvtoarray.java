package com.klinker.android.send_message;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by DELL PC on 04/10/2016.
 */
public class csvtoarray {

   public String [] read() {


       File sdcard = Environment.getExternalStorageDirectory();

//Get the text file
       File file = new File(sdcard, "campaign.csv");
       LinkedList<String> numlist = new LinkedList<String>();
       StringBuilder text = new StringBuilder();
       String[] outputarray = new String[0];

       try {
           BufferedReader br = new BufferedReader(new FileReader(file));
           String line;

           while ((line = br.readLine()) != null) {
               text.append(line);
               numlist.add(line);
               text.append('\n');
               //

               //
           }
           android.util.Log.e("thomas", text.toString());
           br.close();

           outputarray = new String[numlist.size()];
           ;
           outputarray = numlist.toArray(outputarray);


           for (String s : outputarray)
               System.out.println(s);




       } catch (IOException e) {
           //You'll need to add proper error handling here
           android.util.Log.e("thomas", e.getMessage());
       }


       return outputarray;
   }
}
