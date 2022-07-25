package com.example.poliodrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poliodrop.adapter.ChildrenAdapter;
import com.example.poliodrop.models.USer;
import com.example.poliodrop.models.children;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RequestsActivity extends AppCompatActivity {

    EditText edtChildName, editChildAge;
    String saveCurrentDate, saveCurrentTime;
    Button btnSendRequest, btnAddChild;
    DatabaseReference childrenReference;
    TextView childNumber;
    String selectedDate;
    private RecyclerView recyclerView;
    private ChildrenAdapter childrenAdapter;
    ArrayList<children> childrenModel;

    long maxid = 0;
    long addMaxid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        edtChildName = findViewById(R.id.edtChildName);
        editChildAge = findViewById(R.id.edtChildAge);
        btnSendRequest = findViewById(R.id.btnSendRequest);
        btnAddChild = findViewById(R.id.btnAddChild);
        recyclerView = findViewById(R.id.children_view);
        childNumber = findViewById(R.id.number_child);

        childrenReference = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Children");

        childrenModel = new ArrayList<>();
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        childrenAdapter = new ChildrenAdapter(childrenModel, childrenReference, getApplicationContext(), 0);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(childrenAdapter);


        childrenCount();


        btnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String ChildName = edtChildName.getText().toString().trim();
                String ChildAge = editChildAge.getText().toString().trim();
                if (ChildName.isEmpty() || ChildAge.isEmpty()) {
                    Toast.makeText(RequestsActivity.this, "Please fill child Fields or submit Request with Current Children", Toast.LENGTH_LONG).show();
                } else if (Integer.parseInt(ChildAge) > 5) {
                    Toast.makeText(RequestsActivity.this, "Child age must be less then or equal to 5", Toast.LENGTH_LONG).show();

                } else {
                    addChildren();
                    childrenCount();

                }
            }
        });
        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                saveCurrentDate = currentDate.format(calendar.getTime());

                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                saveCurrentTime = currentTime.format(calendar.getTime());

                selectedDate = "Date: " + saveCurrentDate + " Time: " + saveCurrentTime;

                final USer[] user = new USer[1];
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            user[0] = snapshot.getValue(USer.class);
                            saveRequest(user[0]);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        getChildrenList();

    }


    private void addChildren() {
        Map<String, Object> map = new HashMap<>();
        addMaxid = ++maxid;

        String ChildId = ("Child " + addMaxid);

        map.put("name", edtChildName.getText().toString());
        map.put("age", editChildAge.getText().toString());
        map.put("childNumber", ChildId);

        childrenReference.child(ChildId).setValue(map);

        Toast.makeText(getApplicationContext(), "Add Child successfully!", Toast.LENGTH_LONG).show();

        startActivity(getIntent());
        finish();


    }

    // Send Request data to FireBase DataBase

    private void saveRequest(USer user) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests");
        Map<String, Object> map = new HashMap<>();
        String key = reference.push().getKey();
        map.put("userId", user.getId());
        map.put("phone", user.getPhone());
        map.put("email", user.getEmail());
        map.put("parentName", user.getName());
        map.put("area", user.getArea());
        map.put("city", user.getCity());
        map.put("assignedId", "NA");
        map.put("isAssigned", "NA");
        map.put("status", "In process");
        map.put("userApproval", "Not Vaccinated");
        map.put("workerApproval", "Not Vaccinated");
        map.put("status", "NA");
        map.put("noChild", String.valueOf(maxid));
        map.put("date", selectedDate);
        map.put("requestId", key);
        map.put("worker", "NA");
        map.put("nextScheduleDate", "NA");
        map.put("nextScheduleTime","NA");
        map.put("lastVaccination", "NA");
        reference.child(key).setValue(map);
        Toast.makeText(getApplicationContext(), "Request submitted successfully!", Toast.LENGTH_LONG).show();
        finish();

    }

    private void getChildrenList() {

        childrenReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        children childrenList = snapshot1.getValue(children.class);
                        childrenModel.add(childrenList);
                    }

                    childrenAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error->getRequestList--" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void childrenCount() {

        childrenReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                maxid = (snapshot.getChildrenCount());
                if (maxid == 0) {
                    btnSendRequest.setVisibility(View.GONE);
                }
                childNumber.setText(String.valueOf(maxid));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(RequestsActivity.this, "error", Toast.LENGTH_SHORT).show();

            }
        });
    }
}