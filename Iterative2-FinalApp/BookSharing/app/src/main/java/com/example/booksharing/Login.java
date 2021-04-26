package com.example.booksharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {


    EditText editTextEmail,editTextPassword;
    Button buttonLogin,buttonCreateAccount;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.ETEmailAddressLogin);
        editTextPassword = (EditText) findViewById(R.id.ETPasswordLogin);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        buttonCreateAccount = (Button)findViewById(R.id.btnCreateAccount);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });
    }

    private void LoginUser() {
        String Email,Password;

        Email = editTextEmail.getText().toString();
        Password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(getApplicationContext(), "Please enter Email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(Password)) {
            Toast.makeText(getApplicationContext(), "Please enter Password!", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user =  mAuth.getCurrentUser();
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                    Query query = db.orderByChild("Email").equalTo(Email);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("id",dataSnapshot.getKey());
                            editor.putString("isLoggedIn","1");
                            editor.apply();
                            Intent intent = new Intent(Login.this, MainMenu.class);
                            startActivity(intent);
                            finish();

                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}