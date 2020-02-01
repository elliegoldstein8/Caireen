package com.example.queenelizabethviii.caireen2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

   // DatabaseHelper helper = new DatabaseHelper(this);

    Button button,button2;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUp();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               EditText a = (EditText) findViewById(R.id.Email1);
               final  String str = a.getText().toString();
               EditText b = (EditText) findViewById(R.id.Password2);
               final  String pass = b.getText().toString();

               if (str.isEmpty()||pass.isEmpty()){
                   Toast.makeText(MainActivity.this, "Enter username & password", Toast.LENGTH_SHORT).show();
               }

             else{
                   final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,"Please wait...", "Logging in...", true);
                   firebaseAuth.signInWithEmailAndPassword(str, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     progressDialog.dismiss();
                     if (task.isSuccessful()){
                         Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                         openHomePage();
                     }
                     else{
                         progressDialog.dismiss();
                         Log.e("ERROR", task.getException().toString());
                         Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                     }
                 }
             });

           } }
       });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() !=null){
            //handle the already login user
            openHomePage();
        }
    }

    public void openHomePage(){
        android.content.Intent intent = new Intent(this, HomePageu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }//open new activity

    public void openSignUp(){
        Intent intent = new Intent(this, SignIn.class );
        startActivity(intent);
    }//open new activity

    public void forgotPassword(View view){
        Intent intent = new Intent(this, PasswordRecovery.class );
        startActivity(intent);
    }


}
