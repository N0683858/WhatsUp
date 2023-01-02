package com.hamidraza.whatsup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.nio.channels.GatheringByteChannel;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    // ======= FIREBASE =========== //
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorageRef;

    // =========== Settings Page Layout ============ //
    private CircleImageView mCircleImageView;
    private TextView dpName, dpStatus;

    private Button mStatusBtn, mImgBtn;

    // ============== Image cropper ========== //
    private static final int GALLERY_PICK = 1;

    // ============== Progress Dialog ============ //
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mCircleImageView = findViewById(R.id.settings_profile_img);
        dpName = findViewById(R.id.settings_display_name_tv);
        dpStatus = findViewById(R.id.settings_status_tv);

        mStatusBtn = findViewById(R.id.settings_status_btn);
        mImgBtn = findViewById(R.id.settings_img_btn);

        // ================ FIREBASE =================== //
        // connecting with the database using reference
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser(); // get user reference
        String currentUID = mCurrentUser.getUid(); // get user id
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID); // point to the correct user in the database

        mImageStorageRef = FirebaseStorage.getInstance().getReference(); // getting storage reference

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Link (+ retrieve) values in database (so they can be changed later)
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String thumbnailImg = snapshot.child("thumbnail_img").getValue().toString();

                dpName.setText(name);
                dpStatus.setText(status);
                //setting image to circle image view
                Picasso.get().load(image).into(mCircleImageView);

            } // ADD ON COMPLETE LISTENER

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String statusValue = dpStatus.getText().toString();
                Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                statusIntent.putExtra("status_value", statusValue);
                startActivity(statusIntent);
            }
        });

        mImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                // choosing image from gallery //
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

//                // start picker to get image for cropping and then use the image in cropping activity
//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingsActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();

            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);

//            Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_SHORT).show();
        }

        // HOW TO HANDLE (CROP IMAGE) CODE FROM GITHUB (modified to fit the project) //
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) // this code runs after user selects cropped image
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                // ==== Progress Dialog === //
                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                // getting image uri and storing it into firebase storage
                Uri resultUri = result.getUri();
                String current_userID = mCurrentUser.getUid();

                final StorageReference filePath = mImageStorageRef.child("profile_images").child(current_userID + ".jpg"); // where to store the image in the firebase storage

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                               final String download_url = String.valueOf(uri);
                                mUserDatabase.child("image").setValue(download_url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this,"Uploaded Successfully!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        mProgressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

//                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//
//                        if(task.isSuccessful()){
//
//                            String downloadURL = task.getResult().getStorage().getDownloadUrl().toString(); // get download url of the image so we can store the string in the database with user info
//                            mUserDatabase.child("image").setValue(downloadURL).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                    if(task.isSuccessful())
//                                    {
//                                        mProgressDialog.dismiss();
//                                        Toast.makeText(SettingsActivity.this, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//                        else{
//                            mProgressDialog.dismiss();
//                            Toast.makeText(SettingsActivity.this, "Error in  Uploading.", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

//    public static String random_string()
//    {
//        Random generator = new Random();
//
//        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(30);
//        char tempChar;
//
//        for (int i = 0; i < randomLength; i++)
//        {
//            tempChar = (char) (generator.nextInt(96) + 32);
//            randomStringBuilder.append(tempChar);
//        }
//
//        return randomStringBuilder.toString();
//    }
}