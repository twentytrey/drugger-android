package xyz.drugger.app.database.models;

public class Product {
    int catentry_id;int owner_id;int itemspc_id;String catenttype_id;String partnumber;String lastupdate;
    String endofservicedate;String expires;String name;String shortdescription;String fullimage;int published;
    String currency;String symbol;double cost;String catgroup_id;String catalog_id;String category;int offer_id;
    double salesprice;String offercurrency;int quantity;String mfpartnumber;int store_id;
    private String storename;

    public Product(){}
//    public Product(int ctentry_id,int ownr_id,int itmspc_id,String ctenttype_id,String prtnumber,String lstupdate,
//                   String endservicedate,String expres,String nme,String shrtdescription,String fllimage,int pblished,
//                   String curr, String sym,double costprice,String ctgroup_id,String ctlog_id,String ctgory,int offr_id,
//                   double slsprice,String offrcurrency,int qty){
//
//    }

    public Product(String catenttyp_id,String partnmber,String lstupdate,String endservicedate,String expres,String nme,
                   String shrtdescription,String fllimage,String crrency,String ctgroup_id,String ctgory,String ctlog_id,
                   String offcurr,String sym,int ctentry_id,int ownr_id,int itmspc_id,int pblished, int offr_id,double cst,
                   double slsprice,int qty,String mfprtnumber,int stor_id){
        this.catenttype_id=catenttyp_id;this.partnumber=partnmber;this.lastupdate=lstupdate;this.endofservicedate=endservicedate;
        this.expires=expres;this.name=nme;this.shortdescription=shrtdescription;this.fullimage=fllimage;this.currency=crrency;
        this.catgroup_id=ctgroup_id;this.category=ctgory;this.catalog_id=ctlog_id;this.offercurrency=offcurr;
        this.catentry_id=ctentry_id;this.owner_id=ownr_id;this.itemspc_id=itmspc_id;this.published=pblished;this.offer_id=offr_id;
        this.cost=cst;this.salesprice=slsprice;this.symbol=sym;this.quantity=qty;this.mfpartnumber=mfprtnumber;this.store_id=stor_id;
    }

    public void setCatalog_id(String catalog_id) {
        this.catalog_id = catalog_id;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setCatentry_id(int catentry_id) {
        this.catentry_id = catentry_id;
    }
    public void setCatenttype_id(String catenttype_id) { this.catenttype_id = catenttype_id; }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public void setCatgroup_id(String catgroup_id) {
        this.catgroup_id = catgroup_id;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
    public void setEndofservicedate(String endofservicedate) { this.endofservicedate = endofservicedate; }
    public void setExpires(String expires) {
        this.expires = expires;
    }
    public void setFullimage(String fullimage) { this.fullimage = fullimage; }
    public void setItemspc_id(int itemspc_id) {
        this.itemspc_id = itemspc_id;
    }
    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }
    public void setPartnumber(String partnumber) {
        this.partnumber = partnumber;
    }
    public void setOffer_id(int offer_id) {
        this.offer_id = offer_id;
    }
    public void setPublished(int published) {
        this.published = published;
    }
    public void setSalesprice(double salesprice) {
        this.salesprice = salesprice;
    }
    public void setShortdescription(String shortdescription) { this.shortdescription = shortdescription; }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public void setOffercurrency(String offercurrency) {
        this.offercurrency = offercurrency;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setMfpartnumber(String mfpartnumber) { this.mfpartnumber = mfpartnumber; }
    public void setStore_id(int store_id) { this.store_id = store_id; }
    public int getStore_id() { return store_id; }
    public String getMfpartnumber() { return mfpartnumber; }
    public String getShortdescription() {
        return shortdescription;
    }
    public String getName() {
        return name;
    }
    public String getLastupdate() {
        return lastupdate;
    }
    public String getExpires() {
        return expires;
    }
    public String getEndofservicedate() {
        return endofservicedate;
    }
    public String getOffercurrency() {
        return offercurrency;
    }
    public String getCatgroup_id() {
        return catgroup_id;
    }
    public String getCategory() {
        return category;
    }
    public String getCatalog_id() {
        return catalog_id;
    }
    public int getPublished() {
        return published;
    }
    public int getOffer_id() {
        return offer_id;
    }
    public String getCatenttype_id() {
        return catenttype_id;
    }
    public double getCost() {
        return cost;
    }
    public double getSalesprice() {
        return salesprice;
    }
    public String getCurrency() {
        return currency;
    }
    public int getCatentry_id() {
        return catentry_id;
    }
    public int getItemspc_id() {
        return itemspc_id;
    }
    public int getOwner_id() {
        return owner_id;
    }
    public String getFullimage() {
        return fullimage;
    }
    public String getPartnumber() {
        return partnumber;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getSymbol() {
        return symbol;
    }
    public String getStorename() { return storename; }
    public void setStorename(String storname){ this.storename=storname; }
}
