package xyz.drugger.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import xyz.drugger.app.R;
import xyz.drugger.app.pojo.OItem;

public class ReturnItemsAdapter extends RecyclerView.Adapter<ReturnItemsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<OItem> items;

    public ReturnItemsAdapter(Context ctx, ArrayList<OItem>itms){ context=ctx;items=itms; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.return_order_item,parent,false);
        ViewHolder productViewHolder = new ViewHolder(view);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OItem orderitem=items.get(position);
        holder.returnitemquantity.setText("Quantity Purchased: "+orderitem.getQuantity());
        holder.returnitemname.setText(orderitem.getName());
        DecimalFormat formatter=new DecimalFormat("#,###,###.##");
        holder.returnitemprice.setText(orderitem.getSymbol()+formatter.format(orderitem.getPrice()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView returnitemname,returnitemprice,returnitemquantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            returnitemname=itemView.findViewById(R.id.return_item_name);
            returnitemprice=itemView.findViewById(R.id.return_item_price);
            returnitemquantity=itemView.findViewById(R.id.return_item_quantity);
        }
    }
}
