package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.Utils.DataUtil;

public class DataActivity extends AppCompatActivity {

    private static final String TAG = "DataActivity";
    private EditText recipeInput;
    private EditText expiredInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        DataUtil.init(this);
        recipeInput = findViewById(R.id.recipe_input);
        expiredInput = findViewById(R.id.expired_input);

        //add data
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> {
            View rootView = getWindow().getDecorView().getRootView();
            String recipeName = recipeInput.getText().toString();
            String expiredTime = expiredInput.getText().toString();
            if (!recipeName.isEmpty() && !expiredTime.isEmpty()) {
                addRecipe(recipeName, expiredTime, 1);
                //clear input
                recipeInput.setText("");
                expiredInput.setText("");
                showContextMenu("Data added successfully！");
            } else {
                showContextMenu("Mandatory fields cannot be blank");
            }
        });

        //Read all data and display
        Button displayButton = findViewById(R.id.display_button);
        displayButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FoodShowActivity.class);
            startActivity(intent);
        });

        Button searchButton = findViewById(R.id.button);
        //search
        searchButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FoodSearchActivity.class);
            startActivity(intent);
        });
    }

    //add data
    private void addRecipe(String recipeName, String expireDate, int userId) {
        String entry = recipeName + "," + expireDate + "," + userId;
        DataUtil.writeEntryToDataset(entry);
    }

    //Show success popup
    private void showContextMenu(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle("status panel")
                .setNegativeButton("OK", (dialog, id) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
