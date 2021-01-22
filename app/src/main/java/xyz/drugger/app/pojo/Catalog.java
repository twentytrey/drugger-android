package xyz.drugger.app.pojo;

public class Catalog {
    int catalogID;
    String name;
    String description;
    public Catalog(int catalog_id, String catalog_name, String catalog_description){
        catalogID=catalog_id;
        name=catalog_name;
        description=catalog_description;
    }

    public int getCatalogID() { return catalogID; }
    public String getDescription() { return description; }
    public String getName() { return name; }
}
