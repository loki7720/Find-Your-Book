package com.example.booksharing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    String ID;
    TextView textViewName,textViewEmail,textViewPhone,textViewAddress,textViewLogout;
    ImageButton imageButtonEdit;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        ID = preferences.getString("id", "");

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);



        textViewName = (TextView) view.findViewById(R.id.textViewName);
        textViewEmail = (TextView) view.findViewById(R.id.textViewEmail);
        textViewPhone = (TextView) view.findViewById(R.id.textViewPhoneNumber);
        textViewAddress = (TextView) view.findViewById(R.id.textViewAddress);


        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(ID);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String FullName = dataSnapshot.child("FullName").getValue().toString();
                String Email = dataSnapshot.child("Email").getValue().toString();
                String MobileNumber = dataSnapshot.child("MobileNumber").getValue().toString();
                String Address = dataSnapshot.child("Address").getValue().toString();

                textViewName.setText(FullName);
                textViewEmail.setText(Email);
                textViewPhone.setText(MobileNumber);
                textViewAddress.setText(Address);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageButtonEdit = (ImageButton) view.findViewById(R.id.ibtnEdit);
        imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfile.class);
                startActivity(intent);
            }
        });

        textViewLogout = (TextView) view.findViewById(R.id.textViewLogout);
        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("isLoggedIn","0");
                editor.apply();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}