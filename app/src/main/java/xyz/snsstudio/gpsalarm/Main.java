package xyz.snsstudio.gpsalarm;

import static xyz.snsstudio.gpsalarm.Constant.TIME_TEXT;
import static xyz.snsstudio.gpsalarm.Constant.ALARM_NAME_TEXT;
import static xyz.snsstudio.gpsalarm.Constant.REPEAT_TEXT;
import static xyz.snsstudio.gpsalarm.Constant.ID_INT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
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
        //TODO This feels dirty
        //Thats because it is...

        startService(new Intent(this, GPSAlarmService.class));

        //region CreateList
        ListView lview = (ListView) findViewById(android.R.id.list);
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
                editModal(Integer.parseInt(idString));

            }
        });
        //endregion
    }

    public void editModal(final int Id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("What would you like to do with ID " + Id + "?");

        alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(Main.this, "Fix Edit", Toast.LENGTH_SHORT).show();
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
                            //Here i get the items from the Json and push them to NewAlarm
                            String Hour = obj.getString("Hour");
                            String Minute = obj.getString("Minute");
                            String Name = obj.getString("Name");
                            String Date = obj.getString("Date");
                            String Tone = obj.getString("Tone");
                            String Volume = obj.getString("Volume");
                            String Type = obj.getString("Type");
                            String Location = obj.getString("Location");


                            intent.putExtra("Id", Id);
                            intent.putExtra("Hour", Hour);
                            intent.putExtra("Minute", Minute);
                            intent.putExtra("Name", Name);
                            intent.putExtra("Date", Date);
                            intent.putExtra("Tone", Tone);
                            intent.putExtra("Volume", Volume);
                            intent.putExtra("Type", Type);
                            intent.putExtra("Location", Location);
                        }
                    }
                    br.close();
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
                //TODO find a legit way instead of this evil workaround

                finish();
                Intent intent = new Intent(Main.this, Main.class);
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

        //region ReadFile
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
        //endregion

        //region PopulateList
        for (id = 0; id < text.size(); id++) {

            JSONObject Json = text.get(id);
            try {

                HashMap temp = new HashMap();
                temp.put(TIME_TEXT, Json.getString("Hour") + ":" + Json.getString("Minute"));
                temp.put(ALARM_NAME_TEXT, Json.getString("Name"));
                if (Json.getString("Date") != "EmptyDate")
                    temp.put(REPEAT_TEXT, Json.getString("Date"));
                if (Json.getString("Id") != "EmptyId")
                    temp.put(ID_INT, Json.getString("Id"));

                list.add(temp);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        //endregion
    }

    public void AddDateTimeButton(View view) {
        finish();
        Intent intent = new Intent(this, NewAlarm.class);
        startActivity(intent);
    }

}