package xyz.drugger.app.pojo;

public class OrderItem {
    public double itemcost;
    public String itemname,symbol;
    public int quantity;
    private String itemcostr;

    public OrderItem(double item_cost, String item_cost_str,String item_name,String sym,int q) {
        itemcost=item_cost;
        itemcostr=item_cost_str;
        itemname=item_name;
        symbol=sym;
        quantity=q;
    }

    public int getQuantity() { return quantity; }
    public String getSymbol() { return symbol; }
    public double getItemcost() { return itemcost; }
    public String getCostStr() { return itemcostr; }
    public String getItemname() { return itemname; }

}
