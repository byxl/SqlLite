package com.example.cis.sqllite;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;

public class foodDetails extends AppCompatActivity {


    DBHelper myDBHelper;
    SQLiteDatabase db;

    TextView foodNameTV;
    TextView foodIngTV;
    TextView foodPrepTV;
    ImageView foodImgView;
    RatingBar ratingBar;
    Button ratingBtn;
    int foodId;
    float foodRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        foodIngTV = findViewById(R.id.foodIngTv);
        foodNameTV = findViewById(R.id.foodNameTV);
        foodPrepTV = findViewById(R.id.foodPrepTV);
        foodImgView = findViewById(R.id.foodImg);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBtn = findViewById(R.id.ratingBtn);

        Intent myIntent = getIntent();
        String foodName = myIntent.getStringExtra("foodName");
        String foodIng = myIntent.getStringExtra("foodIng");
        String foodPrep = myIntent.getStringExtra("foodPrep");
        String foodImg = myIntent.getStringExtra("foodImg");
        foodId = myIntent.getIntExtra("foodId", 0);
        foodRating = myIntent.getFloatExtra("foodRating", 0);

        foodNameTV.setText(foodName);
        foodIngTV.setText(foodIng);
        foodPrepTV.setText(foodPrep);
        ratingBar.setRating(foodRating);


        String extension = "";
        int i = foodImg.lastIndexOf(".");
        if (i > 0)
            extension = foodImg.substring(i);

        foodImg = foodImg.replace(extension, "");

        int id = getResources().getIdentifier(getPackageName() + ":drawable/" + foodImg, null, null);


        foodImgView.setImageResource(id);

        createDB();

        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float r = ratingBar.getRating();
                String updateQuery = "";
                if (foodId != 0) {
                    updateQuery = "update recipe set rating = " + r + " where id = " + foodId + ";";
                    db.execSQL(updateQuery);

                    Intent sentToMain = getIntent();
                    sentToMain.putExtra("newRating", r);
                    setResult(RESULT_OK, sentToMain);
                }
            }
        });


    }

    private void createDB() {
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
        db = myDBHelper.getWritableDatabase();
    }
}
