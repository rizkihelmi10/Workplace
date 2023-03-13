package com.smartlab.wpslidingmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import android.content.Context;
import androidx.core.app.ActivityCompat;



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
import android.provider.Settings;

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

import org.json.JSONObject;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class QRScanner extends Activity {
    Button button;
    View view;
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    SupportMapFragment mapFragment;
    FusedLocationProviderClient locationProviderClient;
    TextView txtCoor,txtLoc;

    dbActivities dba;
    MySQLiteHelper db2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login1);

        dba = new dbActivities();
        db2 = new MySQLiteHelper(this);

        button=findViewById(R.id.btnSave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateCameraPermission();


            }

        });
        txtCoor =findViewById(R.id.edtxtCoor);
        txtLoc = findViewById(R.id.edtxtLoc);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //   mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapViewMain);
        // method to get the location
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
       // getLastLocation();
        isNetworkConnected();

    }
    private void validateCameraPermission(){

        String[] permissions={android.Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this, permissions[0])== PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT);

            scanQR();
        }else{
            ActivityCompat.requestPermissions(this, permissions ,1);
        }
       /* Intent intent = new Intent(this, frmTakePhoto.class);
        startActivity(intent);*/
    }
    public void scanQR(){
        IntentIntegrator integrator = new IntentIntegrator(QRScanner.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }
@Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {

    try {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            String sdata = "";
            String APIID;
            String sUrl = "";

            if (result != null) {
                if (result.getContents() == null) {
                    Log.d("QRScanner", "Cancelled scan");
                } else {

                    int position = 0;

                    String check = result.getContents().toString();
                    Log.i("QRScanner", "check: " + check);

                    if (check.contains("|")) {
                        position = 0;
                    } else if(check.contains("http")){
                        position = 1;
                    } else{
                        position = 2;
                    }

                    switch(position) {
                        case 0:
                            Log.d("QRScanner", "Scanned");

                            Bundle args = new Bundle();
                            Intent intent = new Intent(this, WebViewFragment2.class);

                            sdata = result.getContents();
                            Log.i("QRScanner", "sdata: " + sdata);

                            String[] separated = sdata.split("\\|");
                            args.putString("AssetInfo", result.getContents());
                            Log.i("QRScanner", "result.getContents(): " + result.getContents());

                            APIID = separated[1].trim().replace("*", "");
                            Log.i("QRScanner", "APIID: " + APIID);

                            sUrl = sendAPI(APIID);
                            if (sUrl.isEmpty()){
                                Toast.makeText(this, "API not found", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                args.putString("sURL", sUrl);
                                Log.i("QRScanner", "sURL: " + sUrl);
                                intent.putExtras(args);
                                startActivity(intent);
                            }
                            break;
                        case 1:

                            showPromptUrl(check);
                            break;

                        case 2:
                            ViewDialog alert2 = new ViewDialog();
                            alert2.showDialog(this, check);
                            break;
                        default:
                            Toast.makeText(this, "Invalid Asset Tag", Toast.LENGTH_SHORT).show();
                            break;
                    }




                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }
        }catch (Exception e){
            Log.i("error", "QRScanner Error Message:"+ e.getMessage());
        }
    }


    public void showPromptUrl(final String temp) throws Exception {
        AlertDialog.Builder builder = new AlertDialog.Builder(QRScanner.this);

        builder.setTitle("Open URL");
        builder.setMessage("QR Data : "+ temp +"\nDo you wish to open in new browser?" );

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){

                //Intent callIntent = new Intent(Intent.ACTION_DIAL);// (Intent.ACTION_CALL);
               // callIntent.setData(Uri.parse("tel:" + temp));
               // startActivity(callIntent);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(temp));
                startActivity(browserIntent);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                showDialog(temp);
                dialog.dismiss();
            }
        });
        builder.show();
    }


    public void showDialog(String temp){
        ViewDialog alert3 = new ViewDialog();
        alert3.showDialog(this, temp);
    }

    public class ViewDialog {

        public void showDialog(Activity activity, String msg){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog);

            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }


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
                //urlAddress ="http://urbancubes.voffice.my/workplace"+ "/API/API.wp";

                Log.i("uri2", urlAddress);
                Log.i("token2",token);

                URL url = new URL(urlAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                //jsonParam.put("timestamp", 1488873360);
                jsonParam.put("APIID", APIID);
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

    @SuppressLint("MissingPermission")
    private void getLastLocation() {

       // txtCoor =findViewById(R.id.edtxtCoor);
        //txtLoc = findViewById(R.id.edtxtLoc);

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
                                Geocoder geocoder = new Geocoder(QRScanner.this, Locale.getDefault());
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
    // check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }
    // check if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        //txtCoor =findViewById(R.id.edtxtCoor);
        //txtLoc =findViewById(R.id.edtxtLoc);
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
                Geocoder geocoder = new Geocoder(QRScanner.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude(),1);
                String address = addresses.get(0).getAddressLine(0);
                txtCoor.setText( mLastLocation.getLatitude() + ","+ mLastLocation.getLongitude());
                txtLoc.setText(address);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    private boolean isNetworkConnected() {

        ConnectivityManager connMgr =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
    // request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }
}