package com.booksearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final EditText etTitle = (EditText)findViewById(R.id.etTitle);
        final EditText etAuthor = (EditText)findViewById(R.id.etAuthor);
        final EditText etPublisher = (EditText)findViewById(R.id.etPublisher);
        final EditText isbn = (EditText)findViewById(R.id.etISBN);
        final Button button = (Button) findViewById(R.id.btnSearch);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String author = etAuthor.getText().toString();
                String publisher = etPublisher.getText().toString();
                String isbnText = isbn.getText().toString();

                if(title.isEmpty() && author.isEmpty()
                        && publisher.isEmpty() && isbnText.isEmpty()){
                    String message = getString(R.string.no_search_data);
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                }else {
                    URL queryUrl = ApiUtil.buildURL(title,author,publisher,isbnText);
                    //Shared preferences
                    Context context = getApplicationContext();
                    int position = SpUtil.getPreferenceInt(context,SpUtil.POSITION);

                    if(position == 0 || position == 5){
                        position = 1;
                    }else {
                        position ++;
                    }
                    String key = SpUtil.QUERY + String.valueOf(position);
                    String value = title +"," +author +","+ publisher + "," + isbnText;
                    SpUtil.setPreferenceString(context,key,value);
                    SpUtil.setPreferenceInt(context,SpUtil.POSITION,position);

                    Intent intent = new Intent(getApplicationContext(),BookListActivity.class);
                    intent.putExtra("Query", queryUrl);
                    startActivity(intent);
                }
            }
        });

    }
}
