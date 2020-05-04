package com.mondaybs.monday.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mondaybs.monday.Admin.AdminMaintainProductsActivity;
import com.mondaybs.monday.Buyers.utils.SpacingItemDecoration;
import com.mondaybs.monday.Buyers.utils.Tools;
import com.mondaybs.monday.Model.Products;
import com.mondaybs.monday.R;
import com.mondaybs.monday.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CategoryActivity extends AppCompatActivity {


    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardView loadingCard;

    private DatabaseReference ProductsRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        toolbar = findViewById(R.id.cat_toolbar);



        recyclerView = findViewById(R.id.rv_cat);
        loadingCard = findViewById(R.id.card_loading);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this,8),true));

        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        loadingCard.setVisibility(View.VISIBLE);
        String catName = getIntent().getExtras().getString("cat_name");
        final String businessNameStr = getIntent().getExtras().getString("business_name");
        toolbar.setTitle(catName);

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef.orderByChild("category").equalTo(catName), Products.class)
                        .build();



        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
                    {
                        loadingCard.setVisibility(View.GONE);
                        if(model.getProductState().equals("Approved") && model.getBusiness_name().equals(businessNameStr)) {
                            holder.txtProductName.setText(model.getPname());
                            holder.txtProductDescription.setText(model.getDescription());
                            holder.txtProductPrice.setText("price: ₹" + model.getPrice()+"/-");
                            Picasso.get().load(model.getImage()).into(holder.imageView);


                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (HomeActivity.admin.equals("admin")) {
                                        Intent intent = new Intent(CategoryActivity.this, AdminMaintainProductsActivity.class);
                                        intent.putExtra("pid", model.getPid());
                                        startActivity(intent);
                                    } else {
                                        // This is the functions for the end customers or users
                                        Intent intent = new Intent(CategoryActivity.this, ProductDetailsActivity.class);
                                        intent.putExtra("pid", model.getPid());
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        else{
                            holder.cardView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onViewRecycled(@NonNull ProductViewHolder holder) {
                        super.onViewRecycled(holder);

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
