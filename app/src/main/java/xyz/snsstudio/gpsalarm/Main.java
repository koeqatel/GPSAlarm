package xyz.snsstudio.gpsalarm;

import static xyz.snsstudio.gpsalarm.Constant.TIME_TEXT;
import static xyz.snsstudio.gpsalarm.Constant.ALARM_NAME_TEXT;
import static xyz.snsstudio.gpsalarm.Constant.REPEAT_TEXT;
import static xyz.snsstudio.gpsalarm.Constant.ID_INT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Main extends Activity {

    private ArrayList<HashMap> list;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        PowerManager powerManager = (PowerManager) this.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GPSAlarmService");
        wakeLock.acquire();
        //TODO: I barely remember what a wakelock does but I do remember it being dirty.
        //Find a better way to do whatever I was doing by this.

        startService(new Intent(this, GPSAlarmService.class));

        //CreateList
        ListView lview = (ListView) findViewById(R.id.list);
        populateList();

        final lvAdapter adapter = new lvAdapter(this, list);
        lview.setAdapter(adapter);
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList;
                selectedFromList = (parent.getAdapter().getItem(position).toString());
                String[] list = selectedFromList.split(": =");
                String idString = list[2].substring(0, list[2].indexOf(","));
                editDialog(Integer.parseInt(idString));
            }
        });
    }

    public void editDialog(final int Id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle("What would you like to do with " + Id + "?");
        alert.setTitle("What would you like to do?");

        alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Read file in while loop, make json object, Add object content with Id via Intent
                File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/", "Alarms.json");

                ArrayList<JSONObject> text = new ArrayList<>();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    //Read file line by line
                    Intent intent = new Intent(Main.this, NewAlarm.class);
                    while ((line = br.readLine()) != null) {
                        JSONObject obj = new JSONObject(line);
                        int id = obj.getInt("Id");
                        if (id == Id) {
                            //Here I get the items from the Json and push them to NewAlarm
                            String Name = obj.getString("Name");
//                            String Daily = obj.getString("Daily");
                            int Volume = obj.getInt("Volume");
                            String Tone = obj.getString("Tone");
                            Integer Type = obj.getInt("Type");
                            double Latitude = obj.getDouble("Latitude");
                            double Longitude = obj.getDouble("Longitude");
                            Integer LocRad = obj.getInt("LocationRadius");

                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

                            ArrayList<String> daysOfWeek = new ArrayList<String>();
                            ArrayList<Date> datelist = new ArrayList<>();
                            Date convertedDate = new Date();
                            String[] Daily;
                            String[] DateTimes;
                            String DateTime;
                            String dailyString;


                            DateTime = obj.getString("DateTime");
                            DateTime = DateTime.replace("[", "");
                            DateTime = DateTime.replace("]", "");
                            DateTimes = DateTime.split(",");
                            for (int i = 0; i < DateTimes.length; i++) {

                                try {
                                    convertedDate = dateFormat.parse(DateTimes[i]);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                datelist.add(convertedDate);
                            }

                            dailyString = obj.getString("Daily");
                            dailyString = dailyString.replace("[", "");
                            dailyString = dailyString.replace("]", "");
                            Daily = dailyString.split(", ");

                            for (int i = 0; i < Daily.length; i++)  {
                                daysOfWeek.add(Daily[i]);
                            }

                            Data data = new Data();
                            data.Id = Id;
                            data.Name = Name;
                            data.DateTime = datelist;
                            data.Daily = daysOfWeek;
                            data.Volume = Volume;
                            data.Tone = Tone;
                            data.Type = Type;
                            data.Latitude = Latitude;
                            data.Longitude = Longitude;
                            data.LocationRadius = LocRad;

                        }
                    }
                    br.close();
                    intent.putExtra("Repopulate", true);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setNeutralButton("Delete", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/", "Alarms.json");

                ArrayList<JSONObject> text = new ArrayList<>();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    //Read file line by line
                    while ((line = br.readLine()) != null) {
                        JSONObject obj = new JSONObject(line);
                        int id = obj.getInt("Id");
                        //Unless the id equals the id of the clicked one
                        if (id != Id) {
                            text.add(obj);
                        }
                    }
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String JsonString = new String();

                for (int i = 0; i < text.size(); i++) {
                    //Combine old string with the listobject
                    JsonString = JsonString + text.get(i) + "\n";
                }
                FileWriter out;
                File Root = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/");

                try {
                    //Write to file
                    out = new FileWriter(new File(Root, "Alarms.json"));
                    out.append(JsonString);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //TODO: The app now refreshes the whole page instead of only the listview.
                //This is kind of dirty so find a better way to do this

                Intent intent = new Intent(Main.this, Main.class);
                finish();
                startActivity(intent);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    private void populateList() {

        list = new ArrayList<>();
        File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/", "Alarms.json");
        int id;

        //ReadFile
        ArrayList<JSONObject> text = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            //Read file line by line
            while ((line = br.readLine()) != null) {
                JSONObject obj = new JSONObject(line);
                text.add(obj);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Prepare List items
        String Time = "Time";
        String Name = "Name";
        String Date = "Date";
        int Id = -1;

        //PopulateList
        for (id = 0; id < text.size(); id++) {
            ArrayList<Date> datelist = new ArrayList<>();

            JSONObject Json = text.get(id);
            Date convertedDate = new Date();
            Calendar calendar = Calendar.getInstance();

            try {
                Id = Json.getInt("Id");
                Name = Json.getString("Name");

                String[] DateTimes;
                String DateTime = Json.getString("DateTime");
                DateTime = DateTime.replace("[", "");
                DateTime = DateTime.replace("]", "");
                DateTimes = DateTime.split(", ");

                SimpleDateFormat standardDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                SimpleDateFormat getFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

                try {
                    convertedDate = getFormat.parse(DateTimes[0]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String checkDate = standardDateFormat.format(convertedDate);

                if (checkDate.equals("01-01-1970")) {
                    //region Daily
                    String daily = Json.getString("Daily");
                    daily = daily.replace("[", "");
                    daily = daily.replace("]", "");
                    Date = daily;

                    //endregion
                } else {
                    //region DateTime
                    Date = "";
                    for (int i = 0; i < DateTimes.length; i++) {
                        convertedDate = null;
                        convertedDate = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

                        try {
                            convertedDate = dateFormat.parse(DateTimes[i]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        datelist.add(convertedDate);
                    }

                    for (int i = 0; i < datelist.size(); i++) {
                        String showDate;
                        Date tempDate = datelist.get(i);
                        calendar.setTime(tempDate);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        showDate = dateFormat.format(tempDate);

                        Date = Date + showDate + ", ";
                    }
                    //endregion
                }
            } catch (JSONException e) {
                Toast.makeText(this, "A Json error occured, please contact the writer.", Toast.LENGTH_SHORT).show();
            }
            if (Date != "")
                Date = Date.toString().substring(0, Date.length() - 2);
            calendar.setTime(convertedDate);

            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));

            Date date = new Date();
            date.setTime(cal.getTimeInMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Time = dateFormat.format(date);

            HashMap temp = new HashMap();
            temp.put(TIME_TEXT, Time);
            temp.put(ALARM_NAME_TEXT, Name);
            temp.put(REPEAT_TEXT, Date);
            temp.put(ID_INT, Id);
            list.add(temp);
        }
    }

    public void newAlarmButton_click(View view) {
        finish();
        Intent intent = new Intent(this, NewAlarm.class);
        startActivity(intent);
    }
}