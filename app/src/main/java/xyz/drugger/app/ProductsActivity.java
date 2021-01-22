package xyz.drugger.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import xyz.drugger.app.adapter.ProductsAdapter;
import xyz.drugger.app.database.helpers.DatabaseHelper;
import xyz.drugger.app.database.models.Product;
import xyz.drugger.app.database.models.Store;
import xyz.drugger.app.helper.RecyclerTouchListener;
import xyz.drugger.app.network.PostAuthJsonParser;
import xyz.drugger.app.section.SingleChoiceDialogFragment;

public class ProductsActivity extends AppCompatActivity {
    private static final String TAG = ProductsActivity.class.getSimpleName().toString();
    private Context context;
    private ArrayList<Product> products;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String ac_token;
    private int employer;
    private int user_id;
    private ProductsAdapter productsAdapter;
    private RecyclerView itemsRecyclerView;
    private LinearLayout productStoreChoice;
    private TextView storeSelected;
    private ArrayList<String> storeNames;
    private ArrayList<Store> storesBasics;
    private String holdingstore_name;
    private int holdingstore_id;
    private Toolbar toolbar;
    MaterialSearchView searchView;
    private Menu externalMenu;
    private DatabaseHelper db;
    private Gson gson;
    private String sellingstorename;
    private int sellingstoreid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        toolbar=(Toolbar)findViewById(R.id.product_toolbar);
        setSupportActionBar(toolbar);gson=new Gson();

        context=ProductsActivity.this;
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);

        sellingstorename=sharedPreferences.getString(getString(R.string.sellingstore_name),null);
        sellingstoreid=sharedPreferences.getInt(getString(R.string.sellingstore_id),0);

        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);
        employer=sharedPreferences.getInt(getString(R.string.employer),0);
        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);

        products=new ArrayList<Product>();productsAdapter=new ProductsAdapter(context,products);
        itemsRecyclerView=(RecyclerView)findViewById(R.id.items_recycler_view);
        itemsRecyclerView.setHasFixedSize(true);
        itemsRecyclerView.setLayoutManager(new GridLayoutManager(context,2));
        itemsRecyclerView.setAdapter(productsAdapter);
        itemsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, itemsRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product product=products.get(position);
                int catentry_id=product.getCatentry_id();String catenttype_id=product.getCatenttype_id();
                double cost=product.getCost();String currency=product.getCurrency();
                String endofservicedate=product.getEndofservicedate();String expires=product.getExpires();
                String fullimage=product.getFullimage();int itemspc_id=product.getItemspc_id();
                String lastupdate=product.getLastupdate();String name=product.getName();int store_id=product.getStore_id();
                int offer_id=product.getOffer_id();String offercurrency=product.getOffercurrency();
                int owner_id=product.getOwner_id();String partnumber=product.getPartnumber();
                int published=product.getPublished();int quantity=product.getQuantity();String mfpartnumber=product.getMfpartnumber();
                double salesprice=product.getSalesprice();String shortdescription=product.getShortdescription();
                String symbol=product.getSymbol();
                final Intent intent=new Intent(context,EditProduct.class);
                intent.putExtra("catentry_id",catentry_id);intent.putExtra("catenttype_id",catenttype_id);
                intent.putExtra("currency",currency);intent.putExtra("endofservicedate",endofservicedate);
                intent.putExtra("fullimage",fullimage);intent.putExtra("itemspc_id",itemspc_id);
                intent.putExtra("language_id",1);intent.putExtra("listprice",cost);
                intent.putExtra("member_id",owner_id);intent.putExtra("mfpartnumber",mfpartnumber);
                intent.putExtra("name",name);intent.putExtra("offer_id",offer_id);
                intent.putExtra("partnumber",partnumber);intent.putExtra("published",published);
                intent.putExtra("quantity",quantity);intent.putExtra("salesprice",salesprice);
                intent.putExtra("storeent_id",store_id);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                },1000);
            }

            @Override
            public void onLongClick(View view, int position) { }
        }));

        storeSelected=(TextView)findViewById(R.id.product_store_selected);
        storeSelected.setText(sellingstorename);

        preloadproducts();getStoreItems(sellingstoreid);

        storeNames=new ArrayList<String>();storesBasics=new ArrayList<Store>();
        productStoreChoice=(LinearLayout)findViewById(R.id.product_store_choice);
/**
        productStoreChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment singleChoiceDialog=new SingleChoiceDialogFragment(context,storeNames);
                singleChoiceDialog.setCancelable(false);
                singleChoiceDialog.show(getSupportFragmentManager(),"Single Choice Dialog");
            }
        });
*/
        searchView=(MaterialSearchView)findViewById(R.id.product_search_view);
        searchView.setVoiceSearch(false);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                // filter items based on search
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() { }

            @Override
            public void onSearchViewClosed() { }
        });
    }

    protected void preloadproducts() {
        db=new DatabaseHelper(getApplicationContext());
        products.clear();productsAdapter.notifyDataSetChanged();
        ArrayList<Product>productitems=db.readproducts();
        Log.e(TAG,"PREVIOUSLY SAVED: "+productitems.size());
        products.addAll(productitems);productsAdapter.notifyDataSetChanged();
    }

    private void getStoreItems(int ssid) {
        try{
            JSONObject payload=new JSONObject();
            payload.put("owner_id",employer);
            payload.put("store_id",ssid);
            payload.put("language_id",1);
            //Log.e(TAG,payload.toString());
            new GetStoreItems().execute(payload.toString());
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private class GetStoreItems extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/items_for_store",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null){
                activateProductsRecycler(result);
            }
        }
    }

    private void activateProductsRecycler(String result) {
        try{
            products.clear();productsAdapter.notifyDataSetChanged();
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
                products.add(product1);

                int exists=db.readproduct(catentry_id);
                Log.e(TAG,"PRODUCT EXISTS: "+exists);
                if (exists > 0){
                    int pid=db.updateProduct(product1);
                    Log.e(TAG,name+" previously saved: "+pid);
                }else if (exists <= 0){
                    db.createproduct(product1);
                }
            }
            productsAdapter.notifyDataSetChanged();
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.catgroup_menu,menu);
        MenuItem item=menu.findItem(R.id.action_search);
        externalMenu=menu;searchView.setMenuItem(item);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int Id=item.getItemId();
        switch (Id){
            case R.id.add_catalog:
                startActivity(new Intent(context,AddCatalog.class));
                break;
            case R.id.add_category:
                startActivity(new Intent(context,AddCategory.class));
                break;
            case R.id.add_product:
                startActivity(new Intent(context,AddProductActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
