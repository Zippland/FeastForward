package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.FileHelper;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class DataActivity extends AppCompatActivity {

    private static final String TAG = "DataActivity";
    private EditText recipeInput;
    private EditText expiredInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        recipeInput = findViewById(R.id.recipe_input);
        expiredInput = findViewById(R.id.expired_input);

        //添加数据
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> {
            View rootView = getWindow().getDecorView().getRootView();
            String recipeName = recipeInput.getText().toString();
            String expiredTime = expiredInput.getText().toString();
            if (!recipeName.isEmpty() && !expiredTime.isEmpty()) {
                addRecipe(recipeName, expiredTime, 1);
                //清空输入
                recipeInput.setText("");
                expiredInput.setText("");
                showContextMenu("数据添加成功！");
            } else {
                showContextMenu("必填项不能为空");
            }
        });

        //读取所有数据并显示
        Button displayButton = findViewById(R.id.display_button);
        displayButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecipesShowActivity.class);
            startActivity(intent);
        });

        Button searchButton = findViewById(R.id.button);
        //搜索按钮
        searchButton.setOnClickListener(v -> {

        });
    }

    //添加数据
    private void addRecipe(String recipeName, String expireDate, int userId) {
        String entry = recipeName + "," + expireDate + "," + userId;
        writeEntryToDataset(entry);
    }

    //写入数据
    private void writeEntryToDataset(String entry) {
        File file = new File(getFilesDir(), "dataset.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(entry);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //显示成功弹窗
    private void showContextMenu(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle("状态面板")
                .setNegativeButton("确定", (dialog, id) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
