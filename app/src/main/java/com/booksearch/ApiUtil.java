package com.booksearch;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApiUtil {


    private ApiUtil() {
    }

    public static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes/";
    private static final String QUERY_PARAM_KEY = "q";
    private static final String TITLE = "intitle:";
    private static final String AUTHOR = "inauthor:";
    private static final String PUBLISHER = "inpublisher:";
    private static final String ISBN = "isbn:";


    public static URL buildURL(String title) {


        URL url = null;
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM_KEY, title)
                //.appendQueryParameter(KEY,API_KEY)
                .build();

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildURL(String title, String authors, String publisher, String ISBNpr) {
        URL url = null;
        StringBuilder builder = new StringBuilder();

        if (!title.isEmpty()) builder.append(TITLE + title + "+");
        if (!authors.isEmpty()) builder.append(AUTHOR + authors + "+");
        if (!publisher.isEmpty()) builder.append(PUBLISHER + publisher + "+");
        if (!ISBNpr.isEmpty()) builder.append(ISBN + ISBNpr + "+");
        builder.setLength(builder.length() - 1);
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM_KEY, builder.toString())
                //.appendQueryParameter(KEY,API_KEY)
                .build();

        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getJSON(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = httpURLConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasData = scanner.hasNext();
            if (hasData) {
                return scanner.next();
            }
            return null;
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
            return null;
        } finally {
            httpURLConnection.disconnect();
        }
    }

    public static ArrayList<Book> getBooksFromJson(String json) {
        final String ID = "id";
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISH_DATA = "publishedDate";
        final String ITEMS = "items";
        final String VOLUME_INFO = "volumeInfo";
        final String DESCRIPTON = "description";
        final String IMAGE_LINK = "imageLinks";
        final String THUMBNAIL = "thumbnail";

        ArrayList<Book> books = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray booksArray = jsonObject.getJSONArray(ITEMS);

            int numberOfBOOKS =  0;
            if(booksArray!=null){
                numberOfBOOKS = booksArray.length();
            }

            for (int i = 0; i < numberOfBOOKS; i++) {
                JSONObject jsonBook = booksArray.getJSONObject(i);


                JSONObject volumeInfoJSON = jsonBook.getJSONObject(VOLUME_INFO);

                int authorNum = volumeInfoJSON.getJSONArray(AUTHORS).length();
                JSONObject imageLinksJSON = volumeInfoJSON.getJSONObject(IMAGE_LINK);

                String[] authors = new String[authorNum];

                for (int j = 0; j < authorNum; j++) {
                    authors[j] = volumeInfoJSON.getJSONArray(AUTHORS).get(j).toString();
                }

                Book book = new Book(
                        (volumeInfoJSON.isNull(ID)) ? "" : jsonBook.getString(ID),
                        (volumeInfoJSON.isNull(TITLE)) ? "" : volumeInfoJSON.getString(TITLE),
                        (volumeInfoJSON.isNull(SUBTITLE)) ? "" : volumeInfoJSON.getString(SUBTITLE),
                        authors,
                        (volumeInfoJSON.isNull(PUBLISHER)) ? "" : volumeInfoJSON.getString(PUBLISHER),
                        (volumeInfoJSON.isNull(PUBLISH_DATA)) ? "" : volumeInfoJSON.getString(PUBLISH_DATA),
                        (volumeInfoJSON.isNull(DESCRIPTON)) ? "" : volumeInfoJSON.getString(DESCRIPTON),
                        (imageLinksJSON == null) ? "" : imageLinksJSON.getString(THUMBNAIL)
                );
                books.add(book);
            }
        } catch (JSONException e) {
            Log.d("error", e.getMessage());
        }
        return books;
    }
}
