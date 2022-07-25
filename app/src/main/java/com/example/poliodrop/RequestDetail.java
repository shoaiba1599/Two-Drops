package com.example.poliodrop;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poliodrop.adapter.ChildrenAdapter;
import com.example.poliodrop.models.Request;
import com.example.poliodrop.models.children;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestDetail extends AppCompatActivity {

    DatabaseReference childrenReference;
    private RecyclerView recyclerView;
    private ChildrenAdapter childrenAdapter;
    ArrayList<children> childrenModel;
    TextView requestDate, ParentName, address, phone, noOfChild, parentApprove, workerApprove, status, workerName, lastVaccination,
            nextVaccination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        recyclerView = findViewById(R.id.children_view_Detail);
        requestDate = findViewById(R.id.tvDate_Detail);
        ParentName = findViewById(R.id.tvTitle_Detail);
        address = findViewById(R.id.tvArea_Detail);
        phone = findViewById(R.id.tvPhone_Detail);
        noOfChild = findViewById(R.id.tvChilds_Detail);
        parentApprove = findViewById(R.id.parent_approve_notApprove_Detail);
        workerApprove = findViewById(R.id.worker_approve_notApprove_Detail);
        status = findViewById(R.id.tv_status_Detail);
        workerName = findViewById(R.id.worker_name_Detail);
        lastVaccination = findViewById(R.id.last_vaccine_Detail);
        nextVaccination = findViewById(R.id.next_schedule_Detail);

        String value = getIntent().getStringExtra("requestId");

        childrenModel = new ArrayList<>();
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        childrenAdapter = new ChildrenAdapter(childrenModel, childrenReference, getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(childrenAdapter);


        if (value != null) {

            requestDetail(value);
            getUserId(value);

        } else {
            Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    // Get Request Data From FireBase

    private void requestDetail(String value) {

        childrenReference = FirebaseDatabase.getInstance().getReference("Requests").child(value);
        childrenReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Request request = snapshot.getValue(Request.class);

                    requestDate.setText("Request "+request.getDate());
                    ParentName.setText("Name: "+request.getParentName());
                    address.setText("Address: " +request.getArea()+","+request.getCity());
                    phone.setText("Contact No: "+request.getPhone());
                    noOfChild.setText("No.child: "+request.getNoChild());
                    parentApprove.setText(request.getUserApproval());
                    workerApprove.setText(request.getWorkerApproval());
                    status.setText(request.getStatus());
                    workerName.setText(request.getWorker());
                    lastVaccination.setText(request.getLastVaccination());
                    nextVaccination.setText("Date: " +request.getNextScheduleDate()+" Time: "+request.getNextScheduleTime());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error->getRequestList--" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    // Get User Id From FireBase
    private void getUserId(String value) {

        childrenReference = FirebaseDatabase.getInstance().getReference("Requests").child(value).child("userId");
        childrenReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String Uid = String.valueOf(snapshot.getValue());
                    getChildrenList(Uid);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error->getRequestList--" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    // Get List Of Children From FireBase

    private void getChildrenList(String uid) {
        childrenReference = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Children");
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
}