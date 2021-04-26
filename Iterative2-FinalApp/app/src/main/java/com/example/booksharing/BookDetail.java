package com.example.booksharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookDetail extends AppCompatActivity {


    String ID = "",BookID = "",PostedBy= "";
    ImageView imageView;
    TextView textViewBookTitle,textViewAuthor,textViewEdition,textViewISBN,textViewPrice,textViewCondition,textViewCategory;

    TextView textViewName,textViewEmail,textViewPhone,textViewAddress,textViewLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        ProgressDialog progressDialog = new ProgressDialog(BookDetail.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BookDetail.this);
        ID = preferences.getString("id", "");

        BookID = getIntent().getExtras().getString("BookID");



        imageView = (ImageView) findViewById(R.id.ivBookImg);

        textViewBookTitle = (TextView) findViewById(R.id.txtBookTitle);
        textViewAuthor = (TextView) findViewById(R.id.txtAuthor);
        textViewEdition = (TextView) findViewById(R.id.txtEdition);
        textViewISBN = (TextView) findViewById(R.id.txtISBN);
        textViewPrice = (TextView) findViewById(R.id.txtPrice);
        textViewCondition = (TextView) findViewById(R.id.txtCondition);
        textViewCategory = (TextView) findViewById(R.id.txtCategory);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewPhone = (TextView) findViewById(R.id.textViewPhoneNumber);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);



        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Books").child(BookID);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String Author = dataSnapshot.child("Author").getValue().toString();
                    String BookImage = dataSnapshot.child("BookImage").getValue().toString();
                    String BookTitle = dataSnapshot.child("BookTitle").getValue().toString();
                    String Category = dataSnapshot.child("Category").getValue().toString();
                    String Condition = dataSnapshot.child("Condition").getValue().toString();
                    String Edition = dataSnapshot.child("Edition").getValue().toString();
                    String ISBN = dataSnapshot.child("ISBN").getValue().toString();
                    String Price = dataSnapshot.child("Price").getValue().toString();

                    PostedBy = dataSnapshot.child("PostedBy").getValue().toString();

                DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("Users").child(PostedBy);

                db2.addValueEventListener(new ValueEventListener() {
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

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                    textViewAuthor.setText("By "+Author);
                    textViewBookTitle.setText(BookTitle);
                    textViewEdition.setText("Edition: "+Edition);
                    textViewISBN.setText("ISBN: "+ISBN);
                    textViewPrice.setText("Price: "+Price);
                    textViewCategory.setText("Category: "+Category);
                    textViewCondition.setText("Condition: "+Condition);

                    Glide.with(BookDetail.this).load(BookImage).into(imageView);

                    progressDialog.dismiss();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}