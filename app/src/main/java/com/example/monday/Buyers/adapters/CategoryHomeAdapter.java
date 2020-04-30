package com.example.monday.Buyers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.balysv.materialripple.MaterialRippleLayout;
import com.example.monday.Buyers.CategoryActivity;
import com.example.monday.Model.CategoryHomeModel;
import com.example.monday.R;
import com.example.monday.Sellers.SellerAddNewProductActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryHomeAdapter extends RecyclerView.Adapter<CategoryHomeAdapter.ViewHolder> {

    private List<CategoryHomeModel> categoryHomeModelList;
    Context context;
    MaterialRippleLayout cardView;
    String actName;

    public CategoryHomeAdapter(List<CategoryHomeModel> categoryHomeModelList,Context context,String actName) {
        this.context = context;
        this.categoryHomeModelList = categoryHomeModelList;
        this.actName = actName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_category_card_new,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String image = categoryHomeModelList.get(position).getCategoryImg();
        final String name = categoryHomeModelList.get(position).getCategoryName();
        viewHolder.setCategoryName(name);
        viewHolder.setCategoryImage(image);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(actName.equals("home_cat")) {
                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra("cat_name", name);
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, SellerAddNewProductActivity.class);
                    intent.putExtra("category", name);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryHomeModelList.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView categoryImage;
        private TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.image);
            categoryName = itemView.findViewById(R.id.title);
            cardView = itemView.findViewById(R.id.item_cat_card);
        }

        private void setCategoryImage(String imageUri){
            ////set categories image from database
            Picasso.get().load(imageUri).into(categoryImage);
        }

        private void setCategoryName(String name){
            categoryName.setText(name);
        }
    }


}