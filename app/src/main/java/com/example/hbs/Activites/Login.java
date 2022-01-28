package com.example.hbs.Activites;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class Login extends AppCompatActivity {
    private EditText inputEmail,inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgressBar;
    private PreferenceManager preferenceManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferenceManager=new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGN_IN)){
            Intent intent=new Intent(getApplicationContext(),CategoryActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        findViewById(R.id.textSignUp).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Register.class)));
        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        buttonSignIn=findViewById(R.id.buttonSingIn);
        signInProgressBar=findViewById(R.id.signInProgressBar);
        buttonSignIn.setOnClickListener(v -> validateForm());
    }
    public void validateForm(){
        if(inputEmail.getText().toString().trim().isEmpty()){
            inputEmail.setError("Enter Email here");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString().trim()).matches()){
            inputEmail.setError("Enter Valid Email Here");
        }else if(inputPassword.getText().toString().trim().isEmpty()) {
            inputPassword.setError("Enter Password Here");
        }else{
            signIn();
        }
    }

    public void signIn(){
        buttonSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS).whereEqualTo(Constants.KEY_EMAIL,inputEmail.getText().toString().trim())
                .whereEqualTo(Constants.KEY_PASSWORD,inputPassword.getText().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
                            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGN_IN,true);
                            preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_FIRST_NAME,documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                            preferenceManager.putString(Constants.KEY_LAST_NAME,documentSnapshot.getString(Constants.KEY_LAST_NAME));
                            preferenceManager.putString(Constants.KEY_EMAIL,documentSnapshot.getString(Constants.KEY_EMAIL));
                             Intent intent=new Intent(getApplicationContext(),CategoryActivity.class);
                             intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                             startActivity(intent);
                            signInProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(Login.this, " you are login Successfully", Toast.LENGTH_SHORT).show();
                             finish();

                        }else{
                            signInProgressBar.setVisibility(View.INVISIBLE);
                            buttonSignIn.setVisibility(View.VISIBLE);
                            Toast.makeText(Login.this, "Unable to sign In", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}