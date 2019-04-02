package com.example.oladipo.damisfyp.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseFragment extends Fragment implements BaseContract.view {
    ProgressDialog progressDialog;
    public void setToolbarTitle (String title){
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(title);
    }

    @Override
    public void showloading(String message) {
        //if progress dialog is not being used or showing
        progressDialog = new ProgressDialog(getActivity());
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoading() {
        //if progress dialog is showing
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }


    @Override
    public boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
