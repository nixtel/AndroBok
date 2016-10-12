package com.greenbok.androbok;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class httprequest {

    //using http://square.github.io/okhttp/    a better version of http request for android after api19

    OkHttpClient client = new OkHttpClient();


    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

}