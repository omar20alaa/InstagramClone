package com.instagram.instagramclone.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.instagram.instagramclone.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Start extends AppCompatActivity {

    // vars

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        initFirebase();
    }

    private void initFirebase() {


    } // init firebase

    @OnClick(R.id.btn_login)
    public void loginClick() {
        startActivity(new Intent(this, Login.class));
        finish();
    } // click on login button

    @OnClick(R.id.btn_register)
    public void registerClick() {
        startActivity(new Intent(this, Register.class));
        finish();
    } // click on login button


    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            startActivity(new Intent(this, Main.class));
        }

    } // on start method
}
