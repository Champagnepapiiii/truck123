package com.mondaybs.monday.Sellers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.mondaybs.monday.Buyers.adapters.CategoryHomeAdapter;
import com.mondaybs.monday.Buyers.utils.SpacingItemDecoration;
import com.mondaybs.monday.Buyers.utils.Tools;
import com.mondaybs.monday.Model.CategoryHomeModel;
import com.mondaybs.monday.R;

import java.util.ArrayList;
import java.util.List;

public class SellerProductCategoryActivity extends AppCompatActivity {


    private RecyclerView categoryRecyclerView;
    private CategoryHomeAdapter categoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_category);




        categoryRecyclerView = findViewById(R.id.rv_cat_seller);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SellerProductCategoryActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(SellerProductCategoryActivity.this, 2));
        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(SellerProductCategoryActivity.this,8),true));

        List<CategoryHomeModel> categoryHomeModelList =new ArrayList<CategoryHomeModel>();
        categoryHomeModelList.add(new CategoryHomeModel("https://firebasestorage.googleapis.com/v0/b/monday-e6b24.appspot.com/o/category_images%2Ffresh.png?alt=media&token=588923e2-d0ca-49f8-a068-815fc826d8c1","Fresh Zone"));
        categoryHomeModelList.add(new CategoryHomeModel("https://firebasestorage.googleapis.com/v0/b/monday-e6b24.appspot.com/o/category_images%2Fdaily_essentials.png?alt=media&token=ed3c35fc-381e-4caa-9ffe-f4bdcd98fcbc","Daily Essentials"));
        categoryHomeModelList.add(new CategoryHomeModel("https://firebasestorage.googleapis.com/v0/b/monday-e6b24.appspot.com/o/category_images%2Frestaurant.png?alt=media&token=17181d8e-2477-45f7-b0f2-94f281981885","Restaurant"));
        categoryHomeModelList.add(new CategoryHomeModel("https://firebasestorage.googleapis.com/v0/b/monday-e6b24.appspot.com/o/category_images%2Fsweet-food.png?alt=media&token=17ce1f7d-fb7e-479c-9aa7-77dea66a4551","Sweet Shop"));
        categoryHomeModelList.add(new CategoryHomeModel("https://firebasestorage.googleapis.com/v0/b/monday-e6b24.appspot.com/o/category_images%2Fhealthcare-and-medical.png?alt=media&token=6aafeb07-5be5-4fa0-8a61-304aafa4bac0","Health & Care"));

        categoryAdapter = new CategoryHomeAdapter(categoryHomeModelList,SellerProductCategoryActivity.this,"seller_cat");

        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();


    }
}
