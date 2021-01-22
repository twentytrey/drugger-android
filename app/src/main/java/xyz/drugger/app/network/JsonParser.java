package xyz.drugger.app.network;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class JsonParser {
    OkHttpClient client=new OkHttpClient();
    public JsonParser(){}

    public String getJSONFromUrl(String url) throws IOException {
        Request request=new Request.Builder().url(url).build();
        try(Response response=client.newCall(request).execute()){
            return response.body().string();
        }
    }
}
