package uk.gov.borderforce.qwseizure;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;

import static uk.gov.borderforce.qwseizure.MainActivity.SEIZURES_STORE;


public class PickupForLockup extends AppCompatActivity {

    private static final String TAG = "Pickup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_for_lockup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ScanStatic.wireBarcodeScanButton(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String barcode = ScanStatic.onSealScanResult(data, result);
        EditText barcodeEdit = (EditText) findViewById(R.id.seal_id);
        barcodeEdit.setText(barcode);
        Seizure seizure = loadSeizure(barcode);
        TextView tv = (TextView) findViewById(R.id.scanned_item_summary);
        tv.setText(seizure.toString());

        super.onActivityResult(requestCode, resultCode, data);
    }

    public Seizure loadSeizure(String sealId) {
        SharedPreferences editor = getSharedPreferences(SEIZURES_STORE, 0);
        Gson gson = new Gson();
        String seizureJson = editor.getString(sealId, "{}");

        SeizureState seizure = gson.fromJson(seizureJson, SeizureState.class);
        return seizure;
    }
}
