package com.example.softwarepatternsca4.signin;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.softwarepatternsca4.administrator.AdminBottomNavigation;
import com.example.softwarepatternsca4.customer.BottomNavigationCustomer;
import com.example.softwarepatternsca4.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("");
    private FirebaseAuth mAuth;
    private Button loginButton;
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.buttonLogin);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                String emailValue = emailEditText.getText().toString().trim();
                String passwordValue = passwordEditText.getText().toString().trim();
                if (TextUtils.isEmpty(emailValue) || TextUtils.isEmpty(passwordValue)) {
                    Toast.makeText(Login.this, "Please enter both Email and Password", Toast.LENGTH_SHORT).show();
                }
                mAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    fetchUserByEmail(emailValue);
                                } else {
                                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        TextView signUpTextView = findViewById(R.id.textViewSignUp);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });
    }

    private void fetchUserByEmail(String userEmail) {
        userEmail = userEmail.replace(".", "-");
        DatabaseReference usersRef = databaseReference.child("users").child(userEmail);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isAdmin = false;
                    DataSnapshot adminSnapshot = dataSnapshot.child("admin");
                    if (adminSnapshot.exists()) {
                        isAdmin = adminSnapshot.getValue(Boolean.class);
                        if (isAdmin) {
                            Toast.makeText(Login.this, "Admin.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), AdminBottomNavigation.class);
                            intent.putExtra("EMAIL", dataSnapshot.getKey());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Not Admin.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), BottomNavigationCustomer.class);
                            intent.putExtra("EMAIL", dataSnapshot.getKey());
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching user by email", error.toException());
            }
        });
    }

    private void showSignUpDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_registration, null);
        dialogBuilder.setView(dialogView);
        mAuth = FirebaseAuth.getInstance();

        EditText fullNameEditText = dialogView.findViewById(R.id.fullName);
        EditText passwordEditText = dialogView.findViewById(R.id.password);
        EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirmPassword);
        EditText emailAddressEditText = dialogView.findViewById(R.id.email);
        EditText shippingAddressEditText = dialogView.findViewById(R.id.shippingAddress);
        Spinner paymentMethodSpinner = dialogView.findViewById(R.id.paymentMethod);
        CheckBox adminCheckBox = dialogView.findViewById(R.id.checkBoxAdmin);

        List<String> paymentMethods = new ArrayList<>();
        paymentMethods.add("Visa");
        paymentMethods.add("Mastercard");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        dialogBuilder.setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = fullNameEditText.getText().toString().trim();
                String passwordValue = passwordEditText.getText().toString().trim();
                String confirmPasswordValue = confirmPasswordEditText.getText().toString().trim();
                String email = emailAddressEditText.getText().toString().trim().replace(".", "-");
                String address = shippingAddressEditText.getText().toString().trim().replace(" ", "/");
                String paymentMethod = paymentMethodSpinner.getSelectedItem().toString();
                boolean isAdmin = adminCheckBox.isChecked();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(passwordValue) || TextUtils.isEmpty(passwordValue)
                        || TextUtils.isEmpty(email) || TextUtils.isEmpty(address) || !passwordValue.equals(confirmPasswordValue) || !passwordContainsNumber(passwordValue)) {
                    Toast.makeText(Login.this, "Error, please check all fields and try again", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child("users").child(email).setValue(new User(name, email, address, paymentMethod, isAdmin));
                    mAuth.createUserWithEmailAndPassword(email.replace("-", "."), passwordValue)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        AlertDialog signUpDialog = dialogBuilder.create();
        signUpDialog.show();
    }

    //Method for Checking the Password Contains a Number
    private boolean passwordContainsNumber(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}

