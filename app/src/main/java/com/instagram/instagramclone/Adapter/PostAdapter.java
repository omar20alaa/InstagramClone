package com.instagram.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
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
import com.instagram.instagramclone.Activity.Comment;
import com.instagram.instagramclone.Activity.Followers;
import com.instagram.instagramclone.Fragment.ProfileFragment;
import com.instagram.instagramclone.Model.Post;
import com.instagram.instagramclone.Model.User;
import com.instagram.instagramclone.R;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    // vars
    Context context;
    List<Post> postList;
    FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new MyViewHolder(view);
    } // on create view

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = postList.get(i);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_person);
        requestOptions.error(R.drawable.ic_person);
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(post.getPostimage())
                .into(holder.post_image);

        if (post.getDescription().equals("")) {
            holder.tv_description.setVisibility(View.GONE);
        } else {
            holder.tv_description.setVisibility(View.VISIBLE);
            holder.tv_description.setText(post.getDescription());
        }

        publisherInfo(holder.image_profile, holder.tv_user_name, holder.tv_publisher, post.getPublisher());
        isLiked(post.getPostid(), holder.like_image);
        numberLikes(holder.tv_likes, post.getPostid());

        holder.like_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like_image.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotifications(post.getPublisher(), post.getPostid());

                } else {

                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.comment_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Comment.class);
                intent.putExtra("post_id", post.getPostid());
                intent.putExtra("publisher_id", post.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.tv_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Comment.class);
                intent.putExtra("post_id", post.getPostid());
                intent.putExtra("publisher_id", post.getPublisher());
                context.startActivity(intent);
            }
        });

        getComments(post.getPostid(), holder.tv_comments);

        isSaved(post.getPostid(), holder.save_image);

        holder.save_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save_image.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();

                }
            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profile_id", post.getPublisher());
                editor.apply();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.tv_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profile_id", post.getPublisher());
                editor.apply();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.tv_publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profile_id", post.getPublisher());
                editor.apply();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("post_id", post.getPostid());
                editor.apply();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        holder.tv_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , Followers.class);
                intent.putExtra("id",post.getPostid());
                intent.putExtra("title" , "Likes");
               context.startActivity(intent);
            }
        });

    } // on bind view

    @Override
    public int getItemCount() {
        return postList.size();
    } // get item count

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // bind views
        @BindView(R.id.image_profile)
        ImageView image_profile;

        @BindView(R.id.tv_user_name)
        TextView tv_user_name;

        @BindView(R.id.post_image)
        ImageView post_image;

        @BindView(R.id.like_image)
        ImageView like_image;

        @BindView(R.id.comment_image)
        ImageView comment_image;

        @BindView(R.id.save_image)
        ImageView save_image;

        @BindView(R.id.tv_likes)
        TextView tv_likes;

        @BindView(R.id.tv_publisher)
        TextView tv_publisher;

        @BindView(R.id.tv_description)
        TextView tv_description;

        @BindView(R.id.tv_comments)
        TextView tv_comments;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    } // class view holder

    public void publisherInfo(final ImageView image_profile, final TextView user_name, final TextView publisher, String user_id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.mipmap.ic_launcher_round);
                requestOptions.error(R.mipmap.ic_launcher_round);
                Glide.with(context.getApplicationContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(user.getImageurl())
                        .into(image_profile);
                user_name.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // information of publisher method

    private void isLiked(String postid, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {

                    imageView.setImageResource(R.drawable.ic_favorite);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    } // is following function

    private void numberLikes(final TextView likes, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // number of likes

    private void getComments(String post_id, final TextView comments) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(post_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                comments.setText("View All " + dataSnapshot.getChildrenCount() + " Comments");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } // show comments

    private void isSaved(final String post_id, final ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_id).exists()) {
                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_saving);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    } // check for the post is saved or not

    private void addNotifications(String user_id, String post_id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(user_id);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Liked your post");
        hashMap.put("postid", post_id);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    } // add notification method

}
