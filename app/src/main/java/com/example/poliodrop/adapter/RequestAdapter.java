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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poliodrop.R;
import com.example.poliodrop.RequestDetail;
import com.example.poliodrop.RequestList;
import com.example.poliodrop.SharedPref;
import com.example.poliodrop.UserDashboard;
import com.example.poliodrop.WorkerAndUserList;
import com.example.poliodrop.models.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> implements Filterable {
    private List<Request> request_list;
    private List<Request> request_filterList;
    private Context mContext;
    Bundle bundle = new Bundle();

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    request_filterList = request_list;
                } else {
                    List<Request> filteredList = new ArrayList<>();
                    for (Request request : request_list) {
                        if (request.getParentName().toLowerCase().contains(charSequenceString.toLowerCase()) || request.getArea().toLowerCase().contains(charSequenceString.toLowerCase())) {
                            filteredList.add(request);
                        }
                        request_filterList = filteredList;
                    }

                }
                FilterResults results = new FilterResults();
                results.values = request_filterList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                request_filterList = (List<Request>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView Title, Area, Childs, Date, Phone, status, userApproval, workerApproval, workerName, lastVaccineDate, nextVaccineDate;
        public Button btn_assign, btn_vaccinated, btn_detail, btn_reassign;
        public CheckBox checkBox;
        public ConstraintLayout layout;
        public ImageView imageProfile;
        public CardView cardView;
        public LinearLayout workerNameItem, lastVaccination, nextVaccination;


        public MyViewHolder(View view) {
            super(view);
            Title = (TextView) view.findViewById(R.id.tvTitle);
            Area = (TextView) view.findViewById(R.id.tvArea);
            Childs = (TextView) view.findViewById(R.id.tvChilds);
            Date = (TextView) view.findViewById(R.id.tvDate);
            userApproval = (TextView) view.findViewById(R.id.parent_approve_notApprove);
            workerApproval = (TextView) view.findViewById(R.id.worker_approve_notApprove);
            workerName = (TextView) view.findViewById(R.id.worker_name);
            workerNameItem = (LinearLayout) view.findViewById(R.id.worker);
            lastVaccination = (LinearLayout) view.findViewById(R.id.last_vaccine_item);
            nextVaccination = (LinearLayout) view.findViewById(R.id.next_schedule_item);
            lastVaccineDate = (TextView) view.findViewById(R.id.last_vaccine);
            nextVaccineDate = (TextView) view.findViewById(R.id.next_schedule);
            Phone = (TextView) view.findViewById(R.id.tvPhone);
            cardView = (CardView) view.findViewById(R.id.cardMyProfile);
            btn_assign = (Button) view.findViewById(R.id.btnAssign);
            btn_vaccinated = (Button) view.findViewById(R.id.btn_vaccinated);
            btn_detail = (Button) view.findViewById(R.id.btn_detail);
            btn_reassign = (Button) view.findViewById(R.id.btn_reassign);
            status = (TextView) view.findViewById(R.id.tv_status);

        }
    }

    public RequestAdapter(List<Request> request_list, Context context) {
        this.request_list = request_list;
        this.request_filterList = request_list;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Request request = request_filterList.get(position);


        holder.Title.setText("Name: " + request.getParentName());
        holder.Area.setText("Address: " + request.getCity() + "," + request.getArea());
        holder.Childs.setText("No. child: " + request.getNoChild());
        holder.status.setText(request.getStatus());
        holder.Date.setText("Request " + request.getDate());
        holder.Phone.setText("Contact No: " + request.getPhone());
        holder.workerApproval.setText(request.getWorkerApproval());
        holder.userApproval.setText(request.getUserApproval());
        holder.workerName.setText(request.getWorker());
        holder.lastVaccineDate.setText(request.getLastVaccination());
        holder.nextVaccineDate.setText("Date: " + request.getNextScheduleDate() + " Time: " + request.getNextScheduleTime());

        holder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), RequestDetail.class);
                intent.putExtra("requestId", request.getRequestId());
                view.getContext().startActivity(intent);

            }


        });

        holder.btn_reassign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRequest(request, v);
            }
        });

        if (SharedPref.getUserType(holder.itemView.getContext()).toUpperCase().equals("ADMIN")) {

            holder.btn_vaccinated.setVisibility(View.GONE);


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests")
                    .child(request.getRequestId()).child("isAssigned");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        String value = snapshot.getValue().toString();

                        if (value.equals("Yes")) {
                            holder.btn_assign.setVisibility(View.GONE);

                        } else {
                            holder.btn_assign.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } else {
            holder.btn_assign.setVisibility(View.GONE);
            holder.btn_vaccinated.setVisibility(View.VISIBLE);

            if (SharedPref.getUserType(holder.itemView.getContext()).toUpperCase().equals("USER")) {

                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Requests").child(request.getRequestId());
                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String value = snapshot.child("workerApproval").getValue().toString();
                            String value1 = snapshot.child("userApproval").getValue().toString();


                            if (value.equals("Not Vaccinated") && value1.equals("Not Vaccinated")) {
                                holder.btn_vaccinated.setVisibility(View.GONE);
                            } else if (value1.equals("Not Vaccinated") && value.equals("Vaccinated")) {
                                holder.btn_vaccinated.setVisibility(View.VISIBLE);
                            } else if (value1.equals("Vaccinated") && value.equals("Vaccinated")) {
                                holder.btn_reassign.setVisibility(View.VISIBLE);
                                holder.btn_vaccinated.setVisibility(View.GONE);

                            } else {
                                holder.btn_vaccinated.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            if (SharedPref.getUserType(holder.itemView.getContext()).toUpperCase().equals("WORKER")) {

                holder.workerNameItem.setVisibility(View.GONE);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests").child(request.getRequestId())
                        .child("workerApproval");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String value = snapshot.getValue().toString();

                            if (value.equals("Vaccinated")) {
                                holder.btn_vaccinated.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            holder.btn_vaccinated.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    Calendar calendar = Calendar.getInstance();

                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                    String saveCurrentDate = currentDate.format(calendar.getTime());

                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                    String saveCurrentTime = currentTime.format(calendar.getTime());

                    final String selectedDate = "Date: " + saveCurrentDate + " Time: " + saveCurrentTime;

                    calendar.add(Calendar.DATE, 30);

                    SimpleDateFormat nextSchedule = new SimpleDateFormat("MMM dd, yyyy");
                    String nextScheduleDate = nextSchedule.format(calendar.getTime());

                    if (SharedPref.getUserType(view.getContext()).toUpperCase().equals("USER")) {

                        view.getContext().startActivity(new Intent(view.getContext(), RequestList.class));

                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Requests")
                                .child(request.getRequestId());

                        request.setLastVaccination(selectedDate);
                        request.setUserApproval("Vaccinated");
                        request.setNextScheduleDate(nextScheduleDate);
                        request.setNextScheduleTime(saveCurrentTime);

                        reference.setValue(request);

                        Toast.makeText(mContext, "Child Successfully Vaccinated", Toast.LENGTH_SHORT).show();
                        ((Activity) view.getContext()).finish();

                    } else {

                        view.getContext().startActivity(new Intent(view.getContext(), RequestList.class));

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Requests")
                                .child(request.getRequestId());

                        reference.child("workerApproval").setValue("Vaccinated").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                ((Activity) view.getContext()).finish();

                                Toast.makeText(mContext, "Child Successfully Vaccinated", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });

        }
        holder.btn_assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPref.getUserType(view.getContext()).toUpperCase().equals("ADMIN")) {
                    view.getContext().startActivity(new Intent(view.getContext(), WorkerAndUserList .class).putExtra("myObject", new Gson().toJson(request)));
                    ((Activity) view.getContext()).finish();
                }
            }
        });


    }


    private void saveRequest(Request request, View v) {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        String selectedDate = "Date: " + saveCurrentDate + " Time: " + saveCurrentTime;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests");
        Map<String, Object> map = new HashMap<>();
        String key = reference.push().getKey();
        map.put("userId", request.getUserId());
        map.put("phone", request.getPhone());
        map.put("email", request.getEmail());
        map.put("parentName", request.getParentName());
        map.put("area", request.getArea());
        map.put("city", request.getCity());
        map.put("assignedId", "NA");
        map.put("isAssigned", "NA");
        map.put("status", "In process");
        map.put("userApproval", "Not Vaccinated");
        map.put("workerApproval", "Not Vaccinated");
        map.put("status", "NA");
        map.put("noChild", request.getNoChild());
        map.put("date", selectedDate);
        map.put("requestId", key);
        map.put("worker", "NA");
        map.put("nextScheduleDate", "NA");
        map.put("nextScheduleTime", "NA");
        map.put("lastVaccination", "NA");
        reference.child(key).setValue(map);

        Toast.makeText(mContext, "Request submitted successfully!", Toast.LENGTH_SHORT).show();
        //Toast.makeText(mContext(), "Request submitted successfully!", Toast.LENGTH_LONG).show();
        v.getContext().startActivity(new Intent(v.getContext(), UserDashboard.class));
        ((Activity) v.getContext()).finish();


    }


    @Override
    public int getItemCount() {
        return request_filterList.size();
    }


}

