package com.example.poliodrop.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.poliodrop.Interface.ItemClickListner;
import com.example.poliodrop.R;

public class ChildrenViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView childName, childAge, childNumber;
    private ItemClickListner listner;
    public Button childDelete;


    public ChildrenViewHolder(View itemView) {
        super(itemView);


        childName = (TextView) itemView.findViewById(R.id.child_name);
        childAge = (TextView) itemView.findViewById(R.id.child_age);
        childNumber = (TextView) itemView.findViewById(R.id.childNumber);
        childDelete = (Button) itemView.findViewById(R.id.btn_child_delete);

    }

    public void setItemClickListner(ItemClickListner listner) {
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }

}
