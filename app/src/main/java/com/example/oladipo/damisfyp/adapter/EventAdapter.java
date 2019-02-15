package com.example.oladipo.damisfyp.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.oladipo.damisfyp.R;
import com.example.oladipo.damisfyp.activity.DetailActivity;
import com.example.oladipo.damisfyp.model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.viewHolder> {

    private Context context;
    private List<Event> eventList;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_list_item, viewGroup, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EventAdapter.viewHolder holder, int position) {

        final String eventPostId = eventList.get(position).EventId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.blog_placeholder);
//        Glide.with(context)
//                .load(blogList.get(position).getImage())
//                .into(holder.post_image);
        Glide.with(context)
                .applyDefaultRequestOptions(requestOptions)
                .load(eventList.get(position).getImage())
                .into(holder.post_image);

        holder.post_title.setText(eventList.get(position).getPost());
        holder.post_genere.setText(eventList.get(position).getGenre());

        try {
            long millisecond = eventList.get(position).getTimestamp();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            String timestamp = getTimeAgo(millisecond);
            //holder.post_timestamp.setText(dateString);
            holder.post_timestamp.setText(timestamp);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        //Get Likes Count
        firebaseFirestore.collection("Posts/" + eventPostId + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.likesCount.setText(count + " Like(s)");

                } else {

                    holder.likesCount.setText(0+" Likes");

                }

            }
        });


        //Get Likes
        firebaseFirestore.collection("Posts/" + eventPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){

                    holder.eventLikes.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent_web));

                } else {

                    holder.eventLikes.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray_web));

                }

            }
        });

        //Likes Feature
        holder.eventLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + eventPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(!task.getResult().exists()){

                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + eventPostId + "/Likes").document(currentUserId).set(likesMap);

                        } else {

                            firebaseFirestore.collection("Posts/" + eventPostId + "/Likes").document(currentUserId).delete();

                        }

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.post_image)
        ImageView post_image;
        @BindView(R.id.post_title)
        TextView post_title;
        @BindView(R.id.post_genre)
        TextView post_genere;
        @BindView(R.id.post_timestamp)
        TextView post_timestamp;
        @BindView(R.id.likes)
        ImageView eventLikes;
        @BindView(R.id.likesCount)
        TextView likesCount;
        public viewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                        intent.putExtra("title", eventList.get(position).getPost());
                        intent.putExtra("image", eventList.get(position).getImage());
                        intent.putExtra("description", eventList.get(position).getDescription());
                        intent.putExtra("genre", eventList.get(position).getGenre());
                        intent.putExtra("eventId", eventList.get(position).EventId);
                        //Log.d(Constants.TAG, blogList.get(position).BlogId);
                        itemView.getContext().startActivity(intent);
                    }
                }
            });
        }
    }

    static String getTimeAgo(long time_ago) {
        time_ago=time_ago/1000;
        long cur_time = (Calendar.getInstance().getTimeInMillis())/1000 ;
        long time_elapsed = cur_time - time_ago;
        long seconds = time_elapsed;
        // Seconds
        if (seconds <= 60) {
            return "Just now";
        }
        //Minutes
        else{
            int minutes = Math.round(time_elapsed / 60);

            if (minutes <= 60) {
                if (minutes == 1) {
                    return "1 minute ago";
                } else {
                    return minutes + " minutes ago";
                }
            }
            //Hours
            else {
                int hours = Math.round(time_elapsed / 3600);
                if (hours <= 24) {
                    if (hours == 1) {
                        return "1 hour ago";
                    } else {
                        return hours + " hrs ago";
                    }
                }
                //Days
                else {
                    int days = Math.round(time_elapsed / 86400);
                    if (days <= 7) {
                        if (days == 1) {
                            return "Yesterday";
                        } else {
                            return days + " days ago";
                        }
                    }
                    //Weeks
                    else {
                        int weeks = Math.round(time_elapsed / 604800);
                        if (weeks <= 4.3) {
                            if (weeks == 1) {
                                return "1 week ago";
                            } else {
                                return weeks + " weeks ago";
                            }
                        }
                        //Months
                        else {
                            int months = Math.round(time_elapsed / 2600640);
                            if (months <= 12) {
                                if (months == 1) {
                                    return "A month ago";
                                } else {
                                    return months + " months ago";
                                }
                            }
                            //Years
                            else {
                                int years = Math.round(time_elapsed / 31207680);
                                if (years == 1) {
                                    return "One year ago";
                                } else {
                                    return years + " years ago";
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
