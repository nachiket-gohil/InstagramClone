package com.nachiket.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private Context mContext;

    private TextInputLayout inUsername, inName, inEmail, inPassword;
    private TextInputEditText etUsername, etName, etEmail, etPassword;
    private MaterialButton btnRegister;
    private TextView toLogin;
    private ProgressBar progressBar;
    private String username, name, email, password;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: Started...");
        mContext = RegisterActivity.this;
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = mAuth.getCurrentUser();

        inUsername = findViewById(R.id.input_username);
        etUsername = findViewById(R.id.et_reg_username);
        inName = findViewById(R.id.input_name);
        etName = findViewById(R.id.et_reg_name);
        inEmail = findViewById(R.id.input_email);
        etEmail = findViewById(R.id.et_reg_email);
        inPassword = findViewById(R.id.input_pw);
        etPassword = findViewById(R.id.et_reg_password);
        btnRegister = findViewById(R.id.btn_ca);
        toLogin = findViewById(R.id.text_to_login);
        progressBar = findViewById(R.id.register_progress);
        progressBar.setVisibility(View.GONE);

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: text to Login clicked");
                startActivity(new Intent(mContext, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Register button clicked..");
                username = etUsername.getText().toString();
                name = etName.getText().toString();
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                if (username.isEmpty()) {
                    inUsername.setError("Username is required");
                } else {
                    inUsername.setErrorEnabled(false);
                }
                if (name.isEmpty()) {
                    inName.setError("Name is required");
                } else {
                    inName.setErrorEnabled(false);
                }
                if (email.isEmpty()) {
                    inEmail.setError("Email is required");
                } else {
                    inEmail.setErrorEnabled(false);
                }
                if (password.isEmpty()) {
                    inPassword.setError("Password required");
                } else if (password.length() < 6) {
                    inPassword.setError("Password must contain more than 6 chars");
                } else {
                    inPassword.setErrorEnabled(false);
                }
                if (password.length() > 6) {
                    Log.d(TAG, "onClick: Account creation process started...");
                    createAccount(username, name, email, password);
                }
            }
        });
    }

    private void createAccount(final String username, final String name, final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("username", username);
                    map.put("name", name);
                    map.put("email", email);
                    map.put("password", password);
                    map.put("id", firebaseUser.getUid());
                    map.put("bio", "");
                    map.put("imageurl", "default");
                    Log.d(TAG, "onSuccess: added data to the database.");

                    databaseReference.child("Users").child(firebaseUser.getUid()).setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "onComplete: Account Creation complete..");
                                if (task.isSuccessful()) {
                                    Toast.makeText(mContext, "Account created!!!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(mContext, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to create user!!!!");
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
