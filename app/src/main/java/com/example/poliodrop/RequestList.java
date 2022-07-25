package com.example.poliodrop;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poliodrop.adapter.RequestAdapter;
import com.example.poliodrop.models.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestList extends AppCompatActivity {
    ArrayList<Request> requestModel;
    private RecyclerView recyclerView;
    private RequestAdapter mAdapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        requestModel = new ArrayList<>();
        searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        String value = getIntent().getStringExtra("clickId");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter = new RequestAdapter(requestModel, getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        if (value == null) {

            getRequestList(SharedPref.getUserType(getApplicationContext()).toUpperCase());

        } else {

            scheduleMethod(SharedPref.getUserType(getApplicationContext()).toUpperCase());
        }
    }

    // Get List Of request to show in Schedule Tab From FireBase

    private void scheduleMethod(final String role) {
        final String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refrence = FirebaseDatabase.getInstance().getReference("Requests");
        Query query = refrence.orderByChild("userApproval").equalTo("Vaccinated");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Request request = snapshot1.getValue(Request.class);
                        if (role.equals("ADMIN"))
                            requestModel.add(request);
                        else if (role.equals("WORKER") && request.getAssignedId().equals(myId))
                            requestModel.add(request);
                        else if (role.equals("USER") && request.getUserId().equals(myId))
                            requestModel.add(request);
                    }
                    mAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error->getRequestList--" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    // Get RequestList Other than Schedule From FireBase

    private void getRequestList(final String role) {
        final String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refrence = FirebaseDatabase.getInstance().getReference("Requests");
        Query query = refrence.orderByChild("nextScheduleDate").equalTo("NA");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Request request = snapshot1.getValue(Request.class);
                        if (role.equals("ADMIN"))
                            requestModel.add(request);
                        else if (role.equals("WORKER") && request.getAssignedId().equals(myId))
                            requestModel.add(request);
                        else if (role.equals("USER") && request.getUserId().equals(myId))
                            requestModel.add(request);
                    }
                    mAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error->getRequestList--" + error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}