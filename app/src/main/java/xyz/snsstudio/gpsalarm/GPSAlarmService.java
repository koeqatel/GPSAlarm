package xyz.snsstudio.gpsalarm;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Integer.parseInt;

public class GPSAlarmService extends Service {
    public String pubHour = "";
    public String pubMinute = "";
    public String AlarmDate = "";

    public GPSAlarmService() {

        Timer SecTimer = new Timer();
        TimerTask seclyTask = new TimerTask() {
            public void run() {
                Calendar c = Calendar.getInstance();
                int Second = c.get(Calendar.SECOND);
                if (Second == 0) {
                    int Type;
                    int Minute = c.get(Calendar.MINUTE);
                    int Hour = c.get(Calendar.HOUR_OF_DAY);
                    int curDay = c.get(Calendar.DAY_OF_MONTH);
                    int curMonth = c.get(Calendar.MONTH) + 1;
                    int curYear = c.get(Calendar.YEAR);
                    int curWeekDay = c.get(Calendar.DAY_OF_WEEK);
                    String curWeekDayName = new DateFormatSymbols().getWeekdays()[curWeekDay];
                    String curDayS;
                    String curMonthS;


                    if (curDay < 10)
                        curDayS = "0" + curDay;
                    else
                        curDayS = "" + curDay;

                    if (curMonth < 10)
                        curMonthS = "0" + curMonth;
                    else
                        curMonthS = "" + curMonth;


                    String CurrentDate = curDayS + "-" + curMonthS + "-" + curYear;
                    String Hourstring = Integer.toString(Hour);
                    String Minutestring = Integer.toString(Minute);

                    if (Integer.toString(Hour).length() == 1) {
                        Hourstring = "0" + Integer.toString(Hour);
                    }

                    if (Integer.toString(Minute).length() == 1) {
                        Minutestring = "0" + Integer.toString(Minute);
                    }

                    final String CurrentTime = Hourstring + ":" + Minutestring;

                    //region ReadFile
                    try {
                        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/", "Alarms.json");

                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;
                        //Read file line by line
                        while ((line = br.readLine()) != null) {
                            JSONObject obj = new JSONObject(line);
                            pubHour = obj.getString("Hour").toString();
                            pubMinute = obj.getString("Minute").toString();
                            Type = parseInt(obj.getString("Type"));

                            String AlarmTime = pubHour + ":" + pubMinute;
                            AlarmDate = obj.getString("Date").toString();

                            List<String> Date = new ArrayList<String>(Arrays.asList(AlarmDate.split(", ")));
                            int i = 0;
                            while (i < Date.size()) {
                                if (CurrentDate.equals(Date.get(i)) || curWeekDayName.equals(Date.get(i))) {
                                    if (CurrentTime.equals(AlarmTime)) {
                                        boolean isAtLocation = getLocation(obj.getDouble("Latitude"), obj.getDouble("Longitude"));
                                        if (isAtLocation) {
                                            if (Type == 0 || Type == 2) {
                                                vibrate();
                                            }
                                            if (Type == 1 || Type == 2) {
                                                showAlarm(obj.getString("Name"), obj.getString("Tone"), obj.getInt("Volume"));
                                            }
                                            if (Type == 3) {
                                                showNotification(obj.getString("Name"));
                                            }
                                        }
                                    }
                                    i++;
                                }
                            }
                        }
                        br.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //endregion
                }

            }
        };

        SecTimer.schedule(seclyTask, 0l, 1000 * 1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void vibrate() {

    }

    public void showNotification(String Alarmname) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, Main.class), 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(Alarmname)
                .setContentText(AlarmDate + " " + pubHour + ":" + pubMinute)
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
        startActivity(dialogIntent);
    }

    public boolean getLocation(double lat1, double lng1) {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = manager.getAllProviders();
        Location location;


        double lat2 = 0;
        double lng2 = 0;
        for (int i = 0; i < providers.size(); i++) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
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
        if (distance(lat1, lng1, lat2, lng2) < 0.1) { // if distance < 0.1 miles we take locations as equal
            return true;
        } else {
            return false;
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
