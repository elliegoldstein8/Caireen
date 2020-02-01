package com.example.queenelizabethviii.caireen2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {
    Button signbutton;
    EditText firstname,lastname, email, pass, pass2;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setContentView(R.layout.activity_sign_up);
        signbutton = (Button) findViewById(R.id.signin); //referencing from xml
        firebaseAuth = FirebaseAuth.getInstance();

        //perform method when button is clicked
        signbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }

        });
    }
    public void registerUser() {
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        pass2 = (EditText) findViewById(R.id.password2);

        final String firstnamestr = firstname.getText().toString();
        final String lastnamestr = lastname.getText().toString();
        final String emailstr = email.getText().toString();
        String passstr = pass.getText().toString();
        String pass2str = pass2.getText().toString();

        //Password matching
        if (!passstr.equals(pass2str)) {
            Toast pass = Toast.makeText(SignIn.this, "Passwords don't match!", Toast.LENGTH_LONG);
            pass.show();
        }
        //If there are empty fields
        if (firstnamestr.isEmpty()||lastnamestr.isEmpty()||emailstr.isEmpty()||passstr.isEmpty()||pass2str.isEmpty()){
            Toast.makeText(SignIn.this, "Complete details first.", Toast.LENGTH_SHORT);
        }
        else if (firstnamestr.isEmpty()&&lastnamestr.isEmpty()&&emailstr.isEmpty()&&passstr.isEmpty()&&pass2str.isEmpty()){
            Toast.makeText(SignIn.this, "You didn't put anything.", Toast.LENGTH_SHORT);
        }
        else {
            final ProgressDialog progressDialog = ProgressDialog.show(SignIn.this, "Please wait...", "Processing...", true);
            firebaseAuth.createUserWithEmailAndPassword(emailstr, passstr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    //insert data to database
                    if (task.isSuccessful()) {

                        Users user = new Users(
                                firstnamestr,
                                lastnamestr,
                                emailstr
                        );
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            openSignbaby();
                                        }
                                        else{
                                            progressDialog.dismiss();
                                            Log.e("ERROR", task.getException().toString());
                                            Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                    else{
                        progressDialog.dismiss();
                        Log.e("ERROR", task.getException().toString());
                        Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    public void openSignbaby(){
        android.content.Intent intent = new Intent(this, BabyRegister.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
