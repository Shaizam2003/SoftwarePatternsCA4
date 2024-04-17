package com.example.softwarepatternsca4.administrator;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.example.softwarepatternsca4.adapters.CustomerAdapter;
import com.example.softwarepatternsca4.R;
import com.example.softwarepatternsca4.models.FirebaseSingleton;
import com.example.softwarepatternsca4.models.Customer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerSearch extends Fragment {

    DatabaseReference databaseReference = FirebaseSingleton.getDatabaseReference();
    String userEmail;
    AutoCompleteTextView searchInput;
    ImageButton searchButton;
    RecyclerView recyclerView;
    ArrayAdapter<String> autoCompleteAdapter;
    CustomerAdapter customerAdapter;
    ArrayList<String> searchSuggestions = new ArrayList<>();
    ArrayList<Customer> searchResults = new ArrayList<>();

    public CustomerSearch() {
        // Required empty public constructor
    }

    public CustomerSearch(String email) {
        this.userEmail = email;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_search, container, false);
        populateSearchSuggestions();
        customerAdapter = new CustomerAdapter(searchResults, requireContext());
        searchInput = view.findViewById(R.id.searchBar);
        searchButton = view.findViewById(R.id.searchButton);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(customerAdapter);

        autoCompleteAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, searchSuggestions);
        searchInput.setAdapter(autoCompleteAdapter);

        searchInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            searchResults.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Customer user = dataSnapshot.getValue(Customer.class);
                                String name = dataSnapshot.child("name").getValue(String.class);
                                String email = dataSnapshot.child("email").getValue(String.class);
                                if (selectedOption.equalsIgnoreCase(user.getCustomerName()) || selectedOption.replace(".", "-").equalsIgnoreCase(user.getCustomerEmail()) && !user.getCustomerAdmin()) {
                                    searchResults.add(user);
                                }
                            }
                            customerAdapter.setCustomers(searchResults);
                            customerAdapter.notifyDataSetChanged();
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

    private void populateSearchSuggestions() {
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String potentialEmail = dataSnapshot.getKey();
                        String potentialName = dataSnapshot.child("name").getValue(String.class);
                        Boolean isAdmin = dataSnapshot.child("admin").getValue(Boolean.class);
                        if (isAdmin) {
                            break;
                        } else if (searchSuggestions != null) {
                            if (!searchSuggestions.contains(potentialName)) {
                                searchSuggestions.add(potentialName);
                            }
                            if (!searchSuggestions.contains(potentialEmail)) {
                                searchSuggestions.add(potentialEmail.replace("-", "."));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
