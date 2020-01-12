package com.example.foodup;

import android.app.ProgressDialog;
import android.content.Context;

public class CustomDialog{

    public static ProgressDialog showCustomProgressDialog(Context context, String message){
        ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setTitle("Bitte warten");
        mDialog.setMessage(message);
        //mDialog.setProgressStyle();
        mDialog.show();
        return mDialog;
    }

}