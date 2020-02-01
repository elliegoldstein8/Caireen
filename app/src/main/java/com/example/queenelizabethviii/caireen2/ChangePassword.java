package com.example.queenelizabethviii.caireen2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseUser user;
    EditText current, newpass, newpass2;
    String oldpass, newpasss, newpasss2;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Button button = (Button) findViewById(R.id.changebutton);

        current = (EditText) findViewById(R.id.currentpassword);
        newpass = (EditText) findViewById(R.id.newpassword);
        newpass2 = (EditText) findViewById(R.id.newpassword2);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    changePass();

            }
        });


    }

    public void changePass(){


        oldpass = current.getText().toString();
        newpasss = newpass.getText().toString();
        newpasss2 = newpass2.getText().toString();

        if (oldpass.isEmpty()||newpasss.isEmpty()||newpasss2.isEmpty()){
            Toast.makeText(ChangePassword.this, "Complete fields first.", Toast.LENGTH_LONG).show();
        }
        else {
            progressDialog = ProgressDialog.show(ChangePassword.this, "Please wait...", "Processing...", true);
            user = firebaseAuth.getCurrentUser();
            String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newpasss).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(ChangePassword.this, "Permission denied.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ChangePassword.this, "Password updated.", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    android.content.Intent intent = new Intent(ChangePassword.this, SettingsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ChangePassword.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }
}
