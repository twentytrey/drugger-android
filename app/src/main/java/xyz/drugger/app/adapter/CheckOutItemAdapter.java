package xyz.drugger.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import xyz.drugger.app.R;
import xyz.drugger.app.database.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CheckOutItemAdapter extends RecyclerView.Adapter<CheckOutItemAdapter.CheckOutViewHolder> {
    private Context context;
    private List<Product> cartItems;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onIncreaseQuantity(int position);
        void onDecreaseQuantity(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public CheckOutItemAdapter(Context ctx, List<Product> cartItems){
        this.context=ctx;
        this.cartItems=cartItems;
    }

    @NonNull
    @Override
    public CheckOutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item_layout,parent,false);
        CheckOutViewHolder checkOutViewHolder = new CheckOutViewHolder(view,mListener);
        return checkOutViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutViewHolder holder, int position) {
        Product product=cartItems.get(position);
        Glide.with(context).load(product.getFullimage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.itemimagechk);
        holder.itemnamechk.setText(product.getName());
        holder.itemdescchk.setText(product.getShortdescription());
        holder.itempricechk.setText(product.getSymbol()+(product.getSalesprice()));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CheckOutViewHolder extends RecyclerView.ViewHolder {
        ImageView itemimagechk;
        TextView itemnamechk,itemdescchk,itempricechk;
        AppCompatImageButton increasecheckoutitembtn,decreasecheckoutitembtn,removecheckoutitembtn;
        public CheckOutViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemimagechk=itemView.findViewById(R.id.item_image_chk);
            itemnamechk=itemView.findViewById(R.id.item_name_chk);
            itemdescchk=itemView.findViewById(R.id.item_desc_chk);
            itempricechk=itemView.findViewById(R.id.item_price_chk);

            increasecheckoutitembtn=itemView.findViewById(R.id.increase_checkout_item_btn);
            decreasecheckoutitembtn=itemView.findViewById(R.id.decrease_checkout_item_btn);
            removecheckoutitembtn=itemView.findViewById(R.id.remove_checkout_item_btn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position=getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            removecheckoutitembtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position=getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            increasecheckoutitembtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position=getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onIncreaseQuantity(position);
                        }
                    }
                }
            });
            decreasecheckoutitembtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position=getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onDecreaseQuantity(position);
                        }
                    }
                }
            });
        }
    }
}