package com.instagram.instagramclone.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagram.instagramclone.Adapter.PostAdapter;
import com.instagram.instagramclone.Model.Post;
import com.instagram.instagramclone.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PostDetailFragment extends Fragment {

    // bind views
    @BindView(R.id.details_recycler_view)
    RecyclerView details_recycler_view;


    // vars
    String post_id;
    List<Post> postList;
    PostAdapter postAdapter;
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        ButterKnife.bind(this, view);
        initSharedPreference();
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        details_recycler_view.setHasFixedSize(true);
        details_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        details_recycler_view.setAdapter(postAdapter);
        readPost();
    } // init recycler view

    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(post_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                Post post = dataSnapshot.getValue(Post.class);
                postList.add(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    } // read post

    private void initSharedPreference() {
        preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        post_id = preferences.getString("post_id", "none");
    } // init shared preference


}
