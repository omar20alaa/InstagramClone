package com.instagram.instagramclone.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagram.instagramclone.Model.User;
import com.instagram.instagramclone.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class Story extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    // bind views
    @BindView(R.id.image)
    ImageView image;

    @BindView(R.id.stories)
    StoriesProgressView storiesProgressView;

    @BindView(R.id.story_photo)
    CircleImageView story_photo;

    @BindView(R.id.story_username)
    TextView story_username;

    @BindView(R.id.reverse)
    View reverse;

    @BindView(R.id.skip)
    View skip;

    // vars
    int counter = 0;
    long pressTime = 0L;
    long limit = 500L;
    List<String> images;
    List<String> storyids;
    String userid;


    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;

                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        ButterKnife.bind(this);

        userid = getIntent().getStringExtra("userid");
        reverse.setOnTouchListener(onTouchListener);
        skip.setOnTouchListener(onTouchListener);

        getStories(userid);
        userInfo(userid);
    }

    @OnClick(R.id.reverse)
    public void clickReverse() {
        storiesProgressView.resume();
    }

    @OnClick(R.id.skip)
    public void clickSkip() {
        storiesProgressView.resume();
    }

    @Override
    public void onNext() {
        Glide.with(getApplicationContext())
                .load(images.get(++counter))
                .into(image);
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0)
            return;
        Glide.with(getApplicationContext())
                .load(images.get(++counter))
                .into(image);
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String userid) {
        images = new ArrayList<>();
        storyids = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                images.clear();
                storyids.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    com.instagram.instagramclone.Model.Story story = snapshot.getValue(com.instagram.instagramclone.Model.Story.class);
                    long timecurrent = System.currentTimeMillis();
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                        images.add(story.getImageurl());
                        storyids.add(story.getStoryid());
                    }
                }
                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(Story.this);
                storiesProgressView.startStories(counter);

                Glide.with(getApplicationContext())
                        .load(images.get(counter))
                        .into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // get stories

    private void userInfo(String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext())
                        .load(user.getImageurl())
                        .into(story_photo);
                story_username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
