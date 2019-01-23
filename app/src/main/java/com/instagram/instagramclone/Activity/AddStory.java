package com.instagram.instagramclone.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.instagram.instagramclone.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import java.util.HashMap;

public class AddStory extends AppCompatActivity {

    // vars
    private Uri mImageUri;
    String myUrl = "";
    private StorageTask storageTask;
    StorageReference storageReference;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);
        initFirebase();
        Log.i("QP", "onCreate add story");
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    } // get file extention

    private void initFirebase() {
        storageReference = FirebaseStorage.getInstance().getReference("story");

        Log.i("QP", "crop add story");
        CropImage.activity()
                .setAspectRatio(9, 16)
                .start(AddStory.this);


    } // init firebase


    private void publishStory() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.show();

                if (mImageUri != null) {

                    Log.i("QP", "image not null");
                    final StorageReference imageReference = storageReference.child(System.currentTimeMillis()
                            + "." + getFileExtension(mImageUri));

                    storageTask = imageReference.putFile(mImageUri);
                    storageTask.continueWithTask(new Continuation() {
                        @Override
                        public Task<Uri> then(@NonNull Task task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return imageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                myUrl = downloadUri.toString();

                                String my_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                                        .child(my_id);

                                Log.i("QP", "id"+my_id);

                                String story_id = reference.push().getKey();

                                Log.i("QP", "story id "+story_id);

                                long time_end = System.currentTimeMillis() + 86400000; // 1 day

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("imageurl", myUrl);
                                hashMap.put("timestart", ServerValue.TIMESTAMP);
                                hashMap.put("timeend", time_end);
                                hashMap.put("storyid", story_id);
                                hashMap.put("userid", my_id);
                                reference.child(story_id).setValue(hashMap);

                                progressDialog.dismiss();
                               finish();
                            }
                            else {
                                Toast.makeText(AddStory.this, "Failed !!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddStory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "No image selected !!!", Toast.LENGTH_SHORT).show();
                }

    } // publish story

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

                Log.i("QP", "ok");
                mImageUri = result.getUri();
                publishStory();

            }
         else {
            Toast.makeText(this, "Some thing gone wrong !!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddStory.this, Main.class));
            finish();
        }

    } // on activity result method


}
