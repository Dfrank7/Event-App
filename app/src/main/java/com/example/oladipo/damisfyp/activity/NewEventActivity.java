package com.example.oladipo.damisfyp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.oladipo.damisfyp.R;
import com.example.oladipo.damisfyp.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

public class NewEventActivity extends BaseActivity {

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.postTitle)
    EditText postTitle;
    @BindView(R.id.postDesc)
    EditText description;
    @BindView(R.id.postGenre)
    EditText genreEditText;
    @BindView(R.id.postButton)
    Button newPostBtn;
    private Uri postImageUri = null;

    // private ProgressBar newPostProgress;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    private Bitmap compressedImageFile;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);
        ButterKnife.bind(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        //progressDialog = new ProgressDialog(getApplicationContext());

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(NewEventActivity.this);

            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgressDialog("Please Wait");
                final String desc = description.getText().toString();
                final String title = postTitle.getText().toString();
                final String genre = genreEditText.getText().toString();

                if(!TextUtils.isEmpty(desc) || !TextUtils.isEmpty(title) || !TextUtils.isEmpty(genre)
                        || postImageUri != null){

                    //newPostProgress.setVisibility(View.VISIBLE);

                    final String randomName = UUID.randomUUID().toString();

                    // PHOTO UPLOAD
                    File newImageFile = new File(postImageUri.getPath());
                    try {

                        compressedImageFile = new Compressor(NewEventActivity.this)
                                .setMaxHeight(720)
                                .setMaxWidth(720)
                                .setQuality(50)
                                .compressToBitmap(newImageFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    // PHOTO UPLOAD

                    UploadTask filePath = storageReference.child("post_images")
                            .child(randomName + ".jpg").putBytes(imageData);
                    filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            final String downloadUri = task.getResult().getDownloadUrl().toString();

                            if(task.isSuccessful()){

                                File newThumbFile = new File(postImageUri.getPath());
                                try {

                                    compressedImageFile = new Compressor(NewEventActivity.this)
                                            .setMaxHeight(100)
                                            .setMaxWidth(100)
                                            .setQuality(1)
                                            .compressToBitmap(newThumbFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadTask = storageReference.child("post_images/thumbs")
                                        .child(randomName + ".jpg").putBytes(thumbData);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                        Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("image", downloadUri);
                                        postMap.put("post", title);
                                        postMap.put("description", desc);
                                        postMap.put("genre", genre);
                                        postMap.put("timestamp", System.currentTimeMillis());

                                        //long time = System.currentTimeMillis();

                                        firebaseFirestore.collection("Posts").add(postMap)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        hideProgressDialog();

                                                        if(task.isSuccessful()){

                                                            toast(getApplicationContext(), "Event was Successfully added");
                                                            Intent mainIntent = new Intent(NewEventActivity.this, MainActivity.class);
                                                            startActivity(mainIntent);
                                                            finish();

                                                        } else {
                                                            toast(getApplicationContext(), task.getException().getMessage());

                                                        }

                                                        //newPostProgress.setVisibility(View.INVISIBLE);

                                                    }
                                                });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        //Error handling

                                    }
                                });


                            } else {

                                //newPostProgress.setVisibility(View.INVISIBLE);

                            }

                        }
                    });


                }

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                imageView.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}
