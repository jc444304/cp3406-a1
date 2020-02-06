package com.e.whence;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ExampleUnitTest {

    private MainActivity mainActivity = new MainActivity();

    private void waitSeconds(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void testListHanding() {
        Reminders reminders = mainActivity.reminders;
        reminders.clear();
        assert reminders.size() == 0;
        Reminder reminder = new Reminder();
        reminders.add(reminder);
        assert reminders.size() == 1;
        reminders.remove(reminder);
        assert reminders.size() == 0;
    }

    @Test
    public void testJsonDeserialization() {
        Reminder reminder;
        String reminderName = "tested name";
        String reminderDescription = "tested description";
        int reminderVolume = 80;
        String reminderRingtonePath = "content://media/internal/audio/media/100";
        JSONObject reminderBuffer = null;
        try {
            reminderBuffer = new JSONObject(
                "{" +
                "\"name\": \"" + reminderName + "\"," +
                "\"description\": \"" + reminderDescription + "\"," +
                "\"days\": [\"tue\", \"fri\"]," +
                "\"notify\": true," +
                "\"vibrate\": true," +
                "\"ringtone\": \"" + reminderRingtonePath + "\"," +
                "\"volume\": " + reminderVolume + "," +
                "\"extra\": 1" +
                "}"
            );
        } catch (JSONException exception) {
            assert false;
        }
        assert reminderBuffer != null;
        reminder = new Reminder(reminderBuffer);
        assert reminder.getName().equals(reminderName);
        assert reminder.getDescription().equals(reminderDescription);
        assert reminder.getWeekDays() == (Calendar.TUESDAY | Calendar.FRIDAY);
        assert reminder.isWeekDayOn(Calendar.TUESDAY);
        reminder.setWeekDay(Calendar.MONDAY, true);
        assert reminder.isWeekDayOn(Calendar.MONDAY);
        assert reminder.shouldNotify();
        assert reminder.shouldVibrate();
        assert reminder.getRingtonePath().equals(reminderRingtonePath);
        assert reminder.getVolume() == reminderVolume;
    }

    @Test
    public void testJsonSerialization() {
        Reminder reminder = new Reminder();

        JSONObject reminderBuffer = null;
        try {
            reminderBuffer = reminder.jsonSerialize();
        } catch (JSONException exception) {
            assert false;
        }
        JSONObject otherReminderBuffer = null;
        try {
            otherReminderBuffer = new JSONObject(reminderBuffer.toString());
        } catch (JSONException exception) {
            assert false;
        }
        Reminder otherReminder = new Reminder(otherReminderBuffer);
        /* after you serialize and deserialize the same reminder, they should match: */
        assert reminder.getName().equals(otherReminder.getName());
        assert reminder.getDescription().equals(otherReminder.getDescription());
        assert reminder.isEnabled() == otherReminder.isEnabled();
    }

    @Test
    public void testEffects() {
        Reminder reminder = new Reminder();

        reminder.setRingtonePath("content://media/internal/audio/media/100");
        mainActivity.playRingtone(reminder);
        waitSeconds(2);
        reminder.setRingtonePath("content://media/internal/audio/media/101");
        mainActivity.playRingtone(reminder);
        waitSeconds(2);
        reminder.shouldVibrate(true);
        mainActivity.vibrate();
        waitSeconds(2);
        reminder.shouldVibrate(false);
        mainActivity.vibrate();
        waitSeconds(2);
        reminder.setName("Notification Title");
        reminder.setDescription("Notification Description");
        reminder.shouldNotify(true);
        mainActivity.showNotification(reminder);
        waitSeconds(2);
        reminder.shouldNotify(false);
        mainActivity.showNotification(reminder);
    }

    @Test
    public void testSamplePlan() {
        Reminders reminders = mainActivity.reminders;
        reminders.clear();
        assert reminders.size() == 0;
        Reminder reminder;

        /* my weird wake-up reminder during the week: */
        reminders.add(reminder = new Reminder());
        reminder.setName("Wake up!!!");
        reminder.setWeekDays(
            Calendar.MONDAY |
            Calendar.TUESDAY |
            Calendar.WEDNESDAY |
            /* no class on Thursdays */
            Calendar.FRIDAY);
        reminder.setDescription("Get the hell out of your dream-space you naive millennial and go to school!!!");
        reminder.setHour(6); /* you need to be awake by 6am whether you like it or not, young man! */
        reminder.setWarnMinutes(30); /* annoy me at 5:30am so when i get up at 6 then i'm less angry */

        /* go to Monday's Data Mining lecture: */
        reminders.add(reminder = new Reminder());
        reminder.setName("CP3403 Lecture");
        reminder.setDescription("Here we go! Monday is like any other day.");
        reminder.setWeekDay(Calendar.MONDAY, true);

        /* go to Tuesday's Data Mining practical: */
        reminders.add(reminder = new Reminder());
        reminder.setName("CP3403 Practical");
        reminder.setDescription("Get your shovels ready, it's time to mine some data with Jai!");
        reminder.setWeekDay(Calendar.TUESDAY, true);

        /* go to Wednesday's CMS lecture: */
        reminders.add(reminder = new Reminder());
        reminder.setName("CP3402 Lecture");
        reminder.setDescription("Lindsay the web developer God.");
        reminder.setWeekDay(Calendar.WEDNESDAY, true);
        reminder.setHour(9);

        /* later, go to CMS practical: */
        reminders.add(reminder = new Reminder());
        reminder.setName("CP3402 Practical");
        reminder.setDescription("Oh boy, time to get tormented...");
        reminder.setWeekDay(Calendar.WEDNESDAY, true);
        reminder.setHour(11);

        /* then, go to Mobile Computing practical: */
        reminders.add(reminder = new Reminder());
        reminder.setName("CP3406 Practical");
        reminder.setDescription("Phew... Thank God there are no MAC computers in that room.");
        reminder.setWeekDay(Calendar.WEDNESDAY, true);
        reminder.setHour(13);

        /* go to Friday's Mobile Computing lecture */
        reminders.add(reminder = new Reminder());
        reminder.setName("CP3406 Lecture");
        reminder.setDescription("Finally, Jase!");
        reminder.setWeekDay(Calendar.FRIDAY, true);
        reminder.setHour(11);
        reminder.setWarning(2, 30);

        /* recently, my car does not work! i need to catch the last bus before midnight: */
        reminders.add(reminder = new Reminder());
        reminder.setName("Catch Midnight Bus");
        reminder.setDescription("Be at the bus stop or else you'll be riding with an Uber stranger.");
        reminder.setWeekDays(
            Calendar.MONDAY |
            Calendar.TUESDAY |
            Calendar.WEDNESDAY |
            Calendar.FRIDAY);
        reminder.setHour(23);
        reminder.setHour(45);
        /* warn me 3 times every 10 minutes: */
        reminder.setWarning(3, 10);
        /* it's essential i hear and feel the reminder: */
        reminder.shouldVibrate(true);
        reminder.setRingtonePath("content://media/internal/audio/media/100");
    }

    @Test
    public void testPlanExecution() {
        GregorianCalendar calendar;

        System.out.println("Skip to Monday at 5:29 (1 min before 5:30 warning) ...");
        calendar = new GregorianCalendar(2019, Calendar.MARCH, 4, 5, 29);
        calendar.set(Calendar.SECOND, 57); /* skip almost a minute so we don't wait for testing */
        assert calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
        System.out.println("You will notice the warning ...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Skip to 5:59 (1 min before 6am) ...");
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 57);
        System.out.println("You will notice the prompt ...");

        System.out.println("Skip to 23:44 (1 min before warning me about going to bus stop).");
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 44);
        calendar.set(Calendar.SECOND, 57);

        System.out.println("Skip to Tuesday morning at 5:59am ...");
        calendar = new GregorianCalendar(2019, Calendar.MARCH, 5, 5, 59, 57);
        assert calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY;
    }
}