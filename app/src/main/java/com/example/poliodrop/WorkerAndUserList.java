package com.example.poliodrop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.poliodrop.adapter.UserAdapter;
import com.example.poliodrop.models.Request;
import com.example.poliodrop.models.Worker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class WorkerAndUserList extends AppCompatActivity {

    ArrayList<Worker> workerModel;
    private RecyclerView recyclerView;
    private UserAdapter mAdapter;
    SearchView searchView;
    public static Request request=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_and_user_list);

        String jsonMyObject = null;
        Bundle extras = getIntent().getExtras();

        String value = getIntent().getStringExtra("clickId");

        if (value == null) {

            value = "assignWorker";

        } else {

            value = "worker";
        }

        if (extras != null) {
            jsonMyObject = extras.getString("myObject");
            request = new Gson().fromJson(jsonMyObject, Request.class);
        }

        workerModel=new ArrayList<>();
        searchView=findViewById(R.id.search);
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
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAdapter=new UserAdapter(workerModel,getApplicationContext(), value);
        recyclerView.setLayoutManager(mLayoutManager);
        //  postRecyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        // recyclerView.addOnItemTouchListener(new RecyclerItem);
        getUserList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
    // Get worker List from FireBase
    private void getUserList() {
        DatabaseReference referance= FirebaseDatabase.getInstance().getReference("Worker");
        referance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        Worker worker=snapshot1.getValue(Worker.class);
                        workerModel.add(worker);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error->getUserList--"+error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }
}