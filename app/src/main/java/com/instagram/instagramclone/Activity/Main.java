package com.instagram.instagramclone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.instagram.instagramclone.Fragment.HomeFragment;
import com.instagram.instagramclone.Fragment.NotificationFragment;
import com.instagram.instagramclone.Fragment.ProfileFragment;
import com.instagram.instagramclone.Fragment.SearchFragment;
import com.instagram.instagramclone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main extends AppCompatActivity {

    // bind views

    @BindView(R.id.fragment_container)
    FrameLayout frameLayout;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottom_navigation;

    // vars

    Fragment selectedFragment = null;
    SharedPreferences.Editor editor;

    String edit = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getIntentData();
        initBottomNavigation();
    }

    private void getDataFromEditProfile()
    {
         Intent intent = getIntent();

         try{

             if (intent != null)
             {
                 edit = intent.getExtras().getString("edit");

                 if (edit.equals(null))
                 {
                     edit.equals("");
                 }
                 else if (edit.equals("1"))
                 {

                     getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

                 }

             }

         }
         catch (Exception e)
         {

         }


    }

    private void getIntentData() {

        Bundle intent = getIntent().getExtras();


        if (intent != null) {
            String publisher = intent.getString("publisher_id");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profile_id", publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            Log.i("QP","intent" + intent);
        }



        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            Log.i("QP","intent" + intent);

        }


    } // get intent data

    private void initBottomNavigation() {

        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

    } // bottom navigation method

    private BottomNavigationView.OnNavigationItemSelectedListener
            navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    Log.i("QP", "Home");
                    break;

                case R.id.nav_search:
                    Log.i("QP", "Search");

                    selectedFragment = new SearchFragment();
                    break;

                case R.id.nav_add:
                    selectedFragment = null;
                    startActivity(new Intent(Main.this, Post.class));
                    break;

                case R.id.nav_heart:
                    selectedFragment = new NotificationFragment();
                    break;

                case R.id.nav_profile:
                    editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profile_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    selectedFragment = new ProfileFragment();
                    break;

            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        }
    };

}
