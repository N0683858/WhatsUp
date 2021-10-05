package com.hamidraza.whatsup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartPage extends AppCompatActivity {

    private Button register_btn, login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        // =========== REGISTER BUTTON CLICKED ============== //
        login_btn = findViewById(R.id.start_login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_intent = new Intent(StartPage.this, LoginActivity.class);
                startActivity(login_intent);
            }
        });

        // =========== REGISTER BUTTON CLICKED ============== //
        register_btn = findViewById(R.id.start_register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(StartPage.this, RegisterActivity.class);
                startActivity(reg_intent);
            }
        });
    }
}