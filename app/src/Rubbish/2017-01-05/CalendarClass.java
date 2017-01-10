package xyz.snsstudio.gpsalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sande on 2016-11-25.
 */

public class CalendarClass extends Activity {

    public String CalDay = "1";
    public String CalMonth = "1";
    public String CalYear = "1970";
    public String Date = "1-1-1970";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datepicker);


        java.util.Calendar c = java.util.Calendar.getInstance();
        CalendarView cv = (CalendarView) findViewById(R.id.calendarView);
        cv.setFirstDayOfWeek(java.util.Calendar.SUNDAY);
        cv.setMinDate(c.getTimeInMillis() - 1200);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            ArrayList<String> dates = new ArrayList<>();

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                TextView tv = (TextView) findViewById(R.id.calDateText);
                CalendarView CV = (CalendarView) findViewById(R.id.calendarView);
                CalDay = new SimpleDateFormat("dd").format(new Date(CV.getDate()));
                CalMonth = new SimpleDateFormat("MM").format(new Date(CV.getDate()));
                CalYear = new SimpleDateFormat("yyyy").format(new Date(CV.getDate()));
                Date = CalDay + "-" + CalMonth + "-" + CalYear;
                int i = 0;
                String text = "";

                if (!dates.contains(Date + ", "))
                    dates.add(Date + ", ");
                else if (dates.contains(Date + ", "))
                    dates.remove(Date + ", ");

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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, NewAlarm.class);
        finish();
        startActivity(intent);
    }


    public void cancelButton(View view) {
        Intent intent = new Intent(this, NewAlarm.class);
        intent.putExtra("List", getIntent().getStringArrayListExtra("List"));
        intent.putExtra("DateType", getIntent().getIntExtra("DateType", 0));

        finish();
        startActivity(intent);
    }

    public void saveButton(View view) {
        Intent intent = new Intent(this, NewAlarm.class);

        TextView tv = (TextView) findViewById(R.id.calDateText);
        intent.putExtra("List", getIntent().getStringArrayListExtra("List"));
        intent.putExtra("Dates", tv.getText().toString());
        intent.putExtra("DateType", 1);
        finish();
        startActivity(intent);
    }
}
