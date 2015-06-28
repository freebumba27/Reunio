package com.altaoferta.reunio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmBroadCustReciver extends BroadcastReceiver {
    public AlarmBroadCustReciver() {
    }

    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("time", intent.getStringExtra("time"));
        context.startActivity(i);
    }

}