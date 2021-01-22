package xyz.drugger.app.network;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostAuthJsonParser {
    OkHttpClient client=new OkHttpClient();
    public static final MediaType JSON=MediaType.get("application/json; charset=utf-8");
    public PostAuthJsonParser(){}

    public String getJSONFromUrl(String url,String json,String ac_token) throws IOException {
        RequestBody body=RequestBody.create(json,JSON);
        Request request=new Request.Builder().url(url).
                addHeader("Authorization","Bearer "+ac_token).
                post(body).build();
        try(Response response=client.newCall(request).execute()){
            return response.body().string();
        }
    }
}
