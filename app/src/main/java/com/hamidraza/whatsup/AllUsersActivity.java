package com.hamidraza.whatsup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mListOfAllUsers;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mToolbar = findViewById(R.id.all_users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Current Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        // ========== Setting Recycler View ============= //
        mListOfAllUsers = findViewById(R.id.list_of_users_recycler_view);
        mListOfAllUsers.setHasFixedSize(true);
        mListOfAllUsers.setLayoutManager(new LinearLayoutManager(this));


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseRecyclerAdapter<UsersModel, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UsersModel, UsersViewHolder>() { // 20.56
//            @Override
//            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull UsersModel model) {
//
//            }
//
//            @NonNull
//            @Override
//            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                return null;
//            }
//        };
//    }


}