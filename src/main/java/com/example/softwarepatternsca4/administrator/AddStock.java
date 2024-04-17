package com.example.softwarepatternsca4.administrator;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.softwarepatternsca4.R;
import com.example.softwarepatternsca4.models.FirebaseSingleton;
import com.example.softwarepatternsca4.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddStock extends Fragment implements CategoryObserver, ManufacturerObserver {

    private static final int PICK_IMAGE_REQUEST = 0;
    DatabaseReference databaseReference = FirebaseSingleton.getDatabaseReference();
    String userEmail, imageUrl;
    EditText productTitle, productPrice;
    AutoCompleteTextView productManufacturer;
    AutoCompleteTextView productCategory;
    ImageView productImageView;
    Button chooseImageButton, addStockButton, promotionsButton;
    Spinner productSize;
    ArrayAdapter<String> categoryArrayAdapter;
    ArrayAdapter<String> manufacturerArrayAdapter;
    ArrayList<String> categoriesList = new ArrayList<>();
    ArrayList<String> manufacturersList = new ArrayList<>();

    private List<CategoryObserver> categoryObservers = new ArrayList<>();
    private List<ManufacturerObserver> manufacturerObservers = new ArrayList<>();

    public AddStock(String userEmail) {
        this.userEmail = userEmail;
    }

    public AddStock() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_entry, container, false);
        populateCategorySuggestions();
        populateManufacturerSuggestions();
        productImageView = view.findViewById(R.id.imageView);
        categoryObservers.add(this);
        manufacturerObservers.add(this);
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/rob-ca2.appspot.com/o/productImages%2Fshopping_cart_checkout_FILL0_wght400_GRAD0_opsz24.png?alt=media&token=be46ea99-7430-4427-af1c-dd2a2a6272bf").into(productImageView);
        productTitle = view.findViewById(R.id.title);
        productPrice = view.findViewById(R.id.price);
        productCategory = view.findViewById(R.id.category);
        productManufacturer = view.findViewById(R.id.manufacturer);
        chooseImageButton = view.findViewById(R.id.chooseImageButton);
        promotionsButton = view.findViewById(R.id.loyaltyDiscounts);
        addStockButton = view.findViewById(R.id.addStock);
        productSize = view.findViewById(R.id.size);
        setupAdapters();

        addStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(productTitle.getText()) && !TextUtils.isEmpty(productManufacturer.getText()) && !TextUtils.isEmpty(productPrice.getText()) && !productSize.getSelectedItem().equals("Size") && !TextUtils.isEmpty(productCategory.getText()) && !TextUtils.isEmpty(imageUrl)) {
                    DatabaseReference newProductRef = databaseReference.child("product").push();
                    newProductRef.setValue(new Product(newProductRef.getKey(), productTitle.getText().toString().trim(),
                            productManufacturer.getText().toString().trim(), productPrice.getText().toString().trim(), productSize.getSelectedItem().toString(), productCategory.getText().toString().trim(), String.valueOf(imageUrl), 0));
                    clearAllFields();
                } else {
                    Toast.makeText(requireContext(), "Error, please check all fields and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });


        promotionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPromotionsDialog();
            }
        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPictureChooser();
            }
        });

        productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_image_preview);
                ImageView imageViewPreview = dialog.findViewById(R.id.imageViewPreview);
                Picasso.get().load(imageUrl).into(imageViewPreview);
                dialog.show();
            }
        });

        return view;
    }

    //Method to Create and Display the Promotions Dialog Box
    private void showPromotionsDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_customer_discounts);
        EditText count = dialog.findViewById(R.id.completed_count);
        EditText discount = dialog.findViewById(R.id.next_discount);
        Button save = dialog.findViewById(R.id.saveButton);
        Button clear = dialog.findViewById(R.id.clear);

        databaseReference.child("discounts").child("repeatCustomers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    count.setText(snapshot.child("count").getValue(String.class));
                    discount.setText(snapshot.child("discount").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        save.setOnClickListener(v1 -> {
            databaseReference.child("discounts").child("repeatCustomers").child("count").setValue(count.getText().toString());
            databaseReference.child("discounts").child("repeatCustomers").child("discount").setValue(discount.getText().toString());
            dialog.dismiss();
        });

        clear.setOnClickListener(v1 -> {
            databaseReference.child("discounts").child("repeatCustomers").removeValue();
            dialog.dismiss();
        });

        dialog.show();
    }

    //Method to Initialise and Assign the Relavent Array Adapters for the AutocompleteTextViews and Spinner
    private void setupAdapters() {
        categoryArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriesList);
        productCategory.setAdapter(categoryArrayAdapter);

        manufacturerArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, manufacturersList);
        productManufacturer.setAdapter(manufacturerArrayAdapter);

        List<String> sizes = Arrays.asList("Size", "S", "M", "L", "XL");
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, sizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productSize.setAdapter(sizeAdapter);
    }

    //Method to clear all the text fields
    public void clearAllFields() {
        imageUrl = "";
        productTitle.setText("");
        productManufacturer.setText("");
        productPrice.setText("");
        productCategory.setText("");
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/rob-ca2.appspot.com/o/productImages%2Fshopping_cart_checkout_FILL0_wght400_GRAD0_opsz24.png?alt=media&token=be46ea99-7430-4427-af1c-dd2a2a6272bf").into(productImageView);
        productSize.setSelection(0);
    }

    //Method to Pull all Categories from the Firebase Database
    private void populateCategorySuggestions() {
        databaseReference.child("product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String potentialCategory = dataSnapshot.child("category").getValue(String.class);
                        if (potentialCategory != null) {
                            notifyCategoryObservers(potentialCategory);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //Method to Pull all Manufacturers from the Firebase Database
    private void populateManufacturerSuggestions() {
        databaseReference.child("product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String potentialManufacturer = dataSnapshot.child("manufacturer").getValue(String.class);
                        if (potentialManufacturer != null) {
                            notifyManufacturerObservers(potentialManufacturer);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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

    //Method to Open the Native Android Gallery
    private void openPictureChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //Listener for when a Photo is Chosen - upload the data to Firebase and Display it
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("productImages");
            StorageReference imageRef = storageRef.child("image" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrl = uri.toString();
                            Log.d(TAG, "Download URL: " + imageUrl);
                            Picasso.get().load(imageUrl).into(productImageView);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Image upload failed.", e);
                    });
        }
    }

    //When a Category is Added to the Array, update the RecyclerView
    @Override
    public void onCategoryAdded(String category) {
        if (!categoriesList.contains(category)) {
            categoriesList.add(category);
            categoryArrayAdapter.notifyDataSetChanged();
        }
    }

    //When a Manufacturer is Added to the Array, update the RecyclerView
    @Override
    public void onManufacturerAdded(String manufacturer) {
        if (!manufacturersList.contains(manufacturer)) {
            manufacturersList.add(manufacturer);
            manufacturerArrayAdapter.notifyDataSetChanged();
        }
    }
}

