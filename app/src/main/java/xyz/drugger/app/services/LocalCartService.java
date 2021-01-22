package xyz.drugger.app.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import xyz.drugger.app.database.models.Product;

public class LocalCartService extends Service {
    private final IBinder mBinder = new MyBinder();
    private final List<Product> resultList=new ArrayList<Product>();
    private int counter=1;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    public static final String CART="cart";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        addResultValues();
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        addResultValues();
        return mBinder;
    }

    public class MyBinder extends Binder {
        public LocalCartService getService(){
            return LocalCartService.this;
        }
    }

    public List<Product>getResultList(){
        return resultList;
    }

    private void addResultValues(){
        sharedPreferences=LocalCartService.this.getSharedPreferences("xyz.drugger.app.SHARED_PREFERENCES",MODE_PRIVATE);
        gson=new Gson();
        String cartString=sharedPreferences.getString(CART,null);
        ArrayList<Product>items=gson.fromJson(cartString,new TypeToken<List<Product>>(){}.getType());
        if (items!=null)resultList.addAll(items);

    }

}