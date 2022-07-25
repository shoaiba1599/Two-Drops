package com.example.poliodrop.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poliodrop.R;
import com.example.poliodrop.RequestsActivity;
import com.example.poliodrop.models.children;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ChildrenAdapter extends RecyclerView.Adapter<ChildrenViewHolder> {

    private List<children> children_list;
    private Context mContext;
    private DatabaseReference childrenReference;
    private int i;
    Bundle bundle=new Bundle();

    public ChildrenAdapter(List<children> children_list, DatabaseReference childrenReference, Context context, int i) {
        this.children_list = children_list;
        this.childrenReference = childrenReference;
        this.mContext=context;
        this.i = i;
    }

    @NonNull
    @Override
    public ChildrenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.children_layout, parent, false);

        return new ChildrenViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildrenViewHolder holder, int position) {
final children child = children_list.get(position);
        holder.childAge.setText(child.getAge());
        holder.childName.setText(child.getName());
        holder.childNumber.setText(child.getChildNumber());

        if (i == 1){
            holder.childDelete.setVisibility(View.GONE);
        }

        holder.childDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteChild(child.getChildNumber());
                Toast.makeText(mContext, "Child Deleted Successfully", Toast.LENGTH_SHORT).show();

                v.getContext().startActivity(new Intent(v.getContext(), RequestsActivity.class));
                ((Activity) v.getContext()).finish();

            }
        });
    }

    private void deleteChild(final String childId) {

        childrenReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.getRef().child(childId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return children_list.size();
    }
}


