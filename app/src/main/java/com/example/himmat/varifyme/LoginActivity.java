package com.example.himmat.varifyme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static com.example.himmat.varifyme.R.color.colorAccent;

public class LoginActivity extends AppCompatActivity {


    private EditText opt_sand;
    private EditText phone;
    private Button phonebtn;
    String phoneNumber;
    private ProgressBar progressBar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken token;
    private String mVarificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        opt_sand=findViewById(R.id.opt);
        phone=findViewById(R.id.EnterPhone);
        phonebtn=findViewById(R.id.varifybtn);
        progressBar=findViewById(R.id.prgressbar);
        mAuth=FirebaseAuth.getInstance();
        phonebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               phoneNumber =phone.getText().toString();

               progressBar.setVisibility(View.VISIBLE);
                phonebtn.setEnabled(false);

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + phoneNumber,                // Phone number to verify
                        30,                      // Timeout duration
                        TimeUnit.SECONDS,          // Unit of timeout
                        LoginActivity.this, // Activity (for callback binding)
                        mCallbacks                 // OnVerificationStateChangedCallbacks
                );
            }
        });

     mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
         @Override
         public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

             signInWithPhoneAuthCredential(phoneAuthCredential);

         }

         @Override
         public void onVerificationFailed(FirebaseException e) {

             Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

         }

         @Override
         public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
             super.onCodeSent(s, forceResendingToken);


             //this will sence the code otp automatically
             mVarificationId = s;
             token=forceResendingToken;
         }
     };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                        }
                    }
                });
    }

}