package xyz.drugger.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import xyz.drugger.app.R;
import xyz.drugger.app.database.models.Product;
import xyz.drugger.app.pojo.POSProduct;

public class POSItemsAdapter extends RecyclerView.Adapter<POSItemsAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<Product>items;
    public POSItemsAdapter(Context context, ArrayList<Product> storeItems) { ctx=context;items=storeItems; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.pos_item_layout,parent,false);
        ViewHolder productViewHolder = new ViewHolder(view);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product posProduct=items.get(position);
        Glide.with(ctx).load(posProduct.getFullimage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.itemimagepos);
        holder.itemnamepos.setText(posProduct.getName());
        holder.itempricepos.setText(posProduct.getSymbol()+""+(posProduct.getSalesprice()));
        holder.itemcommentpos.setText(posProduct.getQuantity()+ " in stock");
        String shelfnumber=posProduct.getMfpartnumber();
        if (String.valueOf(shelfnumber).equals("null") || String.valueOf(shelfnumber).equals("")){
            holder.shelfnumberpos.setText("Shelf Unspecified");
        }else if (!String.valueOf(shelfnumber).equals("null") && !String.valueOf(shelfnumber).equals("")){
            holder.shelfnumberpos.setText("Shelf "+posProduct.getMfpartnumber());
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemimagepos;
        TextView itemnamepos,itempricepos,itemcommentpos,shelfnumberpos;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemnamepos=itemView.findViewById(R.id.item_name_pos);
            itempricepos=itemView.findViewById(R.id.item_price_pos);
            itemcommentpos=itemView.findViewById(R.id.item_comment_pos);
            itemimagepos=itemView.findViewById(R.id.item_image_pos);
            shelfnumberpos=itemView.findViewById(R.id.shelf_number_pos);
        }
    }
}
