package xyz.snsstudio.gpsalarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

public class NewAlarm extends Activity {
    String Date;
    String DaysOfWeek = "";
    public int Hours = 6;
    public int Minutes = 0;
    public JSONObject Json = new JSONObject();
    public ArrayList<String> TempSaveList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newalarm);

        loadAfterRedirect();
        if (getIntent().getIntExtra("Id", 0) != 0) {
            //Put everything to text, delete old view with this id
            Button thb = (Button) findViewById(R.id.timeHourButton);
            Button tmb = (Button) findViewById(R.id.timeMinuteButton);
            EditText an = (EditText) findViewById(R.id.alarmName);
            TextView dc = (TextView) findViewById(R.id.dateContent);

            thb.setText(getIntent().getStringExtra("Hour"));
            tmb.setText(getIntent().getStringExtra("Minute"));
            an.setText(getIntent().getStringExtra("Name"));
            Hours = Integer.parseInt(getIntent().getStringExtra("Hour"));
            Minutes = Integer.parseInt(getIntent().getStringExtra("Minute"));
            Date = getIntent().getStringExtra("Date");
            dc.setText(Date);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MultiColumnActivity.class);
        finish();
        startActivity(intent);
    }

    //region TimeButtons

    public void timeUpHourButton(View view) {
        if (Hours == 23) {
            Hours = 0;
        } else {
            Hours = Hours + 1;
        }
        TextView Hour = (TextView) findViewById(R.id.timeHourButton);
        if (Integer.toString(Hours).length() == 1) {
            String Hourstring = "0" + Integer.toString(Hours);
            Hour.setText(Hourstring);
        } else {
            Hour.setText(Integer.toString(Hours));
        }


    }

    public void timeDownHourButton(View view) {
        if (Hours == 0) {
            Hours = 23;
        } else {
            Hours = Hours - 1;
        }
        TextView Hour = (TextView) findViewById(R.id.timeHourButton);
        if (Integer.toString(Hours).length() == 1) {
            String Hourstring = "0" + Integer.toString(Hours);
            Hour.setText(Hourstring);
        } else {
            Hour.setText(Integer.toString(Hours));
        }
    }

    public void timeUpMinuteButton(View view) {
        if (Minutes == 59) {
            Minutes = 0;
        } else {
            Minutes = Minutes + 1;
        }
        TextView Minute = (TextView) findViewById(R.id.timeMinuteButton);
        if (Integer.toString(Minutes).length() == 1) {
            String Minutestring = "0" + Integer.toString(Minutes);
            Minute.setText(Minutestring);
        } else {
            Minute.setText(Integer.toString(Minutes));
        }
    }

    public void timeDownMinuteButton(View view) {
        if (Minutes == 0) {
            Minutes = 59;
        } else {
            Minutes = Minutes - 1;
        }
        TextView Minute = (TextView) findViewById(R.id.timeMinuteButton);
        if (Integer.toString(Minutes).length() == 1) {
            String Minutestring = "0" + Integer.toString(Minutes);
            Minute.setText(Minutestring);
        } else {
            Minute.setText(Integer.toString(Minutes));
        }
    }
//endregion

    public void cancelNewAlarmButton(View view) {
        finish();
        Intent intent = new Intent(this, MultiColumnActivity.class);
        startActivity(intent);
    }

    public void saveNewAlarmButton(View view) throws JSONException, IOException {
        //TODO check if total time in millis is bigger than current time in millies, if not show toast about time in past not being possible.
        File Root = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/");
        File file = new File(Root, "Alarms.json");
        Button thb = (Button) findViewById(R.id.timeHourButton);
        Button tmb = (Button) findViewById(R.id.timeMinuteButton);
        EditText et = (EditText) findViewById(R.id.alarmName);
        ArrayList<JSONObject> text = new ArrayList<>();
        ArrayList<Integer> Ids = new ArrayList<>();
        String JsonString = new String();

        //region Create directory
        try {
            if (Root.mkdir()) {
                System.out.println("Directory created");
            } else {
                System.out.println("Directory is not created, like whatever...");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        //endregion

        //region ReadFile
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        //Read the file line by line
        while ((line = br.readLine()) != null) {
            JSONObject obj = new JSONObject(line);
            text.add(obj);
            Ids.add(obj.getInt("Id"));
        }
        br.close();

        //endregion

        //region Create Json object
        String hourtext = thb.getText().toString();
        String minutetext = tmb.getText().toString();
        String alarmname = et.getText().toString();
        int max = -1;
        if (Ids.size() != 0)
            max = Collections.max(Ids);
        if (getIntent().getIntExtra("Id", 0) == 0)
            Json.put("Id", max + 1);
        else
            Json.put("Id", getIntent().getIntExtra("Id", 0));

        Json.put("Hour", hourtext);
        Json.put("Minute", minutetext);
        Json.put("Name", alarmname);
        if (Date != null && !Date.equals("No dates selected"))
            Json.put("Date", Date);
        else {
            Calendar c = Calendar.getInstance();
            int CurHour = c.get(Calendar.HOUR_OF_DAY);
            int CurMinute = c.get(Calendar.MINUTE);

            int AlarmHour = Integer.parseInt(hourtext);
            int AlarmMinute = Integer.parseInt(minutetext);


            int curDay = c.get(Calendar.DAY_OF_MONTH);
            int curMonth = c.get(Calendar.MONTH) + 1;
            int curYear = c.get(Calendar.YEAR);
            int CurTimeInmins = CurHour * 60 + CurMinute;
            int AlarmTimeInMins = AlarmHour * 60 + AlarmMinute;

            if (CurTimeInmins < AlarmTimeInMins) {
                String CurrentDate;

                if (curDay < 10)
                    CurrentDate = "0" + curDay + "-" + curMonth + "-" + curYear;
                else if (curDay < 10 && curMonth < 10)
                    CurrentDate = "0" + curDay + "-0" + curMonth + "-" + curYear;
                else if (curMonth < 10)
                    CurrentDate = curDay + "-0" + curMonth + "-" + curYear;
                else
                    CurrentDate = curDay + "-" + curMonth + "-" + curYear;
                Json.put("Date", CurrentDate);
            } else {
                String TomorrahDate;
                if (curDay < 10) {
                    curDay++;
                    TomorrahDate = "0" + curDay + "-" + curMonth + "-" + curYear;
                } else if (curDay < 10 && curMonth < 10) {
                    curDay++;
                    TomorrahDate = "0" + curDay + "-0" + curMonth + "-" + curYear;
                } else if (curMonth < 10) {
                    curDay++;
                    TomorrahDate = curDay + "-0" + curMonth + "-" + curYear;
                } else {
                    curDay++;
                    TomorrahDate = curDay + "-" + curMonth + "-" + curYear;
                }

                Json.put("Date", TomorrahDate);
            }
        }
        Json.put("Tone", "EmptyTone");
        Json.put("Volume", "EmptyVolume");
        Json.put("Type", "EmptyType");
        Json.put("Location", "EmptyLocation");
        //endregion

        //region WriteFile
        for (int i = 0; i < text.size(); i++) {
            //Combine old string with the listobject
            if (getIntent().getIntExtra("Id", 0) == text.get(i).getInt("Id")) {
            } else {
                JsonString = JsonString + text.get(i) + "\n";
            }
        }

        JsonString = JsonString + Json.toString();

        FileWriter out;
        try

        {
            //Write to file
            out = new FileWriter(new File(Root, "Alarms.json"));
            out.append(JsonString);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //endregion

        Intent intent = new Intent(this, MultiColumnActivity.class);
        setResult(RESULT_OK, intent);
        finish();
        startActivity(intent);

    }

    public void dateButton(View view) {
        saveAndRedirect(CalendarClass.class);
    }

    public void dailyButton(View view) {
        save();
        setContentView(R.layout.repeatdays);
    }

    public void cancelButton(View view) {
        setContentView(R.layout.newalarm);
        load();
    }

    public void saveButton(View view) {
        CheckBox checkBoxSunday = (CheckBox) findViewById(R.id.checkBoxSunday);
        CheckBox checkBoxMonday = (CheckBox) findViewById(R.id.checkBoxMonday);
        CheckBox checkBoxTuesday = (CheckBox) findViewById(R.id.checkBoxTuesday);
        CheckBox checkBoxWednesday = (CheckBox) findViewById(R.id.checkBoxWednesday);
        CheckBox checkBoxThursday = (CheckBox) findViewById(R.id.checkBoxThursday);
        CheckBox checkBoxFriday = (CheckBox) findViewById(R.id.checkBoxFriday);
        CheckBox checkBoxSaturday = (CheckBox) findViewById(R.id.checkBoxSaturday);

        DaysOfWeek = "";
        if (checkBoxSunday.isChecked())
            DaysOfWeek += "Sunday, ";
        if (checkBoxMonday.isChecked())
            DaysOfWeek += "Monday, ";
        if (checkBoxTuesday.isChecked())
            DaysOfWeek += "Tuesday, ";
        if (checkBoxWednesday.isChecked())
            DaysOfWeek += "Wednesday, ";
        if (checkBoxThursday.isChecked())
            DaysOfWeek += "Thursday, ";
        if (checkBoxFriday.isChecked())
            DaysOfWeek += "Friday, ";
        if (checkBoxSaturday.isChecked())
            DaysOfWeek += "Saturday, ";
        setContentView(R.layout.newalarm);
        load();
        if (!DaysOfWeek.equals(""))
            Date = DaysOfWeek.substring(0, DaysOfWeek.length() - 2);
        TextView dc = (TextView) findViewById(R.id.dateContent);
        dc.setText(Date);

    }

    public void AlarmTone(View vIew) {
        Toast.makeText(this, "Working on it right now!", Toast.LENGTH_SHORT).show();
        save();s
        Intent i = new Intent(Intent.ACTION_GET_CONTENT , android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,1);
        //TODO Hi, i am an app, and i am crashoholic.
    }

    public void AlarmType(View vIew) {
        Toast.makeText(this, "Not Implemented yet", Toast.LENGTH_SHORT).show();
    }

    public void LocationClick(View vIew) {
        Toast.makeText(this, "Not Implemented yet", Toast.LENGTH_SHORT).show();
    }

    public void saveAndRedirect(Class Class) {
        TempSaveList.clear();
        Button thb = (Button) findViewById(R.id.timeHourButton);
        Button tmb = (Button) findViewById(R.id.timeMinuteButton);
        EditText an = (EditText) findViewById(R.id.alarmName);
        TextView dc = (TextView) findViewById(R.id.dateContent);

        TempSaveList.add(thb.getText().toString());
        TempSaveList.add(tmb.getText().toString());
        TempSaveList.add(an.getText().toString());
        TempSaveList.add(dc.getText().toString());

        Intent intent = new Intent(this, Class);
        intent.putExtra("List", TempSaveList);
        startActivity(intent);
    }

    public void save() {
        TempSaveList.clear();
        Button thb = (Button) findViewById(R.id.timeHourButton);
        Button tmb = (Button) findViewById(R.id.timeMinuteButton);
        EditText an = (EditText) findViewById(R.id.alarmName);
        TextView dc = (TextView) findViewById(R.id.dateContent);

        TempSaveList.add(thb.getText().toString());
        TempSaveList.add(tmb.getText().toString());
        TempSaveList.add(an.getText().toString());
        TempSaveList.add(dc.getText().toString());
    }

    public void loadAfterRedirect() {
        try {
            ArrayList<String> IntentList = getIntent().getStringArrayListExtra("List");
            int i = 0;
            while (i < IntentList.size()) {

                TempSaveList.add(IntentList.get(i));
                i++;
            }

            Button thb = (Button) findViewById(R.id.timeHourButton);
            Button tmb = (Button) findViewById(R.id.timeMinuteButton);
            EditText an = (EditText) findViewById(R.id.alarmName);
            TextView dc = (TextView) findViewById(R.id.dateContent);

            thb.setText(TempSaveList.get(0));
            tmb.setText(TempSaveList.get(1));
            an.setText(TempSaveList.get(2));
            Hours = Integer.parseInt(TempSaveList.get(0));
            Minutes = Integer.parseInt(TempSaveList.get(1));
            Date = getIntent().getStringExtra("Dates");
            dc.setText(Date);

        } catch (Exception e) {
            System.out.println("Error: " + e);

        }
    }

    public void load() {
        try {
            Button thb = (Button) findViewById(R.id.timeHourButton);
            Button tmb = (Button) findViewById(R.id.timeMinuteButton);
            EditText an = (EditText) findViewById(R.id.alarmName);
            TextView dc = (TextView) findViewById(R.id.dateContent);

            thb.setText(TempSaveList.get(0));
            tmb.setText(TempSaveList.get(1));
            an.setText(TempSaveList.get(2));
            Hours = Integer.parseInt(TempSaveList.get(0));
            Minutes = Integer.parseInt(TempSaveList.get(1));
            Date = getIntent().getStringExtra("Dates");
            dc.setText(Date);

        } catch (Exception e) {
            System.out.println("Error: " + e);

        }
    }


}