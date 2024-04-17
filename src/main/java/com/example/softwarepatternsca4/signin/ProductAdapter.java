package com.example.softwarepatternsca4.signin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsca4.R;
import com.example.softwarepatternsca4.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
    private List<Product> productList;
    private Context context;
    private String userEmail;

    public ProductAdapter(List<Product> productList, Context context, String userEmail) {
        this.productList = productList;
        this.context = context;
        this.userEmail = userEmail;
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
        holder.titleTextView.setText(product.getCustomTitle());
        holder.manufacturerTextView.setText(product.getCustomManufacturer());
        holder.priceTextView.setText("€" + product.getCustomPrice());
        Picasso.get().load(product.getCustomImage()).into(holder.productImageView);

        databaseReference.child("users").child(userEmail).child("shoppingCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        String productKey = productSnapshot.child("key").getValue(String.class);
                        if (product.getCustomKey().equalsIgnoreCase(productKey)) {
                            holder.isInCart = true;
                            break;
                        } else {
                            holder.isInCart = false;
                        }
                    }
                    Log.d(TAG, "InCart Status: " + holder.isInCart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database Error: " + error.getMessage());
            }
        });

        holder.productImageView.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_view_product);

            ImageView imageView = dialog.findViewById(R.id.productImage);
            TextView title = dialog.findViewById(R.id.productTitle);
            TextView manufacturer = dialog.findViewById(R.id.productManufacturer);
            TextView price = dialog.findViewById(R.id.productPrice);
            Button addToCartButton = dialog.findViewById(R.id.addToCart);
            RatingBar ratingBar = dialog.findViewById(R.id.productRating);

            Picasso.get().load(product.getCustomImage()).into(imageView);
            title.setText(product.getCustomTitle());
            manufacturer.setText(product.getCustomManufacturer());
            price.setText("€" + product.getCustomPrice());
            addToCartButton.setText(holder.isInCart ? "Remove From Cart" : "Add To Cart");

            addToCartButton.setOnClickListener(v1 -> {
                if (!holder.isInCart) {
                    addToCartButton.setText("Remove From Cart");
                    databaseReference.child("users").child(userEmail).child("shoppingCart").push().setValue(product);
                    holder.isInCart = true;
                } else {
                    addToCartButton.setText("Add To Cart");
                    databaseReference.child("users").child(userEmail).child("shoppingCart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Product cartProduct = dataSnapshot.getValue(Product.class);
                                if (cartProduct.getCustomKey().equals(product.getCustomKey())) {
                                    dataSnapshot.getRef().removeValue();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Database Error: " + error.getMessage());
                        }
                    });
                    holder.isInCart = false;
                }
            });

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProducts(List<Product> products) {
        this.productList = products;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productImageView;
        TextView titleTextView, manufacturerTextView, priceTextView;
        boolean isInCart = false;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.title);
            manufacturerTextView = itemView.findViewById(R.id.text_manufacturer);
            priceTextView = itemView.findViewById(R.id.text_price);
        }
    }
}
