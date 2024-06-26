package com.example.softwarepatternsca4.customer;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsca4.R;
import com.example.softwarepatternsca4.adapters.ProductAdapter;
import com.example.softwarepatternsca4.administrator.CategoryObserver;
import com.example.softwarepatternsca4.administrator.ManufacturerObserver;

import com.example.softwarepatternsca4.models.FirebaseSingleton;
import com.example.softwarepatternsca4.models.Product;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductBrowse extends Fragment implements ProductAdapter.CartUpdateListener, CategoryObserver, ManufacturerObserver, TitleObserver {

    DatabaseReference databaseReference = FirebaseSingleton.getDatabaseReference();
    String customerEmail;
    AutoCompleteTextView searchBar;
    ImageButton searchButton;
    Spinner sortBy;
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    ArrayAdapter arrayAdapter;
    ArrayList<String> searchField = new ArrayList<>();
    ArrayList<Product> searchResults = new ArrayList<>();
    List<CategoryObserver> categoryObservers = new ArrayList<>();
    List<ManufacturerObserver> manufacturerObservers = new ArrayList<>();
    List<TitleObserver> titleObservers = new ArrayList<>();

    public ProductBrowse(){
        // Required empty public constructor
    }

    public ProductBrowse(String customerEmail){
        this.customerEmail = customerEmail;
    }

    public ProductBrowse(String customerEmail, boolean b) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_browse, container, false);
        categoryObservers.add(this);
        manufacturerObservers.add(this);
        titleObservers.add(this);
        populateSuggestions();
        searchBar = view.findViewById(R.id.searchBar);
        searchBar.setText("");
        searchButton = view.findViewById(R.id.searchButton);
        sortBy = view.findViewById(R.id.sortBy);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        setupAdapters();

        sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                sortResults(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                databaseReference.child("product").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            searchResults.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Product product = dataSnapshot.getValue(Product.class);
                                Log.d(TAG, product.toString());
                                String category = dataSnapshot.child("category").getValue(String.class);
                                String manufacturer = dataSnapshot.child("manufacturer").getValue(String.class);
                                String title = dataSnapshot.child("title").getValue(String.class);
                                Log.d(TAG, category  + " " + manufacturer + " " + title);
                                if (selectedOption.equalsIgnoreCase(product.getCustomCategory()) || selectedOption.equalsIgnoreCase(product.getCustomManufacturer()) ||
                                        selectedOption.equalsIgnoreCase(product.getCustomTitle())) {
                                    searchResults.add(product);
                                    Log.d(TAG, "New Product Added: " + searchResults.toString());
                                }
                            }

                            productAdapter.setProducts(searchResults);
                            productAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        return view;
    }

    //Method to Initialise and Assign the Relevant Array Adapters for the AutocompleteTextView ,RecyclerView and Spinner
    private void setupAdapters(){
        productAdapter = new ProductAdapter(new ArrayList<>(), requireContext(), customerEmail, this);
        recyclerView.setAdapter(productAdapter);

        arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, searchField);
        searchBar.setAdapter(arrayAdapter);

        List<String> sortTypes = Arrays.asList("Sort By", "Title - Descending", "Title - Ascending", "Price - Descending", "Price - Ascending");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sortTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBy.setAdapter(adapter);
    }

    //Pulling Unique Categories, Manufacturers, and Titles from Firebase
    private void populateSuggestions() {
        databaseReference.child("product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String potentialCategory = dataSnapshot.child("category").getValue(String.class);
                        String potentialManufacturer = dataSnapshot.child("manufacturer").getValue(String.class);
                        String potentialTitle = dataSnapshot.child("title").getValue(String.class);
                        if (potentialCategory != null) {
                            notifyCategoryObservers(potentialCategory);
                        }
                        if (potentialManufacturer != null) {
                            notifyManufacturerObservers(potentialManufacturer);
                        }
                        if (potentialTitle != null) {
                            notifyTitleObservers(potentialTitle);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Depending on the Option Chosen in Spinner, a different set of functionality is performed
    private void sortResults(String selectedOption) {
        SortProductStrategy strategy = null;
        if ("Title - Descending".equals(selectedOption)) {
            strategy = new ProductTitleDescendingSortStrategy();
        } else if ("Title - Ascending".equals(selectedOption)) {
            strategy = new ProductTitleAscendingSortStrategy();
        } else if ("Price - Descending".equals(selectedOption)) {
            strategy = new ProductPriceDescendingSortStrategy();
        } else if ("Price - Ascending".equals(selectedOption)) {
            strategy = new ProductPriceAscendingSortStrategy();
        }
        if (strategy != null) {
            strategy.sort(searchResults);
            productAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCartUpdate() {
        // Handle cart update event if needed
    }

    //Method to Notify all Category Observers
    private void notifyCategoryObservers(String category) {
        for (CategoryObserver observer : categoryObservers) {
            observer.onCategoryAdded(category);
        }
    }

    //Method to Notify all Manufacturer Observers
    private void notifyManufacturerObservers(String manufacturer) {
        for (ManufacturerObserver observer : manufacturerObservers) {
            observer.onManufacturerAdded(manufacturer);
        }
    }

    //Method to Notify all Title Observers
    private void notifyTitleObservers(String title) {
        for (TitleObserver observer : titleObservers) {
            observer.onTitleAdded(title);
        }
    }

    //When a Category is Added to the Array, update the RecyclerView
    @Override
    public void onCategoryAdded(String category) {
        if (!searchField.contains(category)) {
            searchField.add(category);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    //When a Manufacturer is Added to the Array, update the RecyclerView
    @Override
    public void onManufacturerAdded(String manufacturer) {
        if (!searchField.contains(manufacturer)) {
            searchField.add(manufacturer);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    //When a Title is Added to the Array, update the RecyclerView
    @Override
    public void onTitleAdded(String title) {
        if (!searchField.contains(title)) {
            searchField.add(title);
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
