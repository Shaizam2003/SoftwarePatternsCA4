package com.example.softwarepatternsca4.customer;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwarepatternsca4.R;
import com.example.softwarepatternsca4.adapters.CustomerAdapter;
import com.example.softwarepatternsca4.models.FirebaseSingleton;
import com.example.softwarepatternsca4.models.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyOrders extends Fragment {

    DatabaseReference databaseReference = FirebaseSingleton.getDatabaseReference();
    String customerEmail;
    Spinner sortBy;
    RecyclerView recyclerView;
    CustomerAdapter customerOrdersAdapter;
    ArrayList<Order> customerOrders = new ArrayList<>();
    OrderSortingStrategy sortingStrategy;

    public MyOrders(){
        // Required empty public constructor
    }

    public MyOrders(String customerEmail){
        this.customerEmail = customerEmail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        customerOrders.clear();
        sortBy = view.findViewById(R.id.sortBy);
        recyclerView = view.findViewById(R.id.recyclerView);
        customerOrdersAdapter = new CustomerAdapter(new ArrayList<>(), requireContext(), customerEmail, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(customerOrdersAdapter);

        populateRecyclerView();
        setupSortingSpinner();

        return view;
    }

    //Contains all sorting functionality
    private void setupSortingSpinner() {
        List<String> sortTypes = Arrays.asList("Sort By", "Date - Descending", "Date - Ascending", "Price - Descending", "Price - Ascending");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sortTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBy.setAdapter(adapter);

        sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                setSortingStrategy(selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //Depending on the Option Chosen in Spinner, a different set of functionality is performed
    private void setSortingStrategy(String selectedOption) {
        if ("Date - Descending".equals(selectedOption)) {
            sortingStrategy = new OrderDateDescendingSortingStrategy();
        } else if ("Date - Ascending".equals(selectedOption)) {
            sortingStrategy = new OrderDateAscendingSortingStrategy();
        } else if ("Price - Descending".equals(selectedOption)) {
            sortingStrategy = new OrderPriceDescendingSortingStrategy();
        } else if ("Price - Ascending".equals(selectedOption)) {
            sortingStrategy = new OrderPriceAscendingSortingStrategy();
        }
        if (sortingStrategy != null) {
            sortingStrategy.sort(customerOrders);
            customerOrdersAdapter.notifyDataSetChanged();
        }
    }

    //Retrieves all of the User's Orders
    public void populateRecyclerView(){
        databaseReference.child("users").child(customerEmail).child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, snapshot.toString());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Order order = dataSnapshot.getValue(Order.class);
                    customerOrders.add(order);
                }
                Log.d(TAG, customerOrders.toString());
                customerOrdersAdapter.setOrders(customerOrders);
                customerOrdersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

