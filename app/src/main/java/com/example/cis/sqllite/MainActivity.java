package com.example.cis.sqllite;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
    ArrayList<String> RIngList;
    ArrayList<String> RPrepList;
    ArrayList<String> RImgList;
    ArrayList<Integer> RIdList;
    ArrayList<Float> RRatingList;

    int arrayIndex = -1;
    String txt;

    Boolean first = true;

    Intent foodDetails;

    EditText searchText;
    Button searchBtn;
    TextView noResult;
    String allQuery = "select * from recipe";
    String[] categoryArr = {"All", "Main Dish", "Side Dish", "Salad", "Soup", "Desserts", "None", "Sort By Rating"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = "";

        myList = findViewById(R.id.myList);
        mySpinner = findViewById(R.id.mySpinner);
        searchText = findViewById(R.id.searchText);
        searchBtn = findViewById(R.id.searchBtn);
        noResult = findViewById(R.id.noResult);


        foodDetails = new Intent(this, foodDetails.class);


        mySpinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, categoryArr);
        mySpinner.setAdapter(mySpinnerAdapter);


        noResult.setVisibility(View.GONE);


        myDBHelper = new DBHelper(this);
        try {
            myDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("unable to create");
        }
        try {
            myDBHelper.openDataBase();
        } catch (android.database.SQLException sql) {

        }
        db = myDBHelper.getReadableDatabase();


        getResults(allQuery);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cat = categoryArr[position];
                String catQuery;
                String orderByRatingQuery="select * from recipe order by rating DESC;";
                if (position != 0 && position != 6 && position != 7) {
                    catQuery = "select * from recipe where category = '" + cat + "';";


                    if (position == 7)
                        getResults(orderByRatingQuery);

                        if (first) first = false;
                    if (!first && position != 6)
                        getResults(catQuery);
                    if (position != 6)
                        searchText.setText("");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt = searchText.getText().toString();
                String searchQuery = "select * from recipe where ingredients like '%" + txt + "%';";
                getResults(searchQuery);


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                mySpinner.setSelection(6);

            }
        });

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                foodDetails.putExtra("foodName", RNameList.get(position));
                foodDetails.putExtra("foodIng", RIngList.get(position));
                foodDetails.putExtra("foodPrep", RPrepList.get(position));
                foodDetails.putExtra("foodImg", RImgList.get(position));
                foodDetails.putExtra("foodId", RIdList.get(position));
                foodDetails.putExtra("foodRating", RRatingList.get(position));
                arrayIndex = 1;
                MainActivity.this.startActivityForResult(foodDetails, 1);
            }

        });

    }

    public void getResults(String q) {

        RNameList = new ArrayList<String>();
        RIngList = new ArrayList<String>();
        RPrepList = new ArrayList<String>();
        RImgList = new ArrayList<String>();
        RIdList = new ArrayList<Integer>();
        RRatingList = new ArrayList<>();

        Cursor result = db.rawQuery(q, null);
        result.moveToFirst();
        int count = result.getCount();
        if (count >= 1) {
            //have results
            noResult.setVisibility(View.GONE);
            myList.setVisibility(View.VISIBLE);
            do {
                RNameList.add(result.getString(1));
                RIngList.add(result.getString(2));
                RPrepList.add(result.getString(3));
                RImgList.add(result.getString(5));
                RIdList.add(result.getInt(0));
                RRatingList.add(result.getFloat(6));

            } while (result.moveToNext());

        } else {
            //no results=
            noResult.setVisibility(View.VISIBLE);
            myList.setVisibility(View.GONE);
        }

        myListAdapter = new ArrayAdapter<String>(this, R.layout.list_item, RNameList);
        myList.setAdapter(myListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        float newR = 0f;
        if (requestCode == 1) {
            if (resultCode == RESULT_OK)
                newR = data.getFloatExtra("newRating", 0);

            RRatingList.set(arrayIndex, newR);
        }
    }
}
