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

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>{
    private ArrayList<Product>products;
    private Context context;

    public ProductsAdapter(Context ctx, ArrayList<Product>items){ this.products=items;this.context=ctx; }

    @NonNull
    @Override
    public ProductsAdapter.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        ProductsViewHolder productsViewHolder = new ProductsViewHolder(view);
        return productsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ProductsViewHolder holder, int position) {
        Product product=products.get(position);
        Glide.with(context).load(product.getFullimage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.productimage);
        holder.producttitle.setText(product.getName());
        holder.productprice.setText(product.getSymbol()+product.getSalesprice());
        holder.productcostprice.setText(product.getSymbol()+product.getCost());
        holder.productcomment.setText(product.getShortdescription());
        String shelfnumber=product.getMfpartnumber();
        if (String.valueOf(shelfnumber).equals("null") || String.valueOf(shelfnumber).equals("")){
            holder.productshelfnumber.setText("Unspecified");
        }else if (!String.valueOf(shelfnumber).equals("null") && !String.valueOf(shelfnumber).equals("")){
            holder.productshelfnumber.setText(product.getMfpartnumber());
        }
    }

    @Override
    public int getItemCount() { return products.size(); }

    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        ImageView productimage;
        TextView producttitle,productprice,productcostprice,productcomment,productshelfnumber;
        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            productimage=(ImageView)itemView.findViewById(R.id.product_image);
            producttitle=(TextView)itemView.findViewById(R.id.product_title);
            productprice=(TextView)itemView.findViewById(R.id.product_price);
            productcostprice=(TextView)itemView.findViewById(R.id.product_cost_price);
            productcomment=(TextView)itemView.findViewById(R.id.product_comment);
            productshelfnumber=(TextView)itemView.findViewById(R.id.product_shelf_number);
        }
    }
}