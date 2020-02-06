package com.e.whence;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {

    private ToggleButton statusButton;

    private TextView nameInput;

    private TextView descriptionInput;

    private CheckBox
        weekDayOption1,
        weekDayOption2,
        weekDayOption3,
        weekDayOption4,
        weekDayOption5,
        weekDayOption6,
        weekDayOption7;

    private CheckBox notifyOption;

    private CheckBox vibrateOption;

    private TimePicker promptTimePicker;

    private Button ringtoneButton;

    private Uri currentTone;

    private Ringtone ringtone;

    private SeekBar volumeSeeker;

    private NumberPicker warnIntervalsPicker;

    private NumberPicker warnMinutesPicker;

    private Button saveButton;

    private Button deleteButton;

    private Reminder reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_activity);

        reminder = (Reminder) getIntent().getSerializableExtra("reminder");

        statusButton = findViewById(R.id.statusButton);
        statusButton.setChecked(reminder.isEnabled());
        statusButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reminder.enable();
                } else {
                    reminder.disable();
                }
                System.out.println("Set Reminder status to " + reminder.isEnabled() + ".");
            }
        });

        nameInput = findViewById(R.id.nameInput);
        nameInput.setText(reminder.getName());
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                reminder.setName(nameInput.getText().toString());
                System.out.println("Reminder name set to \"" + reminder.getName().replace('"', '\'') + "\".");
            }
        });

        descriptionInput = findViewById(R.id.descriptionInput);
        descriptionInput.setText(reminder.getDescription());
        descriptionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                reminder.setDescription(descriptionInput.getText().toString());
                System.out.println("Reminder description set to \"" + reminder.getDescription().substring(0, 20).replace('"', '\'') + (reminder.getDescription().length() > 20 ? "..." : "") + "\".");
            }
        });

        notifyOption = findViewById(R.id.notifyOption);
        notifyOption.setChecked(reminder.shouldNotify());
        notifyOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reminder.shouldNotify(isChecked);
                System.out.println("Reminder notification set to " + reminder.shouldNotify() + ".");
            }
        });

        vibrateOption = findViewById(R.id.vibrateOption);
        vibrateOption.setChecked(reminder.shouldVibrate());
        vibrateOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reminder.shouldVibrate(isChecked);
                System.out.println("Reminder vibration set to " + reminder.shouldVibrate() + ".");
            }
        });

        weekDayOption1 = findViewById(R.id.weekDayOptionSun);
        weekDayOption1.setChecked(reminder.isWeekDayOn(Calendar.SUNDAY));
        weekDayOption1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reminder.setWeekDay(Calendar.SUNDAY, isChecked);
                System.out.println("Reminder week-day of Sunday set to " + reminder.isWeekDayOn(Calendar.SUNDAY) + ".");
            }
        });
        weekDayOption2 = findViewById(R.id.weekDayOptionMon);
        weekDayOption2.setChecked(reminder.isWeekDayOn(Calendar.MONDAY));
        weekDayOption2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reminder.setWeekDay(Calendar.MONDAY, isChecked);
                System.out.println("Reminder week-day of Monday set to " + reminder.isWeekDayOn(Calendar.MONDAY) + ".");
            }
        });
        weekDayOption3 = findViewById(R.id.weekDayOptionTue);
        weekDayOption3.setChecked(reminder.isWeekDayOn(Calendar.TUESDAY));
        weekDayOption3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reminder.setWeekDay(Calendar.TUESDAY, isChecked);
                System.out.println("Reminder week-day of Tuesday set to " + reminder.isWeekDayOn(Calendar.TUESDAY) + ".");
            }
        });
        weekDayOption4 = findViewById(R.id.weekDayOptionWed);
        weekDayOption4.setChecked(reminder.isWeekDayOn(Calendar.WEDNESDAY));
        weekDayOption4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reminder.setWeekDay(Calendar.WEDNESDAY, isChecked);
                System.out.println("Reminder week-day of Wednesday set to " + reminder.isWeekDayOn(Calendar.WEDNESDAY) + ".");
            }
        });
        weekDayOption5 = findViewById(R.id.weekDayOptionThu);
        weekDayOption5.setChecked(reminder.isWeekDayOn(Calendar.THURSDAY));
        weekDayOption5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reminder.setWeekDay(Calendar.THURSDAY, isChecked);
                System.out.println("Reminder week-day of Thursday set to " + reminder.isWeekDayOn(Calendar.THURSDAY) + ".");
            }
        });
        weekDayOption6 = findViewById(R.id.weekDayOptionFri);
        weekDayOption6.setChecked(reminder.isWeekDayOn(Calendar.FRIDAY));
        weekDayOption6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reminder.setWeekDay(Calendar.FRIDAY, isChecked);
                System.out.println("Reminder week-day of Friday set to " + reminder.isWeekDayOn(Calendar.FRIDAY) + ".");
            }
        });
        weekDayOption7 = findViewById(R.id.weekDayOptionSat);
        weekDayOption7.setChecked(reminder.isWeekDayOn(Calendar.SATURDAY));
        weekDayOption7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                reminder.setWeekDay(Calendar.SATURDAY, isChecked);
                System.out.println("Reminder week-day of Saturday set to " + reminder.isWeekDayOn(Calendar.SATURDAY) + ".");
            }
        });

        promptTimePicker = findViewById(R.id.promptTimePicker);
        promptTimePicker.setCurrentHour(reminder.getHour());
        promptTimePicker.setCurrentMinute(reminder.getMinute());
        promptTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                reminder.setHour(hourOfDay);
                reminder.setMinute(minute);
                System.out.println("Reminder time set to " + reminder.getHour() + ":" + reminder.getMinute() + ".");
            }
        });

        ringtoneButton = findViewById(R.id.ringtoneButton);
        ringtoneButton.setText(RingtoneManager.getRingtone(this, reminder.getRingtonePath() == null ? null : Uri.parse(reminder.getRingtonePath())).getTitle(this));
        ringtoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Choose Ringtone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, reminder.getRingtonePath());
                intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                startActivityForResult(intent, REQUEST_RINGTONE);
            }
        });

        warnIntervalsPicker = findViewById(R.id.warnIntervalsPicker);
        warnIntervalsPicker.setMinValue(0);
        warnIntervalsPicker.setMaxValue(Reminder.MAX_WARN_INTERVALS);
        warnIntervalsPicker.setValue(reminder.getWarnIntervals());
        warnIntervalsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                reminder.setWarnIntervals(newVal);
                System.out.println("Reminder warning-intervals set to " + reminder.getWarnIntervals() + ".");
            }
        });

        warnMinutesPicker = findViewById(R.id.warnMinutesPicker);
        warnMinutesPicker.setMinValue(1);
        warnMinutesPicker.setMaxValue(Reminder.MAX_WARN_MINUTES);
        warnMinutesPicker.setValue(reminder.getWarnMinutes());
        warnMinutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                reminder.setWarnMinutes(newVal);
                System.out.println("Reminder warning-minutes set to " + reminder.getWarnMinutes() + ".");
            }
        });

        volumeSeeker = findViewById(R.id.volumeSeeker);
        volumeSeeker.setProgress(reminder.getVolume());
        volumeSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                reminder.setVolume(volumeSeeker.getProgress());
                System.out.println("Reminder volume set to " + reminder.getVolume() + " percent.");
            }
        });

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Reminder attempting to save changes.");
                reminder.setName(nameInput.getText().toString());
                reminder.setDescription(descriptionInput.getText().toString());
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Reminder attempting to be deleted.");
                getIntent().putExtra("delete", true);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });
    }

    static final int REQUEST_RINGTONE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_RINGTONE:
                if (resultCode == RESULT_OK) {
                    currentTone = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    ringtone = RingtoneManager.getRingtone(this, currentTone);
                    reminder.setRingtonePath(currentTone.toString());
                    ringtoneButton.setText(ringtone.getTitle(this));
                    System.out.println("Reminder ringtone set to \"" + reminder.getRingtonePath() + "\".");
                }
                break;
        }
    }
}
