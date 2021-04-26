package com.example.booksharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditProfile extends AppCompatActivity {


    String ID;
    EditText editTextFullName,editTextEmail,editTextPhoneNumber,editTextAddress;
    Button buttonUpdate;
    ImageButton imageButtonLocation;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EditProfile.this);
        ID = preferences.getString("id", "");

        progressDialog = new ProgressDialog(EditProfile.this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();

        editTextFullName = (EditText) findViewById(R.id.ETFullNameEP);
        editTextEmail = (EditText) findViewById(R.id.ETEmailAddressEP);
        editTextPhoneNumber = (EditText) findViewById(R.id.ETMobileNumberEP);
        editTextAddress = (EditText) findViewById(R.id.ETAddressEP);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(ID);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String FullName = dataSnapshot.child("FullName").getValue().toString();
                String Email = dataSnapshot.child("Email").getValue().toString();
                String MobileNumber = dataSnapshot.child("MobileNumber").getValue().toString();
                String Address = dataSnapshot.child("Address").getValue().toString();

                editTextFullName.setText(FullName);
                editTextEmail.setText(Email);
                editTextPhoneNumber.setText(MobileNumber);
                editTextAddress.setText(Address);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        imageButtonLocation = (ImageButton) findViewById(R.id.ibtnGetLocationEP);
        imageButtonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditProfile.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    editTextAddress.setText("");
                    getCurrentLocation();
                }
            }
        });

        buttonUpdate = (Button) findViewById(R.id.btnUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateData();
            }
        });
    }

    private void UpdateData() {
        progressDialog = new ProgressDialog(EditProfile.this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(ID);
        Map<String, Object> updates = new HashMap<String,Object>();

        updates.put("FullName", editTextFullName.getText().toString().trim());
        updates.put("Address", editTextAddress.getText().toString().trim());
        updates.put("MobileNumber", editTextPhoneNumber.getText().toString().trim());

        ref.updateChildren(updates);
        super.onBackPressed();
    }


    public void getCurrentLocation() {

        progressDialog = new ProgressDialog(EditProfile.this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        LocationServices.getFusedLocationProviderClient(EditProfile.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(EditProfile.this)
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
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                editTextAddress.setText(strReturnedAddress.toString());
                progressDialog.dismiss();

            } else {

                progressDialog.dismiss();
                Toast.makeText(EditProfile.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();

            progressDialog.dismiss();
            Toast.makeText(EditProfile.this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }
}