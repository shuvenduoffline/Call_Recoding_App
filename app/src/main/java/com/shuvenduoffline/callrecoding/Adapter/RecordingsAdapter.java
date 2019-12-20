package com.shuvenduoffline.callrecoding.Adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.shuvenduoffline.callrecoding.Database.Database;
import com.shuvenduoffline.callrecoding.MyNotification;
import com.shuvenduoffline.callrecoding.R;
import com.shuvenduoffline.callrecoding.datamodel.CallLog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.MyViewHolder> {

    private List<CallLog> callrecords;
    private Activity baseactivity;
    public int mExpandedPosition = -1;
    public int previousExpandedPosition = -1;
    public boolean isPlaying = false;
    private MediaPlayer mediaPlayer;
    private int playPosition = -1;

    public RecordingsAdapter(List<CallLog> callrecords, Activity activity) {
        this.callrecords = callrecords;
        this.baseactivity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_clip_layout
                        , parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.record_name.setText(callrecords.get(position).name);
        holder.phone_number.setText("Caller : " + callrecords.get(position).getPhonenumber());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String str = simpleDateFormat.format(new Date(callrecords.get(position).start_time)) + "\n" + callrecords.get(position).getDuration();
        holder.duration.setText(str);


        final boolean isExpanded = position == mExpandedPosition;
        if (isExpanded) {
            holder.btnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            holder.btnPlay.setImageResource(R.drawable.ic_play);
        }

        if (isExpanded)
            previousExpandedPosition = position;

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit name of the clip
                final EditText editText = new EditText(baseactivity);
                editText.setHint("Enter new title");
                AlertDialog.Builder builder1 = new AlertDialog.Builder(baseactivity, R.style.MyDialogTheme);
                builder1.setCancelable(true);
                builder1.setMessage("");
                editText.setText(callrecords.get(position).getName());
                builder1.setView(editText);
                builder1.setPositiveButton(
                        "Update",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Do your code...
                                String input_title = editText.getText().toString().trim();
                                if (input_title == null || input_title.length() < 5) {
                                    editText.setError("Type valid title");
                                    return;
                                }
                                callrecords.get(position).setName(editText.getText().toString().trim());
                                //change in database
                                Database.getInstance(baseactivity.getApplicationContext()).updateCall(callrecords.get(position));
                                notifyItemChanged(position);


                            }
                        });

                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Play the recording
                if (playPosition == position) {
                    StopMediaPlayer();
                } else {
                    playAudioFile(callrecords.get(position).getFilepath(), position);
                }
                mExpandedPosition = isExpanded ? -1 : position;
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(position);
            }
        });


        //Implement delete on long click listner
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(baseactivity, R.style.MyDialogTheme);
                builder1.setMessage("Do you want to remove '" + callrecords.get(position).getName() + "' ? \nYou can't recover it again.");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Delete the recording
                                CallLog clg = callrecords.remove(position);
                                Database.getInstance(baseactivity.getApplicationContext()).removeCall(clg);
                                notifyItemRemoved(position);
                                deleteFile(clg.getFilepath());

                            }
                        });

                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return callrecords.size();
    }

    void deleteFile(String path) {
        try {
            File file = new File(path);
            file.delete();
            if (file.exists()) {

                file.getCanonicalFile().delete();

                if (file.exists()) {
                    baseactivity.getApplicationContext().deleteFile(file.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView record_name;
        public TextView phone_number;
        public TextView duration;
        public ImageButton btnPlay;
        public ImageButton btnEdit;

        public MyViewHolder(View view) {
            super(view);
            record_name = view.findViewById(R.id.record_name);
            phone_number = view.findViewById(R.id.caller_id);
            duration = view.findViewById(R.id.call_duration);
            btnPlay = view.findViewById(R.id.btn_play);
            btnEdit = view.findViewById(R.id.btn_edit);
        }
    }

    //Play the audio file
    void playAudioFile(String path, final int position) {
        try {
            StopMediaPlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Toast.makeText(baseactivity, "Play Complete", Toast.LENGTH_SHORT).show();
                    mExpandedPosition = -1;
                    isPlaying=false;
                    notifyItemChanged(playPosition);
                    new MyNotification(baseactivity.getApplicationContext()).CancleNotification(MyNotification.PLAYING_NOTIFICATION);
                    StopMediaPlayer();
                }
            });
            mediaPlayer.start();
            //Show the Notification Playing
            new MyNotification(baseactivity.getApplicationContext()).ShowNotificationForRecordPlaying();
            playPosition = position;
            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Stop the Player and Release
    public void StopMediaPlayer() {
        new MyNotification(baseactivity.getApplicationContext()).CancleNotification(MyNotification.PLAYING_NOTIFICATION);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }


}
