package com.example.oladipo.damisfyp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.oladipo.damisfyp.R;
import com.example.oladipo.damisfyp.activity.AdminActivity;
import com.example.oladipo.damisfyp.base.BaseFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminLogin extends BaseFragment {

    @BindView(R.id.adminEmailAddress)
    EditText emailField;
    @BindView(R.id.adminPassword)
    EditText passwordField;
    @BindView(R.id.adminId)
    EditText adminId;
    @BindView(R.id.adminLoginButton)
    Button loginButton;
    private FirebaseAuth mAuth;
    private String admin = "test123";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
//        if (!TextUtils.isEmpty(code)){
//            code.toLowerCase();
//            if(code != admin){
//                showToast("Admin Id not correct");
//            }else {
//                signInAdmin(code);
//            }
//        }else {
//            //showToast("Fill all fields");
//        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                final String code = adminId.getText().toString().trim();
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)||
                        !TextUtils.isEmpty(code)){
                    code.toLowerCase();
                    showloading("Please wait");
                    if (!code.equals(admin)){
                        showToast("Admin Id not correct");
                    }else {
//                        showloading("Please wait");
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        hideLoading();
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(getContext(), AdminActivity.class);
                                            startActivity(intent);
                                            getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                                            getActivity().finish();
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(getActivity(), "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });
                    }
                }else{
                    showToast("Fill all fields");
                }
            }
        });

    }
}
