package com.example.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    List<String[]> recipeData;

    public RecipeAdapter(List<String[]> recipeData) {
        this.recipeData = recipeData;

    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        String[] result = recipeData.get(position);
        holder.itemName.setText(result[0]);
        holder.expiredTime.setText(result[1]);
        //Calculate Difference Reminder
        LocalDate now = LocalDate.now();
        LocalDate expired = LocalDate.parse(result[1]);
        long between = ChronoUnit.DAYS.between(now, expired);
        if (between > 0) {
            if (between <= 3) {
                holder.expiredStatus.setText("（" + between + " days left to expire）");
            }
        }  else {
            holder.expiredStatus.setText("（expired）");
        }
    }

    @Override
    public int getItemCount() {
        return recipeData.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView expiredTime;
        TextView expiredStatus;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            expiredTime = itemView.findViewById(R.id.expired_time);
            expiredStatus = itemView.findViewById(R.id.expired_status);
        }
    }
}
