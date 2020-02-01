package com.example.queenelizabethviii.caireen2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordRecovery extends AppCompatActivity {

    private Button resetPass;
    private EditText resetEmailInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        mAuth = FirebaseAuth.getInstance();


        resetPass = (Button) findViewById(R.id.send);
        resetEmailInput = (EditText) findViewById(R.id.resetEmail);

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recover();
            }
        });


    }

    public void recover(){
        String userEmail = resetEmailInput.getText().toString();

        if (TextUtils.isEmpty(userEmail)){
            Toast.makeText(PasswordRecovery.this, "Please enter your valid email address.", Toast.LENGTH_LONG).show();
        }
        else{
            mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(PasswordRecovery.this, "Please check your email to reset your password.", Toast.LENGTH_LONG).show();
                        android.content.Intent intent = new Intent(PasswordRecovery.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        String message = task.getException().getMessage();
                        Toast.makeText(PasswordRecovery.this, "Error occurred." + message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
