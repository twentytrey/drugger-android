package xyz.drugger.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.AbstractSequentialList;
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
import xyz.drugger.app.network.PostAuthJsonParser;
import xyz.drugger.app.pojo.StoresBasic;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 123;
    private Uri filePath;
    private Context context;
    private SharedPreferences sharedPreferences;
    private int user_id;
    private String ac_token;
    private CircleImageView productimage;
    private Button submitaddproduct;
    private EditText addproductimage;
    private EditText addproductdescription;
    private EditText addexpirydate;
    private EditText addsalesprice;
    private EditText addcostprice;
    private EditText addproductname;
    private EditText addproductquantity;
    private MaterialSpinner selectstorespinner;
    private int employer;
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_PERMISSION_CODE=100;
    private ArrayList<String> store_names;
    private ArrayList<StoresBasic> storeItems;
    private String[] storeNames;
    private String selectedName;
    private int selectedIndex;
    private StoresBasic selectedStore;
    private EditText addshelfnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        context=AddProductActivity.this;requestCameraPermission();selectedStore=new StoresBasic();
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);
        employer = sharedPreferences.getInt(getString(R.string.employer), 0);
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);

        submitaddproduct=(Button)findViewById(R.id.submit_add_product);
        addproductimage=(EditText)findViewById(R.id.add_product_image);
        addproductdescription=(EditText)findViewById(R.id.add_product_description);
        addexpirydate=(EditText)findViewById(R.id.add_expiry_date);
        addsalesprice=(EditText)findViewById(R.id.add_sales_price);
        addcostprice=(EditText)findViewById(R.id.add_cost_price);
        addproductname=(EditText)findViewById(R.id.add_product_name);
        addproductquantity=(EditText)findViewById(R.id.add_product_quantity);
        addshelfnumber=(EditText)findViewById(R.id.add_shelf_number);
        productimage=(CircleImageView)findViewById(R.id.product_image);
        selectstorespinner=(MaterialSpinner)findViewById(R.id.select_store_spinner);
        productimage.setOnClickListener(this);submitaddproduct.setOnClickListener(this);
        addexpirydate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 && (editable.length() % 3) == 0) {
                    final char c = editable.charAt(editable.length() - 1);
                    if ('-' == c) {
                        editable.delete(editable.length() - 1, editable.length());
                    }
                }
                if (editable.length() > 0 && (editable.length() % 3) == 0) {
                    char c = editable.charAt(editable.length() - 1);
                    if (Character.isDigit(c) && TextUtils.split(editable.toString(), String.valueOf("-")).length <= 2) {
                        editable.insert(editable.length() - 1, String.valueOf("-"));
                    }
                }
            }
        });
        store_names=new ArrayList<String>();
        storeItems=new ArrayList<StoresBasic>();
        try{
            JSONObject payload=new JSONObject();
            payload.put("member_id",employer);
            payload.put("language_id",1);
            new GetStores().execute(payload.toString());
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private void requestCameraPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
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
            } else { Toast.makeText(context, "The camera is necessary to capture product images.", Toast.LENGTH_LONG).show(); }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.product_image:
                launchCamera();//showFileChooser();
                break;
            case R.id.submit_add_product:
                submitProduct();
                break;
        }
    }

    private void launchCamera() {
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,CAMERA_REQUEST);
    }

    private void submitProduct() {
        String listprice = addcostprice.getText().toString();
        String productname = addproductname.getText().toString();
        String salesprice = addsalesprice.getText().toString();
        String quantity = addproductquantity.getText().toString();
        String mfpartnumber=addshelfnumber.getText().toString();
        int store_id = selectedStore.getStoreentID();
        if (listprice.equals("")|| listprice.equals("null")){
            Toast.makeText(context, "Cost Price field is compulsory", Toast.LENGTH_LONG).show();
        } else if (productname.equals("")||productname.equals("null")){
            Toast.makeText(context, "Product Name field is compulsory", Toast.LENGTH_LONG).show();
        } else if (salesprice.equals("")||salesprice.equals("null")){
            Toast.makeText(context, "Sales Price field is compulsory", Toast.LENGTH_LONG).show();
        } else if (quantity.equals("")||quantity.equals("null")){
            Toast.makeText(context, "Inventory Quantity field is compulsory", Toast.LENGTH_LONG).show();
        } else if (store_id==0||String.valueOf(store_id).equals("null")||String.valueOf(store_id).equals("")){
            Toast.makeText(context, "You must select a holding store.", Toast.LENGTH_LONG).show();
        }else {
            try{
                JSONObject payload=new JSONObject();
                payload.put("member_id",employer);
                payload.put("language_id",1);
                payload.put("itemspc_id",null);
                payload.put("catenttype_id","Item");
                payload.put("partnumber",null);
                payload.put("mfpartnumber",mfpartnumber);
                payload.put("mfname",null);
                payload.put("currency","NGN");
                payload.put("listprice",listprice);
                payload.put("catalog_id",null);
                payload.put("catgroup_id",null);
                payload.put("lastupdate",null);
                payload.put("endofservicedate",addexpirydate.getText().toString());
                payload.put("name",productname);
                payload.put("shortdescription",addproductdescription.getText().toString());
                payload.put("fullimage",addproductimage.getText().toString());
                payload.put("available",null);
                payload.put("published",1);
                payload.put("availabilitydate",null);
                payload.put("qtyunit_id",null);
                payload.put("storeent_id",store_id);
                payload.put("salesprice",salesprice);
                payload.put("quantity",quantity);
                new CreateCatentry().execute(payload.toString());
            }
            catch (Exception e){ e.printStackTrace(); }
        }
    }

    private void showFileChooser() {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);
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
            productimage.setImageDrawable(new BitmapDrawable(getResources(),photo));
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
        File file=new File(appDir,fname);
        if (file.exists())file.delete();
        try{
            FileOutputStream out = new FileOutputStream(file);
            photo.compress(Bitmap.CompressFormat.JPEG,90,out);
            out.flush();out.close();
            uploadFile(getString(R.string.base_url)+"/productuploads",file,context);
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    public void uploadMultipart(Uri uri) {
        String path=getRealPathFromURI(filePath);
//        Log.e("filepathfull > ",path);
        if(path==null){
            Toast.makeText(context, "Move your product image file into internal storage.", Toast.LENGTH_SHORT).show();
        }else {
//            Log.e("filepath > ",path);
            File file=new File(path);
//            Log.e("isabs,isfile,exists > ",String.valueOf(file.isAbsolute())+" "+String.valueOf(file.isFile())+" "+String.valueOf(file.exists()));
            uploadFile(getString(R.string.base_url)+"/productuploads",file,context);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
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
//                        Log.e("uploadresponse > ",returns);
                        try{
                            JSONObject jsonObject=new JSONObject(returns);
                            String url=jsonObject.getString("url");
                            addproductimage.setText(url);
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

    private class GetStores extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/list_stores",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null){
                try{
                    JSONArray stores=new JSONArray(result);
                    for (int i=0; i<stores.length(); i++){
                        JSONObject store=stores.getJSONObject(i);
                        int storeent_id=store.getInt("storeent_id");
                        String identifier=store.getString("identifier");
                        StoresBasic storesBasic=new StoresBasic(identifier,storeent_id);
                        store_names.add(identifier);storeItems.add(storesBasic);
                    }
                    storeNames=getStringArray(store_names);
                    selectstorespinner.setItems(storeNames);
                    if (storeNames.length > 0){ selectedName=storeNames[0];selectedIndex=0;selectedStore=storeItems.get(0); }
                    selectstorespinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                            selectedName=storeNames[position];
                            selectedStore=storeItems.get(position);
                        }
                    });
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }
    public static String[] getStringArray(ArrayList<String> arr){
        String[] str = new String[arr.size()];
        for(int j=0; j<arr.size(); j++){
            str[j]=arr.get(j);
        }
        return str;
    }

    private class CreateCatentry extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/create_catentry",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (result != null){
                try{
                    JSONObject response=new JSONObject(result);
                    String status=response.getString("status");
                    String message=response.getString("msg");
                    if (status.equals("OK")){
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        addproductimage.getText().clear();addproductdescription.getText().clear();
                        addexpirydate.getText().clear();addsalesprice.getText().clear();
                        addcostprice.getText().clear();addproductname.getText().clear();addproductquantity.getText().clear();
                    }
                    else if (status.equals("ERR")){ Toast.makeText(context, message, Toast.LENGTH_SHORT).show(); }
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }
}
