package com.avenueinfotech.basicmaps;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,
        GoogleMap.OnMyLocationButtonClickListener, SensorEventListener {

    TextView et;

    GoogleMap mGoogleMap;
    GoogleApiClient mApiClient;
    private ProgressDialog dialog;
    WifiManager wifiManager;
    Switch wifiSwitch;
    TextView wifiStatus;
    private static GPSTracker gps;
    final int REQUEST_LOCATION = 199;
    public final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};

    private AutoCompleteAdapter mAdapter;

    ImageView iv_arrow;
    TextView tv_degrees;

    private static SensorManager sensorService;
    private Sensor sensor;

    private float currentDegree = 0f;

    //    Button button;
    ConnectionDetector cd;

    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1183672799205641~6417254610");


//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
//        mapFragment.getMapAsync(this);



        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        if (googleServicesAvailable()) {
            Toast.makeText(this, "Google services present", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);
            initMap();
        } else {
            Toast.makeText(this, "Google services Absent", Toast.LENGTH_LONG).show();
        }

        iv_arrow = (ImageView)findViewById(R.id.iv_arrow);
        tv_degrees = (TextView)findViewById(R.id.tv_degrees);

        sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

        gps = new GPSTracker(this);

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_ID_MULTIPLE_PERMISSIONS);
        } else {

            if (!gps.canGetLocation()) {
                switchGPS();
            }

//            GeneralUtils.createDirectory();
        }

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setCancelable(false);

//        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

//        wifiSwitch = (Switch)findViewById(R.id.wifiswitch);
//        wifiStatus = (TextView)findViewById(R.id.wifistatus);

//        if (wifiManager.isWifiEnabled()){
////            wifiSwitch.setChecked(true);
//            wifiStatus.setText("Wifi ON");
//        } else {
////            wifiSwitch.setChecked(false);
//            wifiStatus.setText("Wifi OFF");
//        }
//        wifiSwitch = (Switch)findViewById(R.id.wifiswitch);

//        if (wifiManager.isWifiEnabled()){
//            wifiSwitch.setChecked(true);
//        } else {
//            wifiSwitch.setChecked(false);
//        }

//        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    wifiManager.setWifiEnabled(true);
//                    Toast.makeText(MainActivity.this, "Wifi may take a moment to turn ON", Toast.LENGTH_LONG).show();
//                }else {
//                    wifiManager.setWifiEnabled(false);
//                    Toast.makeText(MainActivity.this, "Wifi is switched OFF", Toast.LENGTH_LONG).show();
//                }
//            }
//        });



//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//        mGoogleApiClient.connect();

        cd = new ConnectionDetector(this);

        if (cd.isConnected()) {
            Toast.makeText(MainActivity.this, "Internet Connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Enable Internet for Search", Toast.LENGTH_LONG).show();
        }

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        et  = (EditText) findViewById(R.id.editText);

    }

    public void onButtonClick(View v){

        if(v.getId() == R.id.imageButton)
        {

            promptSpeechInput();
        }


    }

    private void promptSpeechInput() {

        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL , RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE , Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT ,  "Say Something!");

        try {
            startActivityForResult(i, 100);
        }
        catch (ActivityNotFoundException a)
        {
            Toast.makeText(MainActivity.this , "Sorry! Your device dont support voice", Toast.LENGTH_LONG).show();
        }

    }

    public void onActivityResult(int request_code, int result_code, Intent i)
    {
        super.onActivityResult(request_code, result_code, i);

        switch (request_code)
        {
            case 100: if(result_code == RESULT_OK && i != null)
            {
                ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                et.setText(result.get(0));
            }
                break;
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void switchGPS() {
        {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mApiClient != null)
            mApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sensor != null){
            sensorService.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        }else {
            Toast.makeText(MainActivity.this, "Not supported", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorService.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int degree = Math.round(sensorEvent.values[0]);

        tv_degrees.setText(Integer.toString(degree) + (char) 0x00B0);

        RotateAnimation rs = new RotateAnimation(currentDegree, -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);

        rs.setDuration(1000);
        rs.setFillAfter(true);

        iv_arrow.startAnimation(rs);
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
//        if (mApiClient != null && mApiClient.isConnected()) {
//            mAdapter.setGoogleApiClient(null);
//            mApiClient.disconnect();
//        }
        super.onStop();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        goToLocationZoom(39.25, -75.896, 15);

        if (mGoogleMap != null) {

            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder gc = new Geocoder(MainActivity.this);
                    LatLng ll = marker.getPosition();
                    double lat = ll.latitude;
                    double lng = ll.longitude;
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address add = list.get(0);
                    marker.setTitle(add.getAddressLine(1));
                    marker.showInfoWindow();


                }
            });

            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
//                    TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);


                    LatLng ll = marker.getPosition();
                    tvLocality.setText(marker.getTitle());
                    tvLat.setText("Latiude: " + ll.latitude);
                    tvLng.setText("Longitude: " + ll.longitude);
//                    tvSnippet.setText(marker.getSnippet());


                    return v;
                }
            });
        }

        mGoogleMap.setOnMyLocationButtonClickListener(this);


       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
               return;
           }
       }
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//        mGoogleApiClient.connect();
    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 18);
        mGoogleMap.moveCamera(update);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    LocationRequest mLocationRequest;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(460000);
         {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Toast.makeText(this, "Cant get current location", Toast.LENGTH_LONG).show();
        } else {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 19);
            mGoogleMap.animateCamera(update);
        }
    }

    Marker marker;

    public void geoLocate(View view) throws IOException {

//        EditText et = (EditText) findViewById(R.id.editText);
        String location = et.getText().toString();
        List<Address> list = null;

        if( !location.equals("")) {
            Geocoder gc = new Geocoder(this);
            try {
                list = gc.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < list.size(); i++) {
                Address address = list.get(0);
                String locality = address.getLocality();
                String area = address.getSubLocality();


                Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
                Toast.makeText(this, area, Toast.LENGTH_SHORT).show();

                double lat = address.getLatitude();
                double lng = address.getLongitude();
                goToLocationZoom(lat, lng, 25);

                setMarker(locality, lat, lng);
            }
        }

    }

    private void setMarker(String locality, double lat, double lng) {
        if (marker != null) {
            marker.remove();
        }

        MarkerOptions options = new MarkerOptions()
                .title(locality)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iconw))
                .position(new LatLng(lat, lng))
                .snippet("I am here");

        marker = mGoogleMap.addMarker(options);
    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
}
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.mapTypeNone:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
//                break;
//            case R.id.mapTypeNormal:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                break;
//            case R.id.mapTypeSatellite:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                break;
//            case R.id.mapTypeTerrain:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//                break;
//            case R.id.mapTypeHybrid:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                break;
//
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    LocationRequest mLocationRequest;

//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(200000);
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//        if (location == null) {
//            Toast.makeText(this, "Cant get current location", Toast.LENGTH_LONG).show();
//        } else {
//            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 19);
//            mGoogleMap.animateCamera(update);
//        }
//
//    }
//
//    Marker marker;

//    public void geoLocate(View view) throws IOException {
//
//        EditText et = (EditText) findViewById(R.id.editText);
//        String location = et.getText().toString();
//
//        Geocoder gc = new Geocoder(this);
//        List<Address> list = gc.getFromLocationName(location, 1);
//        Address address = list.get(0);
//        String locality = address.getLocality();
////        String area = address.getLocality();
//
//
//        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
//
//
//        double lat = address.getLatitude();
//        double lng = address.getLongitude();
//        goToLocationZoom(lat, lng, 25);
//
//        setMarker(locality, lat, lng);
//
//    }

//    private void setMarker(String locality, double lat, double lng) {
//        if (marker != null) {
//            marker.remove();
//        }
//
//        MarkerOptions options = new MarkerOptions()
//                .title(locality)
//                .draggable(true)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_icon))
//                .position(new LatLng(lat, lng))
//                .snippet("I am here");
//
//        marker = mGoogleMap.addMarker(options);
//    }






//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//        mGoogleApiClient.connect();
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//
//
//        //initialize location as required
////        locationRequest = new LocationRequest();
//
////        locationRequest.setInterval(MINUTE);
////        locationRequest.setFastestInterval(50000);
////        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//
////        requestLocationUpdtaes();
//
////        if (googleServicesAvailable()) {
////            Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
////            setContentView(R.layout.activity_main);
////            initMap();
////        } else {
////            Toast.makeText(this, "Enable GPS", Toast.LENGTH_LONG).show();
////        }
//
////        button = (Button)findViewById(R.id.button);

////        button.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {

////
////            }
////        });

//
//
//
//
//    private void initMap() {
//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
//        mapFragment.getMapAsync(this);
//    }
//
//    public boolean googleServicesAvailable() {
//        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
//        int isAvailable = api.isGooglePlayServicesAvailable(this);
//        if (isAvailable == ConnectionResult.SUCCESS) {
//            return true;
//        } else if (api.isUserResolvableError(isAvailable)) {
//            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
//            dialog.show();
//        } else {
//            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
//        }
//        return false;
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mGoogleMap = googleMap;
//
//        if (mGoogleMap != null) {
//
//            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//                @Override
//                public void onMarkerDragStart(Marker marker) {
//
//                }
//
//                @Override
//                public void onMarkerDrag(Marker marker) {
//
//                }
//
//                @Override
//                public void onMarkerDragEnd(Marker marker) {
//                    Geocoder gc = new Geocoder(MainActivity.this);
//                    LatLng ll = marker.getPosition();
//                    double lat = ll.latitude;
//                    double lng = ll.longitude;
//                    List<Address> list = null;
//                    try {
//                        list = gc.getFromLocation(lat, lng, 1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    Address add = list.get(0);
//                    marker.setTitle(add.getAddressLine(1));
//                    marker.showInfoWindow();
//
//
//                }
//            });
//
//            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//                @Override
//                public View getInfoWindow(Marker marker) {
//                    return null;
//                }
//
//                @Override
//                public View getInfoContents(Marker marker) {
//                    View v = getLayoutInflater().inflate(R.layout.info_window, null);
//
//                    TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
//                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
//                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
////                    TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);
//
//
//                    LatLng ll = marker.getPosition();
//                    tvLocality.setText(marker.getTitle());
//                    tvLat.setText("Latiude: " + ll.latitude);
//                    tvLng.setText("Longitude: " + ll.longitude);
////                    tvSnippet.setText(marker.getSnippet());
//
//
//                    return v;
//                }
//            });
//        }
////         goToLocation(39.008224, 74.8984527, 15);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mGoogleMap.setMyLocationEnabled(true);
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//        mGoogleApiClient.connect();
//    }
//
//    private void goToLocation(double lat, double lng) {
//        LatLng ll = new LatLng(lat, lng);
//        CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
//        mGoogleMap.moveCamera(update);
//    }
//
//    private void goToLocationZoom(double lat, double lng, float zoom) {
//        LatLng ll = new LatLng(lat, lng);
//        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 16);
//        mGoogleMap.moveCamera(update);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.mapTypeNone:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
//                break;
//            case R.id.mapTypeNormal:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                break;
//            case R.id.mapTypeSatellite:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                break;
//            case R.id.mapTypeTerrain:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//                break;
//            case R.id.mapTypeHybrid:
//                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                break;
//
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    LocationRequest mLocationRequest;
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(200000);
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
////    private void requestLocationUpdtaes() {
////        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            return;
////        }
////        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
////    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
////    @Override
////    protected void onStart() {
////        super.onStart();
////        mGoogleApiClient.connect();
////    }
//
////    @Override
////    protected void onResume() {
////        super.onResume();
////        if (mGoogleApiClient.isConnected()){
////            requestLocationUpdtaes();
////        }
////    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        if(location == null){
//            Toast.makeText(this, "Cant get current location", Toast.LENGTH_LONG).show();
//        }else {
//            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 19);
//            mGoogleMap.animateCamera(update);
//        }
//
//    }
//
//    Marker marker;
//
//    public void geoLocate(View view) throws IOException {
//
////        EditText et = (EditText) findViewById(R.id.editText);
////        String location = et.getText().toString();
////
////        Geocoder gc = new Geocoder(this);
////        List<Address> list = gc.getFromLocationName(location, 1);
////        Address address = list.get(0);
////        String locality = address.getLocality();
//////        String area = address.getLocality();
////
////
////
////        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
////
////
////        double lat = address.getLatitude();
////        double lng = address.getLongitude();
////        goToLocationZoom(lat, lng, 25);
////
////        setMarker(locality, lat, lng);
//
//    }
//
//    private void setMarker(String locality, double lat, double lng) {
//        if(marker != null){
//            marker.remove();
//        }
//
//    MarkerOptions options = new MarkerOptions()
//                            .title(locality)
//                            .draggable(true)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_icon))
//                            .position(new LatLng(lat, lng))
//                            .snippet("I am here");
//
//        marker = mGoogleMap.addMarker(options);
//    }



