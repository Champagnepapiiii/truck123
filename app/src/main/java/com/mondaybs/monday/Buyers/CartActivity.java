package com.mondaybs.monday.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mondaybs.monday.Model.Cart;
import com.mondaybs.monday.Prevalent.Prevalent;
import com.mondaybs.monday.R;
import com.mondaybs.monday.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private Button NextProcessBtn;
    private TextView txtTotalAmount, txtMsg1;

    private int overTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);
        txtMsg1 = (TextView) findViewById(R.id.msg1);

    }

    //This method is basically used to get retrieve the information from the firebase database and display in the cart page.
    @Override
    protected void onStart()
    {
        super.onStart();

        CheckOrderState();
        //If you want to get the totalPrice on top of the page, you can write this line
         txtTotalAmount.setText("Total Price= Rs" + String.valueOf(overTotalPrice));


           final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
//        //cartListRef is the current reference to the cart list node and after that we have a user view node and after that
//        // we will go the user defined phone number which is unique for each and every customer and we will get the products
//        //added by the cart and under the user Product section, we can see all the products that have been added in the cart
//
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentOnlineUser)
                        .child("Products"), Cart.class).build();

        // FirebaseRecyclerAdapter is basically used as a AndroidX library and it defines a model class to use as the data source
        // it is basically used to display the items that have been added to the cart

        // We have to make another CartViewHolder in the ViewHolder to hold the items in the cart or the profile of the customer or user.
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                holder.txtProductQuantity.setText("Quantity:" + model.getQuantity());
                holder.txtProductPrice.setText("Price: Rs" + model.getPrice());
                holder.txtProductName.setText(  model.getPname());

                // for a single product we will be find the total
                int oneTypeProductTPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
//
//                // No we will doing the total of the products in the whole cart page
                overTotalPrice = overTotalPrice + oneTypeProductTPrice;

                txtTotalAmount.setText("Total Price: Rs" + String.valueOf(overTotalPrice));
//                //When the user clicks on the next Button on the cart page, we will redirecting him into the confirm final order page.
                NextProcessBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Now we have to display the order total on the top of the page, so whenever the user clicks on the
                        // NextProcessBtn, the total will be shown on top of the page

                        // we need to receive the total price on the confirm final order now, we will define a totalAmount variable and we will use an intent

                        Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                        intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                        startActivity(intent);
                        finish();
                    }
                    //You can also put the  txtTotalAmount.setText("Total Price= Rs" + String.valueOf(overTotalPrice)); line
                    // here to that you can display the total in the same page.

                });

                // If the cx wants to edit the cart, he will be able to edit the cart, he will also be able to delete
                // the items that have been added to the cart.

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // We will add two button which will be edit and remove

                        CharSequence options[] = new CharSequence[]{
                                "Edit",
                                "Remove"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");

                        // Now we will add two addListener for the Edit and Remove button

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //If the user clicks the Edit Button
                                // Products model;
                                if (i == 0){
                                    // We will sending the user from the cart activity to the product activity and
                                    // on the product activity, we will only display those products
                                    // so we to go to the productDetailsActivity,
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    // As we have declared the pid over in the productDetailsActivity, so we will be using the Pid to refer to
                                    // the items
                                    // To get the id, we can type model.pid
                                    // SO whenever a cx clicks on the any product on the CartActivity, so we can get the specific ID by using the model.getPid()
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);

                                }
                                //If the user clicks on the remove Button
                                if (i == 1)
                                {
                                    cartListRef.child("User View").child(Prevalent.currentOnlineUser)
                                            .child("Products").child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(CartActivity.this, "Item removed successfully" , Toast.LENGTH_SHORT).show();
                                                        //Once the item is removed, we will get the users back to the home page
                                                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        //this is used to display the option of Edit and Remove on the app.
                        builder.show();

                    }
                });


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //LayoutInflater is used to create a new View Or Layout from one of your XML layouts.
                //findViewById just gives a reference to a view that has already been created.
                //LayoutInflater is a class that reads the xml appearance description and convert them into java based View objects.
                //It is a fundamental component in Android.
                //LayoutInflater creates View objects based on layouts defined in XML.

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;

            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    // we have to create a method to check the status of the order, so we create a method by the CheckOrderState

    private void CheckOrderState(){
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser);

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped")){
                        //If the order is shipped, we will tell user that the order is shipped successfully, the visibility will be gone
                        // and the text message will be visible & we will also make the next button is not visible to the user anymore in the cart page.
                        txtTotalAmount.setText("Dear "+ userName + "\n order is shipped successfully.");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulations, your final order has been shipped successfully. Soon it will be verified.");
                        NextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "You can purchase more products, once you receive your first order. " ,Toast.LENGTH_SHORT).show();

                    }else if(shippingState.equals("not shipped")){
                        txtTotalAmount.setText("Shipping state = No Shipped");
                        recyclerView.setVisibility(View.GONE);

                        txtMsg1.setVisibility(View.VISIBLE);
                        NextProcessBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "You can purchase more products, once you receive your first order. " ,Toast.LENGTH_SHORT).show();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); }

}

