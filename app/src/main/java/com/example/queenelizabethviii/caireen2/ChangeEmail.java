package com.example.queenelizabethviii.caireen2;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class ChangeEmail extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("email"); //reference
    FirebaseUser user = firebaseAuth.getCurrentUser();
    EditText email, pass;
    String emaill; // panginsert sa detabes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        email = (EditText) findViewById(R.id.ch_email);
        pass = (EditText) findViewById(R.id.ch_email_pass);
        Button button = (Button) findViewById(R.id.ch_email_button);


        emaill = user.getEmail();

        email.setText(emaill);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });

    }

    public void changeEmail(){

        final String emailstr = email.getText().toString();
        String passstr = pass.getText().toString();

        if (emailstr.isEmpty()||passstr.isEmpty()){
            Toast.makeText(ChangeEmail.this, "Email/Password is empty.", Toast.LENGTH_LONG).show();
        }
        else {
            AuthCredential credential = EmailAuthProvider.getCredential(emaill, passstr); //Current Login Credentials
            //Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //------Code for changing email address ---------\\
                    user.updateEmail(emailstr).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangeEmail.this, "Email changed.", Toast.LENGTH_LONG).show();
                                android.content.Intent intent = new Intent(ChangeEmail.this, SettingsActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ChangeEmail.this, "Changing of email unsuccessful.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            });
        }
    }
}
