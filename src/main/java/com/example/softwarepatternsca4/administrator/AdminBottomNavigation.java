package com.example.softwarepatternsca4.administrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.softwarepatternsca4.R;
import com.example.softwarepatternsca4.customer.ProductBrowse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminBottomNavigation extends AppCompatActivity {
    private AddStock addStock;
    private ProductBrowse productBrowse;
    private CustomerSearch customerSearch;

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_bottom_navigation);
        userEmail = getIntent().getStringExtra("EMAIL");
        addStock = new AddStock(userEmail);
        productBrowse = new ProductBrowse(userEmail);
        customerSearch = new CustomerSearch(userEmail);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Set the default selected fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                addStock).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    // Check which navigation item is selected and assign the corresponding fragment
                    switch (item.getItemId()) {
                        case R.id.navigation_add_stock:
                            selectedFragment = addStock;
                            break;
                        case R.id.navigation_product_maintenance:
                            selectedFragment = productBrowse;
                            break;
                        case R.id.navigation_customer_search:
                            selectedFragment = customerSearch;
                            break;
                    }


                    // Replace the fragment container with the selected fragment
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
