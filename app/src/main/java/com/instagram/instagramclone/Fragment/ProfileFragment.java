package com.instagram.instagramclone.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagram.instagramclone.Activity.EditProfile;
import com.instagram.instagramclone.Activity.Followers;
import com.instagram.instagramclone.Activity.Options;
import com.instagram.instagramclone.Adapter.PhotoAdapter;
import com.instagram.instagramclone.Model.Post;
import com.instagram.instagramclone.Model.User;
import com.instagram.instagramclone.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileFragment extends Fragment {

    // bind views

    @BindView(R.id.tv_user_name)
    TextView tv_user_name;

    @BindView(R.id.options)
    ImageView options;

    @BindView(R.id.image_profile)
    ImageView image_profile;

    @BindView(R.id.posts)
    TextView posts;

    @BindView(R.id.followers)
    TextView followers;

    @BindView(R.id.following)
    TextView following;

    @BindView(R.id.edit_profile)
    Button edit_profile;

    @BindView(R.id.fullname)
    TextView fullname;

    @BindView(R.id.bio)
    TextView bio;

    @BindView(R.id.my_photos)
    ImageButton my_photos;

    @BindView(R.id.saved_photos)
    ImageButton saved_photos;

    @BindView(R.id.photo_recycler_view)
    RecyclerView photo_recycler_view;

    @BindView(R.id.saved_photo_recycler_view)
    RecyclerView saved_photo_recycler_view;

    // vars
    FirebaseUser firebaseUser;
    String profile_id = "";
    PhotoAdapter photoAdapter;
    List<Post> postList;
    List<String> mySaves;
    PhotoAdapter savedPhotoAdapter;
    List<Post> postList_saved;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        getPreferenceData();
        userInfo();
        getFollowers();
        getPostNumber();

        if (profile_id.equals(firebaseUser.getUid())) {
            edit_profile.setText("Edit profile");
        }
        else {
            checkFollow();
            saved_photos.setVisibility(View.GONE);
        }
        initPhotoRecyclerView();
        initSavedPhotoRecyclerView();
        myPhoto();
        mySaved();
        return view;
    } // on create view

    @OnClick(R.id.options)
    public void clickOptions()
    {
        Intent intent = new Intent(getContext() , Options.class);
        startActivity(intent);
    } // click on options

    @OnClick(R.id.followers)
    public void clickFollowers()
    {
          Intent intent = new Intent(getContext() , Followers.class);
          intent.putExtra("id",profile_id);
          intent.putExtra("title" , "Followers");
          startActivity(intent);
    } // click on folowers

    @OnClick(R.id.following)
    public void clickFollowing()
    {
        Intent intent = new Intent(getContext() , Followers.class);
        intent.putExtra("id",profile_id);
        intent.putExtra("title" , "Following");
        startActivity(intent);
    } // click on folowers


    @OnClick(R.id.my_photos)
    public void myPhotoClick() {
        photo_recycler_view.setVisibility(View.VISIBLE);
        saved_photo_recycler_view.setVisibility(View.GONE);
    } // click on my photo

    @OnClick(R.id.saved_photos)
    public void mySavedPhotoClick() {
        photo_recycler_view.setVisibility(View.GONE);
        saved_photo_recycler_view.setVisibility(View.VISIBLE);
    } // click on my photo

    private void initPhotoRecyclerView() {

        photo_recycler_view.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        photo_recycler_view.setLayoutManager(layoutManager);
        postList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), postList);
        photo_recycler_view.setAdapter(photoAdapter);

    } // init recycler view

    private void initSavedPhotoRecyclerView() {

        saved_photo_recycler_view.setHasFixedSize(true);
        LinearLayoutManager saved_layoutManager = new GridLayoutManager(getContext(), 3);
        saved_photo_recycler_view.setLayoutManager(saved_layoutManager);
        postList_saved = new ArrayList<>();
        savedPhotoAdapter = new PhotoAdapter(getContext(), postList_saved);
        saved_photo_recycler_view.setAdapter(savedPhotoAdapter);

    } // init recycler view

    private void getPreferenceData() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profile_id = preferences.getString("profile_id", "none");
    } // get Preference Data

    @OnClick(R.id.edit_profile)
    public void editProfile() {
        String btn = edit_profile.getText().toString();
        if (btn.equals("Edit profile")) {
            startActivity(new Intent(getContext(), EditProfile.class));
        } else if (btn.equals("follow")) {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("following").child(profile_id).setValue(true);
            FirebaseDatabase.getInstance().getReference().child("Follow").child(profile_id)
                    .child("followers").child(firebaseUser.getUid()).setValue(true);
            addNotifications();
        } else if (btn.equals("following")) {
            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                    .child("following").child(profile_id).removeValue();
            FirebaseDatabase.getInstance().getReference().child("Follow").child(profile_id)
                    .child("followers").child(firebaseUser.getUid()).removeValue();
        }

    } // click on edit profile

    private void userInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profile_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.mipmap.ic_launcher_round);
                requestOptions.error(R.mipmap.ic_launcher_round);
                Glide.with(getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(user.getImageurl())
                        .into(image_profile);
                tv_user_name.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // user information

    private void checkFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profile_id).exists()) {
                    edit_profile.setText("following");
                } else {
                    edit_profile.setText("follow");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // check follow

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profile_id).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(" " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profile_id).child("following");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(" " + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // get followers

    private void getPostNumber() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profile_id)) {
                        i++;
                    }
                }
                posts.setText(" " + i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // get posts number

    private void myPhoto() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profile_id)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // // show all photos

    private void mySaved() {
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mySaves.add(snapshot.getKey());
                }

                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    } // my saved photo

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList_saved.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    for (String id : mySaves) {
                        if (post.getPostid().equals(id)) {
                            postList_saved.add(post);
                        }
                    }

                }

                savedPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    } // read saved photo

    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(profile_id);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    } // add notification method

}
