package com.example.booksharing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapterViewHolder>{

    Context context;
    private List<BooksModel> booksModelList;
    String Type;

    public BookAdapter(Context context, List<BooksModel> booksModelList, String type) {
        this.context = context;
        this.booksModelList = booksModelList;
        Type = type;
    }

    @Override
    public BookAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_layout,parent,false);
        return new BookAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapterViewHolder holder, int position) {

        final BooksModel booksModel = booksModelList.get(position);

        Glide.with(context).load(booksModelList.get(position).getBookImage()).into(holder.imageViewBookImage);

        holder.textViewTitle.setText(booksModel.getBookTitle());
        holder.textViewAuthor.setText(booksModel.getAuthor());
        holder.textViewEdition.setText("Edition # " + booksModel.getEdition());
        holder.textViewCategory.setText(booksModel.getCategory());
        holder.textViewCondition.setText("Condition: " + booksModel.getCondition());
        holder.textViewPrice.setText("Price $" + booksModel.getPrice());

        holder.imageButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                alertDialog.setTitle("Message");

                alertDialog.setMessage("Do you want to delete book?");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        booksModelList.remove(position);
                        notifyDataSetChanged();
                        FirebaseDatabase database =  FirebaseDatabase.getInstance();
                        DatabaseReference mRef =  database.getReference().child("Books").child(booksModel.getBookID());
                        mRef.removeValue();

                    } });


                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {



                    }});


                alertDialog.show();
            }
        });

        holder.imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditBook.class);
                intent.putExtra("BookID", booksModel.getBookID());
                context.startActivity(intent);
            }
        });

    }

    @Override

    public int getItemCount() {
        return booksModelList.size();
    }
}

class BookAdapterViewHolder extends RecyclerView.ViewHolder{

    TextView textViewTitle,textViewEdition,textViewAuthor,textViewCategory,textViewCondition,textViewPrice;
    ImageView imageViewBookImage;
    ImageButton imageButtonDelete,imageButtonEdit;
    CardView cardView;
    public BookAdapterViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewTitle=itemView.findViewById(R.id.txtTitle);
        textViewEdition=itemView.findViewById(R.id.txtEdition);
        textViewAuthor=itemView.findViewById(R.id.txtAuthor);
        textViewCategory=itemView.findViewById(R.id.txtCategory);
        textViewCondition=itemView.findViewById(R.id.txtCondition);
        textViewPrice=itemView.findViewById(R.id.txtPrice);

        imageViewBookImage=itemView.findViewById(R.id.ivBookImage);

        imageButtonDelete=itemView.findViewById(R.id.ibtnDeleteBook);
        imageButtonEdit=itemView.findViewById(R.id.ibtnEditBook);

        cardView=itemView.findViewById(R.id.cvBook);

    }
}