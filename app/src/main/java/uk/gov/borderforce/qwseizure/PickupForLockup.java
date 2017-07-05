package uk.gov.borderforce.qwseizure;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;

import static uk.gov.borderforce.qwseizure.MainActivity.MESSAGES_CHILD;
import static uk.gov.borderforce.qwseizure.MainActivity.SEIZURES_STORE;


public class PickupForLockup extends AppCompatActivity {

    private static final String TAG = "Pickup";

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<SeizureState, MainActivity.MessageViewHolder>
            mFirebaseAdapter;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_for_lockup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ScanStatic.wireBarcodeScanButton(this);
        initFirebaseViews("");
    }


    private void initFirebaseViews(String sealId) {
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = configureFirebaseAdapter(sealId);

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }

    private FirebaseRecyclerAdapter configureFirebaseAdapter(String sealId) {
        FirebaseRecyclerAdapter firebaseAdapter = new FirebaseRecyclerAdapter<SeizureState,
                MainActivity.MessageViewHolder>(
                SeizureState.class,
                R.layout.item_seizure,
                MainActivity.MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).orderByChild("sealId").startAt(sealId).endAt(sealId)) {

            @Override
            protected void populateViewHolder(MainActivity.MessageViewHolder viewHolder,
                                              SeizureState friendlyMessage, int position) {
                Log.d(TAG, "populating: " + friendlyMessage);
                viewHolder.messageTextView.setText(friendlyMessage.summaryText());
                viewHolder.messengerTextView.setText(friendlyMessage.seizedBy);
                viewHolder.seizeFromTextView.setText(friendlyMessage.seizedFrom.shortDescription());
                Glide.with(PickupForLockup.this)
                        .load("https://thumbs.dreamstime.com/x/cigarette-pack-13800698.jpg")//friendlyMessage.getPhotoUrl())
                        .into(viewHolder.messengerImageView);
            }
        };

        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        return firebaseAdapter;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String barcode = ScanStatic.onSealScanResult(data, result);
        EditText barcodeEdit = (EditText) findViewById(R.id.seal_id);
        barcodeEdit.setText(barcode);
        Seizure seizure = loadSeizure(barcode);
//        TextView tv = (TextView) findViewById(R.id.scanned_item_summary);
//        tv.setText(seizure.toString());

        super.onActivityResult(requestCode, resultCode, data);
    }

    public Seizure loadSeizure(String sealId) {
        SharedPreferences editor = getSharedPreferences(SEIZURES_STORE, 0);
        Gson gson = new Gson();
        String seizureJson = editor.getString(sealId, "{}");
        Log.d(TAG, "loadSeizure:" + sealId);
        initFirebaseViews(sealId);

        SeizureState seizure = gson.fromJson(seizureJson, SeizureState.class);
        return seizure;
    }
}
