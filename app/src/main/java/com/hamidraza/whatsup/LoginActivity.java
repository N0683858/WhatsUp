package com.hamidraza.whatsup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // =========== UI ELEMENTS ============= //
    private TextInputLayout
            emailTextField,
            passwordTextField;

    private Button loginBtn;
    private TextView regTxtView;

    // ========== FIREBASE ============ //
    private FirebaseAuth mAuth;

    // =========== PROGRESS DIALOG =============== //
    private ProgressDialog mloginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTextField = findViewById(R.id.login_email_tf);
        passwordTextField = findViewById(R.id.login_password_tf);

        regTxtView = findViewById(R.id.login_register_tv);

        loginBtn = findViewById(R.id.login_login_btn);

        // =========== PROGRESS DIALOG =============== //
        mloginProgressDialog = new ProgressDialog(this);

        // =========== FIREBASE ==================== //
        mAuth = FirebaseAuth.getInstance();

        // =========== LOGIN BUTTON CLICKED ============== //
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mloginProgressDialog.setTitle("Logging In");
                mloginProgressDialog.setMessage("Please wait while we check your credentials.");
                mloginProgressDialog.setCanceledOnTouchOutside(false);
                mloginProgressDialog.show();
                validateLogin();
            }
        });

        // =========== REGISTER TEXT VIEW CLICKED ============== //
        regTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(reg_intent);
            }
        });
    }

    private void validateLogin()
    {
        final String email = emailTextField.getEditText().getText().toString();
        final String password = passwordTextField.getEditText().getText().toString();

        emailTextField.setError(null);

        // =========== CHECK IF TEXT BOX ARE EMPTY ============== //
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {
            emailTextField.setError("Username or Password empty!");
        }
        else {

            // =========== COMMUNICATION WITH FIREBASE ============= //
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //==== CHECK IF LOGIN SUCCESSFUL ====//
                    Log.d("Login", "Login Successful: (signWithEmailPass() onComplete() is: " + task.isSuccessful());

                    //==== WHAT HAPPENS IF LOGIN SUCCESSFUL/NOT SUCCESSFUL ====//
                    if (!task.isSuccessful())
                    {
                        mloginProgressDialog.hide();
                        emailTextField.setError("Username or Password incorrect!");
                        Toast.makeText(LoginActivity.this, "Cannot sign in. Please try again. ", Toast.LENGTH_SHORT).show();

                        //== GET EXCEPTION ==//
                        // Will tell why is was not successful
                        Log.d("Login", "Login not Sucessful: " + task.getException());
                    }
                    else
                    {
                        mloginProgressDialog.dismiss();
                        Intent main_intent = new Intent(getApplicationContext(), MainActivity.class);
                        main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(main_intent);
                        finish();
                    }
                }
            });
        }
    }
}