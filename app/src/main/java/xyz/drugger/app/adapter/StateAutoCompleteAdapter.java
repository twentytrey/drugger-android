package xyz.drugger.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import xyz.drugger.app.R;

import java.util.ArrayList;
import java.util.List;

import xyz.drugger.app.pojo.State;

public class StateAutoCompleteAdapter extends ArrayAdapter<State> {
    private Context context;
    private int resourceId;
    private List<State> items,tempItems,suggestions;

    public StateAutoCompleteAdapter(@NonNull Context context, int resourceId, ArrayList<State> items) {
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
            if(convertView == null){
                LayoutInflater inflater=((Activity)context).getLayoutInflater();
                view=inflater.inflate(resourceId,parent,false);
            }
            State state=getItem(position);
            TextView name=(TextView)view.findViewById(R.id.stateItemTextView);
            name.setText(state.getName());
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    @Nullable
    @Override
    public State getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount(){
        return items.size();
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @NonNull
    @Override
    public Filter getFilter(){
        return stateFilter;
    }

    private Filter stateFilter=new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue){
            State state=(State)resultValue;
            return state.getName();
        }
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if(charSequence != null){
                suggestions.clear();
                for(State state:tempItems){
                    if(state.getName().toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        suggestions.add(state);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values=suggestions;
                filterResults.count=suggestions.size();
                return filterResults;
            }else{
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence charSequence,FilterResults filterResults) {
            ArrayList<State>tempValues = (ArrayList<State>)filterResults.values;
            if(filterResults != null && filterResults.count > 0) {
                clear();
                for(State stateObj:tempValues){
                    add(stateObj);
                }
                notifyDataSetChanged();
            }else{
                clear();
                notifyDataSetChanged();
            }
        }
    };
}