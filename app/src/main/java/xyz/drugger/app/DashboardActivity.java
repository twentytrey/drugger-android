package xyz.drugger.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;

import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.drugger.app.network.PostAuthJsonParser;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int RUNNABLE_TIMEOUT = 3000;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private int user_id;
    private String ac_token;
    private TextView profilename,profiledesc1;
    private LinearLayout poslayout;
    private LinearLayout viewproducts;
    private LinearLayout editprofile;
    private int PICK_CSV_REQUEST=123;
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_PERMISSION_CODE=100;
    private static final int REQUEST_BT=2;
    private TextView employerprofilevalue;
    private String firstname,lastname,photo,phone1,city,state,country,address1;
    private int addrbook_id,address_id;
    private CircleImageView profileimage;
    private LinearLayout returnproducts;
    private String todaysdate;
    private TextView todaysales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        context=DashboardActivity.this;
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        editor=sharedPreferences.edit();requestStoragePermission();requestCameraPermission();requestBluetoothPermission();

        ImageView logoutbutton = (ImageView) findViewById(R.id.logout_button);
        logoutbutton.setOnClickListener(this);
        profilename=findViewById(R.id.profile_name);
        profiledesc1=findViewById(R.id.profile_desc_1);
        profileimage=findViewById(R.id.profile_image);
        employerprofilevalue=findViewById(R.id.employer_value);
        poslayout=findViewById(R.id.pos_layout);
        poslayout.setOnClickListener(this);
        viewproducts=findViewById(R.id.view_products);
        returnproducts=(LinearLayout)findViewById(R.id.return_products);
        returnproducts.setOnClickListener(this);
        viewproducts.setOnClickListener(this);
        editprofile=findViewById(R.id.edit_profile);
        editprofile.setOnClickListener(this);
        todaysales=findViewById(R.id.todays_sales);

        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);

        Date date=Calendar.getInstance().getTime();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        todaysdate=formatter.format(date);

        try{
            JSONObject payload=new JSONObject();
            payload.put("logonid",user_id);
            new ReadUser().execute(payload.toString());
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private void testPrinter(){
        try{
            EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32);
            printer
                    .printFormattedText(
                            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.drugger_logo, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n" +
                                    "[L]\n" +
                                    "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                                    "[L]\n" +
                                    "[C]================================\n" +
                                    "[L]\n" +
                                    "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                                    "[L]  + Size : S\n" +
                                    "[L]\n" +
                                    "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                                    "[L]  + Size : 57/58\n" +
                                    "[L]\n" +
                                    "[C]--------------------------------\n" +
                                    "[R]TOTAL PRICE :[R]34.98e\n" +
                                    "[R]TAX :[R]4.23e\n" +
                                    "[L]\n" +
                                    "[C]================================\n" +
                                    "[L]\n" +
                                    "[L]<font size='tall'>Customer :</font>\n" +
                                    "[L]Raymond DUPONT\n" +
                                    "[L]5 rue des girafes\n" +
                                    "[L]31547 PERPETES\n" +
                                    "[L]Tel : +33801201456\n" +
                                    "[L]\n" +
                                    "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                                    "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>"
                    );
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    protected void requestBluetoothPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH},REQUEST_BT);
        }
    }

    private void requestCameraPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }
    }

    private void requestStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PICK_CSV_REQUEST);
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PICK_CSV_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]permissions, @NonNull int[]grantResults){
        if(requestCode==PICK_CSV_REQUEST) {
            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){ }
            else{ Toast.makeText(context, "You must grant storage permission to upload product data.", Toast.LENGTH_LONG).show(); }
        }
    }

    private void logoutUser() {
        editor.clear();
        boolean committed=editor.commit();
        if (committed){
            Toast.makeText(context, "Successfully logged out.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() { startActivity(new Intent(context,LoginActivity.class)); }
            },RUNNABLE_TIMEOUT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout_button:
                logoutUser();
                break;
            case R.id.pos_layout:
                startActivity(new Intent(context,PointOfSale.class));
                break;
            case R.id.return_products:
                startActivity(new Intent(context,ReturnsActivity.class));
                break;
            case R.id.view_products:
                startActivity(new Intent(context,ProductsActivity.class));
                break;
            case R.id.edit_profile:
                Intent intent=new Intent(context,EditProfile.class);
                intent.putExtra("firstname",firstname);
                intent.putExtra("lastname",lastname);
                intent.putExtra("photo",photo);
                intent.putExtra("phone1",phone1);
                intent.putExtra("city",city);
                intent.putExtra("state",state);
                intent.putExtra("country",country);
                intent.putExtra("address1",address1);
                intent.putExtra("addrbook_id",addrbook_id);
                intent.putExtra("address_id",address_id);
                startActivity(intent);
                break;
        }
    }

    private class ReadUser extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json = postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/read_organization",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(result != null){
//                Log.e("READUSER : ",result);
                try{
                    JSONObject jsonObject=new JSONObject(result);
                    JSONObject users=jsonObject.getJSONObject("users");
                    JSONObject busprof=jsonObject.getJSONObject("busprof");
                    JSONObject userprof=jsonObject.getJSONObject("userprof");
                    JSONObject address=jsonObject.getJSONObject("address");
                    JSONObject member=jsonObject.getJSONObject("member");
                    JSONObject addrbook=jsonObject.getJSONObject("addrbook");

                    firstname=users.getString("field1");
                    lastname=users.getString("field3");
                    profilename.setText(firstname+" "+lastname);
                    String registertype=users.getString("registertype");
                    String profiletype=users.getString("profiletype");
                    String registration=users.getString("registration");
                    profiledesc1.setText(registration);
                    String employername=busprof.getString("orgentityname");
                    employerprofilevalue.setText(employername);
                    int employer_id=busprof.getInt("org_id");
                    photo=userprof.getString("photo");
//                    Log.e("PHOTO: ",photo);
//                    buildBitmap(photo);
//                    if (!String.valueOf(photo).equals("null") && !String.valueOf(photo).equals("") && photo != null){ buildBitmap(photo); }
                    phone1=address.getString("phone1");
                    address1=address.getString("address1");
                    address_id=address.getInt("address_id");
                    city=address.getString("city");
                    state=address.getString("state");
                    country=address.getString("country");
                    String membertype=member.getString("type");
                    String memberstate=member.getString("state");
                    addrbook_id=addrbook.getInt("addrbook_id");

                    JSONObject payload=new JSONObject();
                    payload.put("editor_id",user_id);
                    payload.put("language_id",1);
                    payload.put("todaysdate", todaysdate);
                    new GetMySales().execute(payload.toString());
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }

    protected void buildBitmap(String uri){
        try{
            URL url = new URL(uri);
            Bitmap image= BitmapFactory.decodeStream(url.openConnection().getInputStream());
            profileimage.setImageDrawable(new BitmapDrawable(getResources(),image));
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private class GetMySales extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/my_sales",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!String.valueOf(result).equals("null") && !String.valueOf(result).equals("") && result != null){
                try{
                    JSONObject response=new JSONObject(result);
                    double total=response.getDouble("total");
                    String totalstr=new DecimalFormat("#,###,###.##").format(total);
                    String symbol=response.getString("symbol");
                    todaysales.setText(symbol+" "+totalstr);
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }
}
