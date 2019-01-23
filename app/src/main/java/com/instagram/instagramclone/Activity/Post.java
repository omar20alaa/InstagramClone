package com.instagram.instagramclone.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.instagram.instagramclone.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Post extends AppCompatActivity {

    // bind views

    @BindView(R.id.tv_post)
    TextView tv_post;

    @BindView(R.id.image_added)
    ImageView image_added;

    @BindView(R.id.et_description)
    MaterialEditText et_description;

    // vars
    Uri imageUri;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
        initFirebase();
    }

    private void initFirebase() {
        storageReference = FirebaseStorage.getInstance().getReference("posts");
        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this);
    } // init firebase reference

    @OnClick(R.id.close_image)
    public void closeImage() {
        startActivity(new Intent(this, Main.class));
        finish();
    } // click on close image

    @OnClick(R.id.tv_post)
    public void posting() {
        uploadImage();
    }

    private void uploadImage() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Posting ...");
        dialog.show();

        if (imageUri != null) {
            final StorageReference file_reference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtention(imageUri));

            uploadTask = file_reference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return file_reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String post_id = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", post_id);
                        hashMap.put("postimage", myUrl);
                        hashMap.put("description", et_description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(post_id).setValue(hashMap);
                        dialog.dismiss();
                        startActivity(new Intent(Post.this, Main.class));
                        finish();
                    } else {
                        Toast.makeText(Post.this, "Failed !!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Post.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image selected !!!", Toast.LENGTH_SHORT).show();
        }

    } // uploading image function

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    } // file extention method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            image_added.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Something wrong !!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Main.class));
            finish();
        }

    } // on activity result
}
