package com.example.oladipo.damisfyp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.oladipo.damisfyp.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BackgroundActivity extends AppCompatActivity {
    @BindView(R.id.signUp)
    Button signUp;
    @BindView(R.id.login) Button login;

    public BackgroundActivity(){}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_background);
        ButterKnife.bind(this);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(BackgroundActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    void finishActivity(){
        finish();
    }
}
