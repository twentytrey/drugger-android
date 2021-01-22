package xyz.drugger.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.acl.Owner;
import java.util.ArrayList;

import xyz.drugger.app.adapter.POSItemsAdapter;
import xyz.drugger.app.database.helpers.DatabaseHelper;
import xyz.drugger.app.database.models.Product;
import xyz.drugger.app.helper.CountDrawable;
import xyz.drugger.app.helper.RecyclerTouchListener;
import xyz.drugger.app.network.GetAuthJsonParser;
import xyz.drugger.app.network.PostAuthJsonParser;
import xyz.drugger.app.pojo.POSProduct;
import xyz.drugger.app.pojo.StoresBasic;
import xyz.drugger.app.section.SingleChoiceDialogFragment;
import xyz.drugger.app.services.AddToCustomerCart;

public class PointOfSale extends AppCompatActivity {
    MaterialSearchView searchView;
    private Toolbar toolbar;
    LinearLayout posStoreChoice;
    TextView storeSelected;
    private ArrayList<String> storeNames;
    private ArrayList<StoresBasic> storesBasics;
    Context context;
    SharedPreferences sharedPreferences;
    private int user_id,employer;
    private String ac_token;
    private ArrayList<Product>storeItems;
    private ArrayList<Product>searchedItems;
    private POSItemsAdapter adapter;
    private RecyclerView posItemsRecycler;
    private Menu externalMenu;
    private ArrayList<Product> storeItemsCopy;
    private Gson gson;
    private Integer ccID;
    private String ccName;
    private int sellingstoreid;
    private String sellingstorename;
    private static final String TAG = ProductsActivity.class.getSimpleName().toString();
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_of_sale);

        toolbar=(Toolbar)findViewById(R.id.pos_toolbar);gson=new Gson();
        setSupportActionBar(toolbar);context=PointOfSale.this;
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);
        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);
        employer=sharedPreferences.getInt(getString(R.string.employer),0);

        sellingstorename=sharedPreferences.getString(getString(R.string.sellingstore_name),null);
        sellingstoreid=sharedPreferences.getInt(getString(R.string.sellingstore_id),0);

        storeItems=new ArrayList<Product>();searchedItems=new ArrayList<Product>();

        posItemsRecycler=findViewById(R.id.pos_items_recycler_view);
        posItemsRecycler.setHasFixedSize(true);
        posItemsRecycler.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        adapter=new POSItemsAdapter(context,storeItems);
        posItemsRecycler.setAdapter(adapter);
        posItemsRecycler.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), posItemsRecycler, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product item=storeItems.get(position);
                String itemstring=(new Gson().toJson(item));
                if (item.getQuantity() > 0 && item.getSalesprice() > 0){
                    Intent intent=new Intent(context,AddToCustomerCart.class);
                    intent.putExtra(AddToCustomerCart.ITEM,itemstring);
                    startService(intent);
                }else if (item.getQuantity() <= 0){
                    Toast.makeText(context, "This item is out of stock.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) { }
        }));

        ccID=3;ccName="Cash Customer";
        preloadproducts();new GetCashCustomer().execute();

        searchView=(MaterialSearchView)findViewById(R.id.pos_search_view);
        searchView.setVoiceSearch(false);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (storeItems.size() <= 0){
                    Toast.makeText(context, "Choose a selling store before searching items.", Toast.LENGTH_SHORT).show();
                }else if (storeItems.size() > 0){
                    searchedItems.clear();storeItemsCopy=new ArrayList<Product>();
                    storeItemsCopy.addAll(storeItems);
                    storeItems.clear();
                    adapter.notifyDataSetChanged();
                    for (Product p:storeItemsCopy){
                        String itemname=p.getName();
                        if (itemname != null && !itemname.equals("") && !itemname.equals("null") && itemname.toLowerCase().contains(newText.toLowerCase())){
                            storeItems.add(p);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                if (storeItems.size() <= 0){
                    Toast.makeText(context, "Choose a selling store before searching items.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSearchViewClosed() { }
        });
        storeSelected=(TextView)findViewById(R.id.pos_store_selected);
        posStoreChoice=(LinearLayout)findViewById(R.id.pos_store_choice);
        storeSelected.setText(sellingstorename);
    }

    protected void preloadproducts() {
        db=new DatabaseHelper(getApplicationContext());
        storeItems.clear();adapter.notifyDataSetChanged();
        ArrayList<Product>productitems=db.readproducts();
         Log.e(TAG,"PREVIOUSLY SAVED: "+productitems.size());
        storeItems.addAll(productitems);adapter.notifyDataSetChanged();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            if(bundle != null){
                String cartcount=bundle.getString(AddToCustomerCart.OUTPUT);
                int resultCode=bundle.getInt(AddToCustomerCart.RESULT);
                if (resultCode==RESULT_OK){
                    setCount(externalMenu,context,cartcount);
                    Toast.makeText(context, "Item has been added to cart.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Failed to add to cart.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public void setCount(Menu defaultMenu,Context context, String count) {
        MenuItem menuItem = defaultMenu.findItem(R.id.customer_cart);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();
        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_cart_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else { badge = new CountDrawable(context); }

        badge.setCount(count);icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_cart_count, badge);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,new IntentFilter(AddToCustomerCart.NOTIFICATION));
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void getStoreItems(int ssid) {
        try{
            JSONObject payload=new JSONObject();
            payload.put("owner_id",employer);
            payload.put("store_id",ssid);
            payload.put("language_id",1);
            new GetStoreItems().execute(payload.toString());
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private class GetStoreItems extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser = new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/items_for_store",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(result != null){
                activateProductsRecycler(result);
            }
        }
    }

    private void activateProductsRecycler(String result) {
        Log.e("PRODUCTS: ",result);
        try{
            storeItems.clear();adapter.notifyDataSetChanged();
            JSONArray responses=new JSONArray(result);
            for (int i=0; i<responses.length(); i++) {
                JSONObject product=responses.getJSONObject(i);
                JSONObject catentry=product.getJSONObject("catentry");
                JSONObject inventory=product.getJSONObject("inventory");

                JSONObject offer=product.getJSONObject("pricing");
                int catentry_id=catentry.getInt("catentry_id");
                int owner_id=catentry.getInt("member_id");
                int itemspc_id=catentry.getInt("itemspc_id");
                String catenttype_id=catentry.getString("catenttype_id");
                String partnumber=catentry.getString("partnumber");
                String lastupdate=catentry.getString("lastupdate");
                String endofservicedate=catentry.getString("endofservicedate");
                String expires=catentry.getString("expires");
                String name=inventory.getString("name");
                int store_id=inventory.getInt("store_id");
                String shortdescription=catentry.getString("shortdescription");

                String fullimage=catentry.getString("fullimage");
                int published=catentry.getInt("published");
                String currency=catentry.getString("currency");
                String mfpartnumber=catentry.getString("mfpartnumber");
                String symbol=product.getString("symbol");
                double cost=catentry.getDouble("cost");
                String catgroup_id=catentry.getString("catgroup_id");
                String category=catentry.getString("category");
                String catalog_id=catentry.getString("catalog_id");

                int offer_id=offer.getInt("offer_id");
                double salesprice=offer.getDouble("price");
                String offercurrency=currency;
                int quantity=inventory.getInt("quantity");
                if (quantity==0){ shortdescription="Out of Stock"; }
                else if (quantity > 0){ shortdescription=quantity+" in stock"; }
                if(fullimage.equals("null") || fullimage.equals("")){ fullimage=getString(R.string.base_url)+getString(R.string.default_product_image); }

                Product product1 = new Product(catenttype_id,partnumber,lastupdate,endofservicedate,expires,name,shortdescription,fullimage,currency,catgroup_id,category,catalog_id,offercurrency,symbol,catentry_id,owner_id,itemspc_id,published,offer_id,cost,salesprice,quantity,mfpartnumber,store_id);
                product1.setStorename(sellingstorename);
                 storeItems.add(product1);
                Log.e("POSPRODUCTS: ",new Gson().toJson(product1));

                int exists=db.readproduct(catentry_id);
                if (exists > 0){
                    int pid=db.updateProduct(product1);
                    Log.e(TAG,name+" previously saved: "+pid);
                }else if (exists <= 0){
                    db.createproduct(product1);
                }
            }
             adapter.notifyDataSetChanged();
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private class GetCashCustomer extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            String json=null;
            try{
                GetAuthJsonParser getAuthJsonParser=new GetAuthJsonParser();
                json=getAuthJsonParser.getJsonFromUrl(getString(R.string.base_url)+"/api/v1.0/get_cc",ac_token);
            }
            catch(Exception e){e.printStackTrace();}
            return json;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                JSONObject jsonObject=new JSONObject(result);
                String customer_id=jsonObject.getString("customer_id");
                String customer_name=jsonObject.getString("customer_name");
                if(customer_id.equals("null")){ccID=null;ccName=null;}
                else{ ccID=Integer.parseInt(customer_id);ccName=customer_name; }
                getStoreItems(sellingstoreid);
            }
            catch (Exception e){ e.printStackTrace(); }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.customer_cart:
                Intent intent = new Intent(context,CheckOutActivity.class);
                intent.putExtra("store",sellingstorename);
                intent.putExtra("store_id",sellingstoreid);
                intent.putExtra("ccID",ccID);
                intent.putExtra("ccName",ccName);
//                Log.e("extras > ","store : "+sellingstorename+" ccID: "+ccID+" ccName: "+ccName);
                context.startActivity(intent);//finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item=menu.findItem(R.id.action_search);
        externalMenu=menu;
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

}
