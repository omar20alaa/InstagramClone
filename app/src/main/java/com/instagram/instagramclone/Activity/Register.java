package com.instagram.instagramclone.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instagram.instagramclone.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Register extends AppCompatActivity {

    // bind views

    @BindView(R.id.et_username)
    MaterialEditText et_username;

    @BindView(R.id.et_full_name)
    MaterialEditText et_full_name;

    @BindView(R.id.et_email)
    MaterialEditText et_email;

    @BindView(R.id.et_password)
    MaterialEditText et_password;

    // vars

    String username = "", full_name = "", email = "", password = "";
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initFirebase();
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
    } // init firebase auth

    @OnClick(R.id.btn_register)
    public void registerClick() {

        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setMessage("Please wait ...");
        progressDialog.show();

        username = et_username.getText().toString().trim();
        full_name = et_full_name.getText().toString().trim();
        email = et_email.getText().toString().trim();
        password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(full_name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fildes are required ...", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Password must have 6 characters ...", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else {

            register(username, full_name, email, password);
        }


    } // click on register

    @OnClick(R.id.tv_login)
    public void loginClick() {
        startActivity(new Intent(this, Login.class));
        finish();
    } // click on login

    private void register(final String username, final String fullname, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("fullname", fullname);
                            hashMap.put("bio", "");
                            hashMap.put("imageurl", "");

                            reference.setValue(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(Register.this, Main.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, "You can't register with this email or password !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    } // register function

}
