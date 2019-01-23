package com.instagram.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.instagram.instagramclone.Activity.Main;
import com.instagram.instagramclone.Model.User;
import com.instagram.instagramclone.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    // vars
    Context context;
    List<com.instagram.instagramclone.Model.Comment> commentList;
    FirebaseUser firebaseUser;

    public CommentAdapter(Context context, List<com.instagram.instagramclone.Model.Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new MyViewHolder(view);
    } // on create view

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final com.instagram.instagramclone.Model.Comment comment = commentList.get(i);

        holder.tv_comment.setText(comment.getComment());
        getUserInfo(holder.image_profile, holder.tv_user_name, comment.getPublisher());

        holder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Main.class);
                intent.putExtra("publisher_id", comment.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Main.class);
                intent.putExtra("publisher_id", comment.getPublisher());
                context.startActivity(intent);
            }
        });

    } // on bind view

    @Override
    public int getItemCount() {
        return commentList.size();
    } // get item count

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // bind views
        @BindView(R.id.image_profile)
        ImageView image_profile;

        @BindView(R.id.tv_user_name)
        TextView tv_user_name;

        @BindView(R.id.tv_comment)
        TextView tv_comment;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    } // class view holder

    private void getUserInfo(final ImageView imageView, final TextView user_name, String publisher_id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisher_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ic_person);
                requestOptions.error(R.drawable.ic_person);

                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(user.getImageurl())
                        .into(imageView);
                user_name.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // get user information


}
