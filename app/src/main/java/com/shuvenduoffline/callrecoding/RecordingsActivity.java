package com.shuvenduoffline.callrecoding;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RecordingsActivity extends AppCompatActivity {

    public  final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ask for permission app overlay
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if ( !Settings.canDrawOverlays(RecordingsActivity.this)){
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:"+getPackageName()));

                        startActivityForResult(intent,PERMISSION_REQUEST_CODE);

                    }
                    else {
                        showPopUpForRecording();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERMISSION_REQUEST_CODE){
            if (resultCode == RESULT_OK){

                //show popup for recording
                showPopUpForRecording();
            }
        }
    }

    private void showPopUpForRecording() {
        //start service that show the popup head
        startService(new Intent(RecordingsActivity.this,PopUpShowService.class));

    }

}
