package com.example.booksharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

public class Signup extends AppCompatActivity {


    EditText editTextFullName,editTextEmail,editTextPhoneNumber,editTextAddress,editTextPassword,editTextConfirmPassword;
    ImageButton imageButtonLocation;
    Button buttonLogin,buttonSignup;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    ProgressDialog progressDialog;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        mAuth = FirebaseAuth.getInstance();

        editTextFullName = (EditText) findViewById(R.id.ETFullName);
        editTextEmail = (EditText) findViewById(R.id.ETEmailAddress);
        editTextPhoneNumber = (EditText) findViewById(R.id.ETMobileNumber);
        editTextAddress = (EditText) findViewById(R.id.ETAddress);
        editTextPassword = (EditText) findViewById(R.id.ETPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.ETConfirmPassword);

        imageButtonLocation = (ImageButton) findViewById(R.id.ibtnGetLocation);
        buttonLogin = (Button)findViewById(R.id.btnLogin);
        buttonSignup = (Button)findViewById(R.id.btnSignup);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterUser();

            }
        });

        imageButtonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Signup.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    editTextAddress.setText("");
                    getCurrentLocation();
                }
            }
        });

    }

    private void RegisterUser() {
        String FullName,Email,MobileNumber,Address,Password,ConfirmPassword;

        FullName = editTextFullName.getText().toString();
        Email = editTextEmail.getText().toString();
        MobileNumber = editTextPhoneNumber.getText().toString();
        Address = editTextAddress.getText().toString();
        Password = editTextPassword.getText().toString();
        ConfirmPassword = editTextConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(FullName)) {
            Toast.makeText(getApplicationContext(), "Please enter Full Name", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(getApplicationContext(), "Please enter Email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(MobileNumber)) {
            Toast.makeText(getApplicationContext(), "Please enter Mobile Number", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(Address)) {
            Toast.makeText(getApplicationContext(), "Please enter Address", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(Password)) {
            Toast.makeText(getApplicationContext(), "Please enter Password!", Toast.LENGTH_LONG).show();
            return;
        }
        if (Password.length()<6) {
            Toast.makeText(getApplicationContext(), "Password must contain 6 characters", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(ConfirmPassword)) {
            Toast.makeText(getApplicationContext(), "Please enter Confirm Password", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Password.equals(ConfirmPassword)){
            Toast.makeText(getApplicationContext(), "Password not match", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog = new ProgressDialog(Signup.this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase database =  FirebaseDatabase.getInstance();
                    Long tsLong = System.currentTimeMillis()/1000;
                    String userId = tsLong.toString();
                    DatabaseReference mRef =  database.getReference().child("Users").child(userId);
                    mRef.child("FullName").setValue(FullName);
                    mRef.child("Email").setValue(Email);
                    mRef.child("MobileNumber").setValue(MobileNumber);
                    mRef.child("Address").setValue(Address);
                    Intent intent = new Intent(Signup.this, Login.class);
                    startActivity(intent);
                    Toast.makeText(Signup.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(Signup.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void getCurrentLocation() {

        progressDialog = new ProgressDialog(Signup.this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationServices.getFusedLocationProviderClient(Signup.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(Signup.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestlocationindex = locationResult.getLocations().size() - 1;

                            double latitude = locationResult.getLocations().get(latestlocationindex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestlocationindex).getLongitude();


                            getCompleteAddressString(latitude, longitude);
                        } else {

                            progressDialog.dismiss();
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                editTextAddress.setText(strReturnedAddress.toString());
                progressDialog.dismiss();

            } else {

                progressDialog.dismiss();
                Toast.makeText(Signup.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();

            progressDialog.dismiss();
            Toast.makeText(Signup.this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }
}