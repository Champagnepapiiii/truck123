package com.mondaybs.monday.Buyers;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mondaybs.monday.Admin.AdminMaintainProductsActivity;
import com.mondaybs.monday.Model.Products;
import com.mondaybs.monday.R;
import com.mondaybs.monday.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mondaybs.monday.ViewHolder.businessNameViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BusinessListActivity extends AppCompatActivity {


    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CardView loadingCard;
    private DatabaseReference ProductsRef;
    List<String> listBn = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        toolbar = findViewById(R.id.cat_toolbar);


        loadingCard = findViewById(R.id.card_loading);
        recyclerView = findViewById(R.id.rv_cat);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        listBn.clear();
        getData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        listBn.clear();
        getData();
    }

    private void getData(){
        loadingCard.setVisibility(View.VISIBLE);
        final String catName = getIntent().getExtras().getString("cat_name");

        toolbar.setTitle(catName);

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef.orderByChild("category").equalTo(catName), Products.class)
                        .build();




        FirebaseRecyclerAdapter<Products, businessNameViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, businessNameViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull businessNameViewHolder holder, int position, @NonNull final Products model)
                    {
                        loadingCard.setVisibility(View.GONE);
                        if(model.getProductState().equals("Approved")) {
                            if(!listBn.contains(model.getBusiness_name())) {
                                listBn.add(model.getBusiness_name());
                                final String businessNameStr = model.getBusiness_name();
                                holder.txtBusinessName.setText(businessNameStr);

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(BusinessListActivity.this, CategoryActivity.class);
                                        intent.putExtra("cat_name", catName);
                                        intent.putExtra("business_name", businessNameStr);
                                        startActivity(intent);
                                    }
                                });
                                Random rnd = new Random();
                                int cardColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                                holder.cardView.setCardBackgroundColor(cardColor);
                            }
                            else{
                                holder.cardView.setVisibility(View.GONE);
                            }
                        }
                        else{
                            holder.cardView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onViewRecycled(@NonNull businessNameViewHolder holder) {
                        super.onViewRecycled(holder);

                    }

                    @NonNull
                    @Override
                    public businessNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_name_layout, parent, false);
                        businessNameViewHolder holder = new businessNameViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
