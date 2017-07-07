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
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GPSAlarmService extends Service {

    int seconds;
    Date currentDate = new Date();

    public GPSAlarmService() {
        Timer SecTimer = new Timer();
        TimerTask seclyTask = new TimerTask() {
            public void run() {
                Calendar calendar = Calendar.getInstance();
                seconds = calendar.get(Calendar.SECOND);
                int curWeekDay = calendar.get(Calendar.DAY_OF_WEEK);
                String curWeekDayName = new DateFormatSymbols().getWeekdays()[curWeekDay];
                calendar.set(Calendar.MILLISECOND, 0);
                currentDate.setTime(calendar.getTimeInMillis());
                if (seconds == 0) {
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/", "Alarms.json");

                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));

                            String line;
                            //Read file line by line
                            while ((line = br.readLine()) != null) {
                                JSONObject obj = new JSONObject(line);

                                String[] dateTimeArray = {obj.getString("DateTime")};
                                String dailyString = obj.getString("Daily");

                                dailyString = dailyString.replace("[", "");
                                dailyString = dailyString.replace("]", "");
                                String[] dailyArray = dailyString.split(", ");

                                int Type = obj.getInt("Type");

                                if (obj.getString("Daily") != "") {
                                    for (int i = 0; i < dailyArray.length; i++) {
                                        String Day = dailyArray[i];

                                        if (curWeekDayName.equals(Day)) {
                                            if (currentDate.getTime() == dateTimeArray[0]) {
                                                //TODO: Hooee?
                                                boolean isAtLocation = getLocation(obj.getDouble("Latitude"), obj.getDouble("Longitude"));
                                                if (isAtLocation) {
                                                    if (Type == 0 || Type == 2) {
                                                        showAlarm(obj.getString("Name"), obj.getString("Tone"), obj.getInt("Volume"), true);
                                                    }
                                                    if (Type == 1 || Type == 2) {
                                                        showAlarm(obj.getString("Name"), obj.getString("Tone"), obj.getInt("Volume"));
                                                    }
                                                    if (Type == 3) {
                                                        showNotification(obj.getString("Name"), curWeekDayName);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    for (int i = 0; i < dateTimeArray.length; i++) {

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                                        Date convertedAlarmDate = new Date(0);
                                        dateTimeArray[i] = dateTimeArray[i].replace("[", "");
                                        dateTimeArray[i] = dateTimeArray[i].replace("]", "");

                                        try {
                                            convertedAlarmDate = dateFormat.parse(dateTimeArray[i]);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        calendar.setTimeInMillis(convertedAlarmDate.getTime());
                                        calendar.set(Calendar.SECOND, 0);
                                        convertedAlarmDate.setTime(calendar.getTimeInMillis());
                                        if (currentDate.getTime() == convertedAlarmDate.getTime()) {

                                            boolean isAtLocation = getLocation(obj.getDouble("Latitude"), obj.getDouble("Longitude"));
                                            if (isAtLocation) {
                                                if (Type == 0 || Type == 2) {
                                                    showAlarm(obj.getString("Name"), obj.getString("Tone"), obj.getInt("Volume"), true);
                                                }
                                                if (Type == 1 || Type == 2) {
                                                    showAlarm(obj.getString("Name"), obj.getString("Tone"), obj.getInt("Volume"));
                                                }
                                                if (Type == 3) {
                                                    showNotification(obj.getString("Name"), convertedAlarmDate);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            br.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception Ex) {
                        System.out.println(Ex);
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

    public void showAlarm(String Alarmname, String Tone, Integer Volume) {
        Intent dialogIntent = new Intent(this, Alarm.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra("Name", Alarmname);
        dialogIntent.putExtra("Tone", Tone);
        dialogIntent.putExtra("Volume", Volume);
        dialogIntent.putExtra("Vibrate", Volume);
        startActivity(dialogIntent);
    }

    public void showAlarm(String Alarmname, String Tone, Integer Volume, boolean Vibration) {
        Intent dialogIntent = new Intent(this, Alarm.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra("Name", Alarmname);
        dialogIntent.putExtra("Tone", Tone);
        dialogIntent.putExtra("Volume", Volume);
        dialogIntent.putExtra("Vibrate", Vibration);
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

    /**
     * calculates the distance between two locations in MILES
     */
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
}
