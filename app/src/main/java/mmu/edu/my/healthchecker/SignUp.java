package mmu.edu.my.healthchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    EditText mFullName,mEmail,mPassword;
    Button mSignUpBtn;
    TextView mLoginBtn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mFullName = findViewById((R.id.userName));
        mEmail = findViewById((R.id.userEmail));
        mPassword = findViewById((R.id.userPassword));
        mSignUpBtn = findViewById((R.id.signUpButton));
        mLoginBtn = findViewById((R.id.logInButton));

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }



        mSignUpBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is empty");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is empty");
                    return;
                }

                if (password.length() < 6) {
                    mPassword.setError("Password must have at least have 6 character");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUp.this, "User Created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUp.this, "Authentication failed." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));

            }
        });
    }
}