package xyz.drugger.app.database.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.CursorAdapter;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.sql.SQLInput;
import java.util.ArrayList;

import xyz.drugger.app.database.models.Product;
import xyz.drugger.app.database.models.Store;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG="DatabaseHelper";

    //dbversion
    private static final int DATABASE_VERSION=1;

    //dbname
    private static final String DATABASE_NAME="drugger";

    //tablenames
    public static final String TABLE_STORE="storestable";
    public static final String TABLE_PRODUCTS="productstable";

    //store columns
    private static final String STOREENTID="storeent_id";
    private static final String IDENTIFIER="identifier";
    private static final String ADDRESS="address";

    //product columns
    private static final String CATENTRYID="catentry_id";
    private static final String OWNERID="owner_id";
    private static final String ITEMSPCID="itemspc_id";
    private static final String CATENTTYPEID="catenttype_id";
    private static final String PARTNUMBER="partnumber";
    private static final String LASTUPDATE="lastupdate";
    private static final String ENDOFSERVICEDATE="endofservicedate";
    private static final String EXPIRES="expires";
    private static final String NAME="name";
    private static final String SHORTDESCRIPTION="shortdescription";
    private static final String FULLIMAGE="fullimage";
    private static final String PUBLISHED="published";
    private static final String CURRENCY="currency";
    private static final String SYMBOL="symbol";
    private static final String COST="cost";
    private static final String CATGROUPID="catgroup_id";
    private static final String CATALOGID="catalog_id";
    private static final String CATEGORY="category";
    private static final String OFFERID="offer_id";
    private static final String SALESPRICE="salesprice";
    private static final String OFFERCURRENCY="offercurrency";
    private static final String QUANTITY="quantity";

    //create statements
    private static final String CREATE_STORE="CREATE TABLE "+TABLE_STORE+"("+STOREENTID+" INTEGER PRIMARY KEY, "+
            IDENTIFIER+" VARCHAR(254), "+ADDRESS+" VARCHAR(512));";
    private static final String CREATE_PRODUCT="CREATE TABLE "+TABLE_PRODUCTS+"("+CATENTRYID+" INTEGER PRIMARY KEY, "+
            OWNERID+" INTEGER, "+ITEMSPCID+" INTEGER, "+CATENTTYPEID+" VARCHAR(16), "+PARTNUMBER+" VARCHAR(64), "+
            LASTUPDATE+" VARCHAR(60), "+ENDOFSERVICEDATE+ " VARCHAR(60), "+EXPIRES+" VARCHAR(60), "+NAME+" VARCHAR(128), "+
            SHORTDESCRIPTION+" VARCHAR(512), "+FULLIMAGE+" VARCHAR(255), "+PUBLISHED+" INTEGER, "+CURRENCY+" CHAR(3), "+
            SYMBOL+" CHAR(16), "+COST+" DECIMAL(20,5), "+CATGROUPID+" CHAR(16), "+CATALOGID+" CHAR(16), "+CATEGORY+
            " VARCHAR(255), "+OFFERID+" INTEGER, "+SALESPRICE+" DECIMAL(20,5), "+OFFERCURRENCY+" CHAR(3), "+QUANTITY+
            " INTEGER);";

    public DatabaseHelper(Context ctx) {
        super(ctx,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create required tables
        db.execSQL(CREATE_STORE);
        db.execSQL(CREATE_PRODUCT);
        Log.e(LOG,"called oncreate dbhelper");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_STORE);
        Log.e(LOG,"called onupgrade dbhelper");
        //create new tables
        onCreate(db);
    }
    public long createstore(Store store) {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(STOREENTID,store.getStoreent_id());
        values.put(IDENTIFIER,store.getIdentifier());
        values.put(ADDRESS,store.getAddress());
        long store_id=db.insert(TABLE_STORE,null,values);
        return store_id;
    }
    public int updateStore(Store store){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(STOREENTID,store.getStoreent_id());
        values.put(IDENTIFIER,store.getIdentifier());
        values.put(ADDRESS,store.getAddress());
        return db.update(TABLE_STORE,values,STOREENTID+" = ?",new String[]{String.valueOf(store.getStoreent_id())});
    }
    public int readstore(long store_id){
        SQLiteDatabase db=this.getReadableDatabase();Store store=new Store();
        String selectquery="SELECT * FROM "+TABLE_STORE+" WHERE "+STOREENTID+" = "+store_id;
        Cursor c=db.rawQuery(selectquery,null);
        try{
            int count=c.getCount();
            Log.e(LOG,"count : "+count+" store_id: "+store_id);
            c.close();
            return count;
        } finally {
            c.close();
        }
    }
    public ArrayList<Store>readstores(){
        ArrayList<Store>stores=new ArrayList<Store>();
        String selectquery="SELECT * FROM "+TABLE_STORE;
        Log.e(LOG,selectquery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c=db.rawQuery(selectquery,null);
        if (c.moveToFirst()){
            do{
                Store store=new Store();
                store.setStoreent_id(c.getInt(c.getColumnIndex(STOREENTID)));
                store.setIdentifier(c.getString(c.getColumnIndex(IDENTIFIER)));
                store.setAddress(c.getString(c.getColumnIndex(ADDRESS)));
                stores.add(store);
            }
            while (c.moveToNext());
        }
        return stores;
    }
    public int updateProduct(Product product){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(CATENTRYID,product.getCatentry_id());
        values.put(OWNERID,product.getOwner_id());
        values.put(ITEMSPCID,product.getItemspc_id());
        values.put(CATENTTYPEID,product.getCatenttype_id());
        values.put(PARTNUMBER,product.getPartnumber());
        values.put(LASTUPDATE,product.getLastupdate());
        values.put(ENDOFSERVICEDATE,product.getEndofservicedate());
        values.put(EXPIRES,product.getExpires());
        values.put(NAME,product.getName());
        values.put(SHORTDESCRIPTION,product.getShortdescription());
        values.put(FULLIMAGE,product.getFullimage());
        values.put(PUBLISHED,product.getPublished());
        values.put(CURRENCY,product.getCurrency());
        values.put(SYMBOL,product.getSymbol());
        values.put(COST,product.getCost());
        values.put(CATGROUPID,product.getCatgroup_id());
        values.put(CATALOGID,product.getCatalog_id());
        values.put(CATEGORY,product.getCategory());
        values.put(OFFERID,product.getOffer_id());
        values.put(SALESPRICE,product.getSalesprice());
        values.put(OFFERCURRENCY,product.getOffercurrency());
        values.put(QUANTITY,product.getQuantity());
        return db.update(TABLE_PRODUCTS,values,CATENTRYID+" = ?",new String[]{String.valueOf(product.getCatentry_id())});
    }
    public long createproduct(Product product){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(CATENTRYID,product.getCatentry_id());
        values.put(OWNERID,product.getOwner_id());
        values.put(ITEMSPCID,product.getItemspc_id());
        values.put(CATENTTYPEID,product.getCatenttype_id());
        values.put(PARTNUMBER,product.getPartnumber());
        values.put(LASTUPDATE,product.getLastupdate());
        values.put(ENDOFSERVICEDATE,product.getEndofservicedate());
        values.put(EXPIRES,product.getExpires());
        values.put(NAME,product.getName());
        values.put(SHORTDESCRIPTION,product.getShortdescription());
        values.put(FULLIMAGE,product.getFullimage());
        values.put(PUBLISHED,product.getPublished());
        values.put(CURRENCY,product.getCurrency());
        values.put(SYMBOL,product.getSymbol());
        values.put(COST,product.getCost());
        values.put(CATGROUPID,product.getCatgroup_id());
        values.put(CATALOGID,product.getCatalog_id());
        values.put(CATEGORY,product.getCategory());
        values.put(OFFERID,product.getOffer_id());
        values.put(SALESPRICE,product.getSalesprice());
        values.put(OFFERCURRENCY,product.getOffercurrency());
        values.put(QUANTITY,product.getQuantity());
        long product_id=db.insert(TABLE_PRODUCTS,null,values);
        return product_id;
    }

    public int readproduct(long catentry_id){
        SQLiteDatabase db=this.getReadableDatabase();Product p=new Product();
        String selectquery="SELECT * FROM "+TABLE_PRODUCTS+" WHERE "+CATENTRYID+" = "+catentry_id;
        Cursor c=db.rawQuery(selectquery,null);
        try{
            int count=c.getCount();
            Log.e(LOG,"count : "+count+" catentry_id: "+catentry_id);
            c.close();return count;
        } finally {
            c.close();
        }
    }

    public ArrayList<Product>readproducts(){
        ArrayList<Product>products=new ArrayList<Product>();
        String selectquery="SELECT * FROM "+TABLE_PRODUCTS;
        Log.e(LOG,selectquery);
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c=db.rawQuery(selectquery,null);
        Log.e(LOG,"counts: "+c.getCount());
        if (c.moveToFirst()){
            do{
                Product p=new Product();
                p.setCatentry_id(c.getInt(c.getColumnIndex(CATENTRYID)));
                p.setOwner_id(c.getInt(c.getColumnIndex(OWNERID)));
                p.setItemspc_id(c.getInt(c.getColumnIndex(ITEMSPCID)));
                p.setCatenttype_id(c.getString(c.getColumnIndex(CATENTTYPEID)));
                p.setPartnumber(c.getString(c.getColumnIndex(PARTNUMBER)));
                p.setEndofservicedate(c.getString(c.getColumnIndex(ENDOFSERVICEDATE)));
                p.setExpires(c.getString(c.getColumnIndex(EXPIRES)));
                p.setName(c.getString(c.getColumnIndex(NAME)));
                p.setShortdescription(c.getString(c.getColumnIndex(SHORTDESCRIPTION)));
                p.setFullimage(c.getString(c.getColumnIndex(FULLIMAGE)));
                p.setPublished(c.getInt(c.getColumnIndex(PUBLISHED)));
                p.setCurrency(c.getString(c.getColumnIndex(CURRENCY)));
                p.setSymbol(c.getString(c.getColumnIndex(SYMBOL)));
                p.setCost(c.getDouble(c.getColumnIndex(COST)));
                p.setCatgroup_id(c.getString(c.getColumnIndex(CATGROUPID)));
                p.setCatalog_id(c.getString(c.getColumnIndex(CATALOGID)));
                p.setCategory(c.getString(c.getColumnIndex(CATEGORY)));
                p.setOffer_id(c.getInt(c.getColumnIndex(OFFERID)));
                p.setSalesprice(c.getDouble(c.getColumnIndex(SALESPRICE)));
                p.setOffercurrency(c.getString(c.getColumnIndex(OFFERCURRENCY)));
                p.setQuantity(c.getInt(c.getColumnIndex(QUANTITY)));
                products.add(p);
            }
            while (c.moveToNext());
        }
        return products;
    }

    public void closeDB(@Nullable SQLiteDatabase db){
        if (db==null)db=this.getReadableDatabase();
        if (db!=null && db.isOpen()){ db.close(); }
    }
    public void deleteDB(Context context){
        SQLiteDatabase db=this.getReadableDatabase();
        closeDB(db);
        boolean done=context.deleteDatabase(DATABASE_NAME);
        if (done){Log.e(LOG,"successfully deleted database "+DATABASE_NAME);}
        else if (!done){Log.e(LOG,"failed to delete database "+DATABASE_NAME);}
    }
}
