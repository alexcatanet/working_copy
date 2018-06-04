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
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.RSR_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityMap.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConnected(MainActivity.this)) mBuilder(MainActivity.this).show();
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

    public AlertDialog mBuilder(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                finishActivity(0);
            }
        });
        return builder.create();
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
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return super.onCreateOptionsMenu(menu);
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

