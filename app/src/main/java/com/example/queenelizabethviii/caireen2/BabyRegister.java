package com.example.queenelizabethviii.caireen2;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class BabyRegister extends AppCompatActivity {
    CircleImageView imageView2;
    Button button4, ok;
    RadioButton radioButton;
    RadioGroup rdGroup;
    String gender = "";
    SQLiteDatabase db;
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;
    // DatabaseHelper helper = new DatabaseHelper(this);
    StorageReference mStorageRef;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    FirebaseStorage storage = FirebaseStorage.getInstance(); //for storing files sa firebase
    DatabaseReference myRef = database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid()).child("baby"); //reference
    DatabaseReference newRef = myRef.push(); //auto-generated key para sa baby
    String refkey = newRef.getKey(); //para makuha ang key sa baby
    private int PERMISSION_CAMERA =1;
    private int PERMISSION_STORAGE=0;
    String monthseu;
    DatePickerDialog.OnDateSetListener mDateListener;



    EditText babyname,babybday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_register);
        //for storing files to firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();
        imageView2 = findViewById(R.id.alarm_icon);
        button4 = findViewById(R.id.button4);

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });
        babyname = findViewById(R.id.babynamestring);
        babybday = findViewById(R.id.babybday);
        babyname = findViewById(R.id.babynamestring);
        babybday = findViewById(R.id.babybday);
        //  String link = url;

        babybday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(BabyRegister.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        }); //open Date picker

        mDateListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                //Log.d(TAG, "OnDateSet: date: " + year+ "/"+ month+"/"+dayOfMonth );
                String date = month + "/"+ dayOfMonth + "/" + year;
                babybday.setText(date);
            }
        }; //display date


        rdGroup = (RadioGroup) findViewById(R.id.rdGroup);

        rdGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        ok = findViewById(R.id.ok);
        ok.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //    registerBaby();
                openHome();
            }
        });
    }

    private void SelectImage(){

        final CharSequence[] items={"Camera","Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(BabyRegister.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(items[i].equals("Gallery")) {
                    ActivityCompat.requestPermissions(BabyRegister.this,new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_STORAGE);

                }else if(items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
                else if(items[i].equals("Camera")) {
                    ActivityCompat.requestPermissions(BabyRegister.this,new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA);
                    //mangayog permission for camera

                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //actions if granted ang permission sa camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_CAMERA);
            }
            else{
                Toast.makeText(BabyRegister.this, "Permission denied.", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == PERMISSION_STORAGE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //actions if granted ang permission sa gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "SELECT_FILE"), SELECT_FILE);
            }
            else{
                Toast.makeText(BabyRegister.this, "Permission denied.", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void onRadioButtonClicked(View view)
    {
        int radioId = rdGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        gender = radioButton.getText().toString();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK){

            if(requestCode== REQUEST_CAMERA) {
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String pictureFile = "Campic_" + timeStamp;
                String filename = pictureFile + ".jpg";

                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data"); //get bitmap data
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ProgressDialog progressDialog = ProgressDialog.show(BabyRegister.this, "Please wait...", "Uploading...", true);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataa = baos.toByteArray(); //convert bitmap photo to bytes
                mStorageRef = storage.getReferenceFromUrl("gs://caireen-d548e.appspot.com"); //storage reference sa firebase
                UploadTask uploadTask = mStorageRef.child("Photos/").child(filename).putBytes(dataa); //upload the photo sa reference
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(BabyRegister.this, "Please try again later.", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        newRef.child("img_path").setValue(downloadUrl.toString()); //put the link of the photo to database
                        imageView2.setImageBitmap(bmp);
                        progressDialog.dismiss();
                    }
                });

            }else if(requestCode == SELECT_FILE){
                final ProgressDialog progressDialog = ProgressDialog.show(BabyRegister.this, "Please wait...", "Uploading...", true);
                final Uri selectedImageUri = data.getData();
                StorageReference filepath = mStorageRef.child("Photos").child(selectedImageUri.getLastPathSegment());
                filepath.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //para mabutang ang link sa pic sa database
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        String link = downloadUrl.toString();
                        newRef.child("img_path").setValue(link);
                        imageView2.setImageURI(selectedImageUri);
                        //  Picasso.get().load(selectedImageUri).into(imageView2);
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(BabyRegister.this, "Please try again later.", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }
    }

    public void openHome(){
        babyname = findViewById(R.id.babynamestring);
        babybday = findViewById(R.id.babybday);
        String babynamestr = babyname.getText().toString();
        String babybdaystr = babybday.getText().toString();

        if (babynamestr.isEmpty()||babybdaystr.isEmpty()){
            Toast.makeText(BabyRegister.this, "Please complete filling the details.", Toast.LENGTH_LONG).show();
        } //to be edited kay wala gagana
        else{
            registerBaby();
        }
        android.content.Intent intent = new Intent(this, HomePageu.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void calMonths(){

        String babybdaystr = babybday.getText().toString();

        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("M/dd/yyyy").parse(babybdaystr);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        LocalDate birthdate = new LocalDate(date1);
        LocalDate now = new LocalDate();

        Period period = new Period(birthdate, now, PeriodType.months());
        int monthseuu = period.getMonths();
        monthseu = Integer.toString(monthseuu);


    }

    public void registerBaby(){
        final ProgressDialog progressDialog = ProgressDialog.show(BabyRegister.this, "Please wait...", "Processing...", true);

        calMonths();

        String babynamestr = babyname.getText().toString();
        String babybdaystr = babybday.getText().toString();

        myRef.child(refkey).child("baby_id").setValue(refkey);
        myRef.child(refkey).child("baby_name").setValue(babynamestr); //input sa database
        myRef.child(refkey).child("baby_bday").setValue(babybdaystr); //input sa database
        myRef.child(refkey).child("baby_gender").setValue(gender); //input sa database
        myRef.child(refkey).child("age_in_months").setValue(monthseu); //input sa database
        //  database.setPersistenceEnabled(true);
        progressDialog.dismiss();
    }
}

