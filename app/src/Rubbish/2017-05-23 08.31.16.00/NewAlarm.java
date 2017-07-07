package xyz.snsstudio.gpsalarm;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class NewAlarm extends Activity {
    public int Hours = 6;
    public int Minutes = 0;
    public JSONObject Json = new JSONObject();
    public ArrayList<String> TempSaveList = new ArrayList<>();
    String FName = "";
    String DaysOfWeek = "";
    Long DateInMillis;
    int AlarmType = 1; //0 = vibrate, 1 = sound, 2 = vibrate and sound, 3 = Notification




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newalarm);

        loadAfterRedirect();
        if (getIntent().getIntExtra("Id", -1) != -1) {
            Button timeHourButton = (Button) findViewById(R.id.timeHourButton);
            Button timeMinuteButton = (Button) findViewById(R.id.timeMinuteButton);
            EditText alarmName = (EditText) findViewById(R.id.alarmName);
            TextView dateContent = (TextView) findViewById(R.id.dateContent);
            SeekBar volumeBar = (SeekBar) findViewById(R.id.volumeBar);
            Button toneButton = (Button) findViewById(R.id.toneButton);

            FName = getIntent().getStringExtra("Tone");
            String[] UFFName = FName.split("/");

            timeHourButton.setText(getIntent().getStringExtra("Hour"));
            timeMinuteButton.setText(getIntent().getStringExtra("Minute"));
            alarmName.setText(getIntent().getStringExtra("Name"));
            volumeBar.setProgress(getIntent().getIntExtra("Volume", 75));
            toneButton.setText("Alarm tone: " + UFFName[UFFName.length - 1]);
//            dateContent.setText(DateInMillis);
            //TODO: Disabled shit here

            DateInMillis = getIntent().getLongExtra("Date", 0);
            AlarmType = getIntent().getIntExtra("Type", 0);

            //Only location is missing.
        } else {

            String curDayS;
            String curMonthS;
            Calendar c = Calendar.getInstance();

            int Minute = c.get(Calendar.MINUTE);
            int Hour = c.get(Calendar.HOUR_OF_DAY);

            int curDay = c.get(Calendar.DAY_OF_MONTH);
            int curMonth = c.get(Calendar.MONTH) + 1;
            int curYear = c.get(Calendar.YEAR);

            if (curDay < 10)
                curDayS = "0" + curDay;
            else
                curDayS = "" + curDay;

            if (curMonth < 10)
                curMonthS = "0" + curMonth;
            else
                curMonthS = "" + curMonth;


            String Hourstring = Integer.toString(Hour);
            String Minutestring = Integer.toString(Minute);

            if (Integer.toString(Hour).length() == 1) {
                Hourstring = "0" + Integer.toString(Hour);
            }

            if (Integer.toString(Minute).length() == 1) {
                Minutestring = "0" + Integer.toString(Minute);
            }


            Date = curDayS + "-" + curMonthS + "-" + curYear;
            final String CurrentTime = Hourstring + ":" + Minutestring;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Main.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 10) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                System.out.println(data.getData().toString());
                Button tb = (Button) findViewById(R.id.toneButton);
                try {
                    FName = getRealPathFromURI(data.getData());
                    String[] UFFName = FName.split("/");
                    tb.setText("Alarm tone: " + UFFName[UFFName.length - 1]);
                } catch (Exception Ex) {
                    Toast.makeText(this, "Error: " + Ex + "\n" + FName, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //region TimeButtons

    public void timeUpHourButton_click(View view) {
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

    public void timeDownHourButton_click(View view) {
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

    public void timeUpMinuteButton_click(View view) {
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

    public void timeDownMinuteButton_click(View view) {
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

    public void timeHourButton_click(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Alarm time");
        dialog.setContentView(R.layout.dialog_timepicker);
        //TODO: Add timepicker functionality.

    }

    public void timeMinuteButton_click(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Alarm time");
        dialog.setContentView(R.layout.dialog_timepicker);

    }
    //endregion

    public void cancelNewAlarmButton_click(View view) {
        finish();
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

    public void saveNewAlarmButton_click(View view) throws JSONException {
        //TODO check if total time in millis is bigger than current time in millies, if not show toast about time in past not being possible.

        File Root = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/");
        File file = new File(Root, "Alarms.json");

        Button thb = (Button) findViewById(R.id.timeHourButton);
        Button tmb = (Button) findViewById(R.id.timeMinuteButton);
        EditText et = (EditText) findViewById(R.id.alarmName);

        ArrayList<String> text = new ArrayList<>();
        ArrayList<JSONObject> json = new ArrayList<>();
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
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            //Read the file line by line
            while ((line = br.readLine()) != null) {
                JSONObject obj = new JSONObject(line);
                json.add(obj);
                text.add(line);
                Ids.add(obj.getInt("Id"));
            }
            br.close();
        } catch (Exception Ex) {
            System.out.println(Ex);
        }
        //endregion

        //region Create Json object
        String hourtext = thb.getText().toString();
        String minutetext = tmb.getText().toString();
        String alarmname = et.getText().toString();
        int max = -1;
        if (Ids.size() != 0)
            max = Collections.max(Ids);
        if (getIntent().getIntExtra("Id", -1) == -1)
            Json.put("Id", max + 1);
        else
            Json.put("Id", getIntent().getIntExtra("Id", -1));
        Json.put("Hour", hourtext);
        Json.put("Minute", minutetext);
        Json.put("Name", alarmname);

        //Add the date
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

        Json.put("Tone", FName);
        if (FName == null) {
            AlarmType = 0;
        }

        SeekBar seek = (SeekBar) findViewById(R.id.volumeBar);

        Json.put("Volume", seek.getProgress());
        Json.put("Type", AlarmType);
        Json.put("Latitude", getIntent().getDoubleExtra("Latitude", 0));
        Json.put("Longitude", getIntent().getDoubleExtra("Longitude", 0));
        Json.put("LocationType", getIntent().getDoubleExtra("LocationType", 0));
        Json.put("LocationRadius", getIntent().getDoubleExtra("LocationRadius", 0));
        //endregion

        //region WriteFile
        for (int i = 0; i < text.size(); i++) {
            //Combine old string with the listobject
            if (getIntent().getIntExtra("Id", -1) == json.get(i).getInt("Id")) {
            } else {
                JsonString = JsonString + text.get(i) + "\n";
            }
        }

        JsonString = JsonString + Json.toString();

        FileWriter out;
        try {
            //Write to file
            out = new FileWriter(new File(Root, "Alarms.json"));
            out.append(JsonString);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //endregion

        Intent intent = new Intent(this, Main.class);
        setResult(RESULT_OK, intent);
        finish();
        startActivity(intent);

    }

    public void dateButton_click(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Pick day(s) to repeat");
        dialog.setContentView(R.layout.dialog_datepicker);

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);


        Calendar c = Calendar.getInstance();
        CalendarView calendarView = (CalendarView) dialog.findViewById(R.id.calendarView);
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setMinDate(c.getTimeInMillis() - 1200);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            ArrayList<Long> dates = new ArrayList<>();

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                int i = 0;
                String text = "";
                TextView tv = (TextView) dialog.findViewById(R.id.calDateText);
                CalendarView calendar = (CalendarView) dialog.findViewById(R.id.calendarView);


                if (!dates.contains(calendar.getDate()))
                    dates.add(calendar.getDate());
                else if (dates.contains(calendar.getDate()))
                    dates.remove(calendar.getDate());

                while (i < dates.size()) {
                    text = text + dates.get(i);
                    i++;
                }
                if (dates.size() >= 1)
                    tv.setText(text.toString().substring(0, text.length() - 2));
                else
                    tv.setText("No dates selected");
            }
        });

        //region SaveButton
        Button saveButton = (Button) dialog.findViewById(R.id.saveCalButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) dialog.findViewById(R.id.calDateText);
                Date = tv.getText().toString();
                TextView dc = (TextView) findViewById(R.id.dateContent);
                dc.setText(Date);
                dialog.dismiss();


            }
        });
        //endregion

        //region CancelButton
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelCalButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //endregion

        dialog.show();
    }

    public void dailyButton_click(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Pick day(s) to repeat");
        dialog.setContentView(R.layout.dialog_repeatdays);

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);

        //region SaveButton
        Button saveDayButton = (Button) dialog.findViewById(R.id.saveDayButton);
        saveDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBoxSunday = (CheckBox) dialog.findViewById(R.id.checkBoxSunday);
                CheckBox checkBoxMonday = (CheckBox) dialog.findViewById(R.id.checkBoxMonday);
                CheckBox checkBoxTuesday = (CheckBox) dialog.findViewById(R.id.checkBoxTuesday);
                CheckBox checkBoxWednesday = (CheckBox) dialog.findViewById(R.id.checkBoxWednesday);
                CheckBox checkBoxThursday = (CheckBox) dialog.findViewById(R.id.checkBoxThursday);
                CheckBox checkBoxFriday = (CheckBox) dialog.findViewById(R.id.checkBoxFriday);
                CheckBox checkBoxSaturday = (CheckBox) dialog.findViewById(R.id.checkBoxSaturday);

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
                if (!DaysOfWeek.equals(""))
                    Date = DaysOfWeek.substring(0, DaysOfWeek.length() - 2);
                TextView dc = (TextView) findViewById(R.id.dateContent);
                dc.setText(Date);
                dialog.dismiss();
            }
        });
        //endregion

        //region CancelButton
        Button cancelDayButton = (Button) dialog.findViewById(R.id.cancelDayButton);
        cancelDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //endregion
        dialog.show();
    }

    public void cancelButton_click(View view) {
        setContentView(R.layout.newalarm);
    }

    public void alarmToneButton_click(View view) {
//        Toast.makeText(this, "Working on it!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 10);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = "";
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void alarmTypeButton_click(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Select the alarm type");
        dialog.setContentView(R.layout.dialog_typepicker);

        final Button typeButton = (Button) this.findViewById(R.id.typeButton);
        Button vibrationButton = (Button) dialog.findViewById(R.id.vibrationTypeButton);

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);

        vibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmType = 0;
                typeButton.setText("Vibration alarm");
                dialog.dismiss();
            }
        });

        Button soundButton = (Button) dialog.findViewById(R.id.soundTypeButton);
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmType = 1;
                typeButton.setText("Sound alarm");
                dialog.dismiss();
            }
        });

        Button bothButton = (Button) dialog.findViewById(R.id.bothTypeButton);
        bothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmType = 2;
                typeButton.setText("Sound and vibration alarm");
                dialog.dismiss();
            }
        });

        Button notificationButton = (Button) dialog.findViewById(R.id.notificationTypeButton);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmType = 3;
                dialog.dismiss();
            }
        });

        //endregion
        dialog.show();
    }

    public void locationButton_click(View view) {
        Button thb = (Button) findViewById(R.id.timeHourButton);
        Button tmb = (Button) findViewById(R.id.timeMinuteButton);
        EditText et = (EditText) findViewById(R.id.alarmName);

        ArrayList<Integer> Ids = new ArrayList<>();

        Intent intent = new Intent(this, MapsActivity.class);
        int id = -1;
        String hourtext = thb.getText().toString();
        String minutetext = tmb.getText().toString();
        String alarmname = et.getText().toString();


        int max = -1;
        if (Ids.size() != 0)
            max = Collections.max(Ids);
        if (getIntent().getIntExtra("Id", -1) == -1) {
            max = max + 1;
            id = max;
        } else
            id = getIntent().getIntExtra("Id", -1);

        String hour = hourtext;
        String minute = minutetext;
        String name = alarmname;
        String date;

        if (Date != null && !Date.equals("No dates selected"))
            date = Date;
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
                date = CurrentDate;
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

                date = TomorrahDate;
            }
        }


        String tone = FName;
        if (FName == null) {
            AlarmType = 0;
        }
        SeekBar seek = (SeekBar) findViewById(R.id.volumeBar);
        int volume = seek.getProgress();

        int type = AlarmType;
        double Latitude = getIntent().getDoubleExtra("Latitude", 0);
        double Longitude = getIntent().getDoubleExtra("Longitude", 0);
        double LocType = getIntent().getDoubleExtra("LocationType", 0);
        double LocRad = getIntent().getDoubleExtra("LocationRadius", 0);


        intent.putExtra("Id", id);
        intent.putExtra("Hour", hour);
        intent.putExtra("Minute", minute);
        intent.putExtra("Name", name);
        intent.putExtra("Date", date);
        intent.putExtra("Tone", tone);
        intent.putExtra("Volume", volume);
        intent.putExtra("Type", type);
        intent.putExtra("Latitude", Latitude);
        intent.putExtra("Longitude", Longitude);
        intent.putExtra("LocationType", LocType);
        intent.putExtra("LocationRadius", LocRad);
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
            System.out.println("Error: " + e + ", the NullPointerException is normal on startup. ");

        }
    }
}