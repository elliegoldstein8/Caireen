package com.example.queenelizabethviii.caireen2;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddDiary extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); //para maidentify kinsa na user sa firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();  //for storing data to firebase
    DatabaseReference reRef =  database.getReference("Users").child(firebaseAuth.getCurrentUser().getUid());
    DatabaseReference myRef = reRef.child("baby"); //reference
    DatabaseReference lastnajud;
    String timeStamp = new SimpleDateFormat("MM/dd/yy, h:mm a").format(new Date());
    String babyid, babyimage;
    Integer REQUEST_CAMERA=1, SELECT_FILE=0;
    private int PERMISSION_CAMERA =1, PERMISSION_STORAGE=0;
    FirebaseStorage storage = FirebaseStorage.getInstance(); //for storing files sa firebase
    StorageReference mStorageRef = storage.getReferenceFromUrl("gs://caireen-d548e.appspot.com"); //storage reference sa firebase
    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
    EditText title,desc;
    String titles, descs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);
        title = (EditText) findViewById(R.id.titlediary);
        desc = (EditText) findViewById(R.id.descdiary);
        TextView date_time = (TextView) findViewById(R.id.time_date1);
        Button add = (Button) findViewById(R.id.addvaccine);
        date_time.setText(timeStamp);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot child : dataSnapshot.getChildren()){
                    String key = child.getKey();
                    final DatabaseReference newRef = myRef.child(key);
                    final ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            babyid = child.child("baby_id").getValue(String.class);
                            if (babyid != null) {
                                lastnajud = myRef.child(babyid);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                    newRef.addListenerForSingleValueEvent(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titles = title.getText().toString();
                descs = desc.getText().toString();
                if (titles.isEmpty() && descs.isEmpty()){
                    Toast.makeText(AddDiary.this, "Enter details first.", Toast.LENGTH_LONG).show();
                }
                else{
                addRecord();
                addtoActivity();
                }
            }
        });

    }

    private void SelectImage(){

        final CharSequence[] items={"Camera","Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddDiary.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(items[i].equals("Gallery")) {
                    ActivityCompat.requestPermissions(AddDiary.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_STORAGE);

                }else if(items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
                else if(items[i].equals("Camera")) {
                    ActivityCompat.requestPermissions(AddDiary.this,new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA);
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
                Toast.makeText(AddDiary.this, "Permission denied.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(AddDiary.this, "Permission denied.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*** NOTE: DAPAT DLI KAUPLOAD UG PICTURE ANG USER UG PHOTO OFFLINE KAY DLI SUPPORTED SA FIREBASE ANG OFFLINE STORING OF PHOTOS
     * ENABLE ONDISCONNECT() METHOD PARA MABAL AN KUNG OFFLINE ANG USER OR DLI***/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        final DatabaseReference  lastlastref = myRef.child(babyid).child("baby_features").child("baby_diary").push();
        final ImageView photo = (ImageView) findViewById(R.id.add_photo_diary);

        if(resultCode== Activity.RESULT_OK){

            if(requestCode== REQUEST_CAMERA) {
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String pictureFile = "Campic_" + timeStamp;
                final String filename = pictureFile + ".jpg";

                Bundle bundle = data.getExtras();
                final Bitmap bmp = (Bitmap) bundle.get("data"); //get bitmap data
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ProgressDialog progressDialog = ProgressDialog.show(AddDiary.this, "Please wait...", "Uploading...", true);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] dataa = baos.toByteArray(); //convert bitmap photo to bytes

                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean connected = dataSnapshot.getValue(Boolean.class);
                        if (connected){
                            UploadTask uploadTask = mStorageRef.child("Photos/").child(filename).putBytes(dataa); //upload the photo sa reference
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddDiary.this, "Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    babyimage = downloadUrl.toString();
                                    photo.setImageBitmap(bmp);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        else{
                            //AlertDialog alertDialog = new Alert
                            progressDialog.dismiss();
                            AlertDialog.Builder adb = new AlertDialog.Builder(AddDiary.this);
                            adb.setTitle("You cannot upload photo offline.");
                            adb.setIcon(R.drawable.ic_signal_wifi_off_black_24dp);
                            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            adb.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }else if(requestCode == SELECT_FILE){
                final ProgressDialog progressDialog = ProgressDialog.show(AddDiary.this, "Please wait...", "Uploading...", true);
                final Uri selectedImageUri = data.getData();
                final StorageReference filepath = mStorageRef.child("Photos/").child(selectedImageUri.getLastPathSegment());

                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean connected = dataSnapshot.getValue(Boolean.class);
                        if (connected){
                            filepath.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    babyimage = downloadUrl.toString();
                                    photo.setImageURI(selectedImageUri);
                                    //  Picasso.get().load(selectedImageUri).into(imageView2);
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddDiary.this, "Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else{
                            progressDialog.dismiss();
                            AlertDialog.Builder adb = new AlertDialog.Builder(AddDiary.this);
                            adb.setTitle("You cannot upload photo offline.");
                            adb.setIcon(R.drawable.ic_signal_wifi_off_black_24dp);
                            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            adb.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        }
    }

    public void addRecord(){
        titles = title.getText().toString();
        descs = desc.getText().toString();

            DatabaseReference lastlastref = myRef.child(babyid).child("baby_features").child("baby_diary").push();

            lastlastref.child("diary_title").setValue(titles);
            lastlastref.child("diary_description").setValue(descs);
            lastlastref.child("date_time").setValue(timeStamp);
            lastlastref.child("img_path").setValue(babyimage);

            android.content.Intent intent = new Intent(this, GrowthDiary.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

    }

    public void addphoto(View view){
        ImageView photo = (ImageView) findViewById(R.id.add_photo_diary);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
    }

    public void addtoActivity(){
        DatabaseReference Refu = lastnajud.child("Activities").push();
        Refu.child("Activity").setValue("Diary");
        Refu.child("Timestamp").setValue(timeStamp);
    }
}
