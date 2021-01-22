package xyz.drugger.app.pojo;

public class State {
    private String stateprovabbr;
    private String name;
    private int language_id;

    public String getStateprovabbr(){return stateprovabbr;}
    public String getName(){return name;}
    public int getLanguage_id(){return language_id;}

    public void setStateprovabbr(String stateprovabbr){this.stateprovabbr=stateprovabbr;}
    public void setLanguage_id(int language_id){this.language_id=language_id;}
    public void setName(String name){this.name=name;}
}
