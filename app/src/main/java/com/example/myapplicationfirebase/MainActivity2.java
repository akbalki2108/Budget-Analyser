package com.example.myapplicationfirebase;


import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity2 extends Login implements NavigationView.OnNavigationItemSelectedListener{


    private static final int PReqCode = 2 ;
    private static final int REQUESCODE = 2 ;


    TextView fullName,email,phone;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID,strEmail,strName,strPhoneNo="default";


    ImageView profileImage;

    StorageReference storageReference;

    TextView verifyEmail;
    Button resendCode;

    private DrawerLayout drawer;
    ActionBarDrawerToggle toggle;

    private TodayItemsAdapter todayItemsAdapter;
    private List<Data> myDataList;

    private Toolbar toolbar;
    private TextView budgetTv, todayTv, weekTv, monthTv, savingTv;
    private ImageView weekBtnImageView, todayBtnImageView, monthBtnImageView, analyticsImageView,budgetBtnImageView, historyBtnImageView;
    private String onlineUserID = "";
    private DatabaseReference budgetRef, expensesRef, personalRef;
    private CardView budgetCardView, todayCardView, analyticsCardView;



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
        setContentView(R.layout.activity_main2);

        drawer =findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        getSupportActionBar().setTitle("Home");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.nav_open,R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MainFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        View headerView = navigationView.getHeaderView(0);
        fullName = headerView.findViewById(R.id.person_name);
        email = headerView.findViewById(R.id.person_email);
        profileImage = headerView.findViewById(R.id.profileImage);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        user = fAuth.getCurrentUser();
        userID = user.getUid();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Budget Analyser");


        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        onlineUserID = user.getUid();

        budgetRef = FirebaseDatabase.getInstance().getReference("Budget").child(onlineUserID);
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserID);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserID);







        DocumentReference documentReference = fStore.collection("users").document(userID);
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

                    email.setText(strEmail);
                    fullName.setText(strName);

                    setUserEmail(strEmail);
                    setUserName(strName);
                    setUserPhoneNo(strPhoneNo);
//                    Toast.makeText(MainActivity.this, "Current data: " + value.getData().get("fName").toString(), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity2.this, "Current data: null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        showProfile(profileImage);


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public void verifyEmail(View view){
//        if(!user.isEmailVerified()){
//            resendCode.setVisibility(View.VISIBLE);
//            verifyEmail.setVisibility(View.VISIBLE);
//
//            resendCode.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Toast.makeText(MainActivity.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("TAG","onFailure: Email not sent"+ e.getMessage());
//                        }
//                    });
//                }
//            });
//        }
//    }




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
        StorageReference profileRef = storageReference.child("users/"+fAuth.getUid()+"/profile.jpg");
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
        startActivity(new Intent(MainActivity2.this, ResetPassword.class));
    }

    //upload image to firebase
    private void uploadImageToFirebase(Uri imageUri){
        StorageReference fileReference = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(MainActivity2.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity2.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MainFragment()).commit();
                break;
            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
//            case R.id.nav_settings:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new SettingFragment()).commit();
//                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_send:
                Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity2.this, Login.class));
                Toast.makeText(MainActivity2.this, "signOut Successfully", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }








}