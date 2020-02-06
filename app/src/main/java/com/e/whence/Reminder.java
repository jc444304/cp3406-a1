package com.e.whence;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;

public class Reminder implements Serializable {

    private boolean enabled = true;

    boolean isEnabled() {
        return enabled;
    }

    void enable() {
        enabled = true;
    }

    void disable() {
        enabled = false;
    }

    private String name = "Un-named Reminder";

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    private String description = "";

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    private int weekDays = 0;

    int getWeekDays() {
        return weekDays;
    }

    boolean isWeekDayOn(int calendarWeekDay) {
        return (weekDays & (1 << calendarWeekDay)) != 0;
    }

    void setWeekDays(int weekDays) {
        this.weekDays = weekDays;
    }

    void setWeekDay(int calendarDay, boolean on) {
        int mask = 1 << calendarDay;
        if (on) {
            weekDays |= mask;
        } else {
            weekDays ^= mask;
        }
    }

    private int hour;

    int getHour() {
        return hour;
    }

    void setHour(int hour) {
        this.hour = hour;
    }

    private int minute;

    int getMinute() {
        return minute;
    }

    void setMinute(int minute) {
        this.minute = minute;
    }

    static final int MAX_WARN_INTERVALS = 5;

    private int warnIntervals = 3;

    int getWarnIntervals() {
        return warnIntervals;
    }

    void setWarnIntervals(int warnIntervals) {
        this.warnIntervals = warnIntervals < 0 ? 0 : warnIntervals > MAX_WARN_INTERVALS ? MAX_WARN_INTERVALS : warnIntervals;
    }

    static final int MAX_WARN_MINUTES = 2 * 60;

    private int warnMinutes = 5;

    int getWarnMinutes() {
        return warnMinutes;
    }

    void setWarnMinutes(int warnMinutes) {
        this.warnMinutes = warnMinutes < 0 ? 0 : warnMinutes > MAX_WARN_MINUTES ? MAX_WARN_MINUTES : warnMinutes;
    }

    void setWarning(int warnIntervals, int warnMinutes) {
        setWarnIntervals(warnIntervals);
        setWarnMinutes(warnMinutes);
    }

    private boolean notify = true;

    boolean shouldNotify() {
        return notify;
    }

    void shouldNotify(boolean notify) {
        this.notify = notify;
    }

    private boolean vibrate = true;

    boolean shouldVibrate() {
        return vibrate;
    }

    void shouldVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    private String ringtonePath;

    String getRingtonePath() {
        return ringtonePath;
    }

    void setRingtonePath(String ringtonePath) {
        this.ringtonePath = ringtonePath;
    }

    private int volume = 50;

    int getVolume() {
        return volume;
    }

    void setVolume(int volume) {
        this.volume = volume > 0 ? (volume < 100 ? volume : 100) : 0;
    }

    long lastUpdateTime;

    Reminder() {}

    Reminder(@NonNull JSONObject jsonObject) {
        jsonDeserialize(jsonObject);
    }

    private void jsonDeserialize(JSONObject reminderBuffer) {

        Object objectBuffer;

        try {
            objectBuffer = reminderBuffer.get("name");
        } catch (JSONException exception) {
            objectBuffer = null;
        }
        if (objectBuffer instanceof String) {
            setName((String) objectBuffer);
        }

        try {
            objectBuffer = reminderBuffer.get("description");
        } catch (JSONException exception) {
            objectBuffer = null;
        }
        if (objectBuffer instanceof String) {
            setDescription((String) objectBuffer);
        }

        try {
            objectBuffer = reminderBuffer.get("days");
        } catch (JSONException exception) {
            objectBuffer = null;
        }
        if (objectBuffer instanceof JSONArray) {
            JSONArray weekDaysBuffer = (JSONArray) objectBuffer;
            for (int i = 0; i < weekDaysBuffer.length(); i++) {
                Object elementBuffer;
                try {
                    elementBuffer = weekDaysBuffer.get(i);
                } catch (JSONException exception) {
                    elementBuffer = null;
                }
                if (elementBuffer instanceof String) {
                    String weekDayBuffer = (String) elementBuffer;
                    switch (weekDayBuffer.toLowerCase()) {
                        case "sun":
                            setWeekDay(Calendar.SUNDAY, true);
                            break;
                        case "mon":
                            setWeekDay(Calendar.MONDAY, true);
                            break;
                        case "tue":
                            setWeekDay(Calendar.TUESDAY, true);
                            break;
                        case "wed":
                            setWeekDay(Calendar.WEDNESDAY, true);
                            break;
                        case "thu":
                            setWeekDay(Calendar.THURSDAY, true);
                            break;
                        case "fri":
                            setWeekDay(Calendar.FRIDAY, true);
                            break;
                        case "sat":
                            setWeekDay(Calendar.SATURDAY, true);
                            break;
                    }
                }
            }
        }

        try {
            objectBuffer = reminderBuffer.get("hour");
        } catch (JSONException exception) {
            objectBuffer = null;
        }
        if (objectBuffer instanceof Integer) {
            setHour((int) objectBuffer);
        }

        try {
            objectBuffer = reminderBuffer.get("minute");
        } catch (JSONException exception) {
            objectBuffer = null;
        }
        if (objectBuffer instanceof Integer) {
            setMinute((int) objectBuffer);
        }

        try {
            objectBuffer = reminderBuffer.get("notify");
        } catch (JSONException exception) {
            objectBuffer = null;
        }
        if (objectBuffer instanceof Boolean) {
            shouldNotify((boolean) objectBuffer);
        }

        try {
            objectBuffer = reminderBuffer.get("vibrate");
        } catch (JSONException exception) {
            objectBuffer = null;
        }
        if (objectBuffer instanceof Boolean) {
            shouldVibrate((boolean) objectBuffer);
        }

        try {
            objectBuffer = reminderBuffer.get("ringtone");
        } catch (JSONException exception) {
            objectBuffer = null;
        }
        if (objectBuffer instanceof String) {
            setRingtonePath((String) objectBuffer);
        } else {
            setRingtonePath(null);
        }
    }

    JSONObject jsonSerialize() throws JSONException {

        JSONObject reminderBuffer = new JSONObject();

        if (getName() != null && getName().length() > 0) {
            reminderBuffer.put("name", getName());
        }

        if (getDescription() != null && getDescription().length() > 0) {
            reminderBuffer.put("description", getDescription());
        }

        if (getWeekDays() > 0) {
            JSONArray daysBuffer = new JSONArray();
            if (isWeekDayOn(Calendar.SUNDAY)) {
                daysBuffer.put("sun");
            }
            if (isWeekDayOn(Calendar.MONDAY)) {
                daysBuffer.put("mon");
            }
            if (isWeekDayOn(Calendar.TUESDAY)) {
                daysBuffer.put("tue");
            }
            if (isWeekDayOn(Calendar.WEDNESDAY)) {
                daysBuffer.put("wed");
            }
            if (isWeekDayOn(Calendar.THURSDAY)) {
                daysBuffer.put("thu");
            }
            if (isWeekDayOn(Calendar.FRIDAY)) {
                daysBuffer.put("fri");
            }
            if (isWeekDayOn(Calendar.SATURDAY)) {
                daysBuffer.put("sat");
            }
            reminderBuffer.put("days", daysBuffer);
        }

        reminderBuffer.put("hour", getHour());
        reminderBuffer.put("minute", getMinute());

        if (shouldNotify()) {
            reminderBuffer.put("notify", true);
        }

        if (shouldVibrate()) {
            reminderBuffer.put("vibrate", true);
        }

        if (getRingtonePath() != null) {
            reminderBuffer.put("ringtone", getRingtonePath());
        }

        reminderBuffer.put("warn-intervals", getWarnIntervals());
        reminderBuffer.put("warn-minutes", getWarnMinutes());

        return reminderBuffer;
    }

    @Override
    public String toString() {
        return getName();
    }
}
