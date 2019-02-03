package com.example.oladipo.damisfyp.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    public BaseActivity(){}

    public void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void showProgressDialog(String message){
        //if progress dialog is not being used or showing
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    public void hideProgressDialog(){
        //if progress dialog is showing
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
