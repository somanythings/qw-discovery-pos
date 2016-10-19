package uk.gov.borderforce.qwseizure;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import static uk.gov.borderforce.qwseizure.NegativeStopActivity.shortTime;

public class SeizedGoodsActivity extends AppCompatActivity {
    final static String TAG = "SeizedGoodsActivity";
    final static Person NullPerson = new Person("", "", "", "");
    final static SeizedGoods NullSeizedGoods = new SeizedGoods("", "");
    final static SeizureState NullState = new SeizureState(Calendar.getInstance(), "", NullPerson, NullSeizedGoods);
    SeizureState state = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "SeizedGoodsActivity.onCreate");
        state = NullState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seized_goods);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        wireTimeText();
        wireDateText(findViewById(R.id.datePicker));
        wireOptText();
        wireDateButton(this, findViewById(R.id.datePickerButton));
        wireTimeButton(this, findViewById(R.id.timePickerButton));
        wireCancelButton(this);
        wireSaveButton(this);
        updateState(state);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void wireSaveButton(final Activity activity) {
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Will save this eventually", Snackbar.LENGTH_SHORT)
                        .setAction("Seizure", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d(TAG, "will save seizure");
                            }
                        }).show();

                //todo find out how to return state to parent activity, rather than this public static call
                MainActivity.addSeizure(SFactory.S("Lance Paine", Calendar.getInstance(), state.freeText));
                Intent returnIntent = new Intent();
                returnIntent.putExtra("-veresult", state.summaryText());

                activity.setResult(RESULT_OK, returnIntent);
                activity.finish();
            }
        });
    }

    private void wireCancelButton(final Activity negativeStopActivity) {
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
                updateState(state.copyOnTextChange(v.getText()));
                return true;
            }
        });
    }

    private void updateState(SeizureState state) {
        this.state = state;
        TextView tv = (TextView) (findViewById(R.id.textView));
        tv.setText(this.state.summaryText());
        EditText dp = (EditText) (findViewById(R.id.datePicker));
        dp.setText(this.state.cal.get(Calendar.DAY_OF_MONTH) + "/" + this.state.cal.get(Calendar.MONTH)
                + "/" + this.state.cal.get(Calendar.YEAR));

        EditText tm = (EditText) (findViewById(R.id.timePicker));
        tm.setText(shortTime.format(this.state.cal.getTime()));
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
                updateState(state.copyOnTimeChange(hour, min));
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
                updateState(state.copyOnDateChange(day, month, year));
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
                        updateState(state.copyOnDateChange(dayOfMonth, month, year));
                    }
                };
                DatePickerDialog dpd = new DatePickerDialog(context, dateChangeListener, state.year(), state.month(), state.dayOfMonth());
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
                        updateState(state.copyOnTimeChange(hourOfDay, minute));
                    }
                }, state.cal.get(Calendar.HOUR_OF_DAY), state.cal.get(Calendar.MINUTE), true);
                dpd.show();
            }
        });
    }


    protected void onRadioButtonClicked(View v) {
        Log.d(TAG, "radioButtonClicked " + v.getId());
        String currentType = getCurrentGoodType(v);
        updateState(state.copyOnGoodsChange(currentType));
    }

    private String getCurrentGoodType(View v) {
        switch (v.getId()) {
            case R.id.radio_tobacco:
                Log.d(TAG, "tobacco");
                return "tobacco";
            case R.id.radio_weapon:
                Log.d(TAG, "weapon");
                return "weapon";
            case R.id.radio_cigarettes:
                Log.d(TAG, "cigarettes");
                return "cigarettes";
            case R.id.radio_class_a:
                Log.d(TAG, "drugs");
                return "drugs";
            case R.id.radio_alcohol:
                Log.d(TAG, "alcohol");
                return "alcohol";
            default:
                return "";
        }
    }

}
