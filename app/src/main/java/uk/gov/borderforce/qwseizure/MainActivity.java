package uk.gov.borderforce.qwseizure;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static uk.gov.borderforce.qwseizure.R.layout.seizure;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";
    int clicked = 0;
    final static List<Seizure> seizures = new ArrayList<Seizure>();
    ListView lv;

    public static final String MESSAGES_CHILD = "messages";
    public static final String NEGATIVE_STOPS_CHILD = "negative_stops";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<SeizureState, MessageViewHolder>
            mFirebaseAdapter;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private final String ANONYMOUS = "anonymous";

    private String mUsername = ANONYMOUS;
    private String mPhotoUrl = "";
    private GoogleApiClient mGoogleApiClient;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public final TextView messageTextView;
        public final TextView messengerTextView;
        public final CircleImageView messengerImageView;
        public final TextView seizeFromTextView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.seizureTextView);
            seizeFromTextView = (TextView) itemView.findViewById(R.id.seizedFromText);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }

    public static final String SEIZURES_STORE = "SEIZURES_STORE";
    public static final String NEGATIVE_STORE = "NEGATIVE_STORE";


    public static void addSeizure(Seizure s) {
        Log.d(TAG, "addSeizure " + s);
        seizures.add(s);
    }

    private void saveNegativeSeizure(String extra) {
        Gson gson = new Gson();
        NegativeStop seizure = gson.fromJson(extra, NegativeStop.class);
        Log.d(TAG, "will save -ve: " + extra);
        saveNegativeStop(seizure, extra);
    }

    private void saveNegativeStop(NegativeStop s, String json) {
        SharedPreferences.Editor editor = getSharedPreferences(NEGATIVE_STORE, 0).edit();
        editor.putString("" + s.when, json);

        mFirebaseDatabaseReference.child(NEGATIVE_STOPS_CHILD)
                .push().setValue(s);

        editor.commit();
    }

    private void saveSeizureStateFromJson(String extra) {
        Gson gson = new Gson();
        SeizureState seizure = gson.fromJson(extra, SeizureState.class);
        mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                .push().setValue(seizure);

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadSeizures();
        loadNegativeStops();
//        refreshSeizureListView();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

//        if (initFirebaseAuth()) return;

        // New child entries
        initFirebaseViews();

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

    private void initFirebaseViews() {
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = configureFirebaseAdapter();

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }

    private FirebaseRecyclerAdapter configureFirebaseAdapter() {
         FirebaseRecyclerAdapter firebaseAdapter = new FirebaseRecyclerAdapter<SeizureState,
                MessageViewHolder>(
                SeizureState.class,
                R.layout.item_seizure,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              SeizureState friendlyMessage, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messageTextView.setText(friendlyMessage.summaryText());
                viewHolder.messengerTextView.setText(friendlyMessage.seizedBy);
                viewHolder.seizeFromTextView.setText(friendlyMessage.seizedFromDesc());
                Glide.with(MainActivity.this)
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

    private boolean initFirebaseAuth() {
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
        return false;
    }

//    private void refreshSeizureListView() {
//        lv = (ListView) findViewById(R.id.seizure_list);
//        Context mainActivity = this;
//        Seizure[] sarr = seizures.toArray(new Seizure[0]);
//
//        ArrayAdapter<Seizure> adapter = new ArrayAdapter<Seizure>(mainActivity,
//                android.R.layout.simple_list_item_1,
//                android.R.id.text1, sarr);
//        lv.setAdapter(adapter);
//    }

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
//                lv.refreshDrawableState();
                return true;
            case R.id.lockup_operator:
                lockupOperatorMode();
                return true;
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    final int NEGATIVE_ACT = 1;
    final int SEIZED_ACT = 2;
    final int LOCKUP_ACT = 3;

    private void recordNegativeStop() {
        Intent intent = new Intent(this, NegativeStopActivity.class);
        startActivityForResult(intent, NEGATIVE_ACT);
        Log.d(TAG, "recordNegativeDone?");
//        lv.refreshDrawableState();
    }

    private void recordSeizedGoods() {
        Intent intent = new Intent(this, SeizedGoodsActivity.class);
        startActivityForResult(intent, SEIZED_ACT);
//        lv.refreshDrawableState();
    }

    private void lockupOperatorMode() {
        Intent intent = new Intent(this, PickupForLockup.class);
        startActivityForResult(intent, LOCKUP_ACT);
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
                        String extra = data.getStringExtra("SEIZURE");
                        Log.d(TAG, "Seizure result: " + extra);
                        saveSeizureStateFromJson(extra);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
