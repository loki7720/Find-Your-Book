package com.example.booksharing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CatalogFragment extends Fragment {

    public CatalogFragment() {
        // Required empty public constructor
    }
   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    FloatingActionButton floatingActionButtonAdd;

    RecyclerView recyclerView;
    BookAdapter bookAdapter;
    List<BooksModel> booksModelList;
    BooksModel booksModel;

    LinearLayout linearLayoutNoBook;

    String ID = "";
    Boolean data = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_catalog, container, false);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        ID = preferences.getString("id", "");

        floatingActionButtonAdd = (FloatingActionButton) view.findViewById(R.id.fbtnAdd);
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data = false;
                Intent intent = new Intent(getContext(), PostBook.class);
                startActivity(intent);
            }
        });

        linearLayoutNoBook = (LinearLayout) view.findViewById(R.id.noBook);
        recyclerView = (RecyclerView) view.findViewById(R.id.BookRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        booksModelList=new ArrayList<>();
        bookAdapter = new BookAdapter(getContext(),booksModelList,"MyCatalog");
        recyclerView.setAdapter(bookAdapter);


        return view;
    }



    private void fetchMyBooksList() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Query query = FirebaseDatabase.getInstance().getReference("Books");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()){
                    linearLayoutNoBook.setVisibility(View.VISIBLE);
                }else {

                    linearLayoutNoBook.setVisibility(View.GONE);
                }
                progressDialog.dismiss();

                if (data == true) {
                    booksModelList.clear();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String BookID, BookTitle, Author, Edition,ISBN, PostedBy, PostedDate, Category,Condition,Price,BookImage;
                        if (ID.equals(childSnapshot.child("PostedBy").getValue().toString())) {

                            BookID = childSnapshot.getKey().toString();
                            BookTitle = childSnapshot.child("BookTitle").getValue().toString();
                            Author = childSnapshot.child("Author").getValue().toString();
                            Edition = childSnapshot.child("Edition").getValue().toString();
                            ISBN = childSnapshot.child("ISBN").getValue().toString();
                            PostedBy = childSnapshot.child("PostedBy").getValue().toString();
                            PostedDate = childSnapshot.child("PostedDate").getValue().toString();
                            Category = childSnapshot.child("Category").getValue().toString();
                            Condition = childSnapshot.child("Condition").getValue().toString();
                            Price = childSnapshot.child("Price").getValue().toString();
                            BookImage = childSnapshot.child("BookImage").getValue().toString();


                            booksModel = new BooksModel(BookID, BookTitle, Author, Edition,ISBN, PostedBy, PostedDate, Category,Condition,Price,BookImage);
                            booksModelList.add(booksModel);
                            bookAdapter.notifyDataSetChanged();
                        }
                    }

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        data = true;
        fetchMyBooksList();
    }

}