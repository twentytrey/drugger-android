package xyz.drugger.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.ETC1;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import xyz.drugger.app.network.PostAuthJsonParser;

public class AddCatalog extends AppCompatActivity implements View.OnClickListener {
    private EditText editcatalogdescription;
    private EditText editcatalogname;
    private Context context;
    private SharedPreferences sharedPreferences;
    private int user_id;
    private int employer;
    private String ac_token;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_catalog);

        context=AddCatalog.this;
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        user_id=sharedPreferences.getInt(getString(R.string.user_id),0);
        employer=sharedPreferences.getInt(getString(R.string.employer),0);
        ac_token=sharedPreferences.getString(getString(R.string.access_token),null);

        editcatalogname=(EditText)findViewById(R.id.edit_catalog_name);
        editcatalogdescription=(EditText)findViewById(R.id.edit_catalog_description);
        saveButton=(Button)findViewById(R.id.submit_catalog);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_catalog:
                saveCatalog();
                break;
        }
    }

    private void saveCatalog() {
        String catalogname=editcatalogname.getText().toString();
        String catalogdescription=editcatalogdescription.getText().toString();
        try{
            JSONObject payload=new JSONObject();
            payload.put("member_id",employer);
            payload.put("identifier",catalogname);
            payload.put("description",catalogdescription);
            payload.put("language_id",1);
            new SubmitCatalog().execute(payload.toString());
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private class SubmitCatalog extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/create_catalog",strings[0],ac_token);
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
                    if (status.equals("OK")){ Toast.makeText(context, message, Toast.LENGTH_SHORT).show(); }
                    else if (status.equals("ERR")){ Toast.makeText(context, message, Toast.LENGTH_SHORT).show(); }
                }
                catch (Exception e){ e.printStackTrace(); }
            }
        }
    }
}