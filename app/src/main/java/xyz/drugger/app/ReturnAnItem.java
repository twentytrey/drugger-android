package xyz.drugger.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;

import xyz.drugger.app.network.PostAuthJsonParser;

public class ReturnAnItem extends AppCompatActivity {
    private Context context;
    private SharedPreferences sharedPreferences;
    private int user_id,employer;
    private String ac_token;
    private String rma_status,name,symbol;
    private double price;
    private int orders_id,catentry_id,itemspc_id,member_id,orderitems_id,quantity,store_id;
    private TextView returnitemname,returnitemprice,returnitemquantity;
    EditText rtnname,rtnrefundorreplace,rtncreditamount,rtnquantity;
    Button submitrtnproduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_an_item);

        context=ReturnAnItem.this;
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);
        employer=sharedPreferences.getInt(getString(R.string.employer),0);
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);
        rma_status="APP";Intent intent = getIntent();

        member_id=intent.getIntExtra("member_id",0);
        orderitems_id=intent.getIntExtra("orderitems_id",0);
        catentry_id=intent.getIntExtra("catentry_id",0);
        price=intent.getDoubleExtra("price",0);
        quantity=intent.getIntExtra("quantity",0);
        itemspc_id=intent.getIntExtra("itemspc_id",0);
        name=intent.getStringExtra("name");
        symbol=intent.getStringExtra("symbol");
        store_id=intent.getIntExtra("store_id",0);
        orders_id=intent.getIntExtra("orders_id",0);

        rtnname=findViewById(R.id.rtn_name);rtnrefundorreplace=findViewById(R.id.rtn_refundorreplace);
        rtncreditamount=findViewById(R.id.rtn_creditamount);rtnquantity=findViewById(R.id.rtn_quantity);
        submitrtnproduct=findViewById(R.id.submit_rtn_product);

        rtnname.setText(name);rtnrefundorreplace.setText("REF");rtncreditamount.setText(String.valueOf(price));
        rtnquantity.setText(String.valueOf(quantity));
        submitrtnproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitreturn();
            }
        });

        returnitemname=(TextView)findViewById(R.id.return_item_name);
        returnitemprice=(TextView)findViewById(R.id.return_item_price);
        returnitemquantity=(TextView)findViewById(R.id.return_item_quantity);
        returnitemname.setText(name);
        returnitemprice.setText(new DecimalFormat("#,###,###.##").format(price));
        returnitemquantity.setText("Quantity Purchased "+quantity);
    }

    private void submitreturn() {
        try{
            JSONObject payload=new JSONObject();
            //store_id,member_id,refundagainstordid,creditamount,status,quantity,orderitems_id,
            // catentry_id,itemspc_id,refundorreplace,
            payload.put("store_id",store_id);payload.put("member_id",member_id);
            payload.put("refundagainstordid",orders_id);payload.put("creditamount",rtncreditamount.getText().toString());
            payload.put("status",rma_status);payload.put("quantity",rtnquantity.getText().toString());
            payload.put("orderitems_id",orderitems_id);payload.put("catentry_id",catentry_id);
            payload.put("itemspc_id",itemspc_id);payload.put("refundorreplace",rtnrefundorreplace.getText().toString());
            new ReturnItem().execute(payload.toString());
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private class ReturnItem extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/create_rma",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(!String.valueOf(result).equals("null") && !String.valueOf(result).equals("") && result!=null){
                try{
                    JSONObject response=new JSONObject(result);
                    String message=response.getString("msg");
                    String status=response.getString("status");
                    if (status.equals("OK")){ Toast.makeText(context, message, Toast.LENGTH_SHORT).show();finish(); }
                    else  if (status.equals("ERR")){ Toast.makeText(context, message, Toast.LENGTH_SHORT).show(); }
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }
}
