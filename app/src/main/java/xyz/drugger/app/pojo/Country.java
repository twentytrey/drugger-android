package xyz.drugger.app.pojo;

public class Country {
    private String countryabbr;
    private int lang_id;
    private String name;
    private String callingcode;

    public String getCountryabbr(){return countryabbr;}
    public int getLang_id(){return lang_id;}
    public String getName(){return name;}
    public String getCallingcode(){return callingcode;}

    public void setCountryabbr(String countryabbr){this.countryabbr=countryabbr;}
    public void setLang_id(int lang_id){this.lang_id=lang_id;}
    public void setName(String name){this.name=name;}
    public void setCallingcode(String callingcode){this.callingcode=callingcode;}
}
