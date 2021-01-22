package xyz.drugger.app.database.models;

public class Store {
    int storeent_id;
    String identifier;
    String address;

    public Store(){}
    public Store(int store_id,String name,String addr){
        this.storeent_id=store_id;
        this.identifier=name;
        this.address=addr;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setStoreent_id(int storeent_id) {
        this.storeent_id = storeent_id;
    }

    public void setAddress(String address) { this.address = address; }

    public int getStoreent_id() {
        return storeent_id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getAddress() { return address; }
}
