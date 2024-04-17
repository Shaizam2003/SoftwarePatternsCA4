package com.example.softwarepatternsca4.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.softwarepatternsca4.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationCustomer extends AppCompatActivity {

    private ProductBrowse ProductBrowse;
    private ViewCart ViewCart;
    private MyOrders MyOrders;
    String customerEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_bottom_navigation);
        customerEmail = getIntent().getStringExtra("EMAIL");
        ProductBrowse = new ProductBrowse(customerEmail, false);
        ViewCart = new ViewCart(customerEmail);
        MyOrders = new MyOrders(customerEmail);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                ProductBrowse).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getItemId() == R.id.product_browse) {
                        selectedFragment = ProductBrowse;
                    } else if (item.getItemId() == R.id.view_cart) {
                        selectedFragment = ViewCart;
                    } else if (item.getItemId() == R.id.my_orders) {
                        selectedFragment = MyOrders;
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                        return true;
                    } else {
                        return false;
                    }
                }
            };
}

