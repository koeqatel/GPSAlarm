package xyz.snsstudio.gpsalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

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
                    int Type = 0;
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
                            //TODO ask for type, if type is notification run "showNotification" else run "showAlarm"
                            while (i < Date.size()) {
                                if (CurrentDate.equals(Date.get(i)) || curWeekDayName.equals(Date.get(i))) {
                                    if (CurrentTime.equals(AlarmTime)) {
                                        if (Type == 0 || Type == 2) {
                                            showNotification(obj.getString("Name"));
                                        } else if (Type == 1 || Type == 2) {
                                            showAlarm(obj.getString("Name"), obj.getString("Tone"));
                                            break;
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

    public void showNotification(String Alarmname) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MultiColumnActivity.class), 0);
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

    public void showAlarm(String Alarmname, String Tone) {
        Intent dialogIntent = new Intent(this, Alarm.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.putExtra("Name", Alarmname);
        dialogIntent.putExtra("Tone", Tone);
        startActivity(dialogIntent);
    }
}
