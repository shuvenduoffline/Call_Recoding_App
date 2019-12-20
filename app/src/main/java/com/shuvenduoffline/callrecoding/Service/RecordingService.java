package com.shuvenduoffline.callrecoding.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.shuvenduoffline.callrecoding.Notification.MyNotification;
import com.shuvenduoffline.callrecoding.OutgoingReceiver;
import com.shuvenduoffline.callrecoding.R;
import com.shuvenduoffline.callrecoding.datamodel.CallLog;

import java.io.File;
import java.util.Date;

import static com.shuvenduoffline.callrecoding.Notification.MyNotification.RECORDING_NOTIFICATION;

public class RecordingService extends Service {


    private static WindowManager windowManager;
    private static View recoderview;
    private static CallLog phoneCall;
    private static boolean isRecording = false;
    private static MediaRecorder mediaRecorder;
    private static boolean isPopUpShowing = false;
    private static Context ctx;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();

    }


    public static void ShowPopUpForRecording(final String number) {
        //recoding pop up view

        if (!isPopUpShowing) {
            isPopUpShowing = true;
            recoderview = LayoutInflater.from(ctx).inflate(R.layout.pop_up_layout, null);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );

            params.gravity = Gravity.CENTER;

            windowManager = (WindowManager) ctx.getSystemService(WINDOW_SERVICE);

            windowManager.addView(recoderview, params);

            Button btnStartRecord = recoderview.findViewById(R.id.btn_record_call);
            Button btnCancle = recoderview.findViewById(R.id.btn_cancel);


            btnStartRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ctx, "Starting Recording!", Toast.LENGTH_SHORT).show();
                    if (recoderview != null && windowManager != null) {
                        windowManager.removeView(recoderview);
                    }
                    isPopUpShowing = false;
                    CallLog clg = new CallLog();
                    clg.setPhonenumber(number);
                    startRecording(clg);
                }
            });

            btnCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ctx, "Cancel", Toast.LENGTH_SHORT).show();
                    //To dismiss the recording popup
                    if (recoderview != null && windowManager != null) {
                        windowManager.removeView(recoderview);
                    }
                    isPopUpShowing = false;

                }
            });
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {


        //Sytstem manager that will notify call state
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);

        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                // super.onCallStateChanged(state, phoneNumber);

                if (TelephonyManager.CALL_STATE_IDLE == state) {
                    // called when call state is idle , i.e when we hang up
                    if (isPopUpShowing && recoderview != null && windowManager != null) {
                        windowManager.removeView(recoderview);
                        isPopUpShowing = false;
                    }

                    stopRecording(phoneNumber);

                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //called when call is connected

                } else if (state == TelephonyManager.CALL_STATE_RINGING) {

                    //when any call rings show recording popup
                    ShowPopUpForRecording("ABC");
                }


            }
        }, PhoneStateListener.LISTEN_CALL_STATE);


        //Start the Outgoing Call Reciver
        OutgoingReceiver outgoingReceiver = new OutgoingReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        getApplicationContext().registerReceiver(outgoingReceiver, intentFilter);

        //todo chnage to sticky
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //remove recorder popup when the service is destroyed
        if (recoderview != null && windowManager != null) {
            windowManager.removeView(recoderview);
        }

        //Removing the reciver
        OutgoingReceiver outgoingReceiver = new OutgoingReceiver();
        getApplicationContext().unregisterReceiver(outgoingReceiver);

    }


    private void stopRecording(String phonenumber) {

        if (isRecording) {
            new MyNotification(getApplicationContext()).CancleNotification(RECORDING_NOTIFICATION);
            try {
                phoneCall.setEnd_time(new Date().getTime());
                phoneCall.setName(String.valueOf(new Date().getTime()));
                if (phonenumber != null && phonenumber.trim().length() > 8)
                    phoneCall.setPhonenumber(phonenumber);
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
                phoneCall.save(getBaseContext());
                Toast.makeText(this, "Call Recording is saved!", Toast.LENGTH_SHORT).show();
                // displayNotification(phoneCall);
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        phoneCall = null;
    }

    private static void startRecording(CallLog phonecall) {
        if (!isRecording) {

            isRecording = true;
            phoneCall = phonecall;
            File file = null;
            try {
                phoneCall.setName(String.valueOf(new Date().getTime()));
                phoneCall.setStart_time(new Date().getTime());
                File dir = getFilesDirectory();
                mediaRecorder = null;
                mediaRecorder = new MediaRecorder();
                file = File.createTempFile(String.valueOf(new Date().getTime()), ".3gp", dir);
                phoneCall.setFilepath(file.getAbsolutePath());
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                mediaRecorder.setAudioSamplingRate(8000);
                mediaRecorder.setAudioEncodingBitRate(12200);//12200
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setOutputFile(file.getAbsolutePath());
                mediaRecorder.prepare();

                //start the recorder after 1 second
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mediaRecorder.start();
                        } catch (Exception e) {
                            Toast.makeText(ctx, "Unable to Record!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, 1000);

                new MyNotification(ctx).ShowNotificationForRecordOnging();
            } catch (Exception e) {
                Toast.makeText(ctx, "Something went wrong!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                isRecording = false;
                if (file != null) file.delete();
                phoneCall = null;
                isRecording = false;
                mediaRecorder = null;
            }
        }
    }


    public static File getFilesDirectory() {

        String filesDir = (new StringBuilder()).append(Environment.getExternalStorageDirectory().getAbsolutePath()).append("/").append("calls").append("/").toString();
        File myDir = new File(filesDir);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        return myDir;
    }
}
