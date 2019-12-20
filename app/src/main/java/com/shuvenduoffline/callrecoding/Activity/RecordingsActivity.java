package com.shuvenduoffline.callrecoding.Activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shuvenduoffline.callrecoding.Adapter.RecordingsAdapter;
import com.shuvenduoffline.callrecoding.Database.Database;
import com.shuvenduoffline.callrecoding.R;
import com.shuvenduoffline.callrecoding.Service.RecordingService;
import com.shuvenduoffline.callrecoding.datamodel.CallLog;

import java.util.ArrayList;

public class RecordingsActivity extends AppCompatActivity {

    public final int PERMISSION_REQUEST_CODE = 1;
    public final int READ_STORAGE_PERMISSION_REQUEST_CODE = 2;
    private RecyclerView recyclerView;
    private RecordingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);


        ArrayList<CallLog> records = Database.getInstance(getApplicationContext()).getAllCalls();

        adapter = new RecordingsAdapter(records, RecordingsActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);


    }

    public void askPermissionToRWExternalStore() {
        try {
            ActivityCompat.requestPermissions(RecordingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean isPermissionToRWEnternalStoreGiven() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result1 = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int result2 = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int result3 = getApplicationContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void askPermissionToAddWindows() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, PERMISSION_REQUEST_CODE);
    }

    public boolean isPermissionIsGivenToAddWindos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(RecordingsActivity.this)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PERMISSION_REQUEST_CODE || requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            StartCallRecordingService();
        }

    }

    private void StartCallRecordingService() {
        //start service that show the popup head
        if (isPermissionIsGivenToAddWindos() && isPermissionToRWEnternalStoreGiven()) {

            startService(new Intent(RecordingsActivity.this, RecordingService.class));

        } else if (!isPermissionIsGivenToAddWindos() && !isPermissionToRWEnternalStoreGiven()) {

            askPermissionToRWExternalStore();
            askPermissionToAddWindows();

        } else if (!isPermissionToRWEnternalStoreGiven()) {

            askPermissionToRWExternalStore();

        } else if (!isPermissionIsGivenToAddWindos()) {

            askPermissionToAddWindows();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.home, menu);

        MenuItem power = menu.findItem(R.id.action_on_off);
        // set your desired icon here based on a flag if you like
        if (isMyServiceRunning(RecordingService.class)) {
            power.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_power));
        } else {
            power.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_stand_by_black));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_on_off) {
            if (isMyServiceRunning(RecordingService.class)) {

                //stop the service when its running
                stopService(new Intent(getApplicationContext(), RecordingService.class));
                item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_stand_by_black));
                Toast.makeText(this, "Call Recording Service is Stop Now!", Toast.LENGTH_SHORT).show();
            } else {

                //start activity when not running already
                startService(new Intent(getApplicationContext(), RecordingService.class));
                Toast.makeText(this, "Call Recording Service is Active Now!", Toast.LENGTH_SHORT).show();
                item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_power));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem power = menu.findItem(R.id.action_on_off);
        // set your desired icon here based on a flag if you like
        if (isMyServiceRunning(RecordingService.class)) {
            power.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_power));
        } else {
            power.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_stand_by_black));
        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //stop the audio play when activity destroy
        if (adapter != null && adapter.isPlaying) {
            adapter.StopMediaPlayer();
        }
    }
}
