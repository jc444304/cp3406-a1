package com.e.whence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

class Reminders extends ArrayList<Reminder> {

    void jsonDeserialize(JSONArray remindersBuffer) throws JSONException {
        Object objectBuffer;
        for (int i = 0; i < remindersBuffer.length(); i++) {
            Reminder reminder;
            objectBuffer = remindersBuffer.get(i);
            if (objectBuffer instanceof JSONObject) {
                JSONObject reminderBuffer = (JSONObject) objectBuffer;
                reminder = new Reminder(reminderBuffer);
            } else {
                reminder = new Reminder();
            }
            add(reminder);
        }
    }

    JSONArray jsonSerialize() throws JSONException {
        JSONArray remindersBuffer = new JSONArray();
        for (Reminder reminder : this) {
            remindersBuffer.put(reminder.jsonSerialize());
        }
        return remindersBuffer;
    }
}
