package xyz.drugger.app.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import xyz.drugger.app.database.models.Product;

public class AddToCustomerCart extends IntentService {
    private int result=Activity.RESULT_CANCELED;
    public static final String ITEM="item";
    public static final String OUTPUT="output";
    public static final String RESULT="result";
    public static final String NOTIFICATION="xyz.drugger.app.services.receiver";
    private static final String CART="cart";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private ArrayList<Product> cartItems;

    public AddToCustomerCart(){
        super("AddToCustomerCart");
    }

    protected void dissolveArray(ArrayList<Product>arrayitems){
        String string=gson.toJson(arrayitems);
        editor=sharedPreferences.edit();
        editor.remove(CART).apply();
        editor.putString(CART,string);
        boolean applied = editor.commit();
//        Log.e("cartitem strings > ",string);
//        Log.e("commit boolean > ",String.valueOf(applied));
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        sharedPreferences=AddToCustomerCart.this.getSharedPreferences("xyz.drugger.app.SHARED_PREFERENCES",MODE_PRIVATE);
        gson=new Gson();cartItems=new ArrayList<Product>();

        String productJson=intent.getStringExtra(ITEM);
        if(productJson != null){
            /**
             * get Array from sharedprefs
             * append productjson to array
             * save array back to sharedprefs
             * return count as output
             */
            String cartString=sharedPreferences.getString(CART,null);
            if(cartString != null){
//                Log.e("current cartstring > ",cartString);
                cartItems=gson.fromJson(cartString,new TypeToken<List<Product>>(){}.getType());
                Product newAddition=gson.fromJson(productJson,Product.class);
                cartItems.add(newAddition);
                dissolveArray(cartItems);
            }else if(cartString==null){
                Product product=gson.fromJson(productJson,Product.class);
                cartItems.add(product);
                dissolveArray(cartItems);
            }
            result=Activity.RESULT_OK;
            publishResults(String.valueOf(cartItems.size()),result);
        }
    }

    private void publishResults(String output, int result){
        Intent intent=new Intent(NOTIFICATION);
        intent.putExtra(OUTPUT,output);
        intent.putExtra(RESULT,result);
        sendBroadcast(intent);
    }
}
