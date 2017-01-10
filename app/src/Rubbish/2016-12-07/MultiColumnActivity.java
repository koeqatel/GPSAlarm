package xyz.snsstudio.gpsalarm;

import static xyz.snsstudio.gpsalarm.Constant.TIME_TEXT;
import static xyz.snsstudio.gpsalarm.Constant.ALARM_NAME_TEXT;
import static xyz.snsstudio.gpsalarm.Constant.REPEAT_TEXT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.view.View;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

public class MultiColumnActivity extends Activity {

    private ArrayList<HashMap> list;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        PowerManager powerManager = (PowerManager) this.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GPSAlarmService");
        wakeLock.acquire();

        startService(new Intent(this, MyService.class));

        //region CreateList
        ListView lview = (ListView) findViewById(android.R.id.list);
        populateList();
        listviewAdapter adapter = new listviewAdapter(this, list);
        lview.setAdapter(adapter);
        //endregion
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
                if (Json.getString("Repeat") != "EmptyRepeat")
                    temp.put(REPEAT_TEXT, Json.getString("Repeat"));
                if (Json.getString("Date") != "EmptyDate")
                    temp.put(REPEAT_TEXT, Json.getString("Date"));

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