package xyz.drugger.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import xyz.drugger.app.R;
import xyz.drugger.app.pojo.OrderItem;

public class OrderItemsAdapter extends ArrayAdapter<OrderItem> {

    private ArrayList<OrderItem>orderItems;
    private Activity context;

    public OrderItemsAdapter(Activity ctx,ArrayList<OrderItem>items){
        super(ctx, R.layout.orderitems_item,items);
        this.orderItems=items;
        context=ctx;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.orderitems_item,null,true);

        TextView itemname=(TextView)rowView.findViewById(R.id.item_name);
        TextView itemqty=(TextView)rowView.findViewById(R.id.item_qty);
        TextView itemcost=(TextView)rowView.findViewById(R.id.item_cost);

        itemname.setText(orderItems.get(position).getItemname());
        itemqty.setText("Purchased "+orderItems.get(position).getQuantity());
        itemcost.setText(orderItems.get(position).getCostStr());

        return rowView;
    }
}
