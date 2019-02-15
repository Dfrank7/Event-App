package com.example.oladipo.damisfyp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.oladipo.damisfyp.R;
import com.example.oladipo.damisfyp.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends BaseActivity {

    @BindView(R.id.register_email_text)
    EditText emailField;
    @BindView(R.id.staff_id)
    EditText passwordField;
    @BindView(R.id.staff_name)
    EditText nameField;
    @BindView(R.id.register_signInButton)
    Button signInButton;
    @BindView(R.id.registerLinearLayout)
    LinearLayout linearLayout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String user_id;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        ButterKnife.bind(this);
        //mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
        }

//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                if (firebaseAuth.getCurrentUser() != null){
//
//                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        };

        register();
    }

    private void register(){
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnectionAvailable(getApplicationContext())){
                    final String email = emailField.getText().toString();
                    final String password = passwordField.getText().toString();
                    final String name = nameField.getText().toString().trim();
                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)){
                        toast(getApplicationContext(),"Fill all fields");
                    }else if (password.length()<8){
                        toast(getApplicationContext(),"password too small (min of 8)");
                    }else {
                        //showProgressDialog("please wait");
                        showProgressDialog("please Wait");
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password). addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //hideProgressDialog();
                                if (task.isSuccessful()){
                                    storeUser(email, name, password);
                                }else {
                                    hideProgressDialog();
                                    toast(getApplicationContext(),task.getException().getMessage());
                                }
                            }
                        });
                    }

                }else {
                    Snackbar.make(linearLayout, "No Internet Connection", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void storeUser(String email, String name, String password){
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> postMap = new HashMap<>();
        postMap.put("Email", email);
        postMap.put("Staff Id", password);
        postMap.put("Staff Name", name);
        firestore.collection("Users").document(user_id).set(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //hideProgressDialog();
                hideProgressDialog();
                if(task.isSuccessful()){
                    toast(getApplicationContext(), "Account Successfully Created.");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    toast(getApplicationContext(),"(FIRESTORE Error) : " + error);
                }

                //setupProgress.setVisibility(View.INVISIBLE);

            }
        });
    }
}
