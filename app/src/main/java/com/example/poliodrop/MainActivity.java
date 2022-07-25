package com.example.poliodrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    ProgressDialog progressDialog;
    FirebaseAuth maAuth;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maAuth = FirebaseAuth.getInstance();
        txtEmail = findViewById(R.id.edtEmail);
        txtPassword = findViewById(R.id.edtPassword);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Authenticating...");
        progressDialog.setMessage("Please wait...");

        if (maAuth.getCurrentUser() != null) {
            if (SharedPref.getInstance(getApplicationContext()).getUserType(getApplicationContext()).toUpperCase().equals("USER")) {
                startActivity(new Intent(MainActivity.this, UserDashboard.class));
                finish();
            } else if (SharedPref.getUserType(getApplicationContext()).toUpperCase().equals("WORKER")) {
                startActivity(new Intent(MainActivity.this, WorkerDashboard.class));
                finish();
            } else if (SharedPref.getUserType(getApplicationContext()).toUpperCase().equals("ADMIN")) {
                startActivity(new Intent(MainActivity.this, AdminDashboard.class));
                finish();
            }
        }
        findViewById(R.id.linkSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ActivitySignup.class));
            }
        });
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();
                if (email.length() <= 0)
                    txtEmail.setError("Required!");
                else if (password.length() <= 0)
                    txtPassword.setError("Required!");
                else
                    signIn(email, password);
            }
        });
    }

    private void signIn(String email, String password) {
        progressDialog.show();
        final String[] id = new String[1];
        maAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    id[0] = task.getResult().getUser().getUid();
                    Toast.makeText(getApplicationContext(), "Search Admin", Toast.LENGTH_SHORT).show();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Admin").child(id[0]);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                role = "Admin";
                                ExecuteRole(role);
                            } else {
                                Toast.makeText(getApplicationContext(), "Search Workers", Toast.LENGTH_SHORT).show();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Worker").child(id[0]);
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            role = "Worker";
                                            ExecuteRole(role);

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Search User", Toast.LENGTH_SHORT).show();

                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(id[0]);
                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        role = "User";
                                                        ExecuteRole(role);
                                                    } else {

                                                        progressDialog.dismiss();

                                                        Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),error.toString() , Toast.LENGTH_LONG).show();

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Invalid Login credentials",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    // Set execution Role

    private void ExecuteRole(String role) {
        SharedPref.getInstance(getApplicationContext()).setUserType(role, getApplicationContext());
        if (role.toUpperCase().equals("USER")) {
            progressDialog.dismiss();

            Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, UserDashboard.class));

        } else if (role.toUpperCase().equals("WORKER")) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, WorkerDashboard.class));

        } else if (role.toUpperCase().equals("ADMIN")) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, AdminDashboard.class));

        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Invalid Login credential", Toast.LENGTH_SHORT).show();

        }
    }
}