package uk.gov.borderforce.qwseizure;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class NegativeStopActivity extends AppCompatActivity {
    final static SimpleDateFormat longDateTimeFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' HH:mm");
    final static SimpleDateFormat shortTime = new SimpleDateFormat("HH:mm");
    final static String TAG = "NegativeStopActivity";


    NegativeStopState stopState = null;

    public void recordNegativeStop() {
        java.util.Calendar cal = Calendar.getInstance();
//        setContentView(R.layout.negativestop);
        updateNegativeStopState(new NegativeStopState(cal, ""));
        wireTimeText();
        wireDateText(findViewById(R.id.datePicker));
        wireOptText();
        wireDateButton(this, findViewById(R.id.datePickerButton));
        wireTimeButton(this, findViewById(R.id.timePickerButton));
        wireCancelButton(this);
        wireSaveButton(this);
    }

    private void wireSaveButton(NegativeStopActivity negativeStopActivity) {
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Will save this eventually", Snackbar.LENGTH_SHORT)
                        .setAction("Negative", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(TAG, "will save");
                            }
                        }).show();

                Seizure negative = SFactory.Negative("Lance Paine", GregorianCalendar.getInstance().getTimeInMillis(), stopState.freeText);
//                MainActivity.addSeizure(negative);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("-veresult", stopState.summaryText());

                Gson gson = new Gson();
                String stopStateAsJson = gson.toJson(negative);
                returnIntent.putExtra("NEGATIVE", stopStateAsJson);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private void wireCancelButton(final NegativeStopActivity negativeStopActivity) {
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                negativeStopActivity.finish();
            }
        });
    }

    private void wireOptText() {
        EditText text = (EditText) (findViewById(R.id.optText));
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "editor changed");
                updateNegativeStopState(stopState.copyOnTextChange(v.getText()));
                return true;
            }
        });
    }

    private void wireTimeText() {
        EditText timePicker = (EditText) (findViewById(R.id.timePicker));
        timePicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "timepicker changed");
                String[] hourAndMin = v.getText().toString().split(":");
                int hour = Integer.parseInt(hourAndMin[0]);
                int min = Integer.parseInt(hourAndMin[1]);
                updateNegativeStopState(stopState.copyOnTimeChange(hour, min));
                return true;
            }
        });
    }

    private void wireDateText(View viewById) {
        EditText datePicker = (EditText) viewById;
        datePicker.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                Log.d(TAG, actionId + "datePicker changed" + v.getText());
                String[] dayMonthYear = v.getText().toString().split("/");
                int day = Integer.parseInt(dayMonthYear[0]);
                int month = Integer.parseInt(dayMonthYear[1]);
                int year = Integer.parseInt(dayMonthYear[2]);
                updateNegativeStopState(stopState.copyOnDateChange(day, month, year));
                return true;
            }
        });
    }

    private void wireDateButton(final Context context, View viewById) {
        ImageButton dateB = (ImageButton) viewById;
        dateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateChangeListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        updateNegativeStopState(stopState.copyOnDateChange(dayOfMonth, month, year));
                    }
                };
                DatePickerDialog dpd = new DatePickerDialog(context, dateChangeListener, stopState.year(), stopState.month(), stopState.dayOfMonth());
                dpd.show();
            }
        });
    }

    private void wireTimeButton(final Context context, View viewById) {
        ImageButton timeB = (ImageButton) viewById;
        timeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dpd = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        updateNegativeStopState(stopState.copyOnTimeChange(hourOfDay, minute));
                    }
                }, stopState.cal.get(Calendar.HOUR_OF_DAY), stopState.cal.get(Calendar.MINUTE), true);
                dpd.show();
            }
        });
    }

    public void updateNegativeStopState(NegativeStopState newState) {
        stopState = newState;
        TextView tv = (TextView) (findViewById(R.id.textView));
        tv.setText(stopState.summaryText());
        EditText dp = (EditText) (findViewById(R.id.datePicker));
        dp.setText(stopState.cal.get(Calendar.DAY_OF_MONTH) + "/" + stopState.cal.get(Calendar.MONTH)
                + "/" + stopState.cal.get(Calendar.YEAR));

        EditText tm = (EditText) (findViewById(R.id.timePicker));
        tm.setText(shortTime.format(stopState.cal.getTime()));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.negativestop);
        setTitle("Negative Stop");
        recordNegativeStop();
    }
}
