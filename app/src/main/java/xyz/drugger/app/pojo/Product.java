package xyz.drugger.app.pojo;

public class Product {
    private String catenttype_id,partnumber,lastupdate,endofservicedate,expires,name,shortdescription,fullimage,currency,catgroup_id,category,catalog_id,offercurrency,symbol;
    private int catentry_id,owner_id,itemspc_id,published,offer_id;
    private double cost,salesprice;
    public Product(String catenttyp_id,String partnmber,String lstupdate,String endservicedate,String expres,String nme,String shrtdescription,String fllimage,
                   String crrency,String ctgroup_id,String ctgory,String ctlog_id,String offcurr,String sym,
                   int ctentry_id,int ownr_id,int itmspc_id,int pblished, int offr_id,double cst, double slsprice){
        this.catenttype_id=catenttyp_id;this.partnumber=partnmber;this.lastupdate=lstupdate;this.endofservicedate=endservicedate;
        this.expires=expres;this.name=nme;this.shortdescription=shrtdescription;this.fullimage=fllimage;this.currency=crrency;
        this.catgroup_id=ctgroup_id;this.category=ctgory;this.catalog_id=ctlog_id;this.offercurrency=offcurr;
        this.catentry_id=ctentry_id;this.owner_id=ownr_id;this.itemspc_id=itmspc_id;this.published=pblished;this.offer_id=offr_id;
        this.cost=cst;this.salesprice=slsprice;this.symbol=sym;
    }
    public String getPartnumber() { return partnumber; }
    public String getFullimage() { return fullimage; }
    public int getOwner_id() { return owner_id; }
    public int getItemspc_id() { return itemspc_id; }
    public int getCatentry_id() { return catentry_id; }
    public String getCurrency() { return currency; }
    public double getCost() { return cost; }
    public double getSalesprice() { return salesprice; }
    public String getCatenttype_id() { return catenttype_id; }
    public int getOffer_id() { return offer_id; }
    public int getPublished() { return published; }
    public String getCatalog_id() { return catalog_id; }
    public String getCategory() { return category; }
    public String getCatgroup_id() { return catgroup_id; }
    public String getEndofservicedate() { return endofservicedate; }
    public String getExpires() { return expires; }
    public String getLastupdate() { return lastupdate; }
    public String getName() { return name; }
    public String getOffercurrency() { return offercurrency; }
    public String getShortdescription() { return shortdescription; }
    public String getSymbol() { return symbol; }
}

