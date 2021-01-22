package xyz.drugger.app.pojo;

public class OItem {
    private int member_id,orderitems_id,catentry_id,itemspc_id,storeent_id;
    private String name,symbol;
    private double price,quantity;
    private int orders_id;

    public OItem(int mmid, int oiid, int ctid, int itmid, String nme, double prce, double qty, String sym, int store_id, int ordrs_id){
        member_id=mmid;orderitems_id=oiid;catentry_id=ctid;itemspc_id=itmid;orders_id=ordrs_id;
        name=nme;price=prce;quantity=qty;this.symbol=sym;storeent_id=store_id;
    }

    public int getOrders_id() { return orders_id; }
    public int getStoreent_id() { return storeent_id; }
    public String getSymbol() { return symbol; }
    public int getItemspc_id() { return itemspc_id; }
    public int getCatentry_id() { return catentry_id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getQuantity() { return quantity; }
    public int getMember_id() { return member_id; }
    public int getOrderitems_id() { return orderitems_id; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setName(String name) { this.name = name; }
    public void setItemspc_id(int itemspc_id) { this.itemspc_id = itemspc_id; }
    public void setCatentry_id(int catentry_id) { this.catentry_id = catentry_id; }
    public void setMember_id(int member_id) { this.member_id = member_id; }
    public void setOrderitems_id(int orderitems_id) { this.orderitems_id = orderitems_id; }
    public void setPrice(double price) { this.price = price; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setStoreent_id(int storeent_id) { this.storeent_id = storeent_id; }
    public void setOrders_id(int orders_id) { this.orders_id = orders_id; }
}
