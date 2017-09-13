package xyz.snsstudio.gpsalarm;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GPSAlarmService extends Service {

    int seconds;
    Date pickedDateTime = new Date();
    Date pickedDateTimeWithSnooze = new Date();
    String pickedDate;
    String pickedDay;
    int type;


    public GPSAlarmService() {
        Timer SecTimer = new Timer();
        TimerTask seclyTask = new TimerTask() {
            public void run() {

                Calendar currCal = Calendar.getInstance();  //The current time
                seconds = currCal.get(Calendar.SECOND);

                if (seconds == 0) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/", "Alarms.json");

                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;
                        //Read file line by line
                        while ((line = br.readLine()) != null) {
                            JSONObject picked = new JSONObject(line);

                            pickedDateTime = convertToSingleDate(picked.getString("DateTime"));
                            pickedDay = removeBrackets(picked.getString("Daily"));
                            pickedDate = removeBrackets(picked.getString("DateTime"));
                            type = Integer.parseInt(removeBrackets(picked.getString("Type")));

                            if (Data.Snooze) {
                                pickedDateTimeWithSnooze = convertToSingleDate(picked.getString("DateTime"));
                                Calendar date = Calendar.getInstance();
                                date.setTime(pickedDateTimeWithSnooze);
                                long t = date.getTimeInMillis();
                                int SnoozeTimes = Data.SnoozeTimes;
                                int Delay = Integer.parseInt(picked.getString("SnoozeDelay"));
                                int addedTime = 1000 * 60 * Delay * SnoozeTimes;
                                pickedDateTimeWithSnooze = new Date(t + addedTime);
                            }

                            int currDayInt = currCal.get(Calendar.DAY_OF_WEEK);
                            String currentDay = new DateFormatSymbols().getWeekdays()[currDayInt];
                            Date currentDateTime = Calendar.getInstance().getTime();

                            Calendar currentDateCal = Calendar.getInstance();
                            currentDateCal.set(Calendar.SECOND, 0);
                            Date currentDate = new Date();
                            currentDate.setTime(currentDateCal.getTimeInMillis());

                            if (convertToTime(pickedDateTime).equals(convertToTime(currentDateTime)) || convertToTime(pickedDateTimeWithSnooze).equals(convertToTime(currentDateTime))) {


                                //If daily is filled, else it's date
                                if (pickedDay != "") {
                                    //Its a dayname
                                    String[] pickedDays = pickedDay.split(", ");
                                    for (int i = 0; i < pickedDays.length; i++) {
                                        if (pickedDays[i].equals(currentDay)) {
                                            if (getLocation(picked.getLong("Latitude"), picked.getLong("Longitude"))) {
                                                if (type == 0 || type == 2) {
                                                    showAlarm(picked.getString("Name"), picked.getString("Tone"), picked.getInt("Volume"), picked.getInt("SnoozeDelay"), true);
                                                }
                                                if (type == 1 || type == 2) {
                                                    showAlarm(picked.getString("Name"), picked.getString("Tone"), picked.getInt("Volume"), picked.getInt("SnoozeDelay"));
                                                }
                                                if (type == 3) {
                                                    showNotification(picked.getString("Name"), pickedDays[i]);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    //Its a date
                                    String[] pickedDateTimes = pickedDate.split(", ");
                                    for (int i = 0; i < pickedDateTimes.length; i++) {
                                        Date pickedDate = convertToDate(pickedDateTimes[i]);
                                        if (pickedDate.compareTo(currentDate) < 0) {
                                            if (getLocation(Long.parseLong(picked.getString("Latitude")), Long.parseLong(picked.getString("Longitude")))) {
                                                if (type == 0 || type == 2) {
                                                    showAlarm(picked.getString("Name"), picked.getString("Tone"), picked.getInt("Volume"), picked.getInt("SnoozeDelay"), true);
                                                }
                                                if (type == 1 || type == 2) {
                                                    showAlarm(picked.getString("Name"), picked.getString("Tone"), picked.getInt("Volume"), picked.getInt("SnoozeDelay"));
                                                }
                                                if (type == 3) {
                                                    showNotification(picked.getString("Name"), pickedDate);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        };
        SecTimer.schedule(seclyTask, 0l, 1000 * 1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void showNotification(String alarmName, Date alarmDate) {
        String showDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm");
        showDate = dateFormat.format(alarmDate);

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, Main.class), 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(alarmName)
                .setContentText(showDate)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public void showNotification(String alarmName, String showDate) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, Main.class), 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(alarmName)
                .setContentText(showDate)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public void showAlarm(String Alarmname, String Tone, Integer Volume, Integer SnoozeDelay) {
        Intent dialogIntent = new Intent(this, Alarm.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra("Name", Alarmname);
        dialogIntent.putExtra("Tone", Tone);
        dialogIntent.putExtra("Volume", Volume);
        dialogIntent.putExtra("SnoozeDelay", SnoozeDelay);
        startActivity(dialogIntent);
    }

    public void showAlarm(String Alarmname, String Tone, Integer Volume, Integer SnoozeDelay, boolean Vibration) {
        Intent dialogIntent = new Intent(this, Alarm.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra("Name", Alarmname);
        dialogIntent.putExtra("Tone", Tone);
        dialogIntent.putExtra("Volume", Volume);
        dialogIntent.putExtra("Vibrate", Vibration);
        dialogIntent.putExtra("SnoozeDelay", SnoozeDelay);
        startActivity(dialogIntent);
    }

    public boolean getLocation(double lat1, double lng1) {
        if (lat1 != 100 && lng1 != 200) {

            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = manager.getAllProviders();
            Location location;


            double lat2 = 0;
            double lng2 = 0;
            for (int i = 0; i < providers.size(); i++) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //  Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return false;
                }
                location = manager.getLastKnownLocation(providers.get(i));
                location.getTime();

                lat2 = location.getLatitude();
                lng2 = location.getLongitude();
            }

            // lat1 and lng1 are the values of a previously stored location
            //LocationRadius should be in place of 1
            if (distance(lat1, lng1, lat2, lng2) < 1 / 1000) { // if distance < 0.1 kilometers we take locations as equal
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist; // output distance, in KILOMETERS
    }

    public String removeBrackets(String input) {
        String convertedInput = input.replace("[", "");
        return convertedInput.replace("]", "");
    }

    public Date convertToSingleDate(String dateTimeString) {
        Date date = new Date();
        date.setTime(0);
        String convertedDateTimeString = removeBrackets(dateTimeString);

        if (convertedDateTimeString != "") {

            String[] convertedDateTimeArray = convertedDateTimeString.split(", ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            try {
                date = dateFormat.parse(convertedDateTimeArray[0]);
            } catch (ParseException e) {
                Toast.makeText(this, "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        }
        return date;
    }

    public Date convertToDate(String dateTimeString) {
        Date date = new Date();
        date.setTime(0);
        String convertedDateTimeString = removeBrackets(dateTimeString);

        if (convertedDateTimeString != "") {

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            try {
                date = dateFormat.parse(dateTimeString);
            } catch (ParseException e) {
                Toast.makeText(this, "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        }
        return date;
    }

    public String convertToTime(Date dateTimeDate) {
        String Time;

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Time = dateFormat.format(dateTimeDate);

        return Time;
    }
}
