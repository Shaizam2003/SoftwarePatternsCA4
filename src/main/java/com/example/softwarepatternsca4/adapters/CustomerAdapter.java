package com.example.softwarepatternsca4.adapters;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsca4.R;
import com.example.softwarepatternsca4.models.FirebaseSingleton;
import com.example.softwarepatternsca4.models.Order;
import com.example.softwarepatternsca4.models.Customer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    DatabaseReference databaseReference = FirebaseSingleton.getDatabaseReference();
    private List<Customer> customerList;
    private Context context;

    public CustomerAdapter(List<Customer> customerList, Context context) {
        this.customerList = customerList;
        this.context = context;
    }

    public CustomerAdapter(ArrayList<Customer> customerList, Context context, String customerEmail, boolean b) {
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.name.setText(customer.getCustomerName());
        holder.email.setText(customer.getCustomerEmail().replace("-", "."));
        holder.address.setText(customer.getCustomerAddress().replace("/", " "));

        holder.name.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.customer_order_details);

            TextView name = dialog.findViewById(R.id.text_name);
            name.setText("Name: " + customer.getCustomerName());
            TextView address = dialog.findViewById(R.id.text_address);
            address.setText("Address: " + customer.getCustomerAddress().replace("/", " "));
            TextView email = dialog.findViewById(R.id.text_email);
            email.setText("Email: " + customer.getCustomerEmail().replace("-", "."));

            RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            ArrayList<Order> ordersList = new ArrayList<>();
            OrderAdapter ordersAdapter = new OrderAdapter(ordersList, context, customer.getCustomerEmail(), true);
            recyclerView.setAdapter(ordersAdapter);

            databaseReference.child("customers").child(customer.getCustomerEmail()).child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, snapshot.toString());
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Order order = dataSnapshot.getValue(Order.class);
                        ordersList.add(order);
                    }
                    ordersAdapter.setOrders(ordersList);
                    ordersAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to read value.", error.toException());
                }
            });

            dialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public void setCustomers(List<Customer> customers) {
        this.customerList = customers;
    }

    public void setOrders(ArrayList<Order> customerOrders) {
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView name, email, address;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            email = itemView.findViewById(R.id.text_email);
            address = itemView.findViewById(R.id.text_address);
        }
    }
}