package com.example.poliodrop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkerDashboard extends AppCompatActivity {

    private LinearLayout workerItems;
    private TextView adminApprove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_dashboard);

        workerItems = findViewById(R.id.worker_items);
        adminApprove = findViewById(R.id.admin_approve);

        findViewById(R.id.contactSupport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WorkerDashboard.this,ContactActivity.class));

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Worker").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
              ;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String checkActivation = snapshot.child("activation").getValue().toString();
                    if (checkActivation.equals("activated")) {
                        workerItems.setVisibility(View.VISIBLE);
                        adminApprove.setVisibility(View.GONE);
                    }

                    else{
                        workerItems.setVisibility(View.GONE);
                        adminApprove.setVisibility(View.VISIBLE);
                    }
                }

                else{}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        findViewById(R.id.cardMyRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WorkerDashboard.this, RequestList.class));
            }
        });

        findViewById(R.id.scheduleRequestsWorker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RequestList.class);
                intent.putExtra("clickId","schedule");
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                //perform any action;
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(WorkerDashboard.this, MainActivity.class));
                SharedPref.getInstance(getApplicationContext()).setUserType("0", getApplicationContext());
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}