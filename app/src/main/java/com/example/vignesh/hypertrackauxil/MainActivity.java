package com.example.vignesh.hypertrackauxil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hypertrack.lib.HyperTrack;
import com.hypertrack.lib.HyperTrackMapFragment;
import com.hypertrack.lib.callbacks.HyperTrackCallback;
import com.hypertrack.lib.models.Action;
import com.hypertrack.lib.models.ActionParams;
import com.hypertrack.lib.models.ActionParamsBuilder;
import com.hypertrack.lib.models.ErrorResponse;
import com.hypertrack.lib.models.Place;
import com.hypertrack.lib.models.SuccessResponse;

import java.util.ArrayList;

import static android.R.attr.alertDialogIcon;
import static android.R.attr.name;
import static android.R.attr.track;

public class MainActivity extends AppCompatActivity {

    private EditText actionId;
    private ProgressDialog progressDialog;
    private Button trackBtn;

    String globalLookUpId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startAction();

        // Initialize UI Views
        initUIViews();
    }

    private void initUIViews() {
        // Initialize AssignAction Button
        Button logoutButton = (Button) findViewById(R.id.logout_btn);
        //if (logoutButton != null)
        //    logoutButton.setOnClickListener(logoutButtonClickListener);

        actionId = (EditText) findViewById(R.id.actionId);

        trackBtn = (Button) findViewById(R.id.track_btn);
        trackBtn.setOnClickListener(trackBtnListener);
    }

    private void startAction() {
        Place expectedPlace = new Place().setLocation(29.606367, -95.581224)
                .setName("Trial Action");

        /**
         * Create ActionParams object specifying the Visit Action parameters including
         * ExpectedPlace, ExpectedAt time and Action's Lookup_id.
         */
        ActionParams actionParams = new ActionParamsBuilder().setExpectedPlace(expectedPlace)
                .setType(Action.ACTION_TYPE_VISIT)
                .setLookupId("tempId")
                .build();

        /**
         * Call createAndAssignAction to assign Visit action to the current user
         * configured in the SDK using the ActionParams created above.
         */
        HyperTrack.createAndAssignAction(actionParams, new HyperTrackCallback() {
            @Override
            public void onSuccess(@NonNull SuccessResponse response) {
                // Handle createAndAssignAction success here
                Action action = (Action) response.getResponseObject();

                String lookUpId = action.getLookupId();
                Toast.makeText(MainActivity.this, lookUpId,Toast.LENGTH_SHORT).show();
                globalLookUpId = lookUpId;

            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                // Handle createAndAssignAction error here
                Toast.makeText(MainActivity.this, errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View.OnClickListener trackBtnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // Call trackAction API method with action ID for tracking.
            // Start YourMapActivity containing HyperTrackMapFragment view with the
            // customization on succes response of trackAction method

            ArrayList<String> actions = new ArrayList<>();
            actions.add(actionId.getText().toString());



            HyperTrack.trackActionByLookupId(globalLookUpId, new HyperTrackCallback() {
                @Override
                public void onSuccess(@NonNull SuccessResponse response) {

                    if (progressDialog != null) {
                        progressDialog.cancel();
                    }

                    //Start Activity containing HyperTrackMapFragment
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError(@NonNull ErrorResponse errorResponse) {
                    if (progressDialog != null) {
                        progressDialog.cancel();
                    }

                    Toast.makeText(MainActivity.this, "Error Occurred while trackActions: " +
                            errorResponse.getErrorMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }
    };

    /* Click Listener for AssignAction Button
    private View.OnClickListener logoutButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, R.string.main_logout_success_msg,
                    Toast.LENGTH_SHORT).show();

            // Stop HyperTrack SDK
            HyperTrack.stopTracking();

            // Proceed to LoginActivity for a fresh User Login
            Intent loginIntent = new Intent(MainActivity.this,
                    LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);
            finish();
        }
    };
    */
}
