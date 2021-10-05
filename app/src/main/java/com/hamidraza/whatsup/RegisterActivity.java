package com.hamidraza.whatsup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.auth.FirebaseAuthException;

public class RegisterActivity extends AppCompatActivity {

    // =========== UI ELEMENTS ============= //
    private TextInputLayout
            displayNameTextField,
            emailTextField,
            passwordTextField,
            confirmTextField;

    private Button registerBtn;

    private TextView loginTxtView;

    // =========== FIREBASE =============== //
    private FirebaseAuth mAuth;

    // =========== PROGRESS DIALOG =============== //
    private ProgressDialog mRegProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // =========== INITIALISING UI ELEMENTS ============ //
        displayNameTextField = findViewById(R.id.reg_display_name_tf);
        emailTextField = findViewById(R.id.reg_email_tf);
        passwordTextField = findViewById(R.id.reg_password_tf);
        confirmTextField = findViewById(R.id.reg_confirm_password_tf);

        registerBtn = findViewById(R.id.reg_register_btn);

        loginTxtView = findViewById(R.id.reg_login_tv);

        // =========== PROGRESS DIALOG =============== //
        mRegProgressDialog = new ProgressDialog(this);

        // =========== FIREBASE ================== //
        mAuth = FirebaseAuth.getInstance();

        // =========== REGISTER BUTTON CLICKED ============== //
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        // =========== LOGIN TEXT VIEW CLICKED ============== //
        loginTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(login_intent);
            }
        });
    }

    private void attemptRegistration() {

        // ==== RESET ERRORS ==== //
        emailTextField.setError(null);
        passwordTextField.setError(null);

        // ==== STORE VALUES INTO VARIABLES ==== //
        String email = emailTextField.getEditText().getText().toString();
        String password = passwordTextField.getEditText().getText().toString();

        boolean cancelRegisteration = false;
        View focusView = null;

        // =========== GIVE USER FEEDBACK ====================== //
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();

        // =========== DETAILS VALIDATION ======================== //
        if(TextUtils.isEmpty(password) || !isPasswordValid(password))
        {
            passwordTextField.setError("Password too short or does not match!"); // WILL DISPLAY AN ERROR MESSAGES
            focusView = passwordTextField; // FOCUS ON PASSWORD TEXT VIEW
            cancelRegisteration = true; // DON'T REGISTER USER DETAILS INTO THE FIREBASE DATABASE
        }

        // ==== CHECK IF EMAIL IS VALID ==== //
        if(TextUtils.isEmpty(email))
        {
            emailTextField.setError("This field cannot be left empty!");
            focusView = emailTextField;
            cancelRegisteration = true;
        }
        else if(!isEmailValid(email))
        {
            emailTextField.setError("This email address in invalid!");
            focusView = emailTextField;
            cancelRegisteration = true;
        }

        if(cancelRegisteration)
        {
            // There was an error and registeration was cancelled
            // focus on the first field in the form
            focusView.requestFocus();
        }
        else // IF THEY PASS ALL THE VALIDATION REGISTER USER DETAILS
        {
            mRegProgressDialog.setTitle("Registering User");
            mRegProgressDialog.setMessage("Please wait while we create your account!");
            mRegProgressDialog.setCanceledOnTouchOutside(false);
            mRegProgressDialog.show();
            createFirebaseUser();
        }
    }

    private boolean isEmailValid(String email)
    {
        // will check if the email string contains '@' symbol and return true, else return false
        return email.contains("@");
    }

    private boolean isPasswordValid(String password)
    {
        String confirmPassword = confirmTextField.getEditText().getText().toString();
        // 'confirm password' matches the the password and is more than 4 characters long
        return confirmPassword.equals(password) && password.length() > 4;
    }

    private void createFirebaseUser()
    {
        String email = emailTextField.getEditText().getText().toString();
        String password = passwordTextField.getEditText().getText().toString();

        // ==================== CREATING A NEW USER AND SAVING THE DETAILS INTO DATABASE =========================== //
        // Register them by email and password of the new created user
        // into the firebase database using mAuth object
        // this method returns a task which can be used to listen to that event which is being created.
        // This listener with onComplete() method will report back if the new user has been successfully
        // been created on the firebase database or not
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d("Register", "createUser onComplete: " + task.isSuccessful());

                if(!task.isSuccessful())
                {
                    mRegProgressDialog.hide();
                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                    Log.d("Register", "user creation failed" + e.getMessage());
                    // Show error dialog to show the user the registration was not successful
                    Toast.makeText(RegisterActivity.this, "Cannot create account. Please try again.", Toast.LENGTH_SHORT).show();
                    showErrorDialog("Registration Failed!");

                }
                else
                {
                    mRegProgressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "New User Created!", Toast.LENGTH_SHORT).show();
                    Intent main_intent = new Intent(getApplicationContext(), MainActivity.class);
                    main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(main_intent);
                    finish();
                }
            }
        });
    }

    private void showErrorDialog(String message)
    {
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}