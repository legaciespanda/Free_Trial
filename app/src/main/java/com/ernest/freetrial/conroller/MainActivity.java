package com.ernest.freetrial.conroller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ernest.freetrial.R;
import com.ernest.freetrial.viewmodel.MainActivityViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import io.trialy.library.Trialy;
import io.trialy.library.TrialyCallback;

import static io.trialy.library.Constants.STATUS_TRIAL_JUST_ENDED;
import static io.trialy.library.Constants.STATUS_TRIAL_JUST_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_NOT_YET_STARTED;
import static io.trialy.library.Constants.STATUS_TRIAL_OVER;
import static io.trialy.library.Constants.STATUS_TRIAL_RESET;
import static io.trialy.library.Constants.STATUS_TRIAL_RUNNING;

public class MainActivity extends AppCompatActivity {

    private Context mContext = this;
    //An instance of the library
    Trialy mTrialy;
    //An instance of the MainActivityViewModel
    private MainActivityViewModel mViewModel;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /**
         * initialize ViewModel
         */
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        /**
         * Initialize the library and check the current trial status on every launch
         * */
        mTrialy = new Trialy(mContext, mViewModel.getTrialyAppKey());
        mTrialy.checkTrial(mViewModel.getTrialySku(), mTrialyCallback);

        /**
         * When the user purchases the IAP, record the conversion from trial
         * to paid user (optional, just for analytics purposes)
         * */
        mTrialy.recordConversion(mViewModel.getTrialySku(), mTrialyCallback);

        /**
         *  start a trial once user launches the application for the first time
         * */
        mTrialy.startTrial(mViewModel.getTrialySku(), mTrialyCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTrialy.startTrial(mViewModel.getTrialySku(), mTrialyCallback);
    }

    private TrialyCallback mTrialyCallback = new TrialyCallback() {
        @Override
        public void onResult(int status, long timeRemaining, String sku) {
            Log.i(TAG, "onResult status: " + status + "; time remaining: " + timeRemaining);

            switch (status){
                case STATUS_TRIAL_JUST_STARTED:
                    //The trial has just started - enable the premium features for the user
                    //TODO: Activate the premium features for the user (depends on your app)

                    updateTimeRemainingLabel(timeRemaining);
                    //Optional: Show an informational dialog
                    int daysRemaining = Math.round(timeRemaining / (60 * 60 * 24));
                    showDialog("Trial started", String.format(Locale.ENGLISH, "You can now try the premium features for %d days",  daysRemaining), "OK");
                    break;

                case STATUS_TRIAL_RUNNING:
                    //The trial is currently running
                    //TODO: Enable the premium features for the user (depends on your app)
                    updateTimeRemainingLabel(timeRemaining);
                    break;

                case STATUS_TRIAL_JUST_ENDED:
                    //The trial has just ended - block access to the premium features (if the user hasn't paid for them in the meantime)
                    //TODO: Deactivate the premium features for the user (depends on your app)

                    //Hide the "Time remaining"-label
                    updateTimeRemainingLabel(-1);
                    break;

                case STATUS_TRIAL_NOT_YET_STARTED:
                    //The user hasn't requested a trial yet - no need to do anything
                    break;
                case STATUS_TRIAL_OVER:
                    //The trial is over - show subscription dialog on activity launch
                    break;

                default:
                    Log.e(TAG, "Trialy response: " + Trialy.getStatusMessage(status));
                    break;
            }
            Snackbar.make(findViewById(android.R.id.content), "onCheckResult: " + Trialy.getStatusMessage(status), Snackbar.LENGTH_LONG)
                    .setAction("OK", null).show();
        }
    };

    private void showDialog(String title, String message, String buttonLabel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(buttonLabel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
    }

    private void updateTimeRemainingLabel(long timeRemaining){
        if(timeRemaining == -1){
            //Hide the llTimeRemaining-LinearLayout
            LinearLayout llTimeRemaining = (LinearLayout)findViewById(R.id.linearLayout);
            llTimeRemaining.setVisibility(View.GONE);
            return;
        }
        //Convert the "timeRemaining"-value (in seconds) to days
        int daysRemaining = (int) timeRemaining / (60 * 60 * 24);
        //Update the tvTimeRemaining-TextView
        TextView tvTimeRemaining = (TextView)findViewById(R.id.tvTimeRemaining);
        tvTimeRemaining.setText(String.format(Locale.ENGLISH, "Your trial ends in %d days",  daysRemaining));
        //Show the llTimeRemaining-LinearLayout
        LinearLayout llTimeRemaining = (LinearLayout)findViewById(R.id.linearLayout);
        llTimeRemaining.setVisibility(View.VISIBLE);
    }


}
