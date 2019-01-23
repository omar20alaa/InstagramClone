package com.instagram.instagramclone.Activity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagram.instagramclone.Adapter.CommentAdapter;
import com.instagram.instagramclone.Model.User;
import com.instagram.instagramclone.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Comment extends AppCompatActivity {

    // bind views
    @BindView(R.id.comment_recycler_view)
    RecyclerView comment_recycler_view;

    @BindView(R.id.image_profile)
    ImageView image_profile;

    @BindView(R.id.add_comment)
    MaterialEditText add_comment;

    @BindView(R.id.tv_post)
    TextView tv_post;

    @BindView(R.id.tool_bar)
    Toolbar tool_bar;

    // vars
    String post_id = "", publisher_id = "";
    FirebaseUser firebaseUser;
    CommentAdapter commentAdapter;
    List<com.instagram.instagramclone.Model.Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        initToolbar();
        initRecyclerView();
        getIntentData();
        getImage();
        readComments();
    }

    private void initRecyclerView() {
        comment_recycler_view.setHasFixedSize(true);
        comment_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        comment_recycler_view.setAdapter(commentAdapter);
    } // init recycler view

    private void readComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(post_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.instagram.instagramclone.Model.Comment comment = snapshot.getValue(com.instagram.instagramclone.Model.Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // read comments method

    private void initToolbar() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tool_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    } // init tool bar method

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            post_id = intent.getStringExtra("post_id");
            publisher_id = intent.getStringExtra("publisher_id");

        }
    } // get intent data

    @OnClick(R.id.tv_post)
    public void clickPost() {
        if (add_comment.getText().toString().equals("")) {
            Toast.makeText(this, "You can't send empty activity", Toast.LENGTH_SHORT).show();
        } else {
            addComment();
        }
    }

    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(post_id);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", add_comment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        reference.push().setValue(hashMap);
        addNotifications();
        add_comment.setText("");

    } // adding an comment

    private void getImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ic_person);
                requestOptions.error(R.drawable.ic_person);
                Glide.with(getApplicationContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(user.getImageurl())
                        .into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    } // get image

    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(publisher_id);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "commented: " + add_comment.getText().toString().trim());
        hashMap.put("postid", "");
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    } // add notification method
}
