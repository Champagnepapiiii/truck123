package com.mondaybs.monday.Buyers;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mondaybs.monday.Admin.LoginActivity;
import com.mondaybs.monday.Buyers.Fragments.HomeFragment;
import com.mondaybs.monday.Buyers.Menu.DrawerAdapter;
import com.mondaybs.monday.Buyers.Menu.DrawerItem;
import com.mondaybs.monday.Buyers.Menu.SimpleItem;
import com.mondaybs.monday.Prevalent.Prevalent;
import com.mondaybs.monday.R;
import com.mondaybs.monday.Sellers.SellerRegistrationActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, DrawerAdapter.OnItemSelectedListener, ForceUpdateChecker.OnUpdateNeededListener {

    private SlidingRootNav slidingRootNav;
    private static final int POS_CART = 0;
    private static final int POS_SEARCH = 1;
    private static final int POS_SETTINGS = 2;
    private static final int POS_TERMS_AND_COND = 3;
    private static final int POS_SELLER = 4;
    private static final int POS_LOGOUT = 5;
    private static final int POS_ADMIN = 6;
    FrameLayout fl1;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    View contextView;
    Boolean doubleBackToExitPressedOnce = false;
    DrawerAdapter drawadapter;
    ImageButton btnCart,btnSearch;
    TextView userNameTextView;
    CircleImageView profileImageView;

    public static String admin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        contextView = findViewById(android.R.id.content);

        admin = getIntent().getExtras().get("admin").toString();

        btnCart = findViewById(R.id.btn_cart);
        btnSearch = findViewById(R.id.btn_search);
        fl1 = findViewById(R.id.frame_tabs1);


        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_tabs1,new HomeFragment()).commit();


        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();


        drawadapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_CART),
                createItemFor(POS_SEARCH),
                createItemFor(POS_SETTINGS),
                createItemFor(POS_TERMS_AND_COND),
                createItemFor(POS_SELLER),
                createItemFor(POS_LOGOUT),
                createItemFor(POS_ADMIN)));
        drawadapter.setListener(this);

        RecyclerView list = findViewById(R.id.list_navigation);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(drawadapter);



        if(admin.equals("not admin")) {

            btnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, CartActivity.class));
                }
            });
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, SearchProductsActivity.class));
                }
            });


            userNameTextView = findViewById(R.id.home_profile_name);

            profileImageView = findViewById(R.id.home_image_user);
            FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("image").exists()) {
                        Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.profile).into(profileImageView);

                    }
                    if (dataSnapshot.child("name").exists()) {
                        userNameTextView.setText(dataSnapshot.child("name").getValue().toString());

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }


    @Override
    public void onItemSelected(int position) {
        slidingRootNav.closeMenu();
        if(admin.equals("not admin")) {
            if (position == POS_CART) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));

            }
            if (position == POS_SEARCH) {
                startActivity(new Intent(HomeActivity.this, SearchProductsActivity.class));

            }
            if (position == POS_SETTINGS) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
            if (position == POS_TERMS_AND_COND) {
                startActivity(new Intent(HomeActivity.this, termsActivity.class));
            }

            if (position == POS_SELLER) {
                startActivity(new Intent(HomeActivity.this, SellerRegistrationActivity.class));
            }

            if (position == POS_LOGOUT) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                alert.setMessage("Are you sure?")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(HomeActivity.this, PhoneActivity.class));

                            }
                        }).setNegativeButton("Cancel", null);
                AlertDialog alert1 = alert.create();
                alert1.show();
            }
            if (position == POS_ADMIN) {
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        }
    }



    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorSecondary))
                .withSelectedIconTint(color(R.color.textColorSecondary))
                .withSelectedTextTint(color(R.color.textColorSecondary));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }
    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    public void onBackPressed() {

        if(admin.equals("not admin")) {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }
            slidingRootNav.closeMenu();
            this.doubleBackToExitPressedOnce = true;
            Snackbar.make(contextView, "Press again to exit", 2000).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }else{
            super.onBackPressed();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onUpdateNeeded(final String updateUrl) {
        Toast.makeText(this, "update needed", Toast.LENGTH_SHORT).show();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue shopping.")
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectStore(updateUrl);
                            }
                        }).setNegativeButton("No, thanks",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}


