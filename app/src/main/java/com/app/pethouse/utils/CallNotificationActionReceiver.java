package com.app.pethouse.utils;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

import com.app.pethouse.activities.auth.LoginActivity;
import com.app.pethouse.activities.general.IncomingCallActivity;

public class CallNotificationActionReceiver extends BroadcastReceiver {


    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        if (intent != null && intent.getExtras() != null) {

            String action = "";
            action = intent.getStringExtra("ACTION_TYPE");

            if (action != null && !action.equalsIgnoreCase("")) {
                performClickAction(context, action);
            }

            // Close the notification after the click action is performed.
            /*
            Intent iclose = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(iclose);
            context.stopService(new Intent(context, IncomingCallService.class));
            */
        }


    }

    private void performClickAction(Context context, String action) {
        if (action.equalsIgnoreCase("RECEIVE_CALL")) {
            Intent intentCallReceive = new Intent(mContext, LoginActivity.class);
            intentCallReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intentCallReceive);
            Intent iclose = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(iclose);
            context.stopService(new Intent(context, IncomingCallService.class));
        } else if (action.equalsIgnoreCase("DIALOG_CALL")) {
            Intent intentCallReceive = new Intent(mContext, IncomingCallActivity.class);
            intentCallReceive.putExtra("Call", "incoming");
            intentCallReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intentCallReceive);
        } else {
            context.stopService(new Intent(context, IncomingCallService.class));
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }
    }
}