package com.example.oladipo.damisfyp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.oladipo.damisfyp.R;
import com.example.oladipo.damisfyp.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends BaseActivity {
    @BindView(R.id.event_detail_title)
    TextView titleText;
    @BindView(R.id.event_detail_desc)
    TextView descText;
    //    @BindView(R.id.post_detail_genre)
//    TextView genreText;
    @BindView(R.id.event_imageview)
    ImageView imageView;
    @BindView(R.id.commentViewButton)
    Button commentViewButton;
    @BindView(R.id.comment)
    TextView comment;
    @BindView(R.id.commentCount)
    TextView commentCount;
    Intent intent;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String eventPostId;
    String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        bindData();
        commentViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(DetailActivity.this, CommentActivity.class)
                            .putExtra("eventId", eventPostId);
                    startActivity(intent);
                }
            }
        });
    }

    private void bindData(){

        intent = getIntent();
        title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("description");
        String genre = intent.getStringExtra("genre");
        String image = intent.getStringExtra("image");
        eventPostId = intent.getStringExtra("eventId");
        firestore.collection("Events/" + eventPostId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()){
                    int count = documentSnapshots.size();
                    if (count>1) {
                        commentCount.setText(String.valueOf(count));
                        comment.setText("Comments");
                    }else {
                        commentCount.setText(String.valueOf(count));
                        comment.setText("Comment");
                    }
                }else {
                    commentCount.setText("No");
                    comment.setText("Comment");
                    commentViewButton.setText("Comment");
                }
            }
        });
        titleText.setText(title);
        descText.setText(desc);
        //genreText.setText(genre);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.blog_placeholder);

        Glide.with(getApplicationContext())
                .applyDefaultRequestOptions(requestOptions)
                .load(image)
                .into(imageView);

    }
    private void share(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, title);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_share:
                share();
        }
        return super.onOptionsItemSelected(item);
    }
}
