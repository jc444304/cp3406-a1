package com.e.whence;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements Application.ActivityLifecycleCallbacks {

    final String SAVED_FILE_NAME = "default.json";

    Reminders reminders = new Reminders();

    Calendar calendar;

    private ArrayAdapter<Reminder> adapter;

    private ListView remindersList;

    private Button addButton;

    private Vibrator vibrator;

    final private long vibrateSequences[] = {100, 50, 50};

    public void vibrate() {
        System.out.println("Reminder attempting to vibrate.");
        if (vibrator != null) {
            vibrator.vibrate(vibrateSequences, 2);
        }
    }

    public void playRingtone(Reminder reminder) {
        System.out.println("Reminder attempting to play ringtone.");
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(reminder.getRingtonePath()));
        mediaPlayer.setVolume(reminder.getVolume(), reminder.getVolume());
        mediaPlayer.start();
    }

    public void showNotification(Reminder reminder) {
        System.out.println("Reminder attempting to show notification.");
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setTicker("Whence")
            .setContentTitle("Reminder: " + "\n" + "Due Time: " + reminder.getHour() + ":" + reminder.getMinute() + ".\n" + reminder.getName())
            .setContentText(reminder.getDescription())
            .setDefaults(
                Notification.DEFAULT_LIGHTS |
                Notification.DEFAULT_SOUND |
                (reminder.shouldVibrate() ? Notification.DEFAULT_VIBRATE : 0)
            )
            .setContentIntent(contentIntent)
            .setContentInfo("Info");
        notificationManager.notify(1, builder.build());
    }

    private Thread thread = new Thread() {
        @Override
        public void run() {
            while (true) {
                try {
                    /* only run every second to cease using excess resources: */
                    Thread.sleep(1000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }

                Calendar synchedCalendar;
                if (calendar == null) {
                    /* if we are not testing then we don't enforce the date and time and use current time instead: */
                    synchedCalendar = GregorianCalendar.getInstance();
                } else {
                    synchedCalendar = calendar;
                }

                /* run each reminder: */
                for (Reminder reminder : reminders) {
                    /* reminder is ignored unless enabled */
                    if (!reminder.isEnabled()) continue;
                    /* proceed if today is an enabled week day: */
                    if (reminder.isWeekDayOn(synchedCalendar.get(Calendar.DAY_OF_WEEK))) {
                        /* create a calender for the future time when the reminder will prompt */
                        GregorianCalendar otherCalendar = new GregorianCalendar(
                            synchedCalendar.get(Calendar.YEAR),
                            synchedCalendar.get(Calendar.MONTH),
                            synchedCalendar.get(Calendar.DAY_OF_MONTH),
                            reminder.getHour(),
                            reminder.getMinute()
                        );
                        long timeDifference = otherCalendar.getTimeInMillis() - synchedCalendar.getTimeInMillis();
                        long minutesDifference = TimeUnit.MILLISECONDS.toMinutes(timeDifference);
                        boolean doVibration = false, doRingtone = false, doNote = false;
                        /* check if the reminder is passed its warning time: */
                        for (int i = 0; i < reminder.getWarnIntervals(); i++) {
                            if (synchedCalendar.getTimeInMillis() > reminder.lastUpdateTime) {
                                if (minutesDifference < reminder.getWarnMinutes() * reminder.getWarnIntervals()) {
                                    reminder.lastUpdateTime = synchedCalendar.getTimeInMillis();
                                    doVibration = true;
                                    doRingtone = true;
                                    doNote = true;
                                }
                            }
                        }
                        /* check if the reminder needs to be prompted */
                        if (synchedCalendar.get(Calendar.HOUR_OF_DAY) > reminder.getHour()) {
                            if (synchedCalendar.get(Calendar.MINUTE) > reminder.getMinute()) {
                                doVibration = true;
                                doRingtone = true;
                                doNote = true;
                                /* now that the reminder has expired, we can disable it: */
                                reminder.disable();
                            }
                        }
                        /* check if reminder must vibrate: */
                        if (doVibration && reminder.shouldVibrate()) {
                            vibrate();
                        }
                        /* if the ringtone exists, then play it: */
                        if (doRingtone && reminder.getRingtonePath() != null) {
                            playRingtone(reminder);
                        }
                        /* if a notification needs to be shown: */
                        if (doNote && reminder.shouldNotify()) {
                            showNotification(reminder);
                        }
                    }
                }
            }
        }
    };

    MainActivity() {
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        try {
            load(SAVED_FILE_NAME);
        } catch (IOException exception) {
            System.out.println("Failed to handle file for loading.");
        } catch (JSONException exception) {
            System.out.println("Failed to parse JSON data for loading.");
        }

        adapter = new ArrayAdapter<>(this, R.layout.reminder_item, R.id.reminderItemNameText, reminders);
        remindersList = findViewById(R.id.remindersList);
        remindersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Reminder " + (id + 1) + " of " + remindersList.getCount() + " selected.");
                Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
                intent.putExtra("reminder", reminders.get((int)id));
                intent.putExtra("reminder_id", id);
                startActivityForResult(intent, REQUEST_REMINDER);
            }
        });
        remindersList.setAdapter(adapter);

        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Attempting to add new reminder.");
                Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
                intent.putExtra("reminder_id", (long) reminders.size());
                Reminder reminder = new Reminder();
                reminders.add(reminder);
                adapter.notifyDataSetChanged();
                intent.putExtra("reminder", reminder);
                startActivityForResult(intent, REQUEST_REMINDER);
            }
        });
    }

    static final int REQUEST_REMINDER = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_REMINDER:
                if (resultCode == RESULT_OK) {
                    long reminderId = data.getLongExtra("reminder_id", 0);
                    if (reminderId < 0 || reminderId >= reminders.size()) break;

                    boolean mustDelete = data.getBooleanExtra("delete", false);
                    if (mustDelete) {
                        System.out.println("Reminder " + reminderId + " will be deleted.");
                        reminders.remove((int) reminderId);
                        adapter.notifyDataSetChanged();
                        break;
                    }

                    Reminder reminder = (Reminder) data.getSerializableExtra("reminder");
                    if (reminder != null) {
                        System.out.println("Reminder " + reminderId + " has been changed.");
                        reminders.set((int) reminderId, reminder);
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    File file;

    void load(String fileName) throws IOException, JSONException {
        System.out.println("Reminders attempting to load.");

        file = new File(getCacheDir().toString() + "/" + fileName);
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Failed to create new configuration file.");
        }

        FileInputStream fileHandle = new FileInputStream(file);
        int fileSize = fileHandle.available();
        byte[] fileByteData = new byte[fileSize];
        fileHandle.read(fileByteData);
        fileHandle.close();
        String fileStringData = new String(fileByteData, StandardCharsets.UTF_8);

        JSONObject mainBuffer;
        try {
            mainBuffer = new JSONObject(fileStringData);
        } catch (JSONException exception) {
            file.delete();
            throw exception;
        }

        Object objectBuffer;
        try {
            objectBuffer = mainBuffer.get("reminders");
        } catch (JSONException exception) {
            objectBuffer = null;
        }
        if (objectBuffer instanceof JSONArray) {
            System.out.println("Reminders attempting to deserialize.");
            JSONArray remindersBuffer = (JSONArray) objectBuffer;
            reminders.jsonDeserialize(remindersBuffer);
            System.out.println("Reminders deserialize in total: " + reminders.size() + ".");
        } else {
            System.out.println("Reminders not found in serialization.");
        }
    }

    void save(String fileName) throws IOException, JSONException {
        System.out.println("Reminders attempting to save.");

        if (file != null && !file.exists()) {
            file.createNewFile();
        } else {
            file = new File(getCacheDir().toString() + "/" + fileName);
        }

        FileOutputStream fileHandle = new FileOutputStream(file);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("reminders", reminders.jsonSerialize());
        fileHandle.write(jsonObject.toString().getBytes());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {
        try {
            save(SAVED_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        try {
            save(SAVED_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        try {
            save(SAVED_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
