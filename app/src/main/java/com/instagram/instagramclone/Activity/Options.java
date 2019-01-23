package com.instagram.instagramclone.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.instagram.instagramclone.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Options extends AppCompatActivity {


    @BindView(R.id.tool_bar)
    Toolbar tool_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        ButterKnife.bind(this);
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tool_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    } // init tool bar

    @OnClick(R.id.logout)
    public void logoutClick()
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,Start.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

    } // click on settings

    @OnClick(R.id.setting)
    public void settingClick()
    {

    } // click on settings
}
