package uk.gov.borderforce.qwseizure;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanStatic {
    private static final String TAG = "Scan";


    public static void wireBarcodeScanButton(final Activity activity) {
        ImageButton scan = (ImageButton) activity.findViewById(R.id.scan_seal_id);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "should open scan");
                IntentIntegrator bla = new IntentIntegrator(activity);
                bla.initiateScan(); // `this` is the current Activity
            }
        });
    }

    public static String onSealScanResult(Intent intent, IntentResult result) {
        String contents = intent.getStringExtra("SCAN_RESULT");
        String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
        Log.d(TAG, "con: " + contents);
        Log.d(TAG, "form" + format);
        String contents1 = result.getContents();
        Log.d(TAG, "contents: " + contents1);
        return contents1;
    }
}
