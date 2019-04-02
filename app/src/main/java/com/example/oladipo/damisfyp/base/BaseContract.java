package com.example.oladipo.damisfyp.base;

import android.content.Context;

public interface BaseContract{

    interface view{
        void showloading(String message);
        void showToast(String message);
        void hideLoading();
        boolean isConnectionAvailable(Context context);

    }
}
