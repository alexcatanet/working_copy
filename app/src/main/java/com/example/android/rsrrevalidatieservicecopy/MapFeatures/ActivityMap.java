package com.example.android.rsrrevalidatieservicecopy.MapFeatures;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.android.rsrrevalidatieservicecopy.R;
import com.example.android.rsrrevalidatieservicecopy.Adapter.CustomInfoWindowAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class ActivityMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.menu_arrow);
        }
        setContentView(R.layout.map_activity);

        showGPSAlert(ActivityMap.this);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final Button callingButton = findViewById(R.id.calling_btn);

        /*
          Build the popup dialog which it have 2 main functions:
          Asking the user to remember the current location
          Dialing the customer's service of RSR pechhulp.
         */
        final View.OnClickListener onClickListener = new View.OnClickListener() {
            boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.calling_btn:
                        if (tabletSize) { // Checking if this is a tablet dev.
                            dialContactPhone();
                        } else {

                            final Dialog dialog = new Dialog(ActivityMap.this);
                            dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.popup_dialog);
                            Button cancelBtn = dialog.findViewById(R.id.cancel_btn);
                            Button dialingBtn = dialog.findViewById(R.id.dialog_btn_dialing);

                            View.OnClickListener onClickListener1 = new View.OnClickListener() {
                                public void onClick(View v) {
                                    switch (v.getId()) {
                                        case R.id.cancel_btn:
                                            dialog.dismiss();
                                            break;
                                        case R.id.dialog_btn_dialing:
                                            dialContactPhone();
                                            break;
                                    }
                                }
                            };

                            cancelBtn.setOnClickListener(onClickListener1);
                            dialingBtn.setOnClickListener(onClickListener1);

                            dialog.setCanceledOnTouchOutside(false);

                            Window window = dialog.getWindow();
                            WindowManager.LayoutParams layoutParams = window.getAttributes();

                            layoutParams.gravity = Gravity.BOTTOM;
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            window.setAttributes(layoutParams);

                            dialog.show();
                            break;
                        }
                }
            }
        };
        callingButton.setOnClickListener(onClickListener);
    }

    //  Call the contact phone given
    private void dialContactPhone() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityMap.this, new String[]{Manifest
                    .permission.CALL_PHONE}, 256);
            return;
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts
                ("tel", "+31 900 7788990", null)));
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        this.mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(ActivityMap.this));

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(12000); // one second
        mLocationRequest.setFastestInterval(12000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {

                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(String.valueOf(R.string.location_title_text));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_mini));

                Geocoder geocoder = new Geocoder(getBaseContext());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String addressText = "";

                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);

                    addressText = address.getAddressLine(0);
                }
                markerOptions.snippet(addressText);

                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                mCurrLocationMarker.showInfoWindow();

                // Moving map camera
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // This thread waiting for the user's response!
                // After the user sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.require_permission_message)
                        .setMessage(R.string.require_location_permission_message)
                        .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ActivityMap.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // We can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
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
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted, now we can proceed to work!
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {

                    // Permission denied!
                    Toast.makeText(this, R.string.permission_denied_toast_message, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showGPSAlert(ActivityMap.this);
    }
}

