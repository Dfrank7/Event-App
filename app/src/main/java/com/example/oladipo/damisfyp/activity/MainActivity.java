package com.example.oladipo.damisfyp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.oladipo.damisfyp.R;
import com.example.oladipo.damisfyp.adapter.EventAdapter;
import com.example.oladipo.damisfyp.model.Event;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.event_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.blog_linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.swipe)
    SwipeRefreshLayout refreshLayout;
    private FirebaseFirestore firestore;
    private List<Event> eventList= new ArrayList<>();
    private EventAdapter eventAdapter;
    private ProgressDialog progressDialog;
    private boolean isFirstPageFirstLoad = true;
    private DocumentSnapshot lastVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(this, eventList);
        recyclerView.smoothScrollToPosition(0);
        recyclerView.setAdapter(eventAdapter);
        firestore = FirebaseFirestore.getInstance();
        eventList.clear();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isConnectionAvailable(getApplicationContext())) {
                    eventAdapter = new EventAdapter(getApplicationContext(), eventList);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.smoothScrollToPosition(0);
                    recyclerView.setAdapter(eventAdapter);
                    eventList.clear();
                    fetchPost();
                }else {
                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }
                    Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewEventActivity.class));
            }
        });
    }

    private void fetchPost(){
        Query firstQuery = firestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                progressBar.setVisibility(View.GONE);

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED){
                        String eventPostId = doc.getDocument().getId();
                        Event event = doc.getDocument().toObject(Event.class).withId(eventPostId);
                        eventList.add(event);
                       eventAdapter.notifyDataSetChanged();
                    }
                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);

                    }
                }
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isConnectionAvailable(this)){
            progressBar.setVisibility(View.GONE);
            fetchPost();
            Snackbar.make(findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_LONG)
                    .show();

        }else {
            fetchPost();
            eventList.clear();
        }
    }

    public boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
