package xyz.drugger.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.drugger.app.R;

import java.util.ArrayList;
import java.util.List;

import xyz.drugger.app.pojo.Country;

public class CountryAutoCompleteAdapter extends ArrayAdapter<Country> {
    private Context context;
    private int resourceId;
    private List<Country> items, tempItems, suggestions;

    public CountryAutoCompleteAdapter(@NonNull Context context, int resourceId, ArrayList<Country> items) {
        super(context, resourceId, items);
        this.items=items;
        this.context=context;
        this.resourceId=resourceId;
        tempItems=new ArrayList<>(items);
        suggestions=new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=convertView;
        try{
            if(convertView==null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                view = inflater.inflate(resourceId,parent,false);
            }
            Country country=getItem(position);
            TextView name=(TextView)view.findViewById(R.id.countryItemTextView);
            name.setText(country.getName());
        }catch(Exception e){ e.printStackTrace(); }
        return view;
    }

    @Nullable
    @Override
    public Country getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    private Filter countryFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Country country=(Country)resultValue;
            return country.getName();
        }
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if(charSequence != null) {
                suggestions.clear();
                for(Country country:tempItems) {
                    if(country.getName().toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        suggestions.add(country);
                    }
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=suggestions;
                filterResults.count=suggestions.size();
                return filterResults;
            }else{
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence charSequence,FilterResults filterResults){
            ArrayList<Country> tempValues = (ArrayList<Country>) filterResults.values;
            if(filterResults != null && filterResults.count > 0){
                clear();
                for(Country countryObj : tempValues) {
                    add(countryObj);
                }
                notifyDataSetChanged();
            }else{
                clear();
                notifyDataSetChanged();
            }
        }
    };
}