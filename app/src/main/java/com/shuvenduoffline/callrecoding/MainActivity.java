package com.shuvenduoffline.callrecoding;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //to show full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        //To give a delay of 2 sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //start the recording list activity
                Intent intent = new Intent(MainActivity.this, RecordingsActivity.class);
                startActivity(intent);

                //finishes the current activity
                finish();
            }
        }, 2000);
    }
}
