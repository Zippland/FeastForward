package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Adapter.RecipeAdapter;
import com.example.myapplication.Utils.DataUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FoodSearchActivity extends AppCompatActivity {
    SearchView recipeSearch;
    RecyclerView recipeSearchList;
    RecipeAdapter recipeAdapter;
    List<Recipe> recipeData;
    List<Recipe> tempData;
    List<Recipe> sortData;
    boolean isSort = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void init() {
        View rootView = getWindow().getDecorView().getRootView();
        recipeSearch = findViewById(R.id.recipe_search);
        recipeSearchList = findViewById(R.id.recipe_search_list);
        recipeSearch.setOnQueryTextListener(queryTextListener);
        recipeData = DataUtil.parseCsv();
        tempData = new ArrayList<>(recipeData);
        sortData = new ArrayList<>(tempData);
        recipeAdapter = new RecipeAdapter(sortData);
        recipeSearchList.setAdapter(recipeAdapter);
        recipeSearchList.setLayoutManager(new LinearLayoutManager(this));
        ImageView sortButton = findViewById(R.id.button_sort);
        sortButton.setOnClickListener(v -> {
            isSort = !isSort;
            sortData.clear();
            sortData.addAll(tempData);
            if (isSort) {
                sortButton.setImageResource(R.drawable.sort_open);
                sortData.sort(Comparator.comparing(Recipe::getTitle));
                Snackbar.make(rootView, "You've turned on alphabetical sorting!", Toast.LENGTH_SHORT).show();
            } else {
                sortButton.setImageResource(R.drawable.sort_close);
                Snackbar.make(rootView, "You've turned off initials!", Toast.LENGTH_SHORT).show();
            }
            recipeAdapter.notifyDataSetChanged();
        });
    }

    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public boolean onQueryTextChange(String newText) {
            new Thread(() -> {
                tempData.clear();
                sortData.clear();
                if (!TextUtils.isEmpty(newText)) {
                    for (Recipe recipeDatum : recipeData) {
                        int containNumber = 0;
                        char[] charArray = newText.toLowerCase().toCharArray();
                        for (char c : charArray) {
                            if (recipeDatum.getTitle().toLowerCase().contains(String.valueOf(c))) {
                                containNumber++;
                            }
                        }
                        if (containNumber == newText.length()) {
                            tempData.add(recipeDatum);
                        }
                    }
                } else {
                    tempData.addAll(recipeData);
                }
                sortData.addAll(tempData);
                if (isSort)
                    sortData.sort(Comparator.comparing(Recipe::getTitle));
                runOnUiThread(() -> recipeAdapter.notifyDataSetChanged());
            }).start();
            return false;
        }
    };
}