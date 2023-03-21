package com.example.myapplicationfirebase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    Button forget;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forget = findViewById(R.id.forgot_to_reset);
        email = findViewById(R.id.forgot_email );

        forget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String txt_email = email.getText().toString();

                if(txt_email.isEmpty()){
                    Toast.makeText(ForgotPassword.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                }else{

                    FirebaseAuth.getInstance().sendPasswordResetEmail(txt_email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPassword.this, "email send succesfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(ForgotPassword.this, "reseting password failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }
}