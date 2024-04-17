package com.example.softwarepatternsca4.administrator;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsca4.R;

import com.example.softwarepatternsca4.adapters.ProductAdapter;
import com.example.softwarepatternsca4.adapters.ReviewAdapter;
import com.example.softwarepatternsca4.models.Product;
import com.example.softwarepatternsca4.models.Review;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> implements ProductAdapter.CartUpdateListener {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://rob-ca2-default-rtdb.europe-west1.firebasedatabase.app/");
    private List<Product> productList;
    private Context context;
    private String adminEmail;
    private ProductAdapter.CartUpdateListener cartUpdateListener;

    public AdminProductAdapter(List<Product> productList, Context context, String adminEmail, ProductAdapter.CartUpdateListener cartUpdateListener) {
        this.productList = productList;
        this.context = context;
        this.adminEmail = adminEmail;
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
        this.productList = products;
    }

    @Override
    public void onCartUpdate() {
        // Implementation of onCartUpdate method goes here if needed
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView title, manufacturer, price;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            manufacturer = itemView.findViewById(R.id.text_manufacturer);
            price = itemView.findViewById(R.id.text_price);
        }

        public void bindProduct(Product product) {
            title.setText(product.getCustomTitle());
            manufacturer.setText(product.getCustomManufacturer());
            price.setText("€" + product.getCustomPrice());

            Picasso.get().load(product.getCustomImage()).into(imageView);

            itemView.setOnClickListener(v -> {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_view_product_admin);
                ImageView imageView = dialog.findViewById(R.id.productImage);
                TextView title = dialog.findViewById(R.id.productTitle);
                TextView size = dialog.findViewById(R.id.size);
                TextView manufacturer = dialog.findViewById(R.id.productManufacturer);
                EditText price = dialog.findViewById(R.id.productPrice);
                EditText stock = dialog.findViewById(R.id.stockProduct);
                Button save = dialog.findViewById(R.id.saveButton);
                RatingBar rating = dialog.findViewById(R.id.productRating);

                ArrayList<Review> reviews = new ArrayList<>();
                RecyclerView recyclerViewReviews = dialog.findViewById(R.id.recyclerViewReviews);
                ReviewAdapter reviewAdapter = new ReviewAdapter(reviews, context);
                recyclerViewReviews.setAdapter(reviewAdapter);
                recyclerViewReviews.setLayoutManager(new LinearLayoutManager(context));
                reviewAdapter.setReviews(reviews);

                // Populate reviews if needed
                // populateReviews(product, reviews, rating, reviewAdapter);

                Picasso.get().load(product.getCustomImage()).into(imageView);
                title.setText(product.getCustomTitle());
                manufacturer.setText(product.getCustomManufacturer());
                price.setText("€" + product.getCustomPrice());
                size.setText("Size: " + product.getCustomSize());
                stock.setText(String.valueOf(product.getCustomStock()));

                save.setOnClickListener(v1 -> {
                    product.setCustomPrice(price.getText().toString().replace("€", "").trim());
                    product.setCustomStock(Integer.parseInt(String.valueOf(stock.getText())));
                    databaseReference.child("product").child(product.getCustomKey()).setValue(product);
                    notifyItemChanged(getAdapterPosition());
                    dialog.dismiss();
                });
                dialog.show();
            });
        }
    }
}
