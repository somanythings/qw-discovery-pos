package uk.gov.borderforce.qwseizure;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;
import java.util.GregorianCalendar;

import at.nineyards.anyline.modules.AnylineBaseModuleView;
import at.nineyards.anyline.modules.mrz.MrzScanView;

import static uk.gov.borderforce.qwseizure.NegativeStopActivity.shortTime;
import static uk.gov.borderforce.qwseizure.Person.NullPerson;


public class SeizedGoodsActivity extends AppCompatActivity {
    final static int SCAN_TRAVEL_DOCUMENT = 3;
    final static String TAG = "SeizedGoodsActivity";
    final static String CIGARETTES = "CIGARETTES";

    final static SeizedGoods NullSeizedGoods = new SeizedGoods("", 0, "");
    final static SeizureState NullState = new SeizureState(GregorianCalendar.getInstance().getTimeInMillis(), "", NullPerson, NullSeizedGoods, "");
    SeizureState state = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private MrzScanView mrzResultView;
    private AnylineBaseModuleView mrzScanView;
    private Button save;
    private PersonViewBindings viewBindings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "SeizedGoodsActivity.onCreate");
        state = NullState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seized_goods);
        setTitle("Seized Goods");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        save = (Button) findViewById(R.id.save);
        setSupportActionBar(toolbar);
        viewBindings = PersonViewBindings.fromParent(this);

        wireTimeText();
        wireDateText(findViewById(R.id.datePicker));
        wireOptText();
        wireDateButton(this, findViewById(R.id.datePickerButton));
        wireTimeButton(this, findViewById(R.id.timePickerButton));
        wireCancelButton(this);
        wireSaveButton(this);
        wirePersonMrtzScanButton(this);
        wireBarcodeScanButton(this);
        updateState(state);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void wirePersonMrtzScanButton(final Activity activity) {
        ImageButton scan = (ImageButton) findViewById(R.id.scan_person_id);
        final Activity outer = this;
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "should open scan");
                Intent intent = new Intent(outer, ScanTravelDocumentActivity.class);
                startActivityForResult(intent, SCAN_TRAVEL_DOCUMENT);


//                IntentIntegrator scanIntegrator = new IntentIntegrator(outer);
//                scanIntegrator.initiateScan();
            }
        });
    }

    private void wireBarcodeScanButton(final Activity activity) {
        ImageButton scan = (ImageButton) findViewById(R.id.scan_seal_id);
        final Activity outer = this;
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "should open scan");
                IntentIntegrator bla = new IntentIntegrator(outer);
                bla.initiateScan(); // `this` is the current Activity
            }
        });
    }

    private void wireSaveButton(final Activity activity) {
        Log.d(TAG, "onSave");
        save.setEnabled(!state.sealId.equals(""));
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
                MainActivity.addSeizure(state);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("seized", state.summaryText());
                Gson gson = new Gson();
                String stateAsJson = gson.toJson(state);
                Log.d(TAG, "jsonState: " + stateAsJson);
                returnIntent.putExtra("SEIZURE", stateAsJson);
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
                updateState(state.copyOnTextChange(v.getText().toString()));
                return true;
            }
        });
    }

    private void updateState(SeizureState state) {
        this.state = state;
        save.setEnabled(!state.sealId.equals(""));

        setTitle("Seized Goods - " + state.seizedGoods.what);
        Log.d(TAG, "Summary should be: " + this.state.summaryText());

        TextView tv = (TextView) (findViewById(R.id.textView));
        tv.setText(this.state.summaryText());

        EditText dp = (EditText) (findViewById(R.id.datePicker));
        dp.setText(state.formatShortDate(this));

        EditText tm = (EditText) (findViewById(R.id.timePicker));
        tm.setText(shortTime.format(this.state.currentCal().getTime()));

        EditText seal = (EditText) (findViewById(R.id.seal_id));
        seal.setText(state.sealId);

        EditText person = (EditText) (findViewById(R.id.seized_from_text));
        person.setText(state.seizedFrom.toString());

        setPersonText(viewBindings, state.seizedFrom);
    }

    private void setPersonText(PersonViewBindings viewBindings, Person seizedFrom) {
        viewBindings.dob.setText(seizedFrom.dob);
        viewBindings.documentId.setText(seizedFrom.documentNumber);
        viewBindings.surname.setText(seizedFrom.surNames);
        viewBindings.givenNames.setText(seizedFrom.givenNames);
        viewBindings.sex.setText(seizedFrom.sex);
        viewBindings.nationality.setText(seizedFrom.nationality);
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
                Calendar calendar = state.currentCal();
                TimePickerDialog dpd = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d(TAG, "onTimeSet " + hourOfDay + ":" + minute);
                        updateState(state.copyOnTimeChange(hourOfDay, minute));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                dpd.show();
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "result " + requestCode + " " + resultCode);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

//        if (requestCode == 0) {
        if (requestCode == SCAN_TRAVEL_DOCUMENT) {
            onPersonMrtzScanResult(intent);
        } else {
            if (resultCode == RESULT_OK) {
                onBarcodeScanResult(intent, result);
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
//        }
        }
    }

    private void onBarcodeScanResult(Intent intent, IntentResult result) {
        String contents1 = ScanStatic.onSealScanResult(intent, result);
        updateState(state.copyOnSealChange(contents1));
    }


    ///D/SeizedGoodsActivity: MRTZ scan {"documentType":"P","documentNumber":"LH829577","surNames":"PAINE","givenNames":"LANCE BARRY","countryCode":"NZL","issuingCountryCode":"NZL","nationalityCountryCode":"NZL","dayOfBirth":"800110","expirationDate":"191009","sex":"M","personalNumber":"","personalNumber2":"","checkDigitNumber":"2","checkDigitPersonalNumber":"0","checkDigitDayOfBirth":"6","checkDigitExpirationDate":"4","checkDigitFinal":"0","allCheckDigitsValid":true}
    private void onPersonMrtzScanResult(Intent intent) {
        String mrtzJson = intent.getStringExtra("MRTZ");
        Log.d(TAG, "MRTZ scan " + mrtzJson);

        updateState(state.copyOnPersonChange(mrtzJson));
    }

    protected void onRadioButtonClicked(View v) {
        Log.d(TAG, "radioButtonClicked " + v.getId());
        String currentType = getCurrentGoodType(v);
        setGoodsQuantityView(currentType);
        updateState(state.copyOnGoodsTypeChange(currentType));
    }

    private void setGoodsQuantityView(String currentType) {
        LinearLayout quantityLayout = clearQuantityLayout();

        switch (currentType) {
            case CIGARETTES:
                View child = initCigarettesQuantity(R.layout.cigarettes_quantity, 5);
                quantityLayout.addView(child);
                break;
        }

    }

    @NonNull
    private View initCigarettesQuantity(int id, final int increment) {
        View child = getLayoutInflater().inflate(id, null);


        TextView np = (TextView) child.findViewById(R.id.quantityPicker);
        np.setText("2000");
        np.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                updateState(state.copyOnGoodsQuantityChange(Integer.parseInt(v.getText().toString())));
                return true;
            }
        });
        return child;
    }

    @NonNull
    private LinearLayout clearQuantityLayout() {
        LinearLayout quantityLayout = (LinearLayout) findViewById(R.id.seized_quantity_layout);
        quantityLayout.removeAllViewsInLayout();
        return quantityLayout;
    }

    private String getCurrentGoodType(View v) {
        switch (v.getId()) {
            case R.id.radio_tobacco:
                return "tobacco";
            case R.id.radio_weapon:
                return "weapon";
            case R.id.radio_cigarettes:
                return CIGARETTES;
            case R.id.radio_cigars:
                return "cigars";
            case R.id.radio_class_a:
                return "drugs";
            case R.id.radio_misc:
                return "misc";
            case R.id.radio_poao:
                return "poao";
            case R.id.radio_alcohol:
                return "alcohol";
            default:
                return "";
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("SeizedGoods Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
