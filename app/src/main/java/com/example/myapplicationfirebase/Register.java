package com.example.myapplicationfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText mFullName,mEmail,mPassword,mPhone;
    Button mRegisterBtn,mLoginBtn;
    FirebaseAuth fAuth;
    //ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.person_email);
        mPassword = findViewById(R.id.person_pass);
        mFullName = findViewById(R.id.person_name);
        mPhone = findViewById(R.id.person_phoneNo);
        mRegisterBtn = findViewById(R.id.signup_to_otp);
        mLoginBtn = findViewById(R.id.signup_to_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        //progressBar = findViewById(R.id.progressBar);

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String fullname = mFullName.getText().toString();
                String phone = mPhone.getText().toString();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }
                if(email.length() < 10 ){
                    mEmail.setError("Email is required!!");
                    return;
                }
                if(TextUtils.isEmpty(fullname)){
                    mFullName.setError("full name required!!");
                    return;
                }
                if(TextUtils.isEmpty(phone)){
                    mPhone.setError("phone number required!!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    mPassword.setError("password must be >= 6 char ");
                    return;
                }

                //progressBar.setVisibility(View.VISIBLE);

                //register
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //verification email
                            FirebaseUser fUser = fAuth.getCurrentUser();
                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Register.this, "Verification Email has been sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG","onFailure: Email not sent"+ e.getMessage());
                                }
                            });

                            Toast.makeText(Register.this,"user created",Toast.LENGTH_SHORT).show();

                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);

                            Map<String,Object> user = new HashMap<>();
                            user.put("fName",fullname);
                            user.put("email",email);
                            user.put("phone",phone);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG","onSucess : user profile is created for "+user);
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                        }else{
                            Toast.makeText(Register.this,"Error !!"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            //progressBar.setVisibility(View.GONE);

                        }
                    }
                });
            }
        });


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,Login.class));
            }
        });


    }


}


