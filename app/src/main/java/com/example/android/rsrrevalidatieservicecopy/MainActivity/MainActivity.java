package com.example.android.rsrrevalidatieservicecopy.MainActivity;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.android.rsrrevalidatieservicecopy.InformationAboutRsr.ActivityInfo;
import com.example.android.rsrrevalidatieservicecopy.MapFeatures.ActivityMap;
import com.example.android.rsrrevalidatieservicecopy.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        showGPSAlert(MainActivity.this);         // Checking the GPS status

        Button button = findViewById(R.id.RSR_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityMap.class);
                startActivity(intent);
            }
        });

        // If the user is navigating with a tablet, then linking the second button.
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            Button overRsrBtn = findViewById(R.id.over_btn);
            overRsrBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ActivityInfo.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConnected(MainActivity.this)) mBuilder(MainActivity.this).show();
        showGPSAlert(MainActivity.this);

        int errorCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            final DialogInterface.OnCancelListener cancelListener =
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    };
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            apiAvailability.getErrorDialog(this, errorCode, PLAY_SERVICES_RESOLUTION_REQUEST,
                    cancelListener).show();
        }
        checkPermission();
    }

    // Checking connection state
    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo
                        (ConnectivityManager.TYPE_WIFI);
                android.net.NetworkInfo mobileData = connectivityManager.getNetworkInfo
                        (ConnectivityManager.TYPE_MOBILE);
                return (mobileData != null && mobileData.isConnectedOrConnecting()) ||
                        (wifi != null && wifi.isConnectedOrConnecting());
            }
        }
        return false;
    }

    // Building an alert dialog
    public AlertDialog mBuilder(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(R.string.alert_dialog_title);
        builder.setMessage(R.string.alert_dialog_message);
        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                MainActivity.this.finish();
            }
        });
        return builder.create();
    }

    // Show Alert Dialog to enable GPS
    protected void showGPSAlert(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // Check if location services are enabled
        String locationProvider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (locationProvider == null || locationProvider.equals("")) {
            alertDialogBuilder.setMessage(R.string.gps_disabled_message)
                    .setCancelable(false).setPositiveButton(R.string.positive_btn_message,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Calling an intent to perform an activity in order to open source settings
                            Intent callGPSSettingIntent = new Intent(Settings.
                                    ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(callGPSSettingIntent);
                        }
                    });
            alertDialogBuilder.setNegativeButton(R.string.negative_button,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            MainActivity.this.finish();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    // Check if the user has already granted permission before accessing a resource.
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && //
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Explicitly request permissions from the user if the permissions have not previously been granted.
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, //
                            Manifest.permission.ACCESS_FINE_LOCATION}, 256);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            return false;
        } else {
            getMenuInflater().inflate(R.menu.menu_info, menu);
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                Intent intent = new Intent(this, ActivityInfo.class);
                startActivityForResult(intent, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

