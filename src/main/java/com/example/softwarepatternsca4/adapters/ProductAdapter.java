package com.example.softwarepatternsca4.adapters;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsca4.R;
import com.example.softwarepatternsca4.customer.ProductBrowse;
import com.example.softwarepatternsca4.models.FirebaseSingleton;
import com.example.softwarepatternsca4.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface CartUpdateListener {
        void onCartUpdate();
    }

    DatabaseReference databaseReference = FirebaseSingleton.getDatabaseReference();
    private final List<Product> productList;
    private final Context context;
    private final String userEmail;
    private final boolean isAdmin;
    private final CartUpdateListener cartUpdateListener;

    public ProductAdapter(List<Product> productList, Context context, String userEmail, boolean isAdmin, CartUpdateListener cartUpdateListener) {
        this.productList = productList;
        this.context = context;
        this.userEmail = userEmail;
        this.isAdmin = isAdmin;
        this.cartUpdateListener = cartUpdateListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bindProduct(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProducts(List<Product> products) {
        productList.clear();
        productList.addAll(products);
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, manufacturer, price, size;
        boolean inCart = false;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            size = itemView.findViewById(R.id.text_size);
            manufacturer = itemView.findViewById(R.id.text_manufacturer);
            price = itemView.findViewById(R.id.text_price);
        }

        public void bindProduct(Product product) {
            title.setText(product.getCustomTitle());
            manufacturer.setText(product.getCustomManufacturer());
            price.setText("€" + product.getCustomPrice());
            size.setText("Size: " + product.getCustomSize());

            Picasso.get().load(product.getCustomImage()).into(imageView);

            if (!isAdmin) {
                databaseReference.child("users").child(userEmail).child("shoppingCart").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                                String productKey = productSnapshot.child("key").getValue(String.class);
                                if (product.getCustomKey().equalsIgnoreCase(productKey)) {
                                    inCart = true;
                                    break;
                                } else {
                                    inCart = false;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to read value.", error.toException());
                    }
                });
            }

            itemView.setOnClickListener(v -> {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_view_product);

                // Setting up views and adapters

                dialog.show();
            });
        }
    }
}
