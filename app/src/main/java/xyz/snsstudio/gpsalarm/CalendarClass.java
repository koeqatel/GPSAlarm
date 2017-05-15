package xyz.snsstudio.gpsalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sande on 2016-11-25.
 */

public class CalendarClass extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_datepicker);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, NewAlarm.class);
        finish();
        startActivity(intent);
    }

    public void saveButton_click(View view) {
        Intent intent = new Intent(this, NewAlarm.class);

        TextView tv = (TextView) findViewById(R.id.calDateText);
        intent.putExtra("List", getIntent().getStringArrayListExtra("List"));
        intent.putExtra("Dates", tv.getText().toString());
        intent.putExtra("DateType", 1);
        finish();
        startActivity(intent);
    }
}
