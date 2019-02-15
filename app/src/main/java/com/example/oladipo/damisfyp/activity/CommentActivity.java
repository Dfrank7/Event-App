package com.example.oladipo.damisfyp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oladipo.damisfyp.R;
import com.example.oladipo.damisfyp.adapter.CommentAdapter;
import com.example.oladipo.damisfyp.base.BaseActivity;
import com.example.oladipo.damisfyp.model.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentActivity extends BaseActivity {

    @BindView(R.id.comment_label)
    TextView commentLabel;
    @BindView(R.id.comment_field)
    EditText commentField;
    @BindView(R.id.comment_post_btn)
    ImageView sendButton;
    @BindView(R.id.comment_recycler)
    RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private String message;
    private Comment comment;
    private String eventId;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        commentAdapter = new CommentAdapter(commentList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);
        context = CommentActivity.this;

        Query firstQuery = firestore.collection("Posts/"+eventId+"/Comments").orderBy("timestamp",Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                for (DocumentChange doc:documentSnapshots.getDocumentChanges()){
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        Comment comment = doc.getDocument().toObject(Comment.class);
                        commentList.add(comment);
                        commentAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = getIntent();
                if (!isConnectionAvailable(getApplicationContext())) {
                    toast(getApplicationContext(), "No Internet Connection");
                } else {
                    message = commentField.getText().toString();
                    comment = new Comment(message, getUid().toString(), FieldValue.serverTimestamp());
                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("message", message);
                    postMap.put("user_id", getUid().toString());
                    postMap.put("timestamp", FieldValue.serverTimestamp());
                    if (TextUtils.isEmpty(message)) {
                        commentField.setError("Please enter message");
                    } else {
                        commentField.setText("");
                        firestore.collection("Posts/" + eventId + "/Comments").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
//                                commentField.setText("");
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT);
                                }
                            }
                        });
                    }
                }
            }
        });


    }

    private String getUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    private String getName(){
        return mAuth.getCurrentUser().getDisplayName();
    }

    private String getEmail(){
        return mAuth.getCurrentUser().getEmail();
    }


}
