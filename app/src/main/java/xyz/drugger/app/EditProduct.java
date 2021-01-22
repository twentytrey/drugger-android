package xyz.drugger.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.drugger.app.network.PostAuthJsonParser;

public class EditProduct extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private SharedPreferences sharedPreferences;
    private int employer,user_id;
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
    private EditText addshelfnumber;
    private int storeent_id;
    private String catenttype_id;
    private int catentry_id;
    private String currency;
    private String endofservicedate;
    private String fullimage;
    private int itemspc_id;
    private int language_id;
    private double listprice;
    private int member_id;
    private String mfpartnumber;
    private String name;
    private int offer_id;
    private String partnumber;
    private int published;
    private int quantity;
    private double salesprice;
    private int sellingstore_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        context=EditProduct.this;
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);
        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);
        employer=sharedPreferences.getInt(getString(R.string.employer),0);
        sellingstore_id=sharedPreferences.getInt(getString(R.string.sellingstore_id),0);
        Log.e("SELLINSTORE: ",String.valueOf(sellingstore_id));

        Intent incoming=getIntent();
        catentry_id=incoming.getIntExtra("catentry_id",0);
        catenttype_id=incoming.getStringExtra("catenttype_id");
        currency=incoming.getStringExtra("currency");
        endofservicedate=incoming.getStringExtra("endofservicedate");
        fullimage=incoming.getStringExtra("fullimage");
        itemspc_id=incoming.getIntExtra("itemspc_id",0);
        language_id=incoming.getIntExtra("language_id",0);
        listprice=incoming.getDoubleExtra("listprice",0d);
        member_id=incoming.getIntExtra("member_id",0);
        mfpartnumber=incoming.getStringExtra("mfpartnumber");
        name=incoming.getStringExtra("name");
        offer_id=incoming.getIntExtra("offer_id",0);
        partnumber=incoming.getStringExtra("partnumber");
        published=incoming.getIntExtra("published",0);
        quantity=incoming.getIntExtra("quantity",0);
        salesprice=incoming.getDoubleExtra("salesprice",0);
        storeent_id=incoming.getIntExtra("storeent_id",0);

        productimage=(CircleImageView)findViewById(R.id.product_image);
        if (!fullimage.equals("null") && !fullimage.equals("")){
            Glide.with(context).asBitmap().load(fullimage).into(new BitmapImageViewTarget(productimage){
                @Override
                protected void setResource(Bitmap resource){
                    productimage.setImageDrawable(new BitmapDrawable(getResources(),resource));
                    super.setResource(resource);
                }
            });
        }
        submitaddproduct=(Button)findViewById(R.id.submit_add_product);
        addproductimage=(EditText)findViewById(R.id.add_product_image);
        addproductimage.setText(fullimage);
        addproductdescription=(EditText)findViewById(R.id.add_product_description);
        addexpirydate=(EditText)findViewById(R.id.add_expiry_date);
        addexpirydate.setText(endofservicedate);
        addsalesprice=(EditText)findViewById(R.id.add_sales_price);
        addsalesprice.setText(String.valueOf(salesprice));
        addcostprice=(EditText)findViewById(R.id.add_cost_price);
        addcostprice.setText(String.valueOf(listprice));
        addproductname=(EditText)findViewById(R.id.add_product_name);
        addproductname.setText(name);
        addproductquantity=(EditText)findViewById(R.id.add_product_quantity);
        addproductquantity.setText(String.valueOf(quantity));
        addshelfnumber=(EditText)findViewById(R.id.add_shelf_number);
        if (!String.valueOf(mfpartnumber).equals("null") && !String.valueOf(mfpartnumber).equals("")){addshelfnumber.setText(mfpartnumber);}
        submitaddproduct.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_add_product:
                submitEdit();
        }
    }

    private void submitEdit() {
        try{
            JSONObject payload=new JSONObject();
            payload.put("catentry_id",catentry_id);
            payload.put("catenttype_id",catenttype_id);
            payload.put("currency",currency);
            payload.put("endofservicedate",addexpirydate.getText().toString());
            payload.put("fullimage",addproductimage.getText().toString());
            payload.put("itemspc_id",itemspc_id);
            payload.put("language_id",1);
            payload.put("listprice",addcostprice.getText().toString());
            payload.put("member_id",employer);
            payload.put("mfpartnumber",addshelfnumber.getText().toString());
            payload.put("name",addproductname.getText().toString());
            payload.put("offer_id",offer_id);
            payload.put("partnumber",partnumber);
            payload.put("published",published);
            payload.put("quantity",addproductquantity.getText().toString());
            payload.put("salesprice",addsalesprice.getText().toString());
            payload.put("shortdescription",addproductdescription.getText().toString());
            payload.put("storeent_id",sellingstore_id);
            new UpdateCatentry().execute(payload.toString());
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private class UpdateCatentry extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/update_catentry",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (result != null && !String.valueOf(result).equals("null")){
                try {
                    JSONObject response=new JSONObject(result);
                    String status=response.getString("status");
                    String message=response.getString("msg");
                    if (status.equals("OK")){
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context,ProductsActivity.class));finish();
                    }else if (status.equals("ERR")){
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){ e.printStackTrace(); }
            }
        }
    }
}
