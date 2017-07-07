package xyz.snsstudio.gpsalarm;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;

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
import java.util.concurrent.TimeUnit;

public class NewAlarm extends Activity {
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
    int save_LocationType;
    int save_LocationRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newalarm);

        alarmName = (EditText) findViewById(R.id.alarmName);
        timeHourButton = (Button) findViewById(R.id.timeHourButton);
        timeMinuteButton = (Button) findViewById(R.id.timeMinuteButton);
        typeButton = (Button) findViewById(R.id.typeButton);
        alarmName = (EditText) findViewById(R.id.alarmName);
        dailyButton = (Button) findViewById(R.id.dailyButton);
        dateButton = (Button) findViewById(R.id.dateButton);
        dateText = (TextView) findViewById(R.id.dateContent);
        toneButton = (Button) findViewById(R.id.toneButton);
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        locationButton = (Button) findViewById(R.id.locationButton);

        if  (getIntent().getIntExtra("Id", -1) != -1) {
            save_Id = getIntent().getIntExtra("AlarmId", -1);
            save_Name = getIntent().getStringExtra("AlarmName");
            long_DateTime = getIntent().getLongExtra("Date_Time")
            save_DateTime ;
            save_Type = getIntent().getIntExtra("AlarmType", -1);
            save_Daily;
            save_Tone = getIntent().getStringExtra("AlarmTone");
            save_Volume = getIntent().getIntExtra("AlarmVolume", -1);
            save_Latitude = getIntent().getIntExtra("LocationLatitude", -1);
            save_Longitude = getIntent().getIntExtra("LocationLongitude", -1);
            save_LocationType = getIntent().getIntExtra("Location", -1);
            save_LocationRadius = getIntent().getIntExtra("Id", -1);
        }
    }
}