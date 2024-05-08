package com.example.myapplication;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.RecipeAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class RecipesShowActivity extends AppCompatActivity {
    RecyclerView recipesList;

    RecipeAdapter recipeAdapter;

    List<String[]> recipeData;

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipes_show);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    public void init() {
        file = new File(getFilesDir(), "dataset.csv");
        recipesList = findViewById(R.id.recipe_list);
        recipeData = parseCsv();
        recipeAdapter = new RecipeAdapter(recipeData);
        recipesList.setAdapter(recipeAdapter);
        recipesList.setLayoutManager(new LinearLayoutManager(this));
        recipesList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(@NonNull MotionEvent e) {
                    super.onLongPress(e);
                    View child = recipesList.findChildViewUnder(e.getX(), e.getY());
                    if (child != null) {
                        int position = recipesList.getChildAdapterPosition(child);
                        // Bring up a context menu or perform a delete operation
                        showContextMenu(position);
                    }
                }
                @Override
                public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                    return true;
                }
            });
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                gestureDetector.onTouchEvent(e);
                return false;
            }
            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }


    public List<String[]> parseCsv() {
        List<String[]> result = new ArrayList<>();
        if (!file.exists()) {
            result.add(new String[]{"File does not exist", "2099-12-31", "1"});
            return result;
        }
        try {
            String[] lines = FileHelper.readFileLines(file);
            if (lines.length < 1) {
                result.add(new String[]{"You have not added any data", "2099-12-31", "1"});
                return result;
            }
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    result.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //delete data from dataset
    public void deleteRecipe(int lineNumber) {
        Path path = Paths.get(file.toURI()); // dataset
        try {
            List<String> lines = Files.readAllLines(path);
            if (lines.size() > lineNumber) {
                lines.remove(lineNumber);
            }
            Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Show Delete Popup
    private void showContextMenu(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this data?？")
                .setPositiveButton("Delete", (dialog, id) -> {
                    // Removing data from a dataset
                    recipeData.remove(position);
                    deleteRecipe(position);

                    recipeAdapter.notifyItemRemoved(position);
                    View rootView = getWindow().getDecorView().getRootView();
                    Snackbar.make(rootView, "Remove data successfully！", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}