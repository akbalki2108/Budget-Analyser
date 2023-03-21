package com.example.myapplicationfirebase;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class random2 extends AppCompatActivity {

//    FirebaseDatabase firebaseDatabase;
//    DatabaseReference databaseReference;

    // creating a new array list.
//    ArrayList<String> coursesArrayList;

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        EditText e1 = findViewById(R.id.name);
//        Button b1 = findViewById(R.id.add1);
//
//        Bundle extras = getIntent().getExtras();
//
//
//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String txt_name = e1.getText().toString();
//                String email_logined = extras.getString("email");
//
//                if(txt_name.isEmpty()){
//                    Toast.makeText(Random.this, "Enter valid details", Toast.LENGTH_SHORT).show();
////                    Toast.makeText(Random.this, email_logined, Toast.LENGTH_SHORT).show();
//                }else{
//                    HashMap<String,Object> map= new HashMap<>();
//                    map.put("Name","Aditya");
//                    map.put("Email",txt_name);
//                    try {
//                        FirebaseDatabase.getInstance().getReference("Data").child("name").updateChildren(map);
//                        Toast.makeText(Random.this, "Successfully updated user "+email_logined, Toast.LENGTH_SHORT).show();
//
//                    }catch (Exception e){
//                        Toast.makeText(Random.this, "failed updating user", Toast.LENGTH_SHORT).show();
//                        Log.e("error",e.toString());
//                    }
//                }
//
//            }
//        });
//
//
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference("Data/name/1");
//
//        retrieveTV = findViewById(R.id.idTVRetrieveData);
//        getdata();
//
//        //--------------------------------------------------------------
//
//        coursesLV = findViewById(R.id.idLVCourses);
//        coursesArrayList = new ArrayList<String>();
//        initializeListView();
//
//    }
//
//
//    private void getdata() {
//
//        // calling add value event listener method
//        // for getting the values from database.
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                // this method is call to get the realtime
//                // updates in the data.
//                // this method is called when the data is
//                // changed in our Firebase console.
//                // below line is for getting the data from
//                // snapshot of our database.
//                String value = snapshot.getValue(String.class);
//
//                // after getting the value we are setting
//                // our value to our text view in below line.
//                retrieveTV.setText(value);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // calling on cancelled method when we receive
//                // any error or we are not able to get the data.
//                Toast.makeText(Random.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void initializeListView() {
//        // creating a new array adapter for our list view.
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, coursesArrayList);
//
//        // below line is used for getting reference
//        // of our Firebase Database.
//        databaseReference = FirebaseDatabase.getInstance().getReference("Data/courses");
//
//        // in below line we are calling method for add child event
//        // listener to get the child of our database.
//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                // this method is called when new child is added to
//                // our data base and after adding new child
//                // we are adding that item inside our array list and
//                // notifying our adapter that the data in adapter is changed.
//                coursesArrayList.add(snapshot.getValue(String.class));
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                // this method is called when the new child is added.
//                // when the new child is added to our list we will be
//                // notifying our adapter that data has changed.
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                // below method is called when we remove a child from our database.
//                // inside this method we are removing the child from our array list
//                // by comparing with it's value.
//                // after removing the data we are notifying our adapter that the
//                // data has been changed.
//                coursesArrayList.remove(snapshot.getValue(String.class));
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                // this method is called when we move our
//                // child in our database.
//                // in our code we are note moving any child.
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // this method is called when we get any
//                // error from Firebase with error.
//            }
//        });
//        // below line is used for setting
//        // an adapter to our list view.
//        coursesLV.setAdapter(adapter);
//    }
}
