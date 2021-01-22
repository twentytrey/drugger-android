package xyz.drugger.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xyz.drugger.app.adapter.CheckOutItemAdapter;
import xyz.drugger.app.database.models.Product;
import xyz.drugger.app.network.GetAuthJsonParser;
import xyz.drugger.app.network.PostAuthJsonParser;
import xyz.drugger.app.services.LocalCartService;

public class CheckOutActivity extends AppCompatActivity implements ServiceConnection{
    Context context;
    private LocalCartService s;
    private List<Product> itemsList;
    RecyclerView chkRecyclerView;
    CheckOutItemAdapter checkOutItemAdapter;
    TextView chkChargeSum;
    private TextView chkCharge;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String ac_token;
    private String timeplaced;
    private String store;
    private int orders_id;
    private int user_id;
    private Integer ccID;
    private String ccName;
    private int employer;
    private int store_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        context=CheckOutActivity.this;

        Intent intent=getIntent();
        store=intent.getStringExtra("store");
        store_id=intent.getIntExtra("store_id",0);
        ccID=intent.getIntExtra("ccID",0);
        ccName=intent.getStringExtra("ccName");

        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);
        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);editor=sharedPreferences.edit();
        employer=sharedPreferences.getInt(getString(R.string.employer),0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(context,LocalCartService.class);
        bindService(intent,this,Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause(){
        super.onPause();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LocalCartService.MyBinder b = (LocalCartService.MyBinder) service;
        s=b.getService();
//        Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
        List<Product>wlist=s.getResultList();
//        Toast.makeText(context, String.valueOf(wlist.size()), Toast.LENGTH_SHORT).show();
        activateCheckOutRecycler(wlist);
    }

    private void calculateSum(){
        float grandTotal=0;
        for(Product p:itemsList){ double gross=p.getSalesprice()*p.getQuantity();grandTotal+=gross; }
        final DecimalFormat formatter=new DecimalFormat("#,###,###");
        String chargeText=formatter.format(grandTotal);
        chkChargeSum=findViewById(R.id.chk_charge_sum);
        String symbol="";
        if (itemsList.size()>0){ symbol=itemsList.get(0).getSymbol(); }
        chkChargeSum.setText(symbol+""+chargeText);
    }

    private void activateCheckOutRecycler(List<Product> wlist) {
        itemsList=new ArrayList<Product>();
        Set<Integer>itemIDs = new HashSet<>();
        for(Product p:wlist){ itemIDs.add(p.getCatentry_id()); }
        for (int i:itemIDs){
            float count=0f;
            Product fp = null;
            for(Product p:wlist){
                if(p.getCatentry_id()==i){ count+=1f;fp=p; }
            }
            fp.setQuantity((int)count);fp.setShortdescription("Quantity Ordered "+(int)count);
            itemsList.add(fp);
        }
        calculateSum();

        chkRecyclerView=findViewById(R.id.chk_items_recyclerview);
        chkRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        checkOutItemAdapter=new CheckOutItemAdapter(context,itemsList);
        chkRecyclerView.setAdapter(checkOutItemAdapter);

        checkOutItemAdapter.setOnItemClickListener(new CheckOutItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) { Product product=itemsList.get(position); }

            @Override
            public void onDeleteClick(int position) {
                itemsList.remove(position);calculateSum();
                checkOutItemAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onIncreaseQuantity(int position) {
                Product product=itemsList.get(position);
                float currentQuant=product.getQuantity();
                currentQuant=currentQuant+1;product.setQuantity((int)currentQuant);calculateSum();
                product.setShortdescription("Quantity Ordered "+(int)currentQuant);
                checkOutItemAdapter.notifyItemChanged(position);
            }

            @Override
            public void onDecreaseQuantity(int position) {
                Product product=itemsList.get(position);
                float currentQuant=product.getQuantity();
                if(currentQuant > 0){
                    currentQuant=currentQuant-1;product.setQuantity((int)currentQuant);calculateSum();
                    product.setShortdescription("Quantity Ordered "+(int)currentQuant);
                    checkOutItemAdapter.notifyItemChanged(position);
                }else if(currentQuant <= 0){
                    Toast.makeText(context, "Quantity is already at zero.", Toast.LENGTH_SHORT).show();
                    product.setQuantity(0);calculateSum();
                    product.setShortdescription("Quantity Ordered 0");
                    checkOutItemAdapter.notifyItemChanged(position);
                }
            }
        });

        chkCharge=findViewById(R.id.chk_charge);
        chkCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.remove(LocalCartService.CART);
                boolean sold=editor.commit();
                if(sold){
                    JSONArray orderitems=new JSONArray();
                    for(Product p:itemsList){
//                    Gson gson=new Gson();
//                    String productString=gson.toJson(p);
//                    Log.e("productString > ",productString);
                        Date todaysDate= Calendar.getInstance().getTime();
                        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        timeplaced=formatter.format(todaysDate);
                        int buschn_id=2;
                        try{
                            JSONObject payload=new JSONObject();
                            payload.put("catentry_id",p.getCatentry_id());
                            payload.put("owner_id",p.getOwner_id());
                            payload.put("language_id",1);
                            payload.put("store_id",store_id);
                            payload.put("timeplaced",timeplaced);
                            payload.put("buschn_id",buschn_id);
                            payload.put("quantity",p.getQuantity());
                            payload.put("customer_id",ccID);
                            payload.put("price",p.getSalesprice());
                            payload.put("costprice",p.getCost());
                            payload.put("editor",user_id);
                            orderitems.put(payload);
                        }
                        catch (Exception e){ e.printStackTrace(); }
                    }
                    try{
                        JSONObject payload=new JSONObject();
                        payload.put("orderitems",orderitems);
                        // Log.e("ORDERITEMS: ",payload.toString());
                        new PostOrder().execute(payload.toString());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    itemsList.clear();checkOutItemAdapter.notifyDataSetChanged();chkChargeSum.setText("00.00");
                }
            }
        });
    }

    private class PostOrder extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/create_order",strings[0],ac_token);
            }
            catch(Exception e){ e.printStackTrace(); }
            return json;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(result != null){
                try {
                    JSONObject res=new JSONObject(result);
                    String status=res.getString("status");
                    String msg=res.getString("msg");
                    if(status.equals("OK")){
                    orders_id=res.getInt("orders_id");
                    Intent intent=new Intent(context,BillingActivity.class);
                    intent.putExtra("orders_id",orders_id);
                    intent.putExtra("customer_name",ccName);
                    intent.putExtra("customer_id",ccID);
                    intent.putExtra("store",store);
                    intent.putExtra("orderdate",timeplaced);
                    intent.putExtra("language_id",1);
                    startActivity(intent);finish();
                    Toast.makeText(context, "Check out successful", Toast.LENGTH_LONG).show();
                    }else if (status.equals("ERR")){
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        s=null;
    }

}