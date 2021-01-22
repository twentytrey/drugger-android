package xyz.drugger.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import xyz.drugger.app.network.PostJsonParser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Context context;
    private SharedPreferences sharedPreferences;
    private Button gotosignup,loginbutton;
    private SharedPreferences.Editor editor;
    private EditText login_password,login_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context=LoginActivity.this;
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        editor=sharedPreferences.edit();
        gotosignup=(Button)findViewById(R.id.go_to_signup);gotosignup.setOnClickListener(this);
        loginbutton=(Button)findViewById(R.id.login_button);loginbutton.setOnClickListener(this);
        login_password=(EditText)findViewById(R.id.login_password);
        login_phone=(EditText)findViewById(R.id.login_phone);
        login_phone.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            private PhoneNumberUtil util=null;
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(util==null){ util=PhoneNumberUtil.createInstance(getApplicationContext()); }
                    try{
                        final Phonenumber.PhoneNumber phoneNumber = util.parse(login_phone.getText(),"NG");
                        login_phone.setText(util.format(phoneNumber,PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
                    }catch (Exception e){ e.printStackTrace(); }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                loginUser();
                break;
            case R.id.go_to_signup:
                Intent intent = new Intent(context,SignupActivity.class);
                startActivity(intent);finish();
                break;
        }
    }

    private void loginUser() {
        String logonid=login_phone.getText().toString();
        String logonpassword=login_password.getText().toString();
        if(!logonid.equals("") && !logonpassword.equals("")){
            try{
                JSONObject payload=new JSONObject();
                payload.put("logonid",logonid);
                payload.put("logonpassword",logonpassword);
                new LoginUser().execute(payload.toString());
            }catch (Exception e){ e.printStackTrace(); }
        }
    }

    private class LoginUser extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostJsonParser postJsonParser=new PostJsonParser();
                json=postJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/login_organization",strings[0]);
            } catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(result !=null){
                try{
                    JSONObject jsonObject=new JSONObject(result);
                    String status=jsonObject.getString("status");
                    String message=jsonObject.getString("msg");
                    if (status.equals("OK")){
                        String access_token=jsonObject.getString(getString(R.string.access_token));
                        String refresh_token=jsonObject.getString(getString(R.string.refresh_token));
                        int user_id=jsonObject.getInt(getString(R.string.user_id));
                        JSONObject employerJSON=jsonObject.getJSONObject(getString(R.string.employer_json));
                        int employer=employerJSON.getInt(getString(R.string.employer));
                        String employername=employerJSON.getString(getString(R.string.employername));
                        JSONArray roles=jsonObject.getJSONArray(getString(R.string.roles));
                        int role_id=roles.getJSONObject(0).getInt(getString(R.string.role_id));
                        String rolename=roles.getJSONObject(0).getString(getString(R.string.rolename));
                        int language_id=jsonObject.getInt(getString(R.string.language_id));
                        String profile=jsonObject.getString(getString(R.string.profile));

                        editor.putString(getString(R.string.access_token),access_token);
                        editor.putString(getString(R.string.refresh_token),refresh_token);
                        editor.putInt(getString(R.string.user_id),user_id);
                        editor.putInt(getString(R.string.employer),employer);
                        editor.putString(getString(R.string.employername),employername);
                        editor.putInt(getString(R.string.role_id),role_id);
                        editor.putString(getString(R.string.rolename),rolename);
                        editor.putInt(getString(R.string.language_id),language_id);
                        editor.putString(getString(R.string.profile),profile);
                        boolean committed=editor.commit();
                        if (committed){
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent=new Intent(context,SelectStore.class);
                                    startActivity(intent);finish();
                                }
                            },3000);
                        }
                    }
                    else if (status.equals("ERR")){
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }
}
