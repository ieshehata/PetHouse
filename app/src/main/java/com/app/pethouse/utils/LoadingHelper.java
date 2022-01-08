package com.app.pethouse.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

public class LoadingHelper {
    private AlertDialog.Builder builder;
    private ACProgressFlower dialog;


    public LoadingHelper(Context context) {
        this.dialog = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.BLUE)
                .fadeColor(Color.DKGRAY)
                .build();
        this.builder = new AlertDialog.Builder(context);
    }


    public void showLoading(String message) {
        this.dialog.setTitle(message);
        this.dialog.show();
    }

    public void dismissLoading() {
        this.dialog.dismiss();
    }


    public void showDialog(String title,
                           String message,
                           String positiveText,
                           String negativeText,
                           DialogInterface.OnClickListener positive,
                           DialogInterface.OnClickListener negative
    ) {
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, positive)
                .setNegativeButton(negativeText, negative);
        builder.show();
    }
}
