package com.example.myapplicationfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ResetPassword extends AppCompatActivity {

    EditText oldPass,newPass;
    FirebaseAuth fAuth;
    FirebaseUser user;
    TextView email;
    Button resetPassBtn;

    String emailTxt,oldPassTxt,newPassTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        email = findViewById(R.id.userEmail);
        oldPass = findViewById(R.id.old_pass);
        newPass = findViewById(R.id.new_pass);

        emailTxt = Objects.requireNonNull(user.getEmail()).trim();
        email.setText(emailTxt);

    }

    public void resetPassword(View view) {

        oldPassTxt = oldPass.getText().toString().trim();
        newPassTxt = newPass.getText().toString().trim();

        if(TextUtils.isEmpty(oldPassTxt) && oldPassTxt.length() < 8){
            oldPass.setError("old pass required");
            return;
        }
        if(TextUtils.isEmpty(newPassTxt) && newPassTxt.length() < 8){
            newPass.setError("new pass required");
            return;
        }

        if(emailTxt.isEmpty()){
            Toast.makeText(ResetPassword.this, "Enter valid email", Toast.LENGTH_SHORT).show();

        }else if(oldPassTxt.isEmpty()){
            Toast.makeText(ResetPassword.this, "Enter valid password", Toast.LENGTH_SHORT).show();
        }else{
            fAuth.signInWithEmailAndPassword(emailTxt,oldPassTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        //Toast.makeText(ResetPassword.this,"logged in succesfully",Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(getApplicationContext(),Random.class));
                        try{
                            user.updatePassword(newPassTxt).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ResetPassword.this, "Password reset sucessfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ResetPassword.this,Login.class));
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ResetPassword.this, "Password reset Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(ResetPassword.this,"Error !!",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ResetPassword.this, "failed", Toast.LENGTH_SHORT).show();

                }
            });


        }
    }

    public void backToMain(View view){
        startActivity(new Intent(ResetPassword.this, MainActivity.class));
        finish();
    }

}









