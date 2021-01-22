package xyz.drugger.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kotlin.contracts.Returns;
import xyz.drugger.app.adapter.OrderItemsAdapter;
import xyz.drugger.app.adapter.ReturnItemsAdapter;
import xyz.drugger.app.helper.RecyclerTouchListener;
import xyz.drugger.app.network.PostAuthJsonParser;
import xyz.drugger.app.pojo.OItem;

public class ReturnsActivity extends AppCompatActivity {
    private Context context;
    private SharedPreferences sharedPreferences;
    private int employer,user_id;
    private String ac_token;
    private Toolbar toolbar;
    private Gson gson;
    private int sellingstoreid;
    private String sellingstorename;
    private TextView rtnstoreselected;
    private MaterialSearchView searchView;
    private Menu externalMenu;
    private ArrayList<OItem> orderitems;
    private ReturnItemsAdapter returnitemsadapter;
    private RecyclerView rtnItemsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returns);
        toolbar=(Toolbar)findViewById(R.id.rtn_toolbar);gson=new Gson();
        setSupportActionBar(toolbar);

        context=ReturnsActivity.this;
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);
        employer=sharedPreferences.getInt(getString(R.string.employer),0);
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);

        orderitems=new ArrayList<OItem>();returnitemsadapter=new ReturnItemsAdapter(context,orderitems);
        rtnItemsRecycler=findViewById(R.id.rtn_items_recycler_view);
        rtnItemsRecycler.setHasFixedSize(true);
        rtnItemsRecycler.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        rtnItemsRecycler.setAdapter(returnitemsadapter);
        rtnItemsRecycler.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), rtnItemsRecycler, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                OItem item=orderitems.get(position);
                int member_id=item.getMember_id();
                int orderitems_id=item.getOrderitems_id();
                int catentry_id=item.getCatentry_id();
                double price=item.getPrice();
                double quantity=item.getQuantity();
                int itemspc_id=item.getItemspc_id();
                String name=item.getName();
                String symbol=item.getSymbol();
                int store_id=item.getStoreent_id();
                int orders_id=item.getOrders_id();
                Intent intent=new Intent(context,ReturnAnItem.class);
                intent.putExtra("member_id",member_id);intent.putExtra("orderitems_id",orderitems_id);
                intent.putExtra("catentry_id",catentry_id);intent.putExtra("price",price);
                intent.putExtra("quantity",(int)quantity);intent.putExtra("itemspc_id",itemspc_id);
                intent.putExtra("name",name);intent.putExtra("symbol",symbol);
                intent.putExtra("store_id",store_id);intent.putExtra("orders_id",orders_id);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) { }
        }));

        sellingstorename=sharedPreferences.getString(getString(R.string.sellingstore_name),null);
        sellingstoreid=sharedPreferences.getInt(getString(R.string.sellingstore_id),0);
        rtnstoreselected=(TextView)findViewById(R.id.rtn_store_selected);
        rtnstoreselected.setText(sellingstorename);

        searchView=(MaterialSearchView)findViewById(R.id.rtn_search_view);
        searchView.setVoiceSearch(false);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try{
                    JSONObject payload=new JSONObject();
                    payload.put("orders_id",query);
                    payload.put("language_id",1);
                    new GetInvoice().execute(payload.toString());
                }
                catch (Exception e){ e.printStackTrace(); }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { return false; }
        });
    }

    private class GetInvoice extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/read_returns",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("ORDERITEMs: ",result);
            if(!String.valueOf(result).equals("null") && !String.valueOf(result).equals("") && result != null){
                try{
                    orderitems.clear();returnitemsadapter.notifyDataSetChanged();
                    JSONArray items=new JSONArray(result);
                    for (int i=0; i<items.length(); i++){
                        JSONObject item=items.getJSONObject(i);
                        int member_id=item.getInt("member_id");
                        int orderitems_id=item.getInt("orderitems_id");
                        int catentry_id=item.getInt("catentry_id");
                        double price=item.getDouble("price");
                        double quantity=item.getDouble("quantity");
                        int itemspc_id=item.getInt("itemspc_id");
                        String name=item.getString("name");
                        String symbol=item.getString("symbol");
                        int store_id=item.getInt("storeent_id");
                        int orders_id=item.getInt("orders_id");
                        OItem oItem=new OItem(member_id,orderitems_id,catentry_id,itemspc_id,name,price,quantity,symbol,store_id,orders_id);
                        orderitems.add(oItem);
                    }
                    returnitemsadapter.notifyDataSetChanged();
                }catch (Exception e){ e.printStackTrace(); }
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.returns_menu,menu);
        MenuItem item=menu.findItem(R.id.action_search);
        externalMenu=menu;
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) { searchView.closeSearch(); }
        else { super.onBackPressed(); }
    }

}