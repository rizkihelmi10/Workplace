package com.smartlab.wpslidingmenu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import androidx.annotation.NonNull;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.net.InetAddress;
import android.net.Network;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONObject;

public class ScanFragment extends AppCompatActivity {
    // String name = getArguments().getString("AssetInfo", "");
    Button button;


    dbActivities dba;
    Boolean retbool = false;
    TextView txtCoor,txtLoc;
    int primary;

    private FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    SupportMapFragment mapFragment;
    private FusedLocationProviderClient locationProviderClient;
    Context context;
    QRData fragment;
    public ConnectivityManager conManager = null;
    MySQLiteHelper db2;


    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login1, parent, false);
        button=view.findViewById(R.id.btnSave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNetworkConnected();

                validateCameraPermission();
            }

        });
        txtCoor =(TextView) view.findViewById(R.id.edtxtCoor);
        txtLoc = (TextView) view.findViewById(R.id.edtxtLoc);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //   mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapViewMain);
        // method to get the location
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        return view;

    }


    //--------------------------------------------------------------------------------------------------------------------------------------
    //qr

    private void validateCameraPermission(){
        String[] permissions={android.Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this, permissions[0])== PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT);
            scanQR();
        }else{
            ActivityCompat.requestPermissions(ScanFragment.this, permissions ,1);
        }
    }

    public void scanQR(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
        String sdata="";
        String APIID;
        String sUrl = "";
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                String check = result.getContents().toString();
                if (check.contains("|")) {
                    Log.d("MainActivity", "Scanned");

                    //AssetFragment fragment = new AssetFragment();
                    //sUrl = sendAPI();
                    WebViewFragment fragment = new WebViewFragment();
                    Bundle args = new Bundle();



                    sdata =  result.getContents();
                    String[] separated = sdata.split("\\|");
                    args.putString("AssetInfo", result.getContents());


                    APIID = separated[1].trim().replace("*","");
                    sUrl = sendAPI(APIID);
                    args.putString("sURL",sUrl);

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragment.setArguments(args);
                    fragmentTransaction.replace(R.id.contain_main, fragment);
                    //fragmentTransaction.commit();

                    fragmentTransaction.commitAllowingStateLoss();
                    Toast.makeText(this, "fragmentTransaction", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "invalid Asset Tag", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void ScanButton(View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null){
            if (intentResult.getContents() == null){
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this,intentResult.getContents(),Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/










    public String sendAPI(String APIID){

        String strCurrentLine = "";
        boolean bRetBol = false;
        String Svr = "";
        String token = "";
        String urlAddress="";
        boolean bFlag = false;

        String temp ="";
        final String server = dba.getConfigValue(db2, "server",
                "active='1'");
        final String uid = dba.getConfigValue(db2, "username",
                "active='1'");

        final String pwd = dba.getConfigValue(db2, "password",
                "active='1'");

        try {

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                //from db
                urlAddress = server + "/API/API.wp";

                Log.i("uri",urlAddress);
                Log.i("token",token);

                URL url = new URL(urlAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                //jsonParam.put("timestamp", 1488873360);
                jsonParam.put("APIID", "788000000.1.1");
                jsonParam.put("userid", "");
                jsonParam.put("password", "");


                Log.i("JSON", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));


                BufferedReader br = null;
                if (conn.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    while ((strCurrentLine = br.readLine()) != null) {
                        temp = strCurrentLine;
                    }
                }

                JSONObject reader = new JSONObject(temp);

                temp = reader.getString("URL");
                Log.i("MSG", temp);
                conn.disconnect();

            }

        } catch (Exception e) {
            String error = "";
            for(StackTraceElement elem: e.getStackTrace()) {
                error += elem.toString();
            }
            Log.e("Probs", error);

        }

        return temp;
    }

    //--------------------------------------------------------------------------------------------------------------------------------------
    //location
    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        txtCoor =(TextView) ScanFragment.this.findViewById(R.id.edtxtCoor);
        txtLoc = (TextView) ScanFragment.this.findViewById(R.id.edtxtLoc);

        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        final Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {

                            try {
                                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                String address = addresses.get(0).getAddressLine(0);
/*
                                mapFragment.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(GoogleMap googleMap) {
                                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                                        MarkerOptions options = new MarkerOptions().position(latLng).title("Your Location");
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                                        googleMap.addMarker(options);
                                    }
                                });*/
                                txtLoc.setText(address);
                                txtCoor.setText(location.getLatitude() + ","+location.getLongitude());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        txtCoor =(TextView)ScanFragment.this.findViewById(R.id.edtxtCoor);
        txtLoc = (TextView) ScanFragment.this.findViewById(R.id.edtxtLoc);
        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude(),1);
                String address = addresses.get(0).getAddressLine(0);
                txtCoor.setText( mLastLocation.getLatitude() + ","+ mLastLocation.getLongitude());
                txtLoc.setText(address);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    // check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    // request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(ScanFragment.this, new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isNetworkConnected() {


        ConnectivityManager connMgr =
                (ConnectivityManager) ScanFragment.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else
                    return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }


    // check if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) ScanFragment.this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //check If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

}