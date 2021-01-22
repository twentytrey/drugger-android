package xyz.drugger.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xyz.drugger.app.adapter.CountryAutoCompleteAdapter;
import xyz.drugger.app.adapter.StateAutoCompleteAdapter;
import xyz.drugger.app.network.JsonParser;
import xyz.drugger.app.network.PostAuthJsonParser;
import xyz.drugger.app.network.PostJsonParser;
import xyz.drugger.app.pojo.Country;
import xyz.drugger.app.pojo.State;

public class EditProfile extends AppCompatActivity implements View.OnClickListener{
    private static final int PICK_IMAGE_REQUEST = 123;
    private int user_id,employer;
    private String ac_token;
    private SharedPreferences sharedPreferences;
    private Context context;
    private ArrayList<Country> countries;
    private ArrayList<State>states;
    private TextView countryItem;
    private TextView stateItem;
    private CountryAutoCompleteAdapter countryAutoCompleteAdapter;
    private StateAutoCompleteAdapter stateAutoCompleteAdapter;
    private String countryabbr;
    private EditText field1,field3,address1,city;
    private AutoCompleteTextView countryauto,stateauto;
    private AppCompatAutoCompleteTextView state,country;
    private CircleImageView editprofileimage;

    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_PERMISSION_CODE=100;
    private Uri filePath;
    private String profileimageurl;
    private Button saveprofilebutton;
    private String ifirstname,ilastname,icity,iaddress1,icountry,istate,iphoto,iphone1;
    private int iaddrbook_id,iaddress_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent=getIntent();
        ifirstname=intent.getStringExtra("firstname");
        ilastname=intent.getStringExtra("lastname");
        iphoto=intent.getStringExtra("photo");
        iphone1=intent.getStringExtra("phone1");
        icity=intent.getStringExtra("city");
        istate=intent.getStringExtra("state");
        icountry=intent.getStringExtra("country");
        iaddress1=intent.getStringExtra("address1");
        iaddrbook_id=intent.getIntExtra("addrbook_id",0);
        iaddress_id=intent.getIntExtra("address_id",0);

        context=EditProfile.this;requestCameraPermission();
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);
        employer=sharedPreferences.getInt(getString(R.string.employer),0);
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);

        countryauto=(AppCompatAutoCompleteTextView)findViewById(R.id.profile_country);
        if (!String.valueOf(icountry).equals("null") && !String.valueOf(icountry).equals("")){countryauto.setText(icountry);}
        stateauto=(AutoCompleteTextView)findViewById(R.id.profile_state);
        if (!String.valueOf(istate).equals("null") && !String.valueOf(istate).equals("")){stateauto.setText(istate);}
        countryItem=(TextView)findViewById(R.id.countryItemTextView);
        stateItem=(TextView)findViewById(R.id.stateItemTextView);
        countries=new ArrayList<Country>();states=new ArrayList<State>();
//        country=(AppCompatAutoCompleteTextView)findViewById(R.id.profile_country);
//        state=(AppCompatAutoCompleteTextView)findViewById(R.id.profile_state);
        address1=(EditText)findViewById(R.id.profile_address_1);
        if (!String.valueOf(address1).equals("null") && !String.valueOf(address1).equals("") && address1!= null){address1.setText(iaddress1);}
        city=(EditText)findViewById(R.id.profile_city);
        if (!String.valueOf(city).equals("null") && !String.valueOf(city).equals("") && city!=null){city.setText(icity);}
        field1=(EditText)findViewById(R.id.profile_field_1);
        field3=(EditText)findViewById(R.id.profile_field_3);
        if (!String.valueOf(ifirstname).equals("null") && !String.valueOf(ilastname).equals("") && ifirstname != null && ilastname != null){
            field1.setText(ifirstname);field3.setText(ilastname);
        }
        editprofileimage=(CircleImageView)findViewById(R.id.edit_profile_image);
        editprofileimage.setOnClickListener(this);
        saveprofilebutton=(Button)findViewById(R.id.button_save_profile);
        saveprofilebutton.setOnClickListener(this);

        new GetCountryData().execute();
    }

    private void requestCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode==CAMERA_PERMISSION_CODE){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,CAMERA_REQUEST);
            } else { Toast.makeText(context, "The camera is necessary to capture your profile picture.", Toast.LENGTH_LONG).show(); }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_save_profile:
                saveprofile();
                break;
            case R.id.edit_profile_image:
                launchCamera();
                break;
        }
    }

    private void launchCamera() {
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data !=null && data.getData()!=null){
            filePath=data.getData();
            uploadMultipart(filePath);
        }
        if(requestCode==CAMERA_REQUEST && resultCode==RESULT_OK){
            Bitmap photo=(Bitmap) data.getExtras().get("data");
            editprofileimage.setImageDrawable(new BitmapDrawable(getResources(),photo));
//            ByteArrayOutputStream stream=new ByteArrayOutputStream();
//            photo.compress(Bitmap.CompressFormat.JPEG,100,stream);
//            byte[]byteArray=stream.toByteArray();

            String rootDir= Environment.getExternalStorageDirectory().toString();
            File appDir=new File(rootDir+"/Drugger");
            if(!appDir.exists()){
                boolean made=appDir.mkdirs();
                if (made){ postImage(appDir,photo); }
                else if (!made){ Toast.makeText(context, "Could not save captured image to file.", Toast.LENGTH_LONG).show(); }
            } else if (appDir.exists()){ postImage(appDir,photo); }
        }
    }

    private void postImage(File appDir, Bitmap photo) {
        Random generator = new Random();int n=10000;
        int next=generator.nextInt(n);
        String fname="Image-"+next+".jpg";
        // Log.e("FILENAME: ",fname);
        File file=new File(appDir,fname);
        if (file.exists())file.delete();
        try{
            FileOutputStream out = new FileOutputStream(file);
            photo.compress(Bitmap.CompressFormat.JPEG,90,out);
            out.flush();out.close();
            uploadFile(getString(R.string.base_url)+"/profileuploads",file,context);
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private boolean uploadFile(String serverURL, File file,Context context) {
        try{
            OkHttpClient client=new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("image",file.getName(),
                            RequestBody.create(MediaType.parse("image/*"),file))
                    .build();
            Request request=new Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback(){
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()){
                        String returns = response.body().string();
                        // Log.e("uploadresponse > ",returns);
                        try{
                            JSONObject jsonObject=new JSONObject(returns);
                            String url=jsonObject.getString("url");
                            profileimageurl=url;
                            // Log.e("UPLOADEDIMAGE: ",url);
                        }
                        catch(Exception e){ e.printStackTrace(); }
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    Log.e("uploadfailure > ",e.getMessage());
//                    Log.e("uploadata > ",requestBody.toString());
                }
            });
        }
        catch (Exception e){ e.printStackTrace(); }
        return false;
    }

    private void uploadMultipart(Uri filePath) { }

    private void saveprofile() {
        try {
            JSONObject payload=new JSONObject();
            payload.put("member_id",user_id);
            payload.put("employer_id",employer);
            payload.put("field1",field1.getText().toString());
            payload.put("field3",field3.getText().toString());
            payload.put("address1",address1.getText().toString());
            payload.put("country",countryauto.getText().toString());
            payload.put("state",stateauto.getText().toString());
            payload.put("city",city.getText().toString());
            payload.put("photo",profileimageurl);
            payload.put("address_id",iaddress_id);
            payload.put("addrbook_id",iaddrbook_id);
            new UpdateProfile().execute(payload.toString());
        }catch (Exception e){ e.printStackTrace(); }
    }

    private class GetCountryData extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            JsonParser jsonParser=new JsonParser();
            String json=null;
            try{ json=jsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/list_countries"); }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (!String.valueOf(result).equals("null") && !String.valueOf(result).equals("")){
                // Log.e("COUNTRIES: ",result);
                try{
                    JSONArray jsonArray=new JSONArray(result);
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject country=jsonArray.getJSONObject(i);
                        String countryname=country.getString("name");
                        String countryabbr=country.getString("countryabbr");
                        String callingcode=country.getString("callingcode");
                        int lang_id=country.getInt("lang_id");
                        Country c=new Country();c.setCallingcode(callingcode);c.setCountryabbr(countryabbr);
                        c.setLang_id(lang_id);c.setName(countryname);countries.add(c);
                    }
                    countryAutoCompleteAdapter=new CountryAutoCompleteAdapter(context,R.layout.autocomplete_country_list_item,countries);
                    countryauto.setThreshold(1);countryauto.setAdapter(countryAutoCompleteAdapter);
                    countryauto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Country country=(Country)parent.getItemAtPosition(position);
                            try{
                                JSONObject payload=new JSONObject();
                                payload.put("countryabbr",country.getCountryabbr());
                                new GetStatesForCountry().execute(payload.toString());
                            }
                            catch (Exception e){ e.printStackTrace(); }
                        }
                    });

                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }

    private class GetStatesForCountry extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostJsonParser postJsonParser=new PostJsonParser();
                json=postJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/list_states_for_country",strings[0]);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (!String.valueOf(result).equals("null") && !String.valueOf(result).equals("")){
                try{
                    JSONArray jsonArray=new JSONArray(result);
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject state=jsonArray.getJSONObject(i);
                        String stateprovabbr=state.getString("stateprovabbr");
                        int language_id=state.getInt("language_id");
                        String name=state.getString("name");
                        State s=new State();s.setLanguage_id(language_id);s.setName(name);
                        s.setStateprovabbr(stateprovabbr);states.add(s);
                    }
                    stateAutoCompleteAdapter=new StateAutoCompleteAdapter(context,R.layout.autocomplete_state_list_item,states);
                    stateauto.setThreshold(1);stateauto.setAdapter(stateAutoCompleteAdapter);
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }

    private class UpdateProfile extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/mobile_update_profile",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (!String.valueOf(result).equals("null") && !String.valueOf(result).equals("")){
                try{
                    JSONObject response=new JSONObject(result);
                    String message=response.getString("msg");
                    String status=response.getString("status");
                    if (status.equals("OK")){
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context,DashboardActivity.class));finish();
                    }else if (status.equals("ERR")){
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }
}
