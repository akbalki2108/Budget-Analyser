package com.example.myapplicationfirebase;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class DailyAnalyticsActivity extends AppCompatActivity {

    private Toolbar settingToolbar;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef, personalRef;


    private TextView totalBudgetAmountTextview;
    private RelativeLayout linearLayoutTransport, linearLayoutFood, linearLayoutFoodHouse, linearLayoutEntertainment,
            linearLayoutEducation, linearLayoutCharity, linearLayoutApparel, linearLayoutHealth, linearLayoutPersonalExp, linearLayoutOther;
    private TextView analyticsTransportAmount, analyticsFoodAmount, analyticsHouseExpensesAmount, analyticsEntertainmentAmount,
            analyticsEducationAmount, analyticsCharityAmount, analyticsApparelAmount, analyticsHealthAmount, analyticsPersonalExpensesAmount, analyticsOtherAmount;
    private TextView progress_ratio_transport, progress_ratio_food, progress_ratio_house, progress_ratio_ent,
            progress_ratio_edu, progress_ratio_cha, progress_ratio_app, progress_ratio_hea, progress_ratio_per, progress_ratio_oth;
    private ImageView status_Image_transport, status_Image_food, status_Image_house, status_Image_ent,
            status_Image_edu, status_Image_cha, status_Image_app, status_Image_hea, status_Image_per, status_Image_oth;

    private AnyChartView anyChartview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_analytics);

        settingToolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(settingToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Today Analysis");

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);

        totalBudgetAmountTextview = findViewById(R.id.totalBudgetAmountTextView);

        //genral analysis


        //relative text
        linearLayoutTransport = findViewById(R.id.linearLayoutTransport);
        linearLayoutFood = findViewById(R.id.linearLayoutFood);
        linearLayoutFoodHouse = findViewById(R.id.linearLayoutFoodHouse);
        linearLayoutEntertainment = findViewById(R.id.linearLayoutEntertainment);
        linearLayoutEducation = findViewById(R.id.linearLayoutEducation);
        linearLayoutCharity = findViewById(R.id.linearLayoutCharity);
        linearLayoutApparel = findViewById(R.id.linearLayoutApparel);
        linearLayoutHealth = findViewById(R.id.linearLayoutHealth);
        linearLayoutPersonalExp = findViewById(R.id.linearLayoutPersonalExp);
        linearLayoutOther = findViewById(R.id.linearLayoutOther);

        //Textvie analysis
        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount = findViewById(R.id.analyticsFoodAmount);
        analyticsHouseExpensesAmount = findViewById(R.id.analyticsHouseExpensesAmount);
        analyticsEntertainmentAmount = findViewById(R.id.analyticsEntertainmentAmount);
        analyticsEducationAmount = findViewById(R.id.analyticsEducationAmount);
        analyticsCharityAmount = findViewById(R.id.analyticsCharityAmount);
        analyticsApparelAmount = findViewById(R.id.analyticsApparelAmount);
        analyticsHealthAmount = findViewById(R.id.analyticsHealthAmount);
        analyticsPersonalExpensesAmount = findViewById(R.id.analyticsPersonalExpensesAmount);
        analyticsOtherAmount = findViewById(R.id.analyticsOtherAmount);

        //textview
        progress_ratio_transport = findViewById(R.id.progress_ratio_transport);
        progress_ratio_food = findViewById(R.id.progress_ratio_food);
        progress_ratio_house = findViewById(R.id.progress_ratio_house);
        progress_ratio_ent = findViewById(R.id.progress_ratio_ent);
        progress_ratio_edu = findViewById(R.id.progress_ratio_edu);
        progress_ratio_cha = findViewById(R.id.progress_ratio_cha);
        progress_ratio_app = findViewById(R.id.progress_ratio_app);
        progress_ratio_hea = findViewById(R.id.progress_ratio_hea);
        progress_ratio_per = findViewById(R.id.progress_ratio_per);
        progress_ratio_oth = findViewById(R.id.progress_ratio_oth);

        //ImageView
        status_Image_transport = findViewById(R.id.status_Image_transport);
        status_Image_food = findViewById(R.id.status_Image_food);
        status_Image_house = findViewById(R.id.status_Image_house);
        status_Image_ent = findViewById(R.id.status_Image_ent);
        status_Image_edu = findViewById(R.id.status_Image_edu);
        status_Image_cha = findViewById(R.id.status_Image_cha);
        status_Image_app = findViewById(R.id.status_Image_app);
        status_Image_hea = findViewById(R.id.status_Image_hea);
        status_Image_per = findViewById(R.id.status_Image_per);
        status_Image_oth = findViewById(R.id.status_Image_oth);

        //chart
        anyChartview = findViewById(R.id.anyChartview);


        getTotalDayFoodExpense();
        getTotalDayHouseExpense();
        getTotalDayTransportExpense();
        getTotalDayEntertainmentExpense();
        getTotalDayEducationExpense();
        getTotalDayCharityExpense();
        getTotalDayApparelExpense();
        getTotalDayHealthExpense();
        getTotalDayPersonalExpense();
        getTotalDayOtherExpense();
        getTotalDaySpending();


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setStatusAndImageResource();
                loadGraph();
            }
        },2000);
    }

    private void getTotalDayTransportExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Transport" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsTransportAmount.setText("Spent: " + totalAmount);
//                        Toast.makeText(DailyAnalyticsActivity.this, "" + totalAmount, Toast.LENGTH_SHORT).show();
                    }
                    personalRef.child("dayTrans").setValue(totalAmount);

                } else {
                    linearLayoutTransport.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalDayFoodExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Food" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsFoodAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayFood").setValue(totalAmount);

                } else {
                    personalRef.child("dayFood").setValue(0);
                    linearLayoutFood.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalDayHouseExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "House Expenses" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsHouseExpensesAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayHouse").setValue(totalAmount);

                } else {
                    personalRef.child("dayHouse").setValue(0);
                    linearLayoutFoodHouse.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalDayEntertainmentExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Entertainment" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsEntertainmentAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayEnt").setValue(totalAmount);
                    Toast.makeText(DailyAnalyticsActivity.this, ""+totalAmount, Toast.LENGTH_SHORT).show();

                } else {
                    personalRef.child("dayEnt").setValue(0);
                    linearLayoutEntertainment.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalDayEducationExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Education" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsEducationAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayEdu").setValue(totalAmount);

                } else {
                    personalRef.child("dayEdu").setValue(0);
                    linearLayoutEducation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalDayCharityExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Charity" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsCharityAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayChar").setValue(totalAmount);

                } else {
                    personalRef.child("dayChar").setValue(0);
                    linearLayoutCharity.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalDayApparelExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Apparel" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsApparelAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayApp").setValue(totalAmount);

                } else {
                    personalRef.child("dayApp").setValue(0);
                    linearLayoutApparel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalDayHealthExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Health" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsHealthAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayHealth").setValue(totalAmount);

                } else {
                    personalRef.child("dayHealth").setValue(0);
                    linearLayoutHealth.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalDayPersonalExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Personal Expenses" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsPersonalExpensesAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayPer").setValue(totalAmount);

                } else {
                    personalRef.child("dayPer").setValue(0);
                    linearLayoutPersonalExp.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalDayOtherExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String itemNday = "Other" + date;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemNday").equalTo(itemNday);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsOtherAmount.setText("Spent: " + totalAmount);
                    }
                    personalRef.child("dayOther").setValue(totalAmount);

                } else {
                    personalRef.child("dayOther").setValue(0);
                    linearLayoutOther.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DailyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalDaySpending() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                }
                totalBudgetAmountTextview.setText("Total day's spending: " + totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                totalBudgetAmountTextview.setText("you've not spent today");
            }
        });
    }

    private void setStatusAndImageResource() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float traTotal;
                    if (snapshot.hasChild("dayTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    float foodTotal;
                    if (snapshot.hasChild("dayFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }


                    float houseTotal;
                    if (snapshot.hasChild("dayHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }

                    float entTotal;
                    if (snapshot.hasChild("dayEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("dayEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }

                    float eduTotal;
                    if (snapshot.hasChild("dayEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("dayEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }

                    float charTotal;
                    if (snapshot.hasChild("dayChar")) {
                        charTotal = Integer.parseInt(snapshot.child("dayChar").getValue().toString());
                    } else {
                        charTotal = 0;
                    }

                    float appTotal;
                    if (snapshot.hasChild("dayApp")) {
                        appTotal = Integer.parseInt(snapshot.child("dayApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    float heaTotal;
                    if (snapshot.hasChild("dayHea")) {
                        heaTotal = Integer.parseInt(snapshot.child("dayHea").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }

                    float perTotal;
                    if (snapshot.hasChild("dayPer")) {
                        perTotal = Integer.parseInt(snapshot.child("dayPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    float othTotal;
                    if (snapshot.hasChild("dayOther")) {
                        othTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }

                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("today")) {
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("today").getValue().toString());
                    } else {
                        monthTotalSpentAmount = 0;
                    }

                    //chart

//                    Pie pie = AnyChart.pie();
//                    List<DataEntry> data = new ArrayList<>();
//                    data.add(new ValueDataEntry("Transport", traTotal));
//                    data.add(new ValueDataEntry("Food", foodTotal));
//                    data.add(new ValueDataEntry("House", houseTotal));
//                    data.add(new ValueDataEntry("Entertainment", entTotal));
//                    data.add(new ValueDataEntry("Education", eduTotal));
//                    data.add(new ValueDataEntry("Charity", charTotal));
//                    data.add(new ValueDataEntry("Apparel", appTotal));
//                    data.add(new ValueDataEntry("Health", heaTotal));
//                    data.add(new ValueDataEntry("Personal", perTotal));
//                    data.add(new ValueDataEntry("Other", othTotal));
//
//                    pie.data(data);
//                    pie.title("Daily Analysis");
//                    pie.labels().position("outside");
//                    pie.legend().title().enabled(true);
//                    pie.legend().title().text("Items Spend On").padding(0d, 0d, 10d, 0d);
//
//                    pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE).align(Align.CENTER);
//                    anyChartview.setChart(pie);

                    // Getting Ratios

                    float traRatio;
                    if (snapshot.hasChild("dayTransRatio")) {
                        traRatio = Integer.parseInt(snapshot.child("dayTransRatio").getValue().toString());
                    } else {
                        traRatio = 0;
                    }

                    float foodRatio;
                    if (snapshot.hasChild("dayFoodRatio")) {
                        foodRatio = Integer.parseInt(snapshot.child("dayFoodRatio").getValue().toString());
                    } else {
                        foodRatio = 0;
                    }

                    float houseRatio;
                    if (snapshot.hasChild("dayHouseRatio")) {
                        houseRatio = Integer.parseInt(snapshot.child("dayHouseRatio").getValue().toString());
                    } else {
                        houseRatio = 0;
                    }

                    float entRatio;
                    if (snapshot.hasChild("dayEntRatio")) {
                        entRatio = Integer.parseInt(snapshot.child("dayEntRatio").getValue().toString());
                    } else {
                        entRatio = 0;
                    }

                    float eduRatio;
                    if (snapshot.hasChild("dayEduRatio")) {
                        eduRatio = Integer.parseInt(snapshot.child("dayEduRatio").getValue().toString());
                    } else {
                        eduRatio = 0;
                    }

                    float charRatio;
                    if (snapshot.hasChild("dayCharRatio")) {
                        charRatio = Integer.parseInt(snapshot.child("dayCharRatio").getValue().toString());
                    } else {
                        charRatio = 0;
                    }

                    float appRatio;
                    if (snapshot.hasChild("dayAppRatio")) {
                        appRatio = Integer.parseInt(snapshot.child("dayAppRatio").getValue().toString());
                    } else {
                        appRatio = 0;
                    }

                    float heaRatio;
                    if (snapshot.hasChild("dayHealthRatio")) {
                        heaRatio = Integer.parseInt(snapshot.child("dayHealthRatio").getValue().toString());
                    } else {
                        heaRatio = 0;
                    }

                    float perRatio;
                    if (snapshot.hasChild("dayPerRatio")) {
                        perRatio = Integer.parseInt(snapshot.child("dayPerRatio").getValue().toString());
                    } else {
                        perRatio = 0;
                    }

                    float othRatio;
                    if (snapshot.hasChild("dayOtherRatio")) {
                        othRatio = Integer.parseInt(snapshot.child("dayOtherRatio").getValue().toString());
                    } else {
                        othRatio = 0;
                    }

                    float monthTotalSpentAmountRatio;
                    if (snapshot.hasChild("dailyBudget")) {
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("dailyBudget").getValue().toString());
                    } else {
                        monthTotalSpentAmountRatio = 0;
                    }


                    float transportPercent = (traTotal/traRatio)*100;
                    if (transportPercent<50){
                        progress_ratio_transport.setText(transportPercent+" %"+"used of "+traRatio +" Status:");
                        status_Image_transport.setImageResource(R.drawable.green);
                    } else if (transportPercent >= 50 && transportPercent <100){
                        progress_ratio_transport.setText(transportPercent+" %"+"used of "+traRatio +" Status:");
                        status_Image_transport.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_transport.setText(transportPercent+" %"+"used of "+traRatio +" Status:");
                        status_Image_transport.setImageResource(R.drawable.red);
                    }

                    float foodPercent = (foodTotal/foodRatio)*100;
                    if (foodPercent<50){
                        progress_ratio_food.setText(foodPercent+" %"+"used of "+foodRatio +" Status:");
                        status_Image_food.setImageResource(R.drawable.green);
                    } else if (foodPercent >= 50 && foodPercent <100){
                        progress_ratio_food.setText(foodPercent+" %"+"used of "+foodRatio +" Status:");
                        status_Image_food.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_food.setText(foodPercent+" %"+"used of "+foodRatio +" Status:");
                        status_Image_food.setImageResource(R.drawable.red);
                    }

                    float housePercent = (houseTotal/houseRatio)*100;
                    if (housePercent<50){
                        progress_ratio_house.setText(housePercent+" %"+"used of "+houseRatio +" Status:");
                        status_Image_house.setImageResource(R.drawable.green);
                    } else if (housePercent >= 50 && housePercent <100){
                        progress_ratio_house.setText(housePercent+" %"+"used of "+houseRatio +" Status:");
                        status_Image_house.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_house.setText(housePercent+" %"+"used of "+houseRatio +" Status:");
                        status_Image_house.setImageResource(R.drawable.red);
                    }

                    float entPercent = (entTotal/entRatio)*100;
                    if (entPercent<50){
                        progress_ratio_ent.setText(entPercent+" %"+"used of "+entRatio +" Status:");
                        status_Image_ent.setImageResource(R.drawable.green);
                    } else if (entPercent >= 50 && entPercent <100){
                        progress_ratio_ent.setText(entPercent+" %"+"used of "+entRatio +" Status:");
                        status_Image_ent.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_ent.setText(entPercent+" %"+"used of "+entRatio +" Status:");
                        status_Image_ent.setImageResource(R.drawable.red);
                    }

                    float eduPercent = (eduTotal/eduRatio)*100;
                    if (eduPercent<50){
                        progress_ratio_edu.setText(eduPercent+" %"+"used of "+eduRatio +" Status:");
                        status_Image_edu.setImageResource(R.drawable.green);
                    } else if (eduPercent >= 50 && eduPercent <100){
                        progress_ratio_edu.setText(eduPercent+" %"+"used of "+eduRatio +" Status:");
                        status_Image_edu.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_edu.setText(eduPercent+" %"+"used of "+eduRatio +" Status:");
                        status_Image_edu.setImageResource(R.drawable.red);
                    }

                    float charPercent = (charTotal/charRatio)*100;
                    if (charPercent<50){
                        progress_ratio_cha.setText(charPercent+" %"+"used of "+charRatio +" Status:");
                        status_Image_cha.setImageResource(R.drawable.green);
                    } else if (charPercent >= 50 && charPercent <100){
                        progress_ratio_cha.setText(charPercent+" %"+"used of "+charRatio +" Status:");
                        status_Image_cha.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_cha.setText(charPercent+" %"+"used of "+charRatio +" Status:");
                        status_Image_cha.setImageResource(R.drawable.red);
                    }

                    float appPercent = (appTotal/appRatio)*100;
                    if (appPercent<50){
                        progress_ratio_app.setText(appPercent+" %"+"used of "+appRatio +" Status:");
                        status_Image_app.setImageResource(R.drawable.green);
                    } else if (appPercent >= 50 && appPercent <100){
                        progress_ratio_app.setText(appPercent+" %"+"used of "+appRatio +" Status:");
                        status_Image_app.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_app.setText(appPercent+" %"+"used of "+appRatio +" Status:");
                        status_Image_app.setImageResource(R.drawable.red);
                    }

                    float heaPercent = (heaTotal/heaRatio)*100;
                    if (heaPercent<50){
                        progress_ratio_hea.setText(heaPercent+" %"+"used of "+heaRatio +" Status:");
                        status_Image_hea.setImageResource(R.drawable.green);
                    } else if (heaPercent >= 50 && heaPercent <100){
                        progress_ratio_hea.setText(heaPercent+" %"+"used of "+heaRatio +" Status:");
                        status_Image_hea.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_hea.setText(heaPercent+" %"+"used of "+heaRatio +" Status:");
                        status_Image_hea.setImageResource(R.drawable.red);
                    }

                    float perPercent = (perTotal/perRatio)*100;
                    if (perPercent<50){
                        progress_ratio_per.setText(perPercent+" %"+"used of "+perRatio +" Status:");
                        status_Image_per.setImageResource(R.drawable.green);
                    } else if (perPercent >= 50 && perPercent <100){
                        progress_ratio_per.setText(perPercent+" %"+"used of "+perRatio +" Status:");
                        status_Image_per.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_per.setText(perPercent+" %"+"used of "+perRatio +" Status:");
                        status_Image_per.setImageResource(R.drawable.red);
                    }

                    float otherPercent = (othTotal/othRatio)*100;
                    if (otherPercent<50){
                        progress_ratio_oth.setText(otherPercent+" %"+"used of "+othRatio +" Status:");
                        status_Image_oth.setImageResource(R.drawable.green);
                    } else if (otherPercent >= 50 && otherPercent <100){
                        progress_ratio_oth.setText(otherPercent+" %"+"used of "+othRatio +" Status:");
                        status_Image_oth.setImageResource(R.drawable.brown);
                    } else {
                        progress_ratio_oth.setText(otherPercent+" %"+"used of "+othRatio +" Status:");
                        status_Image_oth.setImageResource(R.drawable.red);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadGraph(){

        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    float traTotal;
                    if (snapshot.hasChild("dayTrans")) {
                        traTotal = Integer.parseInt(snapshot.child("dayTrans").getValue().toString());
                    } else {
                        traTotal = 0;
                    }

                    float foodTotal;
                    if (snapshot.hasChild("dayFood")) {
                        foodTotal = Integer.parseInt(snapshot.child("dayFood").getValue().toString());
                    } else {
                        foodTotal = 0;
                    }


                    float houseTotal;
                    if (snapshot.hasChild("dayHouse")) {
                        houseTotal = Integer.parseInt(snapshot.child("dayHouse").getValue().toString());
                    } else {
                        houseTotal = 0;
                    }

                    float entTotal;
                    if (snapshot.hasChild("dayEnt")) {
                        entTotal = Integer.parseInt(snapshot.child("dayEnt").getValue().toString());
                    } else {
                        entTotal = 0;
                    }

                    float eduTotal;
                    if (snapshot.hasChild("dayEdu")) {
                        eduTotal = Integer.parseInt(snapshot.child("dayEdu").getValue().toString());
                    } else {
                        eduTotal = 0;
                    }

                    float charTotal;
                    if (snapshot.hasChild("dayChar")) {
                        charTotal = Integer.parseInt(snapshot.child("dayChar").getValue().toString());
                    } else {
                        charTotal = 0;
                    }

                    float appTotal;
                    if (snapshot.hasChild("dayApp")) {
                        appTotal = Integer.parseInt(snapshot.child("dayApp").getValue().toString());
                    } else {
                        appTotal = 0;
                    }

                    float heaTotal;
                    if (snapshot.hasChild("dayHea")) {
                        heaTotal = Integer.parseInt(snapshot.child("dayHea").getValue().toString());
                    } else {
                        heaTotal = 0;
                    }

                    float perTotal;
                    if (snapshot.hasChild("dayPer")) {
                        perTotal = Integer.parseInt(snapshot.child("dayPer").getValue().toString());
                    } else {
                        perTotal = 0;
                    }

                    float othTotal;
                    if (snapshot.hasChild("dayOther")) {
                        othTotal = Integer.parseInt(snapshot.child("dayOther").getValue().toString());
                    } else {
                        othTotal = 0;
                    }

                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("today")) {
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("today").getValue().toString());
                    } else {
                        monthTotalSpentAmount = 0;
                    }

                    //chart

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport", traTotal));
                    data.add(new ValueDataEntry("Food", foodTotal));
                    data.add(new ValueDataEntry("House", houseTotal));
                    data.add(new ValueDataEntry("Entertainment", entTotal));
                    data.add(new ValueDataEntry("Education", eduTotal));
                    data.add(new ValueDataEntry("Charity", charTotal));
                    data.add(new ValueDataEntry("Apparel", appTotal));
                    data.add(new ValueDataEntry("Health", heaTotal));
                    data.add(new ValueDataEntry("Personal", perTotal));
                    data.add(new ValueDataEntry("Other", othTotal));

                    pie.data(data);
                    pie.title("Daily Analysis");
                    pie.labels().position("outside");
                    pie.legend().title().enabled(true);
                    pie.legend().title().text("Items Spend On").padding(0d, 0d, 10d, 0d);

                    pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE).align(Align.CENTER);
                    anyChartview.setChart(pie);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
