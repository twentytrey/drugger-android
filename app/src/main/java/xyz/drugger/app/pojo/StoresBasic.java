package xyz.drugger.app.pojo;

public class StoresBasic {
    private String identifier;
    private int storeent_id;
    public StoresBasic(){}
    public StoresBasic(String storename, int storeid){
        this.identifier=storename;
        this.storeent_id=storeid;
    }
    public int getStoreentID(){return storeent_id;}
    public String getIdentifier(){return identifier;}
}
