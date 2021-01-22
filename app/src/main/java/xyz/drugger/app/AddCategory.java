package xyz.drugger.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import xyz.drugger.app.network.PostAuthJsonParser;
import xyz.drugger.app.pojo.Catalog;

public class AddCategory extends AppCompatActivity implements View.OnClickListener {
    private EditText editcatgroupdescription;
    private MaterialSpinner selectcatalogspinner;
    private EditText editcatgroupname;
    private Button submitCategoryBtn;
    private String[] catalogNames;
    private ArrayList<Catalog> catalogItems;
    private ArrayList<String>catalog_names;
    private SharedPreferences sharedPreferences;
    private Context context;
    private String ac_token;
    private int user_id;
    private int employer;
    private String selectedName;
    private int selectedIndex;
    private Catalog selectedCatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        context=AddCategory.this;
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);
        employer=sharedPreferences.getInt(getString(R.string.employer),0);
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);
        catalog_names=new ArrayList<String>();catalogItems=new ArrayList<Catalog>();

        editcatgroupname=(EditText)findViewById(R.id.edit_catgroup_name);
        selectcatalogspinner=(MaterialSpinner)findViewById(R.id.select_catalog_spinner);
        editcatgroupdescription=(EditText)findViewById(R.id.edit_catgroup_description);
        submitCategoryBtn=(Button)findViewById(R.id.submit_catgroup);
        submitCategoryBtn.setOnClickListener(this);

        try{
            JSONObject payload=new JSONObject();
            payload.put("member_id",employer);
            payload.put("language_id",1);
            new GetCatalogs().execute(payload.toString());
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_catgroup:
                submitCategory();
                break;
        }
    }

    private void submitCategory() {
        try{
            JSONObject payload=new JSONObject();
            payload.put("member_id",employer);
            payload.put("language_id",1);
            payload.put("identifier",editcatgroupname.getText().toString());
            payload.put("description",editcatgroupdescription.getText().toString());
            payload.put("published",1);
            payload.put("parent_catalog",selectedCatalog.getCatalogID());
            new SubmitCategory().execute(payload.toString());
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private class SubmitCategory extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/create_category",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if(result != null){
                try{
                    JSONObject response=new JSONObject(result);
                    String status=response.getString("status");
                    String message=response.getString("msg");
                    if (status.equals("OK")){ Toast.makeText(context, message, Toast.LENGTH_SHORT).show(); }
                    else if (status.equals("ERR")){ Toast.makeText(context, message, Toast.LENGTH_SHORT).show(); }
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }

    private class GetCatalogs extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/read_catalogs",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (result != null){
                Log.e("response > ",result);
                try{
                    JSONArray jsonArray=new JSONArray(result);
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        int catalog_id=jsonObject.getInt("catalog_id");
                        String name=jsonObject.getString("name");
                        String description=jsonObject.getString("description");
                        catalog_names.add(name);
                        Catalog catalog=new Catalog(catalog_id,name,description);
                        catalogItems.add(catalog);
                    }
                    catalogNames=getStringArray(catalog_names);
                    selectcatalogspinner.setItems(catalogNames);
                    if (catalogNames.length > 0) {
                        selectedName=catalogNames[0];
                        selectedIndex=0;
                    }
                    selectcatalogspinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                            selectedName=catalogNames[position];
                            selectedCatalog=catalogItems.get(position);
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
}