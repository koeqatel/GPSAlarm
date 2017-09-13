package xyz.snsstudio.gpsalarm;

import java.util.ArrayList;
import java.util.Date;

public class Data {
    public static Integer Id;
    public static String Name;
    public static ArrayList<Date> DateTime;
    public static ArrayList<String> Daily;
    public static String Tone;
    public static Integer Volume;
    public static Integer Type;
    public static Double Latitude;
    public static Double Longitude;
    public static Integer LocationRadius;
    public static Integer SnoozeDelay;
    public static Integer SnoozeTimes;
    public static Boolean Snooze = false;

    public static void clean() {
        Id = null;
        Name = null;
        DateTime = null;
        Daily = null;
        Tone = null;
        Volume = null;
        Type = null;
        Latitude = null;
        Longitude = null;
        LocationRadius = null;
        SnoozeDelay = null;
        Snooze = false;
        SnoozeTimes = 0;
    }
}

