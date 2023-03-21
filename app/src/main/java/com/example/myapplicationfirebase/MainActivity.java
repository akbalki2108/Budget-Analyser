package com.example.myapplicationfirebase;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView budgetTv, todayTv, weekTv, monthTv, savingTv;

    private CardView budgetCardView, todayCardView, analyticsCardView;

    private ImageView weekBtnImageView, todayBtnImageView, monthBtnImageView, analyticsImageView,budgetBtnImageView, historyBtnImageView;

    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    private DatabaseReference budgetRef, expensesRef, personalRef;
    private String onlineUserID = "";
    String strEmail,strName,strPhoneNo="default";
    StorageReference storageReference;

    //to get image from gallary
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result !=null && result.getResultCode() == RESULT_OK){
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        //ImageView imageView = findViewById(R.id.profileImage);
                        //imageView.setImageURI(uri);

                        uploadImageToFirebase(uri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Budget Analyser");

        budgetTv = findViewById(R.id.budgetTv);
        todayTv = findViewById(R.id.todayTv);
        weekTv = findViewById(R.id.weekTv);
        monthTv = findViewById(R.id.monthTv);
        savingTv = findViewById(R.id.savingTv);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        onlineUserID = user.getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference("Budget").child(onlineUserID);
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserID);

        weekBtnImageView = findViewById(R.id.weekBtnImageView);
        todayBtnImageView = findViewById(R.id.todayBtnImageView);
        budgetBtnImageView = findViewById(R.id.budgetBtnImageView);
        monthBtnImageView = findViewById(R.id.monthBtnImageView);
        analyticsImageView = findViewById(R.id.analyticsImageView);
        historyBtnImageView = findViewById(R.id.historyImageView);

//        todayCardView = findViewById(R.id.todayCardView);

        analyticsCardView = findViewById(R.id.analyticsCardView);

        DocumentReference documentReference = fStore.collection("users").document(onlineUserID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                if (value != null && value.exists()) {
                    strName = value.getData().get("fName").toString();
                    strEmail = value.getData().get("email").toString();
                    strPhoneNo = value.getData().get("phone").toString();

//                    email.setText(strEmail);
//                    fullName.setText(strName);

                    setUserEmail(strEmail);
                    setUserName(strName);
                    setUserPhoneNo(strPhoneNo);
//                    Toast.makeText(MainActivity.this, "Current data: " + value.getData().get("fName").toString(), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "Current data: null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        todayBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TodaySpendingActivity.class);
                startActivity(intent);
            }
        });
        budgetBtnImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,BudgetActivity.class);
                startActivity(intent);
            }
        });
        weekBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeekSpendingActivity.class);
                intent.putExtra("type", "week");
                startActivity(intent);
            }
        });
        monthBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeekSpendingActivity.class);
                intent.putExtra("type", "month");
                startActivity(intent);
            }
        });
        analyticsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChooseAnalyticActivity.class);

                startActivity(intent);
            }
        });

        historyBtnImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);

                startActivity(intent);
            }
        });
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
                    Toast.makeText(MainActivity.this, "Please set a budget", Toast.LENGTH_SHORT).show();
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


    }
    private void setUserEmail(String uEmail){
        strEmail.equals(uEmail);
    }
    private void setUserPhoneNo(String uPhoneNo){
        strPhoneNo.equals(uPhoneNo);
    }
    private void setUserName(String uName){
        strName.equals(uName);
    }

    public String getUserEmail(){
        return strEmail;
    }
    public String getUserPhoneNo(){
        return strPhoneNo;
    }
    public String getUserName(){
        return strName;
    }

    public void showProfile(ImageView profileImage1){
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/"+mAuth.getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage1);
            }
        });
    }

    public void changeProfilePhoto(View view){
        //open gallery
        Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
        openGallery.setType("image/jpg");
        openGallery.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startForResult.launch(openGallery);
    }
    public void resetPasswordBtn(View view){
        startActivity(new Intent(MainActivity.this, ResetPassword.class));
    }
    //upload image to firebase
    private void uploadImageToFirebase(Uri imageUri){
        StorageReference fileReference = storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(MainActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
//                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });
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

