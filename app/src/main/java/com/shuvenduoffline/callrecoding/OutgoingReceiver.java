package com.shuvenduoffline.callrecoding;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shuvenduoffline.callrecoding.Service.RecordingService;
import com.shuvenduoffline.callrecoding.datamodel.CallLog;

import static com.shuvenduoffline.callrecoding.Service.RecordingService.ShowPopUpForRecording;

public class OutgoingReceiver extends BroadcastReceiver {
    public OutgoingReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        ShowPopUpForRecording(number);

    }
}