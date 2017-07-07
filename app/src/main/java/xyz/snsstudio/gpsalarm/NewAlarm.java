package xyz.snsstudio.gpsalarm;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
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
    String ToneFullName; //FName
    String[] ToneFileNameParts; //UFFName
    int Hours = 6;
    int Minutes = 0;
    String showdate;
    boolean cancelSave = false;
    ArrayList<Date> dates = new ArrayList<>();
    ArrayList<String> DaysOfWeek = new ArrayList<>();

    EditText alarmName;
    Button timeHourButton;
    Button timeMinuteButton;
    Button typeButton;
    Button dailyButton;
    Button dateButton;
    TextView dateText;
    Button toneButton;
    SeekBar volumeBar;
    Button locationButton;

    int save_Id;
    String save_Name;
    ArrayList<Date> save_DateTime;
    int save_Type;
    ArrayList<String> save_Daily;
    String save_Tone;
    int save_Volume;
    double save_Latitude;
    double save_Longitude;
    int save_LocationRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newalarm);

        alarmName = (EditText) findViewById(R.id.alarmName);
        timeHourButton = (Button) findViewById(R.id.timeHourButton);
        timeMinuteButton = (Button) findViewById(R.id.timeMinuteButton);
        typeButton = (Button) findViewById(R.id.alarmTypeButton);
        alarmName = (EditText) findViewById(R.id.alarmName);
        dailyButton = (Button) findViewById(R.id.dailyButton);
        dateButton = (Button) findViewById(R.id.dateButton);
        dateText = (TextView) findViewById(R.id.dateContent);
        toneButton = (Button) findViewById(R.id.alarmToneButton);
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        locationButton = (Button) findViewById(R.id.locationButton);

        if (getIntent().getBooleanExtra("Repopulate", false)) {
            save_Id = Data.Id;
            save_Name = Data.Name;
            save_Type = Data.Type;
            save_DateTime = Data.DateTime;
            save_Daily = Data.Daily;
            save_Tone = Data.Tone;
            save_Volume = Data.Volume;
            save_Latitude = Data.Latitude;
            save_Longitude = Data.Longitude;
            save_LocationRadius = Data.LocationRadius;

            populateView(Data.Name, Data.DateTime, Data.Daily, Data.Tone, Data.Volume, Data.Type);
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
                try {
                    ToneFullName = getRealPathFromURI(data.getData());
                    ToneFileNameParts = ToneFullName.split("/");
                    toneButton.setText("Alarm tone: " + ToneFileNameParts[ToneFileNameParts.length - 1]);
                } catch (Exception Ex) {
                    Toast.makeText(this, "Error: " + Ex, Toast.LENGTH_SHORT).show();
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
        //TODO: Add timepicker functionality.

    }
    //endregion

    public void newAlarmCancelButton_click(View view) {
        finish();
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }

    public void newAlarmSaveButton_click(View view) throws JSONException {
        File Root = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/");

        //Create directory
        if (Root.mkdir()) {
            System.out.println("Directory created");
        } else {
            System.out.println("For some reason the directory could not be created.");
        }

        prepareData(true);
        if (!cancelSave) {
            Data.clean();
            Intent intent = new Intent(this, Main.class);
            finish();
            startActivity(intent);
        }
    }

    public void dateButton_click(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Pick day(s) to repeat");
        dialog.setContentView(R.layout.dialog_datepicker);

        dates = null;
        dates = new ArrayList<>();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);

        Calendar cal = Calendar.getInstance();
        CalendarView calendarView = (CalendarView) dialog.findViewById(R.id.calendarView);
        calendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        calendarView.setMinDate(cal.getTimeInMillis() - 1200);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String text = "";
                int i = 0;

                TextView calendarDateText = (TextView) dialog.findViewById(R.id.calendarDateText);

                //Set the date, set time vars to 0
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date date = new Date();
                date.setTime(calendar.getTimeInMillis());

                //Add date to list if it doesn't contain it already
                if (!dates.contains(date))
                    dates.add(date);
                else if (dates.contains(date))
                    dates.remove(date);

                //Translate the date from a date object to a string
                while (i < dates.size()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String dateS = formatter.format(dates.get(i));
                    text = text + dateS + ", ";
                    i++;
                }

                if (dates.size() >= 1)
                    calendarDateText.setText(text.toString().substring(0, text.length() - 2));
                else
                    calendarDateText.setText("No dates selected");
            }
        });

        //SaveButton
        Button saveButton = (Button) dialog.findViewById(R.id.calendarSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "";
                int i = 0;


                while (i < dates.size()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    String dateS = formatter.format(dates.get(i));
                    text = text + dateS + ", ";
                    DaysOfWeek = null;
                    DaysOfWeek = new ArrayList<String>();
                    i++;
                }

                dateText.setText((text.toString().substring(0, text.length() - 2)));
                dialog.dismiss();
            }
        });

        //CancelButton
        Button cancelButton = (Button) dialog.findViewById(R.id.calendarCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dates = null;
                dates = new ArrayList<Date>();
                dialog.dismiss();
            }
        });

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
                DaysOfWeek = null;
                DaysOfWeek = new ArrayList<String>();
                String text = "";
                int i = 0;

                CheckBox checkBoxSunday = (CheckBox) dialog.findViewById(R.id.checkBoxSunday);
                CheckBox checkBoxMonday = (CheckBox) dialog.findViewById(R.id.checkBoxMonday);
                CheckBox checkBoxTuesday = (CheckBox) dialog.findViewById(R.id.checkBoxTuesday);
                CheckBox checkBoxWednesday = (CheckBox) dialog.findViewById(R.id.checkBoxWednesday);
                CheckBox checkBoxThursday = (CheckBox) dialog.findViewById(R.id.checkBoxThursday);
                CheckBox checkBoxFriday = (CheckBox) dialog.findViewById(R.id.checkBoxFriday);
                CheckBox checkBoxSaturday = (CheckBox) dialog.findViewById(R.id.checkBoxSaturday);

                if (checkBoxSunday.isChecked())
                    DaysOfWeek.add("Sunday");
                if (checkBoxMonday.isChecked())
                    DaysOfWeek.add("Monday");
                if (checkBoxTuesday.isChecked())
                    DaysOfWeek.add("Tuesday");
                if (checkBoxWednesday.isChecked())
                    DaysOfWeek.add("Wednesday");
                if (checkBoxThursday.isChecked())
                    DaysOfWeek.add("Thursday");
                if (checkBoxFriday.isChecked())
                    DaysOfWeek.add("Friday");
                if (checkBoxSaturday.isChecked())
                    DaysOfWeek.add("Saturday");
                if (DaysOfWeek.size() == 0)
                    save_Daily = DaysOfWeek;


                while (i < DaysOfWeek.size()) {
                    text = text + DaysOfWeek.get(i) + ", ";
                    i++;
                }


                dates = null;
                dates = new ArrayList<Date>();

                dateText.setText((text.toString().substring(0, text.length() - 2)));
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

        final Button typeButton = (Button) this.findViewById(R.id.alarmTypeButton);
        Button vibrationButton = (Button) dialog.findViewById(R.id.vibrationTypeButton);

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);

        vibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_Type = 0;
                typeButton.setText("Vibration alarm");
                dialog.dismiss();
            }
        });

        Button soundButton = (Button) dialog.findViewById(R.id.soundTypeButton);
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_Type = 1;
                typeButton.setText("Sound alarm");
                dialog.dismiss();
            }
        });

        Button bothButton = (Button) dialog.findViewById(R.id.bothTypeButton);
        bothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_Type = 2;
                typeButton.setText("Sound and vibration alarm");
                dialog.dismiss();
            }
        });

        Button notificationButton = (Button) dialog.findViewById(R.id.notificationTypeButton);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_Type = 3;
                typeButton.setText("Notification");
                dialog.dismiss();
            }
        });

        //endregion
        dialog.show();
    }

    public void locationButton_click(View view) {
        prepareData(false);

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void prepareData(boolean saveToFile) {
        File Root = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/");
        File file = new File(Root, "Alarms.json");
        cancelSave = false;
        ArrayList<Integer> Ids = new ArrayList<>();

        //ReadFile
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            //Read the file line by line
            while ((line = br.readLine()) != null) {
                JSONObject obj = new JSONObject(line);
                Ids.add(obj.getInt("Id"));
            }
            br.close();
        } catch (Exception Ex) {
            System.out.println(Ex);
        }

        Integer Id;
        String Name;
        ArrayList<Date> DateTime;
        ArrayList<String> Daily;
        int Type;
        String Tone;
        int Volume;
        double Latitude;
        double Longitude;
        int LocationRadius;

        //Id
        int max = -1;
        if (Ids.size() != 0)
            max = Collections.max(Ids);

        if (Data.Id == null)
            Id = max + 1;
        else
            Id = Data.Id;

        //Name
        if (alarmName.getText().toString() + "" != "")
            Name = alarmName.getText().toString();
        else
            Name = "New Alarm " + (Id + 1);

        //DateTime
        DateTime = new ArrayList<>();
        if (dates.size() != 0) {
            for (int i = 0; i < dates.size(); i++) {
                Date date = dates.get(i);
                int removed = 0;

                Calendar calendar = Calendar.getInstance();
                Long currentTime = calendar.getTimeInMillis();
                calendar.setTime(date);
                Long setTime = calendar.getTimeInMillis();
                calendar.set(Calendar.HOUR_OF_DAY, Hours);
                calendar.set(Calendar.MINUTE, Minutes);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Date finaldate = new Date();
                finaldate.setTime(calendar.getTimeInMillis());

                //If the set time is smaller than the current time the alarm won't go off and should not save.
                if (setTime <= currentTime) {
                    Toast.makeText(this, "Date/Time combination can't be in the past", Toast.LENGTH_SHORT).show();
                    saveToFile = false;
                    cancelSave = true;
                    dates.remove(i);
                    removed++;
                } else {
                    cancelSave = false;
                    dates.set(i - removed, finaldate);
                }
            }


            DateTime = dates;

        } else if (dates.size() == 0 && DaysOfWeek.size() == 0) {
            long datelong;
            Calendar calendar = Calendar.getInstance();
            Long currentTime = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, Hours);
            calendar.set(Calendar.MINUTE, Minutes);
            Long setTime = calendar.getTimeInMillis();

            //Because there is only one date it is safe to change the date to the date of tomorrow.
            if (setTime <= currentTime) {
                setTime = setTime + 86400000;
            }
            datelong = setTime;

            Date dateTime = new Date(datelong);

            DateTime.add(dateTime);
        }

        //Daily
        Daily = new ArrayList<>();
        if (DaysOfWeek.size() != 0) {
            Date zero =  new Date();
            zero.setTime(0);
            long datelong;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(zero);
            calendar.set(Calendar.HOUR_OF_DAY, Hours);
            calendar.set(Calendar.MINUTE, Minutes);
            Long setTime = calendar.getTimeInMillis();

            datelong = setTime;

            Date dateTime = new Date(datelong);

            DateTime.add(dateTime);
        }

        for (int i = 0; i < DaysOfWeek.size(); i++) {
            Daily.add(DaysOfWeek.get(i));
        }

        //Tone & Type
        Tone = ToneFullName;
        if (ToneFullName == null) {
            Type = 0;
            Tone = "";
        } else {
            Type = save_Type;
        }

        //Volume
        SeekBar seek = (SeekBar) findViewById(R.id.volumeBar);
        Volume = seek.getProgress();

        //Latitude,Longitude, Location Type and Location Radius
        if (Data.Latitude != null)
            Latitude = Data.Latitude;
        else
            Latitude = 100d;

        if (Data.Latitude != null)
            Longitude = Data.Longitude;
        else
            Longitude = 200d;

        if (Data.Latitude != null)
            LocationRadius = Data.LocationRadius;
        else
            LocationRadius = 10;


        Data.Id = Id;
        Data.Name = Name;
        Data.DateTime = DateTime;
        Data.Daily = Daily;
        Data.Volume = Volume;
        Data.Tone = Tone;
        Data.Type = Type;
        Data.Latitude = Latitude;
        Data.Longitude = Longitude;
        Data.LocationRadius = LocationRadius;

        saveData(Id, Name, DateTime, Daily, Tone, Volume, Type, Latitude, Longitude, LocationRadius, saveToFile);

    }

    public void saveData(int _Id, String _Name, ArrayList<Date> _DateTime, ArrayList<String> _Daily, String _Tone, int _Volume, int _Type, double _Latitude, double _Longitude, int _LocationRadius, boolean saveToFile) {
        File Root = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/");
        File file = new File(Root, "Alarms.json");

        if (saveToFile) {
            JSONObject Json = new JSONObject();
            try {
                Json.put("Id", _Id);
                Json.put("Name", _Name);
                Json.put("Type", _Type);
                Json.put("DateTime", _DateTime);
                Json.put("Daily", _Daily);
                Json.put("Tone", _Tone);
                Json.put("Volume", _Volume);
                Json.put("Latitude", _Latitude);
                Json.put("Longitude", _Longitude);
                Json.put("LocationRadius", _LocationRadius);
            } catch (JSONException e) {
                Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
            }

            String text = "";
            //ReadFile
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                //Read the file line by line
                while ((line = br.readLine()) != null) {
                    JSONObject obj = new JSONObject(line);
                    if (obj.getInt("Id") != _Id)
                        text = text + line + "\n";
                }
                br.close();
            } catch (Exception Ex) {
                System.out.println(Ex);
            }

            text = text + Json.toString();

                    FileWriter out;
            try {
                //Write to file
                out = new FileWriter(new File(Root, "Alarms.json"));
                out.append(text);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void populateView(String _Name, ArrayList<Date> _DateTime, ArrayList<String> _Daily, String _Tone, int _Volume, int _Type) {
        showdate = "";

        //Name
        alarmName.setText(_Name);

        //Time
        Calendar cal = Calendar.getInstance();
        cal.setTime(_DateTime.get(0));

        String SHour;
        String SMinute;

        if (cal.get(Calendar.HOUR_OF_DAY) < 10)
            SHour = "0" + cal.get(Calendar.HOUR_OF_DAY);
        else
            SHour = "" + cal.get(Calendar.HOUR_OF_DAY);

        if (cal.get(Calendar.MINUTE) < 10)
            SMinute = "0" + cal.get(Calendar.MINUTE);
        else
            SMinute = "" + cal.get(Calendar.MINUTE);

        Hours = cal.get(Calendar.HOUR_OF_DAY);
        Minutes = cal.get(Calendar.MINUTE);
        timeHourButton.setText(SHour);
        timeMinuteButton.setText(SMinute);

        //Type
        if (_Type == 0) {
            typeButton.setText("Vibration alarm");
        } else if (_Type == 1) {
            typeButton.setText("Sound alarm");
        } else if (_Type == 2) {
            typeButton.setText("Sound and Vibration alarm");
        } else if (_Type == 3) {
            typeButton.setText("Notification");
        }

        //Date & Daily
        if (_Daily != null) {
            for (int i = 0; i < _Daily.size(); i++) {
                showdate = showdate + _Daily.get(i) + ", ";
                DaysOfWeek.add(_Daily.get(i));
            }
            showdate = showdate.toString().substring(0, showdate.length() - 2);
            dateText.setText(showdate);
        } else if (_DateTime != null) {
            for (int i = 0; i < _DateTime.size(); i++) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String dateS = formatter.format(_DateTime.get(i));
                showdate = showdate + dateS + ", ";
                dates.add(_DateTime.get(i));
            }
            showdate = showdate.toString().substring(0, showdate.length() - 2);
            dateText.setText(showdate);
        }


        //Tone
        if (_Tone != null) {
            ToneFullName = _Tone;
            ToneFileNameParts = ToneFullName.split("/");
            toneButton.setText("Alarm tone: " + ToneFileNameParts[ToneFileNameParts.length - 1]);
        } else {
            ToneFullName = "";
        }

        //Volume
        volumeBar.setProgress(_Volume);
    }
}
