package uk.gov.borderforce.qwseizure;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    int clicked = 0;
    final static List<Seizure> seizures = new ArrayList<Seizure>();
    ListView lv;

    public static final String SEIZURES_STORE = "SEIZURES_STORE";
    public static final String NEGATIVE_STORE = "NEGATIVE_STORE";


    public static void addSeizure(Seizure s) {
        Log.d(TAG, "addSeizure " + s);
        seizures.add(s);
    }

    private void saveNegativeSeizure(String extra) {
        Gson gson = new Gson();
        NegativeStop seizure = gson.fromJson(extra, NegativeStop.class);
        saveNegativeStop(seizure, extra);
    }

    private void saveNegativeStop(NegativeStop s, String json) {
        SharedPreferences.Editor editor = getSharedPreferences(NEGATIVE_STORE, 0).edit();
        editor.putString("" + s.when.getTimeInMillis(), json);
        editor.commit();
    }

    private void saveSeizureStateFromJson(String extra) {
        Gson gson = new Gson();
        SeizureState seizure = gson.fromJson(extra, SeizureState.class);
        saveSeizure(seizure, extra);
    }


    public void saveSeizure(SeizureState s, String json) {
        SharedPreferences.Editor editor = getSharedPreferences(SEIZURES_STORE, 0).edit();
        editor.putString(s.sealId, json);
        editor.commit();
    }

    public void loadSeizures() {
        SharedPreferences editor = getSharedPreferences(SEIZURES_STORE, 0);
        Gson gson = new Gson();

        for (Map.Entry<String, ?> entry : editor.getAll().entrySet()) {
            SeizureState ss = gson.fromJson(entry.getValue().toString(), SeizureState.class);
            Log.d(TAG, "loaded " + ss.sealId + " -> " + ss.summaryText());
            addSeizure(ss);
        }
    }

    public void loadNegativeStops() {
        SharedPreferences editor = getSharedPreferences(NEGATIVE_STORE, 0);
        Gson gson = new Gson();

        for (Map.Entry<String, ?> entry : editor.getAll().entrySet()) {
            NegativeStop ss = gson.fromJson(entry.getValue().toString(), NegativeStop.class);
            Log.d(TAG, "loaded " + entry.getKey() + " -> " + ss.toString());
            addSeizure(ss);
        }
    }
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "MainActivity.onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadSeizures();
        loadNegativeStops();
        refreshSeizureListView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Negative Stop", Snackbar.LENGTH_LONG)
                        .setAction("Negative", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                recordNegativeStop();
                            }
                        }).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void refreshSeizureListView() {
        lv = (ListView) findViewById(R.id.seizure_list);
        Context mainActivity = this;
        Seizure[] sarr = seizures.toArray(new Seizure[0]);

        ArrayAdapter<Seizure> adapter = new ArrayAdapter<Seizure>(mainActivity,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, sarr);
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(TAG, "onOptions" + id);
        switch (id) {
            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                return true;
            case R.id.negative_stop:
                recordNegativeStop();
                return true;
            case R.id.seized_goods:
                recordSeizedGoods();
                lv.refreshDrawableState();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    final int NEGATIVE_ACT = 1;
    final int SEIZED_ACT = 2;

    private void recordNegativeStop() {
        Intent intent = new Intent(this, NegativeStopActivity.class);
        startActivityForResult(intent, NEGATIVE_ACT);
        Log.d(TAG, "recordNegativeDone?");
        lv.refreshDrawableState();
    }

    private void recordSeizedGoods() {
        Intent intent = new Intent(this, SeizedGoodsActivity.class);
        startActivityForResult(intent, SEIZED_ACT);
        lv.refreshDrawableState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode + " " + resultCode);
        switch (requestCode) {
            case NEGATIVE_ACT:
                if (resultCode == RESULT_OK) {
                    String extra = data.getStringExtra("NEGATIVE");
                    saveNegativeSeizure(extra);
                }
                break;
            case SEIZED_ACT:
                switch (resultCode) {
                    case RESULT_OK:
                        lv.refreshDrawableState();
                        String extra = data.getStringExtra("SEIZURE");
                        Log.d(TAG, "Seizure result: " + extra);
                        saveSeizureStateFromJson(extra);

                        refreshSeizureListView();
                        return;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
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
