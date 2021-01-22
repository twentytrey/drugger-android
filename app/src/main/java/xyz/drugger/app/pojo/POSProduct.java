package xyz.drugger.app.pojo;

public class POSProduct {
    int owner_id,catentry_id,store_id,ffmcenter_id,itemspc_id,quantity;
    double costprice,price;
    String store,expirydate,partnumber,symbol,fullimage,productname;
    private String shortDescription;

    public POSProduct(int oid,int cid,int sid,int fid,int itid,int qty,String stor,String expd,String sym,String fmg,double cp,double p,String name,String pnumber){
        owner_id=oid;catentry_id=cid;store_id=sid;ffmcenter_id=fid;itemspc_id=itid;quantity=qty;
        store=stor;expirydate=expd;symbol=sym;fullimage=fmg;costprice=cp;price=p;productname=name;partnumber=pnumber;
    }

    public double getCostprice() { return costprice; }
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public int getCatentry_id() { return catentry_id; }
    public int getFfmcenter_id() { return ffmcenter_id; }
    public int getItemspc_id() { return itemspc_id; }
    public int getOwner_id() { return owner_id; }
    public int getQuantity() { return quantity; }
    public int getStore_id() { return store_id; }
    public String getExpirydate() { return expirydate; }
    public String getFullimage() { return fullimage; }
    public String getPartnumber() { return partnumber; }
    public String getStore() { return store; }
    public String getProductname() { return productname; }
    public String getShortDescription() { return shortDescription; }
    public void setQuantity(float count) { this.quantity=(int) count; }
    public void setShortDescription(String s) {
        this.shortDescription=s;
    }
}
