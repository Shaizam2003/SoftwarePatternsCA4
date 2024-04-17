package com.example.softwarepatternsca4.customer;

import static android.content.ContentValues.TAG;

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
import android.widget.Toast;

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
    public interface CartUpdateListener {
        void onCartUpdate();
    }

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
    private List<Product> productList;
    private Context context;
    String customerEmail;
    private CartUpdateListener cartUpdateListener;

    public ProductAdapter(List<Product> productList, Context context, String customerEmail, CartUpdateListener cartUpdateListener) {
        this.productList = productList;
        this.context = context;
        this.customerEmail = customerEmail;
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
        holder.title.setText(product.getCustomTitle());
        holder.manufacturer.setText(product.getCustomManufacturer());
        holder.price.setText("€" + product.getCustomPrice());
        Picasso.get().load(product.getCustomImage()).into(holder.imageView);

        databaseReference.child("users").child(customerEmail).child("shoppingCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        String productKey = productSnapshot.child("key").getValue(String.class);
                        if (product.getCustomKey().equalsIgnoreCase(productKey)) {
                            holder.inCart = true;
                            break;
                        } else {
                            holder.inCart = false;
                        }
                    }
                    Log.d(TAG, "InCart Status: " + holder.inCart.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.imageView.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_view_product);

            ImageView imageView = dialog.findViewById(R.id.productImage);
            TextView title = dialog.findViewById(R.id.productTitle);
            TextView manufacturer = dialog.findViewById(R.id.productManufacturer);
            TextView price = dialog.findViewById(R.id.productPrice);
            Button addToCart = dialog.findViewById(R.id.addToCart);
            RatingBar rating = dialog.findViewById(R.id.productRating);

            // Set product details
            Picasso.get().load(product.getCustomImage()).into(imageView);
            title.setText(product.getCustomTitle());
            manufacturer.setText(product.getCustomManufacturer());
            price.setText("€" + product.getCustomPrice());

            // Set add to cart button text based on cart status
            if (holder.inCart) {
                addToCart.setText("Remove From Cart");
            } else {
                addToCart.setText("Add To Cart");
            }

            // Add or remove product from cart on button click
            addToCart.setOnClickListener(v1 -> {
                if (!holder.inCart) {
                    // Add product to cart
                    databaseReference.child("users").child(customerEmail).child("shoppingCart").push().setValue(product);
                    holder.inCart = true;
                    addToCart.setText("Remove From Cart");
                    Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    // Remove product from cart
                    databaseReference.child("users").child(customerEmail).child("shoppingCart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Product cartProduct = dataSnapshot.getValue(Product.class);
                                if (cartProduct.getCustomKey().equals(product.getCustomKey())) {
                                    dataSnapshot.getRef().removeValue();
                                    holder.inCart = false;
                                    addToCart.setText("Add To Cart");
                                    Toast.makeText(context, "Product removed from cart", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
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

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, manufacturer, price;
        Boolean inCart = false;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            manufacturer = itemView.findViewById(R.id.manufacturer);
            price = itemView.findViewById(R.id.price);
        }
    }
}

