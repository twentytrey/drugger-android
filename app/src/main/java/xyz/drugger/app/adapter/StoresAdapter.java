package xyz.drugger.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import xyz.drugger.app.R;
import xyz.drugger.app.database.models.Store;

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.StoresViewHolder> {
    private Context context;
    private ArrayList<Store>stores;

    public StoresAdapter(Context ctx, ArrayList<Store>items){
        this.context=ctx;
        this.stores=items;
    }

    @NonNull
    @Override
    public StoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.stores_item,parent,false);
        StoresViewHolder storesViewHolder=new StoresViewHolder(view);
        return storesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StoresViewHolder holder, int position) {
        Store store=stores.get(position);
        holder.storeitemname.setText(store.getIdentifier());
        holder.storeitemdescription.setText(store.getAddress());
    }

    @Override
    public int getItemCount() { return stores.size(); }

    public class StoresViewHolder extends RecyclerView.ViewHolder {
        TextView storeitemname,storeitemdescription;
        public StoresViewHolder(@NonNull View itemView) {
            super(itemView);
            storeitemname=itemView.findViewById(R.id.store_item_name);
            storeitemdescription=itemView.findViewById(R.id.store_item_description);
        }
    }
}
