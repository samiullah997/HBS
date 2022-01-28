package com.example.hbs.Activites;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hbs.R;
import com.example.hbs.Utilities.Constants;
import com.example.hbs.Utilities.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private EditText inputFirstName, inputLastName, inputEmail, inputPassword, inputConfirmPassword;
    private MaterialButton buttonSignUp;
    private ProgressBar signUpProgressBar;
    private PreferenceManager preferenceManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        preferenceManager=new PreferenceManager(getApplicationContext());
        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.textSignIn).setOnClickListener(v -> onBackPressed());
        inputFirstName=findViewById(R.id.inputFirstName);
        inputLastName=findViewById(R.id.inputLastName);
        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        inputConfirmPassword=findViewById(R.id.inputConfirmPassword);
        buttonSignUp=findViewById(R.id.buttonSingUp);
        signUpProgressBar=findViewById(R.id.signUpProgressBar);

        buttonSignUp.setOnClickListener(v -> validateForm());


    }
    public void validateForm(){
        if(inputFirstName.getText().toString().trim().isEmpty()){
            inputFirstName.setError("Enter Name Here");
        }else if(inputLastName.getText().toString().trim().isEmpty()){
            inputLastName.setError("Enter Last Name Here");
        }else if(inputEmail.getText().toString().trim().isEmpty()){
            inputEmail.setError("Enter Email Here");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()){
            inputEmail.setError("Enter Valid Email");
        }else if(inputPassword.getText().toString().trim().isEmpty()){
            inputPassword.setError("Enter Password Here");
        }else if(inputConfirmPassword.getText().toString().trim().isEmpty()){
            inputConfirmPassword.setError("Enter Confirm Password Here");
        }else if(!inputPassword.getText().toString().trim().equals(inputConfirmPassword.getText().toString().trim())){
            Toast.makeText(this, "Password and Confirm Password must be same", Toast.LENGTH_SHORT).show();
        }
        else{
            signUp();
        }
    }

    public void signUp(){
        buttonSignUp.setVisibility(View.INVISIBLE);
        signUpProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database=FirebaseFirestore.getInstance();
        HashMap<String,Object> user=new HashMap<>();
        user.put(Constants.KEY_FIRST_NAME,inputFirstName.getText().toString().trim());
        user.put(Constants.KEY_LAST_NAME,inputLastName.getText().toString().trim());
        user.put(Constants.KEY_EMAIL,inputEmail.getText().toString().trim());
        user.put(Constants.KEY_PASSWORD,inputPassword.getText().toString().trim());

        database.collection(Constants.KEY_COLLECTION_USERS).add(user).addOnSuccessListener(documentReference -> {
            preferenceManager.putBoolean(Constants.KEY_IS_SIGN_IN,true);
            preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
            preferenceManager.putString(Constants.KEY_FIRST_NAME,inputFirstName.getText().toString().trim());
            preferenceManager.putString(Constants.KEY_LAST_NAME,inputLastName.getText().toString().trim());
            preferenceManager.putString(Constants.KEY_EMAIL,inputEmail.getText().toString().trim());
            Intent intent=new Intent(getApplicationContext(),CategoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            signUpProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "You are Register Successfully", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> {
            signUpProgressBar.setVisibility(View.INVISIBLE);
            buttonSignUp.setVisibility(View.VISIBLE);
            Toast.makeText(Register.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}