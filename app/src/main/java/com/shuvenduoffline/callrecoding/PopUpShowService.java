package com.shuvenduoffline.callrecoding;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class PopUpShowService extends Service {


    private WindowManager windowManager;
    private View recoderview;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //recoding pop up view
        recoderview = LayoutInflater.from(this).inflate(R.layout.pop_up_layout, null);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.CENTER;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        windowManager.addView(recoderview, params);

        Button btnStartRecord = recoderview.findViewById(R.id.btn_record_call);
        Button btnCancle = recoderview.findViewById(R.id.btn_cancel);


        btnStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PopUpShowService.this, "Starting Recording", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PopUpShowService.this, "Cancel", Toast.LENGTH_SHORT).show();
                //To dismiss the recording popup
                stopSelf();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //remove recorder popup when the service is destroyed
        if (recoderview != null) {
            windowManager.removeView(recoderview);
        }

    }
}
