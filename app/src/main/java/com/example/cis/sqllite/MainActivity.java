package com.example.cis.sqllite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    DBHelper myDBHelper;
    SQLiteDatabase db;

    ListView myList;
    Spinner mySpinner;
    ArrayAdapter<String> myListAdapter;
    ArrayAdapter<String> mySpinnerAdapter;

    ArrayList<String> RNameList;

    EditText searchText;
    Button searchBtn;
    TextView noResult;

    String[] categoryArr = {"All", "Main Dish", "Side Dish", "Salad", "Soup", "Desserts", "None"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myList = findViewById(R.id.myList);
        mySpinner = findViewById(R.id.mySpinner);
        searchText = findViewById(R.id.searchText);
        searchBtn = findViewById(R.id.searchBtn);
        noResult = findViewById(R.id.noResult);

        mySpinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categoryArr);
        mySpinner.setAdapter(mySpinnerAdapter);

        noResult.setVisibility(View.GONE);

        String allQuery = "select * from recipe";



        myDBHelper = new DBHelper(this);
        try{
            myDBHelper.createDataBase();
        }catch(IOException ioe){
            throw new Error("unable to create");
        }
        try{
            myDBHelper.openDataBase();
        }catch(android.database.SQLException sql){

        }
        db=myDBHelper.getReadableDatabase();


        getResults(allQuery);

    }

    public void getResults(String q){

        RNameList = new ArrayList<String>();

        Cursor result = db.rawQuery(q, null);
        result.moveToFirst();
        int count = result.getCount();
        if(count >=1){
            //have results
            do{
                RNameList.add(result.getString(1));
            }while(result.moveToNext());

        }else{
            //no results=
        }

        myListAdapter = new ArrayAdapter<String>(this, R.layout.list_item, RNameList);
        myList.setAdapter(myListAdapter);
    }
}
