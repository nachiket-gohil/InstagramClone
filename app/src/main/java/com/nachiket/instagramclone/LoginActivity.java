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
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Context mContext;

    private TextInputLayout inEmail, inPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView toRegister;
    private ProgressBar progressBar;
    private String email, password;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Started..");
        mContext = LoginActivity.this;
        mAuth = FirebaseAuth.getInstance();

        inEmail = findViewById(R.id.input_email);
        etEmail = findViewById(R.id.et_login_email);
        inPassword = findViewById(R.id.input_pw);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        toRegister = findViewById(R.id.text_to_ca);
        progressBar = findViewById(R.id.login_progress);
        progressBar.setVisibility(View.GONE);

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: text to Login clicked");
                startActivity(new Intent(mContext, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Login button clicked..");
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
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
                    signIn(email, password);
                }
            }
        });

    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onComplete: Signed in Success!!!");
                        Toast.makeText(mContext, "Authentication Successful!!!", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            startActivity(new Intent(mContext, MainActivity.class));
                            finish();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If sign in fails, display a message to the user.
                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "onFailure: Authentication failed: "+ e.getMessage());
                Toast.makeText(LoginActivity.this, "Authentication failed."+ e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            startActivity(new Intent(mContext, MainActivity.class));
//            finish();
//        }
//    }
}
