package com.booksearch;

import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class Book implements Parcelable {

    public String id;
    public String title;
    public String subTitle;
    public String authors;
    public String publisher;
    public String publishData;
    public String description;
    public String thumbnail;

    public Book(String id, String title, String subTitle, String[] authors, String publisher, String publishData, String description, String thumbnail) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.authors = TextUtils.join(", ", authors);
        this.publisher = publisher;
        this.publishData = publishData;
        this.description = description;
        this.thumbnail = thumbnail;
    }


    protected Book(Parcel in) {
        id = in.readString();
        title = in.readString();
        subTitle = in.readString();
        authors = in.readString();
        publisher = in.readString();
        publishData = in.readString();
        description = in.readString();
        thumbnail = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeString(authors);
        dest.writeString(publisher);
        dest.writeString(publishData);
        dest.writeString(description);
        dest.writeString(thumbnail);
    }

    @BindingAdapter("android:imageUrl")
    public static void loadImage(ImageView view, String url) {
        if (!url.isEmpty()) {
            Picasso.with(view.getContext())
                    .load(url)
                    .placeholder(R.drawable.book_open)
                    .into(view);
        }else {
            view.setBackgroundResource(R.drawable.book_open);
        }
    }
}
