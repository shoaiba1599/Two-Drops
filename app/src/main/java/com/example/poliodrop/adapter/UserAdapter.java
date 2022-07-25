package com.example.poliodrop.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poliodrop.R;
import com.example.poliodrop.RequestList;
import com.example.poliodrop.WorkerAndUserList;
import com.example.poliodrop.models.Worker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> implements Filterable, View.OnClickListener {
    private List<Worker> worker_list;
    private List<Worker> worker_filterList;
    private Context mContext;
    private String value;
    Bundle bundle = new Bundle();

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    worker_filterList = worker_list;
                } else {
                    List<Worker> filteredList = new ArrayList<>();
                    for (Worker worker : worker_list) {
                        if (worker.getName().toLowerCase().contains(charSequenceString.toLowerCase()) || worker.getArea().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(worker);
                        }
                        worker_filterList = filteredList;
                    }

                }
                FilterResults results = new FilterResults();
                results.values = worker_filterList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                worker_filterList = (List<Worker>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onClick(View view) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView Title, Area, City, contact;
        public Button btn_view;
        public CheckBox checkBox;
        public ConstraintLayout layout;
        public ImageView imageProfile;
        public CardView cardView;
        public Button btnApproval, btnDelete;

        public MyViewHolder(View view) {
            super(view);
            Title = (TextView) view.findViewById(R.id.tvTitle);
            Area = (TextView) view.findViewById(R.id.tvArea);
            City = (TextView) view.findViewById(R.id.tvCity);
            contact = (TextView) view.findViewById(R.id.tvContact);
            btnApproval = (Button) view.findViewById(R.id.btnApproval);
            cardView = (CardView) view.findViewById(R.id.cardMyProfile);
            btnDelete = (Button) view.findViewById(R.id.btnDelete);


        }
    }

    public UserAdapter(ArrayList<Worker> worker_list, Context context, String value) {
        this.worker_list = worker_list;
        this.worker_filterList = worker_list;
        this.mContext = context;
        this.value = value;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Worker worker = worker_filterList.get(position);
        holder.Title.setText("Name: " + worker.getName());
        holder.Area.setText("Area: " + worker.getArea());
        holder.City.setText("City: " + worker.getCity());
        holder.contact.setText("Contact No:" + worker.getPhone());

        if (value == "assignWorker") {
            holder.btnDelete.setVisibility(View.GONE);
        }


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteWorker(worker.getId());
                Toast.makeText(mContext, "Worker Deleted Successfully", Toast.LENGTH_SHORT).show();

                view.getContext().startActivity(new Intent(view.getContext(), WorkerAndUserList.class));
                ((Activity) view.getContext()).finish();

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Worker").child(worker.getId())
                .child("activation");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String value = snapshot.getValue().toString();

                    if (value.equals("activated")) {
                        holder.btnApproval.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.btnApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                view.getContext().startActivity(new Intent(view.getContext(), WorkerAndUserList.class));

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Worker")
                        .child(worker.getId());

                reference.child("activation").setValue("activated").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        ((Activity) view.getContext()).finish();

                        Toast.makeText(mContext, "Worker is Successfully Activated", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (WorkerAndUserList.request != null) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests").child(WorkerAndUserList.request.getRequestId());
                    WorkerAndUserList.request.setIsAssigned("Yes");
                    WorkerAndUserList.request.setStatus("Assigned");
                    WorkerAndUserList.request.setAssignedId(worker.getId());
                    WorkerAndUserList.request.setWorker(worker.getName());
                    reference.setValue(WorkerAndUserList.request);
                    Toast.makeText(view.getContext(), "Request Assigned Successfully", Toast.LENGTH_SHORT).show();

                    view.getContext().startActivity(new Intent(view.getContext(), RequestList.class));
                    ((Activity) view.getContext()).finish();

                }
            }
        });


    }

    private void deleteWorker(final String workerId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Worker").child(workerId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.getRef().removeValue();
                    removeWorkerValues();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void removeWorkerValues() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests").child(WorkerAndUserList.request.getRequestId());
        WorkerAndUserList.request.setIsAssigned("NA");
        WorkerAndUserList.request.setStatus("NA");
        WorkerAndUserList.request.setAssignedId("NA");
        WorkerAndUserList.request.setWorker("NA");
        reference.setValue(WorkerAndUserList.request);


    }

    @Override
    public int getItemCount() {
        return worker_filterList.size();
    }
}

