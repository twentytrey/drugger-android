package xyz.drugger.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import xyz.drugger.app.adapter.StoresAdapter;
import xyz.drugger.app.database.helpers.DatabaseHelper;
import xyz.drugger.app.database.models.Store;
import xyz.drugger.app.helper.RecyclerTouchListener;
import xyz.drugger.app.network.PostAuthJsonParser;

public class SelectStore extends AppCompatActivity {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String ac_token;
    private int user_id;
    private RecyclerView storesRecyclerView;
    private StoresAdapter storesAdapter;
    private int employer;
    private static String TAG=SelectStore.class.getName();
    private ArrayList<Store> storesBasics;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_store);

        context=SelectStore.this;storesBasics=new ArrayList<Store>();
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        editor=sharedPreferences.edit();
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);
        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);
        employer=sharedPreferences.getInt(getString(R.string.employer),0);

        storesRecyclerView=findViewById(R.id.store_selection_recyclerview);
        storesAdapter=new StoresAdapter(context,storesBasics);
        storesRecyclerView.setHasFixedSize(true);
        storesRecyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        storesRecyclerView.setAdapter(storesAdapter);
        storesRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, storesRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Store selectedstore=storesBasics.get(position);
                int store_id=selectedstore.getStoreent_id();
                String store_name=selectedstore.getIdentifier();
                if (store_name.toLowerCase().contains("ayobo")){
                    editor.putInt(getString(R.string.sellingstore_id),store_id);
                    editor.putString(getString(R.string.sellingstore_name),store_name);
                    boolean done=editor.commit();
                    if (done){
                        startActivity(new Intent(context,DashboardActivity.class));
                    }else{
                        Toast.makeText(context, "Failed to save selected store.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) { }
        }));
        try{
            JSONObject payload=new JSONObject();
            payload.put("member_id",employer);
            payload.put("language_id",1);
            new GetStores().execute(payload.toString());
        }
        catch (Exception e){ e.printStackTrace(); }
        preloadstores();
    }

    private void preloadstores(){
        db=new DatabaseHelper(getApplicationContext());
        storesBasics.clear();storesAdapter.notifyDataSetChanged();
//         db.deleteDB(getApplicationContext());
        ArrayList<Store>stores=db.readstores();
        storesBasics.addAll(stores);
        storesAdapter.notifyDataSetChanged();
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
                    storesBasics.clear();storesAdapter.notifyDataSetChanged();
                    JSONArray stores=new JSONArray(result);
                    storesBasics.clear();
                    for (int i=0; i<stores.length(); i++){
                        JSONObject store=stores.getJSONObject(i);
                        int storeent_id=store.getInt("storeent_id");
                        String identifier=store.getString("identifier");
                        JSONObject address=store.getJSONObject("staddress_id_loc");
                        StringBuilder builder=new StringBuilder();
                        builder.append(Html.fromHtml(" &bull; "));
                        builder.append(address.getString("address1"));builder.append(", ");
                        builder.append(address.getString("city"));builder.append(", ");
                        builder.append(address.getString("state"));builder.append(". ");
                        builder.append(address.getString("country"));
                        String description=builder.toString();
                        Store storesBasic=new Store(storeent_id,identifier,description);
                        int exists=db.readstore(storesBasic.getStoreent_id());
                        if (exists > 0){
                            int sid=db.updateStore(storesBasic);
                            Log.e(TAG,identifier+" store was previously saved: "+sid);
                        } else if (exists <= 0){
                            long made=db.createstore(storesBasic);
                        }
                        storesBasics.add(storesBasic);
                    }
                    storesAdapter.notifyDataSetChanged();
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }

    }
}