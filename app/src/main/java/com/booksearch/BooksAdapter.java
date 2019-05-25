package com.booksearch;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.Instant;
import java.util.ArrayList;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder>{

    ArrayList<Book> books;

    public BooksAdapter(ArrayList<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.book_list_item,viewGroup,false);


        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder bookViewHolder, int i) {
        Book book = books.get(i);
        bookViewHolder.bind(book);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle;
        TextView tvAuthors;
        TextView tvDate;
        TextView tvPublisher;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvTitle = (TextView)itemView.findViewById(R.id.tvTitle);
            this.tvAuthors = (TextView)itemView.findViewById(R.id.tvAuthors);
            this.tvDate = (TextView)itemView.findViewById(R.id.tvDate);
            this.tvPublisher = (TextView)itemView.findViewById(R.id.tvPublisher);
            itemView.setOnClickListener(this);
        }
        public void bind(Book book){
            tvTitle.setText(book.title);
            tvDate.setText(book.publishData);
            tvPublisher.setText(book.publisher);
            tvAuthors.setText(book.authors);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Book selectedBook = books.get(position);
            Intent intent = new Intent(v.getContext(),BookDetail.class);
            intent.putExtra("Book",selectedBook);
            v.getContext().startActivity(intent);
        }
    }
}
