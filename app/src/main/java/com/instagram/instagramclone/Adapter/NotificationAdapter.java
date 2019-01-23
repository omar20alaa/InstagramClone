package com.instagram.instagramclone.Adapter;

import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instagram.instagramclone.Fragment.PostDetailFragment;
import com.instagram.instagramclone.Fragment.ProfileFragment;
import com.instagram.instagramclone.Model.NotificationModel;
import com.instagram.instagramclone.Model.Post;
import com.instagram.instagramclone.Model.User;
import com.instagram.instagramclone.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    // vars
    Context context;
    List<NotificationModel> notificationModels;

    public NotificationAdapter(Context context, List<NotificationModel> notificationModels) {
        this.context = context;
        this.notificationModels = notificationModels;
    } // constructor

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new MyViewHolder(view);
    } // on create view

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {

        final NotificationModel notificationModel = notificationModels.get(i);

        getUserInfo(holder.image_profile , holder.user_name , notificationModel.getUserid());

        holder.comment.setText(notificationModel.getText());

        if (notificationModel.getIspost())
        {
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image , notificationModel.getPostid());
        }
        else
        {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notificationModel.getIspost())
                {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();

                    editor.putString("post_id",notificationModel.getPostid());
                    editor.apply();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PostDetailFragment()).commit();
                }
                else
                {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();

                    editor.putString("profile_id",notificationModel.getUserid());
                    editor.apply();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();

                }
            }
        });

    } // on bind view

    @Override
    public int getItemCount() {
        return notificationModels.size();
    } // get item count

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // bind views
        @BindView(R.id.image_profile)
        ImageView image_profile;

        @BindView(R.id.user_name)
        TextView user_name;

        @BindView(R.id.comment)
        TextView comment;

        @BindView(R.id.post_image)
        ImageView post_image;


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

    private void getPostImage(final ImageView imageView, final String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ic_person);
                requestOptions.error(R.drawable.ic_person);


                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(post.getPostimage())
                        .into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    } // get post image method


}
