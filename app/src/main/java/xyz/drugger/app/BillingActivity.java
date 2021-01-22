package xyz.drugger.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import xyz.drugger.app.adapter.OrderItemsAdapter;
import xyz.drugger.app.network.PostAuthJsonParser;
import xyz.drugger.app.pojo.OrderItem;

public class BillingActivity extends AppCompatActivity {

    private static int MAX_HEIGHT=1430;
    private int customer_id,orders_id,language_id;
    private String orderdate,customer_name,store;
    private Context context;
    private SharedPreferences sharedPreferences;
    private ListView receiptItemsView;
    private OrderItemsAdapter orderItemsAdapter;
    private ArrayList<OrderItem> orderItemsArrayList;
    private String ac_token;
    private int user_id;
    private int employer;
    private TextView invoicestore;
    private TextView invoiceaddress;
    private TextView invoiceorderdate;
    private  TextView invoicereceiptno;
    private ArrayList<OrderItem> oitems;
    private ListView receiptitemslist;
    private View rootView;
    private double grandtotal;
    private TextView invoicetotalval;
    private static final int REQUEST_BT=2;
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        context=BillingActivity.this;requestBluetoothPermission();
        sharedPreferences=context.getSharedPreferences(getString(R.string.preference_name),MODE_PRIVATE);
        ac_token=sharedPreferences.getString("access_token",null);
        user_id=sharedPreferences.getInt("user_id",0);
        employer=sharedPreferences.getInt("employer",0);
        oitems=new ArrayList<OrderItem>();orderItemsAdapter=new OrderItemsAdapter(this,oitems);
        receiptitemslist=(ListView)findViewById(R.id.receipt_items_list);
        receiptitemslist.setAdapter(orderItemsAdapter);

        invoicestore=findViewById(R.id.invoice_store);
        invoiceaddress=findViewById(R.id.invoice_address);
        invoiceorderdate=findViewById(R.id.invoice_orderdate);
        invoicereceiptno=findViewById(R.id.invoice_receiptno);
        invoicetotalval=(TextView)findViewById(R.id.invoice_total_val);

        Intent intent=getIntent();
        orders_id=intent.getIntExtra("orders_id",0);
        customer_name=intent.getStringExtra("customer_name");
        customer_id=intent.getIntExtra("customer_id",0);
        store=intent.getStringExtra("store");
        orderdate=intent.getStringExtra("orderdate");
        language_id=intent.getIntExtra("language_id",0);

        try{
            JSONObject payload=new JSONObject();
            payload.put("orders_id",orders_id);
            payload.put("language_id",1);
            new GetOrderItems().execute(payload.toString());
        }
        catch (Exception e){ e.printStackTrace(); }
        //testPrinter();
    }

    protected void requestBluetoothPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH},REQUEST_BT);
        }else{
            //Toast.makeText(context, "Bluetooth is necessary to print receipts.", Toast.LENGTH_SHORT).show();
        }
    }

    private class GetOrderItems extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String json=null;
            try{
                PostAuthJsonParser postAuthJsonParser=new PostAuthJsonParser();
                json=postAuthJsonParser.getJSONFromUrl(getString(R.string.base_url)+"/api/v1.0/read_orderitems",strings[0],ac_token);
            }
            catch (Exception e){ e.printStackTrace(); }
            return json;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (result != null){
                try{
                     Log.e("orderitems > ",result);
                    JSONObject jsonObject=new JSONObject(result);
                    JSONObject storedata=jsonObject.getJSONObject("store");
                    JSONArray itemsdata=jsonObject.getJSONArray("data");
                    String attendant=jsonObject.getString("attendant");
                    String datestamp=jsonObject.getString("datestamp");
                    String orderdate=jsonObject.getString("orderdate");
                    int receiptno=jsonObject.getInt("receiptno");
                    int storeent_id=storedata.getInt("storeent_id");
                    String identifier=storedata.getString("identifier");
                    String address=storedata.getString("address1");
                    String phone=storedata.getString("phone1");
                    invoicestore.setText(identifier);
                    invoiceaddress.setText(address+" "+Html.fromHtml("&bull;") +" "+phone);
                    invoiceorderdate.setText(Html.fromHtml("<b>Date</b> &bull;")+" "+orderdate);
                    invoicereceiptno.setText(String.valueOf(Html.fromHtml("<b>RCN</b>")+" "+receiptno));
                    StringBuilder stringBuilder=new StringBuilder();
                    final DecimalFormat formatter=new DecimalFormat("#,###,###");
                    for (int i=0; i<itemsdata.length(); i++) {
                        JSONObject itemdata=itemsdata.getJSONObject(i);
                        String name=itemdata.getString("name");
                        double totalproduct=itemdata.getDouble("totalproduct");
                        String totalproductstr=formatter.format(totalproduct);
                        grandtotal+=totalproduct;
                        String totalproduct_str=itemdata.getString("totalproduct_str");
                        int quantity=itemdata.getInt("quantity");
                        symbol=itemdata.getString("symbol");
                        OrderItem orderItem=new OrderItem(totalproduct,totalproduct_str,name,symbol,quantity);
                        oitems.add(orderItem);
                        stringBuilder.append("[L]\n");
                        stringBuilder.append("[L]<b>"+name+"</b>[R]"+"NGN"+" "+totalproduct_str+"\n");
                        stringBuilder.append("[L]  + Purchased : "+quantity+"\n");
                        stringBuilder.append("[L]\n");
                    }
                    orderItemsAdapter.notifyDataSetChanged();
                    String chargeText=formatter.format(grandtotal);
                    invoicetotalval.setText(chargeText);
                    Toast.makeText(context, "Print Receipt", Toast.LENGTH_SHORT).show();
                    printReceipt(receiptno,customer_name,datestamp,address,phone,symbol,chargeText,stringBuilder.toString(),attendant,orderdate);
                } catch (Exception e){ e.printStackTrace(); }
            }
        }
    }

    protected void printReceipt(int receiptno,String customername,String datestamp,String address,String phone,String symbol,String ctext,String itemstring,String attendant,String orderdate) {
        Log.e("RECEIPT: ","printing receipt...");
        try{
            EscPosPrinter printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32);
            printer.printFormattedText(
                            "[C]<font size='big'>" + store +"</font>"+
                            "[L]\n" +
                            "[C]<font size='small'>" + address+" "+Html.fromHtml("&bull;") +" "+phone+"</font>"+
                            "[L]\n" +
                            "[C]<font size='small'>" + orderdate +"</font>"+
                            "[L]\n" +
                            "[C]<u><font size='medium'>RECEIPT No."+"0"+receiptno+"</font></u>\n" +
                            "[L]\n" +
                            "[C]================================\n" +
                            itemstring +
                            "[C]--------------------------------\n" +
                            "[R]TOTAL PRICE :[R]"+"NGN"+" "+ctext+"\n" +
                            "[L]\n" +
                            "[C]================================\n" +
                            "[L]\n" +
                            "[L]<font size='tall'>Customer :</font>"+customername + "\n" +
                            "[L]<font size='medium'>Attendant :</font>"+attendant + "\n" +
                            "[L]Walk-In / In-Store\n" +
                            "[C]<barcode type='ean13' height='10'>"+datestamp+"</barcode>\n"
            );
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
