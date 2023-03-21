package com.example.myapplicationfirebase;

import static java.nio.file.Paths.get;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private RecyclerView recyclerView;
    private TodayItemsAdapter todayItemsAdapter;
    private List<Data> myDataList;

    private FirebaseAuth  mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef,personalRef;

    private Toolbar settingsToolbar;
    private Button search;
    private  TextView historyTotalAmountSpent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        settingsToolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("History");

        search = findViewById(R.id.search);
        historyTotalAmountSpent = findViewById(R.id.historyTotalAmountSpent);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recycler_View_Id_Feed);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        myDataList = new ArrayList<>();
        todayItemsAdapter = new TodayItemsAdapter(HistoryActivity.this,myDataList);
        recyclerView.setAdapter(todayItemsAdapter);

        search.setOnClickListener((view) -> {

            showDatePickerDialog();
        });

    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        int months = month+1;
        String date = dayOfMonth+"-"+months+"-"+year;
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);

        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                todayItemsAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);

                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()){
                    Map<String,Object> map;get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(totalAmount));
                    totalAmount += pTotal;
                    if (totalAmount > 0){
                        historyTotalAmountSpent.setVisibility(View.VISIBLE);
                        historyTotalAmountSpent.setText("This day you spent : "+totalAmount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}