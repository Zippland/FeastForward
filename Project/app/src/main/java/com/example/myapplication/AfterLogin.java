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
        int userId = getIntent().getIntExtra("USER_ID", -1);
        String username = getIntent().getStringExtra("USER_NAME");
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
                //startActivity(new Intent(AfterLogin.this, ExpiredFoodAlert.class));
                Intent intent = new Intent(AfterLogin.this, ExpiredFoodAlert.class);
                intent.putExtra("USER_ID", userId);  // Pass user ID to ExpiredFoodAlert
                intent.putExtra("USER_NAME", username);
                startActivity(intent);
            }
        });
    }
}
