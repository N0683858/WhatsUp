package com.hamidraza.whatsup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mNewStatus;
    private Button mSaveBtn;

    // FIREBASE //
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mFirebaseUser;

    //progress
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        // Firebase
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUID = mFirebaseUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);


        // =========== TOOLBAR =============== //
        mToolbar = findViewById(R.id.status_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String statusValue = getIntent().getStringExtra("status_value");

        mNewStatus = findViewById(R.id.status_input);
        mSaveBtn = findViewById(R.id.save_status_btn);
        mNewStatus.getEditText().setText(statusValue);

        // ================ Save Button Clicked ====================== //
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // progress
                mProgressDialog = new ProgressDialog(StatusActivity.this);
                mProgressDialog.setTitle("Saving Changes");
                mProgressDialog.setMessage("Please wait while we save the changes");
                mProgressDialog.show();

                String status = mNewStatus.getEditText().getText().toString();
                mDatabaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Status Updated!", Toast.LENGTH_LONG).show();
                            Intent settingsIntent = new Intent(StatusActivity.this, SettingsActivity.class);
                            startActivity(settingsIntent);
                        }
                        else{

                            Toast.makeText(getApplicationContext(), "There was an error in saving changes.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }
}