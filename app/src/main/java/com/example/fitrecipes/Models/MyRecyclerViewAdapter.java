package com.example.fitrecipes.Models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fitrecipes.Activities.EditRecipeActivity;
import com.example.fitrecipes.Activities.LoginActivity;
import com.example.fitrecipes.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    public static final String USER_KEY = "user_key";
    Context context;
    private List<Recipe> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<Recipe> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recipesrvlayout, parent, false);
        return new ViewHolder(view);
    }

    //public void onBindViewHolder(ViewHolder holder, final int position)
    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        RecipeModel recipeModel = mData.get(position).getRecipeModel();
        if (!LoginActivity.UUID.equals(recipeModel.getId())) {
            holder.delete_icon.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
        }

        String RecipeId = mData.get(position).getRecipeId();
        Glide.with(holder.imageView).load(recipeModel.getRecipe_image()).into(holder.imageView);
        holder.tv_Recipe_name.setText(recipeModel.name);
        holder.tvRecipeInstructions.setText(recipeModel.recipeI);
        holder.tvRecipeSrvPeople.setText(recipeModel.recipe_people);
        holder.tvRecipeIngredients.setText(recipeModel.getRecipeIng());
        holder.tvRecipeDescription.setText(recipeModel.recipeD);
        holder.tvRecipeTime.setText(recipeModel.recipeT);

        holder.delete_icon.setOnClickListener(view -> {
            FirebaseDatabase.getInstance().getReference().child("Recipes").child(RecipeId).removeValue();
            mData.remove(position);
            notifyDataSetChanged();
        });
        //ToDO  code for edit and delete func
        holder.edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent it = new Intent(context, EditRecipeActivity.class);
                it.putExtra("rid", RecipeId);
                context.startActivity(it);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_Recipe_name, tvRecipeTime, tvRecipeDescription, tvRecipeIngredients, tvRecipeSrvPeople, tvRecipeInstructions;
        ImageView imageView, delete_icon, edit;

        ViewHolder(View itemView) {
            super(itemView);
            tv_Recipe_name = itemView.findViewById(R.id.tv_Recipe_name);
            tvRecipeTime = itemView.findViewById(R.id.tv_cook_time);
            tvRecipeDescription = itemView.findViewById(R.id.tv_Recipe_Description);
            tvRecipeIngredients = itemView.findViewById(R.id.tv_Recipe_Ingredients);
            tvRecipeSrvPeople = itemView.findViewById(R.id.tv_People);
            tvRecipeInstructions = itemView.findViewById(R.id.tv_Recipe_Instructions);
            imageView = itemView.findViewById(R.id.img1);
            delete_icon = itemView.findViewById(R.id.delete_icon);
            edit = (ImageView) itemView.findViewById(R.id.edit_icon);
        }
    }
}
