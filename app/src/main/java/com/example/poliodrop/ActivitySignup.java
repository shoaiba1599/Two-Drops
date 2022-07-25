package com.example.poliodrop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ActivitySignup extends AppCompatActivity {


    EditText txtEmail,txtPassword,txtConfirmPassword,txtName,txtPhone, txtNicNumber;
    ProgressDialog progressDialog;
    FirebaseAuth maAuth;
    RadioGroup radioGroup;
    int checkedRadio;
    Spinner edtArea,edtCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        maAuth= FirebaseAuth.getInstance();
        edtArea=findViewById(R.id.edtArea);
        edtCity=findViewById(R.id.edtCity);
        radioGroup=findViewById(R.id.radioRole);
        txtEmail=findViewById(R.id.edtEmail);
        txtNicNumber=findViewById(R.id.edtNic);
        txtPassword=findViewById(R.id.edtPassword);
        txtConfirmPassword=findViewById(R.id.edtConfirmPassword);
        txtName=findViewById(R.id.edtName);
        txtPhone=findViewById(R.id.edtPhone);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Registration...");
        progressDialog.setMessage("Please wait...");
        findViewById(R.id.linkSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivitySignup.this,MainActivity.class));
            }
        });

        findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=txtEmail.getText().toString();
                String nic=txtNicNumber.getText().toString();
                String password=txtPassword.getText().toString();
                String confirmPassword=txtConfirmPassword.getText().toString();
                String name=txtName.getText().toString();
                String phone=txtPhone.getText().toString();
                // Set Validations
                if (email.length()<=0)
                    txtEmail.setError("Required!");
               else if (nic.length()<=0)
                    txtNicNumber.setError("Required!");
                else if(password.length()<=0)
                    txtPassword.setError("Required!");
                else if(confirmPassword.length()<=0)
                    txtConfirmPassword.setError("Required!");
                else if(!(password.equals(confirmPassword)))
                    txtConfirmPassword.setError("Password don't matched.");
                else if(name.length()<=0)
                    txtName.setError("Required!");
                else if(phone.length()<=0)
                    txtPhone.setError("Required!");
                else
                    register(email,password,name,phone,nic);
            }
        });
    }

    // Send data to firebase Auth

    private void register(final String email, String password, final String name, final String phone, final String nic) {
        progressDialog.show();
        maAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    String role = null;
                    String id=maAuth.getCurrentUser().getUid().toString();
                    if( radioGroup.getCheckedRadioButtonId()==R.id.radioWorker)
                        role="Worker";
                    else if( radioGroup.getCheckedRadioButtonId()==R.id.radioUser)
                        role="User";

                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child(role);
                    Map<String,Object> map=new HashMap<>();
                    map.put("email",email);
                    map.put("name",name);
                    map.put("phone",phone);
                    map.put("nic",nic);
                    map.put("activation","not activated");
                    map.put("city",edtCity.getSelectedItem().toString());
                    map.put("area",edtArea.getSelectedItem().toString());
                    map.put("id",id);
                    reference.child(id).updateChildren(map);
                    Toast.makeText(getApplicationContext(),"Registration success",Toast.LENGTH_SHORT).show();
                    SharedPref.setUserType(role,getApplicationContext());
                    if (role.toUpperCase().equals("WORKER"))
                         startActivity(new Intent(ActivitySignup.this, WorkerDashboard.class));
                    else if(role.toUpperCase().equals("USER"))
                        startActivity(new Intent(ActivitySignup.this,UserDashboard.class));

                    finish();
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}