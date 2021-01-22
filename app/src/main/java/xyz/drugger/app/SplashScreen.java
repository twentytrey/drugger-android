package xyz.drugger.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.utils.AnimationUtil;

import org.json.JSONObject;

import xyz.drugger.app.helper.PrefManager;
import xyz.drugger.app.network.GetAuthJsonParser;

public class SplashScreen extends AppCompatActivity {
    private static final int PICK_CSV_REQUEST = 123;
    private SplashScreen context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String ac_token;
    private static int SPLASH_TIME_OUT=3000;
    private ImageView splashlogo;
    private Animation bottomAnim,sideAnim;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=SplashScreen.this;

        splashlogo=findViewById(R.id.logo);
        sideAnim=AnimationUtils.loadAnimation(context,R.anim.side_anim);
        bottomAnim=AnimationUtils.loadAnimation(context,R.anim.bottom_anim);
        splashlogo.setAnimation(sideAnim);

        sharedPreferences=context.getSharedPreferences(context.getString(R.string.preference_name),MODE_PRIVATE);
        editor=sharedPreferences.edit();
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);
        if(ac_token != null){
//            Toast.makeText(context, "access token is not null", Toast.LENGTH_SHORT).show();
            new VerifyIdentity().execute(ac_token);
        }
        else if(ac_token==null){
//            Toast.makeText(context, "access token is null", Toast.LENGTH_SHORT).show();
            new PrefetchData().execute();
        }
    }

    private class VerifyIdentity extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                GetAuthJsonParser getAuthJsonParser=new GetAuthJsonParser();
                json=getAuthJsonParser.getJsonFromUrl(getString(R.string.base_url)+"/api/v1.0/user_identity",strings[0]);
            } catch (Exception e){ e.printStackTrace(); }
            return json;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (result != null){
                Log.e("verification > ",result);
                try{
                    JSONObject jsonObject=new JSONObject(result);
                    String status=jsonObject.getString("status");
                    if (status.equals("OK")){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                prefManager=new PrefManager(context);
                                if (!prefManager.isFirstTimeLaunch()){
                                    launchDashboard();finish();
                                }else if (prefManager.isFirstTimeLaunch()){
                                    launchDashboard();finish();
                                }
                            }
                        },SPLASH_TIME_OUT);
                    }else if (status.equals("ERR")){
                        Toast.makeText(context, "Unauthorized user. Proceed to Login.", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(context,LoginActivity.class);
                                startActivity(intent);finish();
                            }
                        },SPLASH_TIME_OUT);
                    }
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }

    private void launchDashboard() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(context,SelectStore.class));finish();
    }

    private class PrefetchData extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i=new Intent(context,LoginActivity.class);
                    startActivity(i);finish();
                }
            },SPLASH_TIME_OUT);
        }
    }

}