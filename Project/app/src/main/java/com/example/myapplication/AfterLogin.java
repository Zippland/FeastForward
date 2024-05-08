package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AfterLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);

        Button btnRecipeSearch = findViewById(R.id.btnRecipeSearch);
        Button btnDataActivity = findViewById(R.id.btnDataActivity);
        Button btnExpireFoodAlert = findViewById(R.id.btnExpireFoodAlert);

        btnRecipeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AfterLogin.this, RecipeSearch.class));
            }
        });

        btnDataActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AfterLogin.this, DataActivity.class));
            }
        });

        btnExpireFoodAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AfterLogin.this, ExpiredFoodAlert.class));
            }
        });
    }
}
