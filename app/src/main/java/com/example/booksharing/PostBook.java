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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostBook extends AppCompatActivity {

    ImageView imageView;
    EditText editTextBookTitle,editTextAuthor,editTextEdition,editTextISBN,editTextPrice;
    Spinner spinnerCategory,spinnerCondition;
    Button buttonPost;
    Uri imageuri;
    String ID = "";
    String Image = null;
    private static final int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_book);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PostBook.this);
        ID = preferences.getString("id", "");

        imageView = (ImageView) findViewById(R.id.ivImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGallery = new Intent();
                intentGallery.setType("image/*");
                intentGallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intentGallery,"Select Image"),PICK_IMAGE);
            }
        });


        editTextBookTitle = (EditText) findViewById(R.id.etBookTitle);
        editTextAuthor = (EditText) findViewById(R.id.etBookAuthor);
        editTextEdition = (EditText) findViewById(R.id.etEdition);
        editTextISBN = (EditText) findViewById(R.id.etISBN);
        editTextPrice = (EditText) findViewById(R.id.etPrice);


        spinnerCategory = (Spinner) findViewById(R.id.spCategory);
        spinnerCondition = (Spinner) findViewById(R.id.spCondition);

        buttonPost = (Button) findViewById(R.id.btnPost);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postBook();
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


    private void postBook() {

        String BookTitle,Author,Edition,ISBN,Price;


        BookTitle = editTextBookTitle.getText().toString().trim();
        Author = editTextAuthor.getText().toString().trim();
        Edition = editTextEdition.getText().toString().trim();
        ISBN = editTextISBN.getText().toString().trim();
        Price = editTextPrice.getText().toString().trim();

        if (TextUtils.isEmpty(Image)) {
            Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_LONG).show();
            return;
        }

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

        ProgressDialog progressDialog = new ProgressDialog(PostBook.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        Long tsLong = System.currentTimeMillis()/1000;
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

                                FirebaseDatabase database =  FirebaseDatabase.getInstance();
                                DatabaseReference mRef =  database.getReference().child("Books").child("id_"+timeStamp);
                                mRef.child("BookTitle").setValue(BookTitle);
                                mRef.child("Author").setValue(Author);
                                mRef.child("Edition").setValue(Edition);
                                mRef.child("ISBN").setValue(ISBN);
                                mRef.child("Category").setValue(spinnerCategory.getSelectedItem().toString().trim());
                                mRef.child("Condition").setValue(spinnerCondition.getSelectedItem().toString().trim());
                                mRef.child("Price").setValue(Price);
                                mRef.child("PostedBy").setValue(ID);
                                mRef.child("PostedDate").setValue(date);
                                mRef.child("BookImage").setValue(downloadUrl.toString());
                                Toast.makeText(PostBook.this, "Successfully Posted", Toast.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        backPresse();
                                        progressDialog.dismiss();
                                    }
                                },2000);

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //if the upload is not successful
                        //hiding the progress dialog
                        progressDialog.dismiss();
                        //and displaying error message
                        Toast.makeText(PostBook.this, exception.getCause().getLocalizedMessage(), Toast.LENGTH_LONG).show();
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

    private void backPresse() {
        super.onBackPressed();
    }


}