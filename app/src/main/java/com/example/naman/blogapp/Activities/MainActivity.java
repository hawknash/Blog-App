package com.example.naman.blogapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.naman.blogapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private Button loginButton;
    private Button createActButton;
    private EditText emailField;
    private EditText passwordField;
    private Button forgotPassword;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressDialog = new ProgressDialog(this);
        loginButton=(Button)findViewById(R.id.loginButton);
        createActButton=(Button)findViewById(R.id.button2);
        emailField=(EditText)findViewById(R.id.loginEmailEt);
        passwordField=(EditText)findViewById(R.id.loginPasswordEt);
        forgotPassword=(Button)findViewById(R.id.forgotPasswordbutton);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth = FirebaseAuth.getInstance();
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Verifying..");
                progressDialog.show();

                if (!TextUtils.isEmpty(emailField.getText().toString())) {
                    mAuth.sendPasswordResetEmail(emailField.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Reset password instructions has sent to your email",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),
                                                "Email don't exist", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "No such email exists!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,"Please enter the Email!",Toast.LENGTH_LONG).show();
                }


            }
        });

        createActButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,CreateAccountActivity.class));
                finish();

            }
        });


        mAuth = FirebaseAuth.getInstance();

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mUser=firebaseAuth.getCurrentUser();

                if (mUser!=null){

                    Toast.makeText(MainActivity.this,"Signed In",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this,PostListActivity.class));
                    finish();
                }
            }

        };


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(emailField.getText().toString())
                        && !TextUtils.isEmpty(passwordField.getText().toString())){
                    mProgressDialog.setMessage("Please Wait..Signing In");
                    mProgressDialog.show();

                    String email=emailField.getText().toString();
                    String pwd=passwordField.getText().toString();

                    login(email,pwd);

                }else{

                    Toast.makeText(MainActivity.this,"Please enter username/password!",Toast.LENGTH_LONG).show();



                }


            }
        });

    }

    private void login(String email, String pwd) {

        mAuth.signInWithEmailAndPassword(email,pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            mProgressDialog.dismiss();

                            Toast.makeText(MainActivity.this,"Signed in",Toast.LENGTH_LONG).show();

                            startActivity(new Intent(MainActivity.this,PostListActivity.class));
                            finish();
                        }
                        else{
                            mProgressDialog.dismiss();

                           Toast.makeText(MainActivity.this,"User not found!",Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       if(item.getItemId()==R.id.action_signout)
       {
           mAuth.signOut();
       }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
