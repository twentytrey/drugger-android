package xyz.drugger.app.network;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetAuthJsonParser {
    OkHttpClient client=new OkHttpClient();
    public GetAuthJsonParser(){}

    public String getJsonFromUrl(String url,String ac_token) throws IOException {
        Request request=new Request.Builder().url(url).
                addHeader("Authorization","Bearer "+ac_token).get().build();
        try(Response response=client.newCall(request).execute()){
            return response.body().string();
        }
    }
}
