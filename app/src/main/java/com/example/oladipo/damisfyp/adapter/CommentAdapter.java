package com.example.oladipo.damisfyp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.oladipo.damisfyp.R;
import com.example.oladipo.damisfyp.model.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewHolder> {

    private List<Comment> commentList;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String name;

    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }
    @NonNull
    @Override
    public CommentAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_list_item, viewGroup, false);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.viewHolder holder, int position) {
        holder.message.setText(commentList.get(position).getMessage());
        String user_id = commentList.get(position).getUser_id();
        firestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()) {
                        name = task.getResult().getString("name");
                        holder.username.setText(name);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (commentList.size()== 0){
            return 0;
        }else {
            return commentList.size();
        }
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.comment_username)
        TextView username;
        @BindView(R.id.comment_message)
        TextView message;
        public viewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
