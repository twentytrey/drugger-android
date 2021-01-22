package xyz.drugger.app.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE=0;
    private static final String IS_FIRST_TIME_LAUNCH="isfirsttimelaunch";
    private static final String PREF_NAME="xyz.drugger.app.SHARED_PREFERENCES";

    public PrefManager(Context ctx){
        this.context=ctx;
        pref=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor=pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH,isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch(){
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH,true);
    }
}
