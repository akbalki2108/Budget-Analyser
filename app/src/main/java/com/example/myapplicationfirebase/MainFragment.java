package com.example.myapplicationfirebase;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MainFragment extends Fragment{

    private String onlineUserID = "";
    private DatabaseReference budgetRef, expensesRef, personalRef;
    private TextView budgetTv, todayTv, weekTv, monthTv, savingTv;
    private CardView budgetCardView,todayCardView,weekCardView,monthCardView,analyticsCardView,historyCardView;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main,container, false);
        getActivity().setTitle("home");


        budgetTv = v.findViewById(R.id.budgetTv);
        todayTv = v.findViewById(R.id.todayTv);
        weekTv = v.findViewById(R.id.weekTv);
        monthTv = v.findViewById(R.id.monthTv);
        savingTv = v.findViewById(R.id.savingTv);

        budgetCardView =  v.findViewById(R.id.budgetCardView);
        todayCardView = v.findViewById(R.id.todayCardView);
        weekCardView = v.findViewById(R.id.weekCardView);
        monthCardView = v.findViewById(R.id.monthCardView);
        analyticsCardView = v.findViewById(R.id.analyticsCardView);
        historyCardView = v.findViewById(R.id.historyCardView);

        budgetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "budget Activity", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(),BudgetActivity.class);
                startActivity(intent);
            }
        });
        todayCardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "Today Spending Activity", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(),TodaySpendingActivity.class);
                startActivity(intent);
            }
        });
        weekCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Week Spending Activity", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(getActivity(),WeekSpendingActivity.class);
                intent.putExtra("type","Week");
                startActivity(intent);
            }
        });
        monthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "Month Spending Activity", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(),WeekSpendingActivity.class);
                intent.putExtra("type","Month");
                startActivity(intent);
            }
        });
        analyticsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "Analytic Activity", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(),ChooseAnalyticActivity.class);
                startActivity(intent);
            }
        });
        historyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "History Activity", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(),HistoryActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        onlineUserID = user.getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference("Budget").child(onlineUserID);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserID);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalAmount = 0;
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }

                    personalRef.child("budget").setValue(totalAmount);
//                    totalAmountBudget = totalAmount;
                } else {
                    personalRef.child("budget").setValue(0);
                    Toast.makeText(getActivity(), "Please set a budget", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

        getBudgetAmount();
        getTodaySpentAmount();
        getWeekSpentAmount();
        getMonthSpentAmount();
        getSaving();

        return  v;
    }
    private void getBudgetAmount() {
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        budgetTv.setText("₹ "+String.valueOf(totalAmount));

                    }

                } else {
//                   totalAmountBudget=0;
                    budgetTv.setText("₹ "+String.valueOf(0));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTodaySpentAmount() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        String date = dateFormat.format(calendar.getTime());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    todayTv.setText("₹ "+totalAmount);
                }
                personalRef.child("today").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthSpentAmount() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    monthTv.setText("₹ "+totalAmount);
                }
                personalRef.child("month").setValue(totalAmount);
//                totalAmountMonth = totalAmount;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWeekSpentAmount() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch, now);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        Query query = reference.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    weekTv.setText("₹ "+totalAmount);
                }
                personalRef.child("week").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSaving() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int budget;
                    if (snapshot.hasChild("budget")){
                        budget = Integer.parseInt(snapshot.child("budget").getValue().toString());
                    }else {
                        budget = 0;
                    }

                    int monthSpending;
                    if (snapshot.hasChild("month")){
                        monthSpending = Integer.parseInt(Objects.requireNonNull(snapshot.child("month").getValue().toString()));
                    }else {
                        monthSpending = 0;
                    }

                    int savings = budget - monthSpending;
                    savingTv.setText("₹ "+ savings);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
