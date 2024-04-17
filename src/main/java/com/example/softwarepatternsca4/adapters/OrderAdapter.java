package com.example.softwarepatternsca4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsca4.R;
import com.example.softwarepatternsca4.models.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private Context context;
    private String userEmail;
    private boolean isAdmin;

    public OrderAdapter(List<Order> orderList, Context context, String userEmail, boolean isAdmin) {
        this.orderList = orderList;
        this.context = context;
        this.userEmail = userEmail;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.date.setText(order.getDate());
        holder.itemCount.setText(order.getProductList().size() + " item(s)");
        holder.subtotal.setText("€" + order.getSubtotal());

        holder.itemView.setOnClickListener(v -> {
            // Handle item click here if needed
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrders(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView date, itemCount, subtotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            itemCount = itemView.findViewById(R.id.item_count);
            subtotal = itemView.findViewById(R.id.price);
        }
    }
}
