package com.example.booksharing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditBook extends AppCompatActivity {

    ImageView imageView;
    EditText editTextBookTitle,editTextAuthor,editTextEdition,editTextISBN,editTextPrice;
    Spinner spinnerCategory,spinnerCondition;
    Button buttonPost;
    Uri imageuri;
    String Image = "";
    String ID = "",BookID = "";
    String update = "";
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        ProgressDialog progressDialog = new ProgressDialog(EditBook.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EditBook.this);
        ID = preferences.getString("id", "");

        BookID = getIntent().getExtras().getString("BookID");

        imageView = (ImageView) findViewById(R.id.ivImageE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGallery = new Intent();
                intentGallery.setType("image/*");
                intentGallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intentGallery,"Select Image"),PICK_IMAGE);
            }
        });

        editTextBookTitle = (EditText) findViewById(R.id.etBookTitleE);
        editTextAuthor = (EditText) findViewById(R.id.etBookAuthorE);
        editTextEdition = (EditText) findViewById(R.id.etEditionE);
        editTextISBN = (EditText) findViewById(R.id.etISBNE);
        editTextPrice = (EditText) findViewById(R.id.etPriceE);


        spinnerCategory = (Spinner) findViewById(R.id.spCategoryE);
        spinnerCondition = (Spinner) findViewById(R.id.spConditionE);

        buttonPost = (Button) findViewById(R.id.btnPostE);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBook();
            }
        });



            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Books").child(BookID);

            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (update == "") {
                        String Author = dataSnapshot.child("Author").getValue().toString();
                        String BookImage = dataSnapshot.child("BookImage").getValue().toString();
                        String BookTitle = dataSnapshot.child("BookTitle").getValue().toString();
                        String Category = dataSnapshot.child("Category").getValue().toString();
                        String Condition = dataSnapshot.child("Condition").getValue().toString();
                        String Edition = dataSnapshot.child("Edition").getValue().toString();
                        String ISBN = dataSnapshot.child("ISBN").getValue().toString();
                        String Price = dataSnapshot.child("Price").getValue().toString();

                        editTextAuthor.setText(Author);
                        editTextBookTitle.setText(BookTitle);
                        editTextEdition.setText(Edition);
                        editTextISBN.setText(ISBN);
                        editTextPrice.setText(Price);

                        Glide.with(EditBook.this).load(BookImage).into(imageView);

                        progressDialog.dismiss();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imageuri = data.getData();
            imageView.setImageURI(imageuri);
            Image = "Done";
        }
    }

    private void updateBook() {

        String BookTitle,Author,Edition,ISBN,Price;


        BookTitle = editTextBookTitle.getText().toString().trim();
        Author = editTextAuthor.getText().toString().trim();
        Edition = editTextEdition.getText().toString().trim();
        ISBN = editTextISBN.getText().toString().trim();
        Price = editTextPrice.getText().toString().trim();


        if (TextUtils.isEmpty(BookTitle)) {
            Toast.makeText(getApplicationContext(), "Please enter Book Title", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(Author)) {
            Toast.makeText(getApplicationContext(), "Please enter Author Name", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(Edition)) {
            Toast.makeText(getApplicationContext(), "Please enter Book Edition", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(ISBN)) {
            Toast.makeText(getApplicationContext(), "Please enter ISBN", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(Price)) {
            Toast.makeText(getApplicationContext(), "Please enter Price", Toast.LENGTH_LONG).show();
            return;
        }

        if (Image.equals("")) {
            ProgressDialog progressDialog = new ProgressDialog(EditBook.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            update = "Done";
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Books").child(BookID);
            Map<String, Object> updates = new HashMap<String,Object>();
            updates.put("BookTitle", BookTitle);
            updates.put("Author", Author);
            updates.put("Edition", Edition);
            updates.put("ISBN",ISBN);
            updates.put("Category",spinnerCategory.getSelectedItem().toString().trim());
            updates.put("Condition",spinnerCondition.getSelectedItem().toString().trim());
            updates.put("Price",Price);
            Toast.makeText(EditBook.this, "Successfully Updated", Toast.LENGTH_SHORT).show();

            ref.updateChildren(updates);
            backPressed();

            return;
        }

        else {
            ProgressDialog progressDialog = new ProgressDialog(EditBook.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            update = "Done";
            Long tsLong = System.currentTimeMillis() / 1000;
            String timeStamp = tsLong.toString();

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReferenceProfilePic = firebaseStorage.getReference();
            StorageReference imageRef = storageReferenceProfilePic.child("Book Images" + "/" + timeStamp + ".jpg");

            imageRef.putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successful
                            //hiding the progress dialog
                            //and displaying a success toast
                            // String profilePicUrl = taskSnapshot.getDownloadUrl().toString();
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Books").child(BookID);
                                    Map<String, Object> updates = new HashMap<String, Object>();
                                    updates.put("BookTitle", BookTitle);
                                    updates.put("Author", Author);
                                    updates.put("Edition", Edition);
                                    updates.put("ISBN", ISBN);
                                    updates.put("Category", spinnerCategory.getSelectedItem().toString().trim());
                                    updates.put("Condition", spinnerCondition.getSelectedItem().toString().trim());
                                    updates.put("Price", Price);
                                    updates.put("BookImage", downloadUrl.toString());
                                    Toast.makeText(EditBook.this, "Successfully Updated", Toast.LENGTH_SHORT).show();

                                    ref.updateChildren(updates);
                                    backPressed();

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successful
                            //hiding the progress dialog
                            //and displaying error message
                            Toast.makeText(EditBook.this, exception.getCause().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
//                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                        //displaying percentage in progress dialog
//                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });

        }


    }

    private void backPressed() {
        super.onBackPressed();
    }
}