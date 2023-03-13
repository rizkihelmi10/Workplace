package com.smartlab.wpslidingmenu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.integration.android.IntentIntegrator;
import com.opentok.android.Publisher;
import com.opentok.android.Session;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import me.aflak.libraries.callback.FingerprintDialogCallback;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,FingerprintDialogCallback {

    private ProgressBar progBar;
    private int count = 1;
    private Context context;

    public boolean isUniversalUpload = false;
    public String uurl;

    Uri targetURI;
    ImageView imageView;
    private String m_Text = "";
    String imei;

    //------------ upload
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;

    private PermissionRequest myRequest;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_VIDEO = 2;
    private static final int ACTION_MANAGE_OVERLAY_PERMISSION = 111;


    private boolean bUploadFromChrome = false;
    //------------ end upload

    //--- biometric --
    Executor executor;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    //--- end bio

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private String ver = "";
    private WebView webView;
    final private int VISIBILITY = 1;
    final private int iVISIBILITY = 0;


    MySQLiteHelper dbConfigHelper;
    dbActivities dba;
    String webUrl;

    int primary;
    String reqid;
    String gpse;
    String imgPath;

    int heldesk_f = 0;
    ProgressDialog progressBar;

    private int progressBarStatus = 0; // setting progress bar to zero
    private Handler progressBarHandler = new Handler(); // object for handlemyWebClientr class
    private long progress = 0;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    final int SELECT_PHOTO = 1;

    private static final int CAMERA_PIC_REQUEST = 1337;
    private static final int SELECT_FILE = 1;
    private static final int SELECT_DOC = 1;
    String sect = null;

    private int STORAGE_PERMISSION_CODE = 23;
    private boolean isConnected = true;

    String errDesc = "";

    SwipeRefreshLayout mySwipeRefreshLayout;

    //-- screen sharing start
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_CODE = 124;

    private Session session;
    private Publisher publisher;

    private RelativeLayout publisherViewContainer;
    private WebView webViewContainer;
    private String fChat;

    boolean mProcessed;


    @Override
  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

   /*protected void onCreate(Bundle state) {
        super.onCreate(state);*/

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        GpsTracker gt = new GpsTracker(getApplicationContext());

        dba = new dbActivities();
        dbConfigHelper = new MySQLiteHelper(this);
        String token = FirebaseInstanceId.getInstance().getToken();

        showToast(token);
        Log.i("sytoken",token);


        checkConfigDB();
        askpermission();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setBackgroundColor(getResources().getColor(R.color.whiteTextColor));

        ImageView btnExit = findViewById(R.id.btnExit);

        btnExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                AlertDialogLogout();

            }
        });
    }

    public int renew_Session() {

        String strCurrentLine = "";
        int responseCode = 0;
        String Svr = "";
        String sUid = "";
        String token = "";
        String urlAddress = "";
        boolean bFlag = false;

     String chksum;

     //--- get all data from DB
        final String server = dba.getConfigValue(dbConfigHelper, "server",
                "active='1'");
        final String uid = dba.getConfigValue(dbConfigHelper, "username",
                "active='1'");

        final String pwd = dba.getConfigValue(dbConfigHelper, "password",
                "active='1'");

        final String locName = dba.getConfigValue(dbConfigHelper, "loca",
                "active='1'");

        final String sToken = dba.getConfigValue(dbConfigHelper, "token",
                "active='1'");

        if(sToken.length() > 0){
            try {

                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    urlAddress = server + "/websvc/userauth_new.wp";
                    token = FirebaseInstanceId.getInstance().getToken();

                    Log.i("uri renew sessID", urlAddress);
                    Log.i("renew token", token);

                    URL url = new URL(urlAddress);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    token = Decryption(sToken);
                    Log.i("renew Raw Data : ", token);
                    chksum = DES.SHA1(token);


                    JSONObject jsonParam = new JSONObject();

                    jsonParam.put("JWT", sToken);
                    jsonParam.put("CS", chksum);

                    Log.i("JWT renew session", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    String temp = "";

                    BufferedReader br = null;
                    if (conn.getResponseCode() == 200) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((strCurrentLine = br.readLine()) != null) {
                            temp = strCurrentLine;
                        }
                    }

                    Log.i("JWT Session RESPONSE", temp);
                    conn.disconnect();
                    //JSONObject jObject = new JSONObject(temp);
                    // String aJsonString = jObject.getString("JWT");
                    // String decData = Decryption(aJsonString);

                    //Log.i("JWT Decypt", aJsonString);
                    // Log.i("JWT RAw",decData);

                    if (temp.contains("1")) {
                        responseCode = 1;
                        Log.i("bol", temp);
                    }
                    else if (temp.contains("3")){
                        responseCode = 3;
                        //invalid password & username
                        callConfig();

                    }
                    else if (temp.contains("4")){
                        responseCode = 4;
                        callConfig();

                        //invalid password & username

                    }
                }

            } catch (Exception e) {
                String error = "";
                for (StackTraceElement elem : e.getStackTrace()) {
                    error += elem.toString();
                }
                Log.e("Probs", error);

            }
        }



        return responseCode;
    }

    public String Decryption(String strNormalText){

        String normalTextEnc="";
        try {

            CryptLib _crypt = new CryptLib();
            String output= "";
            String key = CryptLib.SHA256("888.8", 32); //32 bytes = 256 bit
            String iv = "lobster997";
            //String iv = CryptLib.generateRandomIV(16); //16 bytes = 128 bit
            normalTextEnc = _crypt.decrypt(strNormalText, key,iv); //decrypt
            Log.i("Workplace decrypted token",output);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Workplace token", e.getMessage());
        }
        return normalTextEnc;
    }

    public boolean sendJSON() {

        String strCurrentLine = "";
        boolean bRetBol = false;
        String Svr = "";
        String sUid = "";
        String token = "";
        String urlAddress = "";
        boolean bFlag = false;

        TextView errMsg = (TextView) findViewById(R.id.txtError);

        try {

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                urlAddress = "https://ge.smartlab.com/workplace/websvc/userauth_new.wp";
                token = FirebaseInstanceId.getInstance().getToken();

                Log.i("uri", urlAddress);
                Log.i("token", token);

                URL url = new URL(urlAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                sUid = "7YuJ7YbBUTW/oGGZ5kW7paZ0rY2MVmJaUYBXRqJDkxOSL9kl8A/fUSxlnkyqzBD78X7Nxxva8HEH7tzI+1Duvg==";
                jsonParam.put("JWT", sUid);


                Log.i("JSON", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                String temp = "";

                BufferedReader br = null;
                if (conn.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((strCurrentLine = br.readLine()) != null) {
                        temp = strCurrentLine;
                    }
                }

                Log.i("JSON RESPONSE", temp);
                conn.disconnect();

                if (temp.contains("1")) {
                    bRetBol = true;
                    Log.i("bol", temp);
                }
            }

        } catch (Exception e) {
            String error = "";
            for (StackTraceElement elem : e.getStackTrace()) {
                error += elem.toString();
            }
            Log.e("Probs", error);
        }

        return bRetBol;
    }

    public String encryption(String strNormalText) {

        String normalTextEnc = "";
        try {

            CryptLib _crypt = new CryptLib();
            String output = "";
            String key = CryptLib.SHA256("888.8", 32); //32 bytes = 256 bit
            String iv = "lobster997";
            //String iv = CryptLib.generateRandomIV(16); //16 bytes = 128 bit
            output = _crypt.encrypt(strNormalText, key, iv); //encrypt
            Log.i("Workplace token", output);
            normalTextEnc = output;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Workplace token", e.getMessage());
        }
        return normalTextEnc;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    private void checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("You need to enable your GPS location");
            dialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //this will navigate user to the device location settings screen
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            AlertDialog alert = dialog.create();
            alert.show();
        }
    }

   @Override
    public void onBackPressed() {
        bUploadFromChrome = false;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }



    }

    //---------------- resend token

    public void Timer_Delay(int millisec) {
        TimerTask tskSaveToDb;
        Timer tmr = new Timer();

        tskSaveToDb = new TimerTask() {
            public void run() {
                delay();
            }
        };

        tmr.schedule(tskSaveToDb, millisec);
    }

    public void delay() {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                i = i + 0;
                j = j + 0;
            }
        }
    }


    public boolean AuthUser(int primary, String Uid, String Pwd, String Svr, boolean active) {
        Timer_Delay(5000);

        String sHttp = "";

        boolean bFlag = false;

        String token = FirebaseInstanceId.getInstance().getToken();
//Log.i("fb",token);

        String usr = Uid;
        String pass = Pwd;
        String url = Svr + getResources().getString(R.string.user_auth);


        String deviceName = android.os.Build.MODEL;
        String deviceMan = android.os.Build.MANUFACTURER;
        String devID = "";


        //upload data to server
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("username", usr));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("password", pass));
        nameValuePairs.add((NameValuePair) new BasicNameValuePair("token", token));

        //showToast(token);
        bFlag = post(url, nameValuePairs, usr);

        return bFlag;
    }


    public boolean post(String url, List<NameValuePair> nameValuePairs, String UserID) {

        boolean bFlag = false;

        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        try {

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (int index = 0; index < nameValuePairs.size(); index++) {
                if (nameValuePairs.get(index).getValue() != null) {
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                    Log.i(nameValuePairs.get(index).getName(), nameValuePairs.get(index).getValue());
                }
            }

            httpPost.setEntity(entity);
            Log.i("getRequestLine()", httpPost.getRequestLine().toString());

            HttpResponse response = httpClient.execute(httpPost, localContext);

            HttpEntity resEntity = response.getEntity();

            Log.i("getStatusLine()", response.getStatusLine().toString());

            if (resEntity != null) {
                String res = EntityUtils.toString(resEntity);
                Log.i("post : res", res);
                bFlag = xmlParse(res, UserID);

            }

            if (resEntity != null)
                resEntity.consumeContent();

            httpClient.getConnectionManager().shutdown();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("post() ~ Exception", e.getClass().getName() + " : " + e.getMessage());
	            /*Toast.makeText(
	                    getApplicationContext(),
	                    "Exception ~ post() :\n" + e.getClass().getName() + " \n" + e.getMessage(),
	                    Toast.LENGTH_LONG).show();*/

            //  Toast.makeText(getApplicationContext(), "Failure connecting to '" + e.getMessage() + "'. \nPlease check your internet connection.", Toast.LENGTH_LONG).show();

            bFlag = false;
        }

        return bFlag;
    }

    public boolean xmlParse(String result, String UserID) {
        boolean bFlag = false;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(result));

            int eventType = xpp.getEventType();

            String uid = "";
            String info = "";
            String id = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.i("Start document", "start");
                } else if (eventType == XmlPullParser.START_TAG) {
                    Log.i("Start tag ", xpp.getName());
                    if (xpp.getName().equals("MSG")) {
                        id = xpp.getName();
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.i("End tag ", xpp.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    if (id.equals("MSG")) {
                        info = xpp.getText();
                    } else
                        uid = xpp.getText();
                }

                eventType = xpp.next();
            }
            Log.i("End document", "end");
            if (uid != "" && uid != null) {
                TextView errMsg = (TextView) findViewById(R.id.txtError);
                if (uid.equals(UserID)) {
                    bFlag = true;
                } else {
                    bFlag = false;
                }
            } else {
                bFlag = false;
                showToast("Error Response : \nUser authentication failed.");
                Log.e("xmlError authentication", info);
            }
        } catch (Exception e1) {
            Log.e("xmlParse() ~ Exception", e1.getClass().getName() + " : " + e1.getMessage(), e1);

          /*  Toast.makeText(
                    getApplicationContext(),
                    "Exception ~ xmlParse() :\n" + e1.getClass().getName() + " \n" + e1.getMessage(),
                    Toast.LENGTH_LONG).show();*/

            bFlag = false;
        }

        return bFlag;

    }

    //--------------------------------- end resend


    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... imageurls) {

            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(imageurls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                InputStream in = httpURLConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    //image end
    public void callclassExecute(String clss, String sfromMain) {
        Log.i("callclassExecute", "execute");


        if (clss.equals("Config")) {
            Intent intent = new Intent(this, Config.class);
            intent.putExtra("sfromMain", sfromMain);
            startActivity(intent);

            if (sfromMain.contains("yes"))
                finish();
        } else if (clss.equals("ConfigList")) {
            Intent intent = new Intent(this, serverlist.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void RenewUrl(String renUrl) {
        isConnected = checkInternetConnection(this);

        if (isConnected == true) {

            WebView myWebView = (WebView) findViewById(R.id.webview1);
            myWebView.setBackgroundColor(Color.TRANSPARENT);
            myWebView.getSettings().setJavaScriptEnabled(true);

            myWebView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
            myWebView.loadUrl(renUrl);

            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
            progressBar1.setMax(100);
            myWebView.setWebViewClient(new WebViewClient());
            myWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100) {
                        progressBar1.setVisibility(View.INVISIBLE);

                    } else {
                        progressBar1.setVisibility(View.VISIBLE);

                        progressBar1.setProgress(newProgress);
                    }
                }
            });

        } else {
            noInternet();
        }

    }

    public void callRefresh() {
        isConnected = checkInternetConnection(this);

        if (isConnected == true) {

            String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
            String usr = config[1];
            String server = config[3];

            String domain = "";
            domain = server.replace("/workplace", "");
            WebView myWebView = (WebView) findViewById(R.id.webview1);
            myWebView.setBackgroundColor(Color.TRANSPARENT);
            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
            myWebView.loadUrl(domain + "/workplace/dashboard/dashboard_V1.asp?android=yes&memberid=" + usr);
            webUrl = domain + "/workplace/dashboard/dashboard_V1.asp?android=yes&memberid=" + usr;
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
            progressBar1.setMax(100);
            myWebView.setWebViewClient(new WebViewClient());
           myWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100) {
                        progressBar1.setVisibility(View.INVISIBLE);

                    } else {
                        progressBar1.setVisibility(View.VISIBLE);

                        progressBar1.setProgress(newProgress);
                    }
                }
            });

        } else {
            noInternet();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
        String usr = config[1];
        String server = config[3];
        int id = item.getItemId();
        isConnected = checkInternetConnection(this);

/*        if (id == R.id.geofence) {
            //mySwipeRefreshLayout.setRefreshing(false);
            callGeoFence();
        }*/


        if (id == R.id.config) {
            //mySwipeRefreshLayout.setRefreshing(false);
            updatedomain();
        }

        if (id == R.id.chat) {

            nativePermissionCheck();

            String domain = "";
            String urltemp;
            heldesk_f = 0;
            WebView myWebView = (WebView) findViewById(R.id.webview1);
            myWebView.getSettings().setJavaScriptEnabled(true);
            String fbtoken = FirebaseInstanceId.getInstance().getToken();
            domain = server.replace("/workplace", "");
            myWebView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
            urltemp = "https://chat.voffice.my/app_chat/signon.php?uname=" + usr + "&email=" + usr + "@smartlab.com&coid=ge.smartlab.com&system=workplace&dp=&android_token=" + fbtoken;
            myWebView.loadUrl(urltemp);
            Log.i("chaturl",urltemp);
            webUrl =urltemp;
            WebSettings webSettings = myWebView.getSettings();

            myWebView.getSettings().setDomStorageEnabled(true);
            myWebView.getSettings().setDatabaseEnabled(true);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                myWebView.getSettings().setDatabasePath("/data/data/" + webView.getContext().getPackageName() + "/databases/");
            }
            webSettings.setJavaScriptEnabled(true);
            myWebView.setWebViewClient(new WebViewClient());
            myWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE))
                { myWebView.setWebContentsDebuggingEnabled(true); }
            }
            ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
            progressBar1.setMax(100);
            myWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100) {
                        progressBar1.setVisibility(View.INVISIBLE);

                    } else {
                        progressBar1.setVisibility(View.VISIBLE);

                        progressBar1.setProgress(newProgress);
                    }
                }
            });

            myWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onPermissionRequest(final PermissionRequest request) {
                    myRequest = request;
                    String[] resources = request.getResources();
                    switch (resources[0]){
                        case PermissionRequest.RESOURCE_AUDIO_CAPTURE:
                            askForPermission(request.getOrigin().toString(), Manifest.permission.RECORD_AUDIO, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                            break;
                        case PermissionRequest.RESOURCE_VIDEO_CAPTURE:
                            askForPermission(request.getOrigin().toString(), Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_RECORD_VIDEO);
                            break;
                    }
                }
            });

        }


        if (id == R.id.nav_newsoffline) {

            //mySwipeRefreshLayout.setRefreshing(false);
            callNewsOffline();
        } else if (id == R.id.nav_viewnewsoffline) {

            // mySwipeRefreshLayout.setRefreshing(false);
            callViewNewsOffline();
        }


        if (isConnected == true) {

            if (id == R.id.nav_dashboard) {

                String domain = "";
                heldesk_f = 0;
                WebView myWebView = (WebView) findViewById(R.id.webview1);
                myWebView.getSettings().setJavaScriptEnabled(true);

                domain = server.replace("/workplace", "");
                myWebView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");

                myWebView.loadUrl(domain + "/workplace/dashboard/dashboard_V1.asp?android=yes&memberid=" + usr);
webUrl = domain + "/workplace/dashboard/dashboard_V1.asp?android=yes&memberid=" + usr;

                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                myWebView.setWebViewClient(new WebViewClient());

                ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
                progressBar1.setMax(100);
               myWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (newProgress == 100) {
                            progressBar1.setVisibility(View.INVISIBLE);

                        } else {
                            progressBar1.setVisibility(View.VISIBLE);

                            progressBar1.setProgress(newProgress);
                        }
                    }
                });

                bUploadFromChrome = false;

            } else if (id == R.id.nav_camera) {

                String domain = "";
                heldesk_f = 0;
                WebView myWebView = (WebView) findViewById(R.id.webview1);
                myWebView.getSettings().setJavaScriptEnabled(true);

                domain = server.replace("/workplace", "");
                myWebView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
                //myWebView.loadUrl(domain + "/workplace/dashboard/wan_main3Col.asp?android=yes&memberid=" + usr);
                myWebView.loadUrl(domain + "/workplace/dashboard/news_V1.asp?android=yes&memberid=" + usr);
                webUrl = domain + "/workplace/dashboard/news_V1.asp?android=yes&memberid=" + usr;
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                myWebView.setWebViewClient(new WebViewClient());

                ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
                progressBar1.setMax(100);
                myWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (newProgress == 100) {
                            progressBar1.setVisibility(View.INVISIBLE);

                        } else {
                            progressBar1.setVisibility(View.VISIBLE);

                            progressBar1.setProgress(newProgress);
                        }
                    }
                });

                bUploadFromChrome = false;

            } else if (id == R.id.nav_gallery) {

                heldesk_f = 0;
//                mySwipeRefreshLayout.setRefreshing(false);
                WebView myWebView = (WebView) findViewById(R.id.webview1);
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.loadUrl(server + "/blog/website/main/AndroidIndex.wp?app=500&usr=" + usr);
                webUrl = server + "/blog/website/main/AndroidIndex.wp?app=500&usr=" + usr;
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                myWebView.setWebViewClient(new WebViewClient());

                ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
                progressBar1.setMax(100);
                myWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (newProgress == 100) {
                            progressBar1.setVisibility(View.INVISIBLE);

                        } else {
                            progressBar1.setVisibility(View.VISIBLE);

                            progressBar1.setProgress(newProgress);
                        }
                    }
                });

                bUploadFromChrome = false;

            } else if (id == R.id.nav_slideshow) {

                //calender
              heldesk_f = 0;
                WebView myWebView = (WebView) findViewById(R.id.webview1);

                myWebView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
                myWebView.loadUrl(server + "/calendar_mobileapps.wp?android=yes&memberid=" + usr);
                //myWebView.loadUrl("https://signalr.smartlab.com/");
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                myWebView.setWebViewClient(new WebViewClient());
                ProgressBar progressBar1 = (ProgressBar)findViewById(R.id.progressSpinner1);
                progressBar1.setMax(100);
                myWebView.setWebChromeClient(new WebChromeClient(){
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (newProgress == 100) {
                            progressBar1.setVisibility(View.INVISIBLE);
                        }
                        else {
                            progressBar1.setVisibility(View.VISIBLE);
                            progressBar1.setProgress(newProgress);
                        }
                    }
                });

                bUploadFromChrome = false;

            } else if (id == R.id.fm) {

                heldesk_f = 0;
                //      mySwipeRefreshLayout.setRefreshing(false);
                WebView myWebView = (WebView) findViewById(R.id.webview1);
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.loadUrl(server + "/index_mobileapps.wp?ptype=fm&imgf=/workplace/skin/mobile/smallicon/filemanager.png&prev=0&next=300&android=yes&portal=no&memberid=" + usr);
                webUrl = server + "/index_mobileapps.wp?ptype=fm&imgf=/workplace/skin/mobile/smallicon/filemanager.png&prev=0&next=300&android=yes&portal=no&memberid=" + usr;
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                myWebView.setWebViewClient(new WebViewClient());

                ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
                progressBar1.setMax(100);
                myWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (newProgress == 100) {
                            progressBar1.setVisibility(View.INVISIBLE);
                        } else {
                            progressBar1.setVisibility(View.VISIBLE);

                            progressBar1.setProgress(newProgress);
                        }
                    }
                });



                bUploadFromChrome = false;

            } else if (id == R.id.job) {

                heldesk_f = 1;
                //    mySwipeRefreshLayout.setRefreshing(false);
                WebView myWebView = (WebView) findViewById(R.id.webview1);
                myWebView.getSettings().setJavaScriptEnabled(true);
                String domain = server.replace("/workplace", "");
                myWebView.loadUrl(domain + "/workorder/1az_mobileapps.wp?android=yes&memberid=" + usr);
                webUrl = server + "/blog/website/main/AndroidIndex.wp?app=500&usr=" + usr;
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                myWebView.setWebViewClient(new WebViewClient());

                ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
                progressBar1.setMax(100);
                myWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (newProgress == 100) {
                            progressBar1.setVisibility(View.INVISIBLE);

                        } else {
                            progressBar1.setVisibility(View.VISIBLE);

                            progressBar1.setProgress(newProgress);
                        }
                    }
                });

                bUploadFromChrome = false;
            } else if (id == R.id.workplace) {
                //   mySwipeRefreshLayout.setRefreshing(false);
                heldesk_f = 0;


                WebView myWebView = (WebView) findViewById(R.id.webview1);
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.loadUrl(server + "/index_mobileapps.wp?android=yes&memberid=" + usr);
                webUrl = server + "/blog/website/main/AndroidIndex.wp?app=500&usr=" + usr;
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                myWebView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
                myWebView.loadUrl("javascript:AndroidPhoto()");
                myWebView.setWebViewClient(new WebViewClient());
                bUploadFromChrome = true;
                //myWebView.setWebChromeClient(new ChromeClient());
                ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
                progressBar1.setMax(100);
                myWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (newProgress == 100) {
                            progressBar1.setVisibility(View.INVISIBLE);

                        } else {
                            progressBar1.setVisibility(View.VISIBLE);

                            progressBar1.setProgress(newProgress);
                        }
                    }
                });


            } else if (id == R.id.attendance) {
                // mySwipeRefreshLayout.setRefreshing(false);
                heldesk_f = 0;


                try {

                    String domain = server.replace("/workplace", "");
                    //  if (getErrorCode(domain + "/cloudtms/report_remoteatt_m.asp?memberID=" + usr + "&android=yes") == true){
                    if (getErrorCode(domain + "/cloudtms/mobile_status.asp?subcsription=yes") == true) {

                        Intent intent = new Intent(this, frmTakePhoto.class);
                        startActivity(intent);

                    } else {
                        new android.app.AlertDialog.Builder(this)
                                .setTitle("No subscription")
                                .setMessage("This module is not subscribed. Please contact administrator")
                                .setCancelable(false)
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...

                                    }
                                }).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                bUploadFromChrome = false;
            } else if (id == R.id.shortcut) {
                // mySwipeRefreshLayout.setRefreshing(false);
                heldesk_f = 0;
                WebView myWebView = (WebView) findViewById(R.id.webview1);
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.loadUrl(server + "/dashboard_mobileapps.wp?android=yes&memberid=" + usr);
                webUrl = server + "/dashboard_mobileapps.wp?android=yes&memberid=" + usr;
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                myWebView.setWebViewClient(new WebViewClient());
                ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
                progressBar1.setMax(100);
                myWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (newProgress == 100) {
                            progressBar1.setVisibility(View.INVISIBLE);

                        } else {
                            progressBar1.setVisibility(View.VISIBLE);

                            progressBar1.setProgress(newProgress);
                        }
                    }
                });

                bUploadFromChrome = false;
            } else if (id == R.id.Analytics) {
                heldesk_f = 0;
                //   mySwipeRefreshLayout.setRefreshing(false);
                WebView myWebView = (WebView) findViewById(R.id.webview1);
                myWebView.getSettings().setJavaScriptEnabled(true);
                myWebView.loadUrl(server + "/eis/e.wp?memberid=" + usr + "&src=m");
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                myWebView.setWebViewClient(new WebViewClient());

                ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
                progressBar1.setMax(100);
                myWebView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (newProgress == 100) {
                            progressBar1.setVisibility(View.INVISIBLE);

                        } else {
                            progressBar1.setVisibility(View.VISIBLE);

                            progressBar1.setProgress(newProgress);
                        }
                    }
                });

                bUploadFromChrome = false;
            } else if (id == R.id.scanqr) {
                Intent intent = new Intent(this, QRScanner.class);
                startActivity(intent);

            }
        } else {

            heldesk_f = 0;
            noInternet();
            bUploadFromChrome = false;

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void askForPermission(String origin, String permission, int requestCode) {
        Log.d("WebView", "inside askForPermission for" + origin + "with" + permission);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    permission)) {


            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{permission},
                        requestCode);


            }
        } else {
            myRequest.grant(myRequest.getResources());
        }
    }



    public boolean getErrorCode(String surl) throws IOException {

        boolean bRetBol = false;
        URL url = null;
        try {
            url = new URL(surl);

            HttpURLConnection huc = null;

            huc = (HttpURLConnection) url.openConnection();

            int responseCode = huc.getResponseCode();
            if (responseCode == 200) {
                bRetBol = true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return bRetBol;


    }

    public void setCookie(String siteName, String cookieName) {
        String cookieString = cookieName + " path=/";
        CookieManager.getInstance().setCookie(siteName, cookieString);
    }

    public String getCookie(String siteName, String cookieName) {
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp = cookies.split(";");
        for (String ar1 : temp) {
            if (ar1.contains(cookieName)) {
                String[] temp1 = ar1.split("=");
                CookieValue = temp1[1];
                break;
            }
        }
        return CookieValue;
    }

    public void nativePermissionCheck() {
        boolean bRetBol = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();
            if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
                permissionsNeeded.add("Audio");
            if (!addPermission(permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add("Camera");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            });
                    return;
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

                return;


            }
        }
    }


    private void getCurrentDomain() {

        String domain = "";
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

       /* final String server = dba.getConfigValue(dbConfigHelper, "server",
                "active='1'");
        final String uid = dba.getConfigValue(dbConfigHelper, "username",
                "active='1'");

        final String pwd = dba.getConfigValue(dbConfigHelper, "password",
                "active='1'");

        final String locName = dba.getConfigValue(dbConfigHelper, "loca",
                "active='1'");*/

        String[] config = dba.getResultConfig(dbConfigHelper,"*","active='1'");

        String pwd = config[2];
        String uid = config[1];
        String server = config[3];
        String locName = config[8];

        AuthUser(primary, uid, pwd, server, true);

        TextView ip = (TextView) headerView.findViewById(R.id.lblIP);
        TextView LocationName = (TextView) headerView.findViewById(R.id.lblLocation);

        String ipAdd = getLocalIpAddress();

        ip.setText(ipAdd);

        if (locName.isEmpty()){
            LocationName.setText("GUEST MODE");
            hideMenu("GUEST MODE");
        }
        else {
            LocationName.setTextColor(Color.parseColor("#34495e"));
            LocationName.setText(locName);
        }


        Log.i("getdomain", server);
        Log.i("getactive", uid);


   /* if (server.contains("https://")){
            domain = server.replace("https://","");
        }

        else{
            domain = server.replace("http://","");
        }
		domain = domain.replace("/workplace", "");*/
        domain = server.replace("/workplace", "");
        TextView navUsername = (TextView) headerView.findViewById(R.id.lblUserid);
        ImageView Userprof = (ImageView) headerView.findViewById(R.id.imageView);
        TextView lblDomain = (TextView) headerView.findViewById(R.id.lblDomain);

        navUsername.setText(capitizeString(uid));
        lblDomain.setText(domain);

        //to hide menu
        Menu menu = navigationView.getMenu();
        Menu menu2 = navigationView.getMenu();
        Menu menu4 = navigationView.getMenu();

        MenuItem target = menu.findItem(R.id.nav_viewnewsoffline);
        MenuItem target3 = menu2.findItem(R.id.nav_gallery); //web portal
        MenuItem target4 = menu4.findItem(R.id.Analytics); //analytic

        target.setVisible(false);
        //detect bydomain to hide menu
        if (lblDomain.getText().toString().contains("mais.voffice.my")) {
            target3.setVisible(false);
            target4.setVisible(false);
        }

        isConnected = checkInternetConnection(this);

        if (isConnected) {
            Log.i("Download image : ", uid);

            DownloadImage task = new DownloadImage();
            DownloadImage task2 = new DownloadImage();

            Bitmap result2 = null;
            Bitmap result = null;
            String[] userdata = null;
            try {

                String pic = httpost();

                //showToast(pic);
                if (pic != null) {
                    userdata = pic.split(",");
                    result = task.execute(userdata[0].replace("\\", "/")).get();
                    result2 = task2.execute(userdata[1].replace("\\", "/")).get();
                    //   result = task.execute("https://ge.smartlab.com/wpdata/smartlabcommy/userdata/ggg.PNG").get();
                    //  result2 = task2.execute("https://ge.smartlab.com/wpdata/smartlabcommy/comdata/1627399370449.png").get();

                }

                Log.i("urlPhoto :", userdata[0]);
                Log.i("urlImage :", userdata[1]);


            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (result != null) {

                ImageView navLogo = (ImageView) headerView.findViewById(R.id.imageViewlogo);
                //toolbar
                ImageView navLogobar = (ImageView) findViewById(R.id.barlogo);
                TextView navTitlebar = (TextView) findViewById(R.id.barCompany);

                //set title
                navTitlebar.setText(userdata[2]);

                Bitmap bitmap = result;

                Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                //Bitmap circleBitmap = Bitmap.createBitmap(90, 90, Bitmap.Config.ARGB_8888);

                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Paint paint = new Paint();
                paint.setShader(shader);
                paint.setAntiAlias(true);
                Canvas c = new Canvas(circleBitmap);
                c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);

                // c.drawCircle(90/2, 90/2, 90/2, paint);

                //Userprof.setImageBitmap(circleBitmap);

                //draw square
                Userprof.setImageBitmap(result);

                float aspectRatio = result2.getWidth() /
                        (float) result2.getHeight();
                int width = 480;
                int height = Math.round(width / aspectRatio);


                navLogo.setImageBitmap(result2);
                // navLogobar.setImageBitmap(Bitmap.createScaledBitmap(result2,1000,600,false));
                navLogobar.setImageBitmap(Bitmap.createScaledBitmap(result2, width, height, false));

            } else {
                Userprof.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vrtwo));
            }

        } else {

            Userprof.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vrtwo));
        }

    }

    private String capitizeString(String name){
        String captilizedString="";
        if(!name.trim().equals("")){
            captilizedString = name.substring(0,1).toUpperCase() + name.substring(1);
        }
        return captilizedString;
    }

    public String httpost() {


        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
        String usr = config[1];
        String server = config[3];

        String domain = "";
        domain = server.replace("/workplace", "");


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //your codes here

        // Creating HTTP client
        HttpClient httpClient = new DefaultHttpClient();
        // Creating HTTP Post
        HttpPost httpPost = new HttpPost(
                domain + "/workplace/dashboard/NativeProfilePic.asp");

        // Building post parameters
        // key and value pair
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("memberid", usr));


        // Url Encoding the POST parameters
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            // writing error to Log
            e.printStackTrace();
        }

        // Making HTTP Request
        try {
            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            // Read the contents of an entity and return it as a String.
            String content = EntityUtils.toString(entity);

            return content;

        } catch (ClientProtocolException e) {
            // writing exception to log
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();
            return null;

        }
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    //http get image start-----------------------------------------

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
//showToast(result);
        }
    }


    //http get image end-----------------------------------------

    public void askpermission() {

        /*if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);

        }*/

        // Check if Android M or higher
    /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         if(!Settings.canDrawOverlays(this)){
             // ask for setting
             Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                     Uri.parse("package:" + getPackageName()));
             startActivity(intent);
         }
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();
            if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
                permissionsNeeded.add("Storage");
            if (!addPermission(permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add("Camera");
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
                permissionsNeeded.add("Network State");

            if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
                permissionsNeeded.add("Storage");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            });
                    return;
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", onClickListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private boolean addPermission(List<String> permissionsList, String permission) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }

        }

        return true;
    }

    public void checkConfigDB() {

        int iCount = dba.countConfigDB(dbConfigHelper);

        if (iCount == 1) {
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.hide();

        } else if (iCount > 1) {
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            fab.hide();
        }


        if (iCount == 0) {
            callclassExecute("Config", "yes");
        } else {


            String[] config = dba.getResultConfig(dbConfigHelper,"*","active='1'");

/*            final String locName = dba.getConfigValue(dbConfigHelper, "loca",
                    "active='1'");

            final String sUid = dba.getConfigValue(dbConfigHelper, "username",
                    "active='1'");

            final String server = dba.getConfigValue(dbConfigHelper, "server",
                    "active='1'");*/

            String locName = config[8];
            String sUid = config[1];
            String server = config[3];

            if(locName.isEmpty()){
                String guest = "";

                try {
                    guest = getIntent().getExtras().getString("guest");
                }catch(Exception e){
                 Log.i("GUESTMODE","GUEST");
                }

                if (guest == null){
                    if(checkLocationCreator(sUid,server)== 1){
                        AdminLocation();
                    }
                    else{
                        UserLocation();
                    }
                }
                else{
                    isConnected = checkInternetConnection(this);
                    getCurrentDomain();
                    callRefresh();
                    //---------------- fp

                    if (checkFPAuth()) {
               /* if(FingerprintDialog.isAvailable(this)) {
                    FingerprintDialog.initialize(this)
                            .title("Workplace Authentication")
                            .message("Please scan your fingerprint to continue")
                            .callback(this)
                            .show();
                }*/


                        executor = ContextCompat.getMainExecutor(this);
                        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);

                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onAuthenticationError(int errorCode, CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);

                                Toast.makeText(MainActivity.this, errString, Toast.LENGTH_LONG).show();
                                MainActivity.this.finish();
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();

                                Toast.makeText(MainActivity.this, "FAILED", Toast.LENGTH_LONG).show();
                            }
                        });


                        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Workplace Authentication")
                                .setSubtitle("Scan face or Fingerprint to continue")
                                .setNegativeButtonText("Exit")
                                .setConfirmationRequired(false)
                                .build();
                        biometricPrompt.authenticate(promptInfo);


                    }
                }

            }

            else {

                isConnected = checkInternetConnection(this);
                getCurrentDomain();
                callRefresh();
                //---------------- fp

                if (checkFPAuth()) {
               /* if(FingerprintDialog.isAvailable(this)) {
                    FingerprintDialog.initialize(this)
                            .title("Workplace Authentication")
                            .message("Please scan your fingerprint to continue")
                            .callback(this)
                            .show();
                }*/


                    executor = ContextCompat.getMainExecutor(this);
                    biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);

                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);

                            Toast.makeText(MainActivity.this, errString, Toast.LENGTH_LONG).show();
                            MainActivity.this.finish();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();

                            Toast.makeText(MainActivity.this, "FAILED", Toast.LENGTH_LONG).show();
                        }
                    });


                    promptInfo = new BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Workplace Authentication")
                            .setSubtitle("Scan face or Fingerprint to continue")
                            .setNegativeButtonText("Exit")
                            .setConfirmationRequired(false)
                            .build();
                    biometricPrompt.authenticate(promptInfo);


                }
                //-------------- end fp
            }

            if (isConnected == true) {
               /* mySwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
                mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        callRefresh();
                    }
                });
                callRefresh();*/
            } else {

                noInternet();
            }
        }
    }

    @Override
    public void onAuthenticationSucceeded() {
        // Logic when fingerprint is recognized
    }

    @Override
    public void onAuthenticationCancel() {
        // Logic when user canceled operation


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Workplace Authentication");
        builder.setMessage("Please fill-up your password");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // m_Text = input.getText().toString();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // m_Text = input.getText().toString();
            }
        });


        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                boolean bRetBol = false;
                m_Text = input.getText().toString();
                bRetBol = validatepassword(m_Text);

                if (bRetBol) {
                    wantToCloseDialog = true;
                } else {
                    showToast("Wrong password!..Please try again");
                }
                if (wantToCloseDialog)
                    dialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = true;
                //Do stuff, possibly set wantToCloseDialog to true then...
                if (wantToCloseDialog)
                    finish();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
    }


    public void changedomain() {
        Intent intent = new Intent(this, DomainList.class);
        startActivity(intent);
    }

    public void updatedomain() {
        Intent intent = new Intent(this, serverlist.class);
        startActivity(intent);
    }

    public void callNewsOffline() {
        //Intent intent = new Intent(this, AddNews.class);
        Intent intent = new Intent(this, AddNewswithdb.class);
        startActivity(intent);
    }

    public void callViewNewsOffline() {
        Intent intent = new Intent(this, dbList.class);
        startActivity(intent);
    }


    public void displayWebView() {

        final String offlineMessageHtml = "<div style='width:100%; height:80%; text-align:center;'><div style='margin-top:100px;'><font size='4pt' color='red'><b>Network not available or connection timed-out</b></font></div></div>";
        final String connectingMessageHtml = "<div style='width:100%; height:80%; text-align:center;'><div style='margin-top:100px;'><font size='4pt' color='white'><b>Connecting...</b></font></div></div>";
        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
        String usr = config[1];
        String server = config[3];

        String url = server;
        String domain = server.replace("http://", "");

        domain = domain.replace("/workplace", "");

        // retrieve UI elements
        webView = (WebView) findViewById(R.id.webview1);

        isConnected = checkInternetConnection(this);

        if (isConnected == true) {

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(false);
            webView.getSettings().setAllowFileAccess(true);
            webView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");

            askpermission();
            //    webView.setWebViewClient(new WebViewClient());
            webView.setWebViewClient(new WebViewClient() {
                //webView.setWebChromeClient(new WebChromeClient(){
                public void onProgressChanged(WebView view, int progress) {

                }
            });

            webView.loadUrl(url + "?android=yes&memberid=" + usr);

        } else

        {
            //progressBar.setVisibility(progressBar.GONE);
            webView.loadData(offlineMessageHtml, "text/html", "utf-8");

        }

    }

    class MyJavascriptInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        MyJavascriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void openGallery() {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        @JavascriptInterface
        public void executeURL(String url) {
            //  showToast("Redirect url to .. " + url);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }

        @JavascriptInterface
        public void openQR() {
            //validateCameraPermission();
            scanQR();
        }

        @JavascriptInterface
        public void GoToMain() {
           //showToast("Choose Photo");
            //callRefresh();

        }


        @JavascriptInterface
        public void InvokeRingtones(int ring) {
            callRing(ring);
        }

        @JavascriptInterface
        public String choosePhoto(String A, String F) {
            //showToast("Choose Photo");
            isUniversalUpload = false;
            AlertDialogPic(A, F);
            if (A == null || A.length() == 0 ){
                 showToast("Invalid news ID");
            }

            return l;
        }
        @JavascriptInterface
        public String choosePhotoUniversal(String A, String F,String tempUrl) {
            //showToast("Choose Photo");
            AlertDialogPicUniversal(A, F,uurl);
            //AlertDialogPic(A, F);
            bUploadFromChrome = false;
            isUniversalUpload = true;
            uurl = tempUrl;
            //showToast("Switch to universal upload :" + uurl);

            return l;
        }

        // TODO Auto-generated method stub
        String l = "";

        @JavascriptInterface
        public String getGPSCoor() {
            String l = "";
            // TODO Auto-generated method stub
            checkGPSStatus();

            l = LongLatitude();

            return l;
        }


        @JavascriptInterface
        public void menuGesture() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }


        @JavascriptInterface
        public void renewSession() {
            WebView myWebView = (WebView) findViewById(R.id.webview1);
            showToast(webUrl);
            myWebView.post(new Runnable() {
                @Override
                public void run() {
                  //enewUrl(webUrl);
                    callRefresh();
                }
            });




        }

        @JavascriptInterface
        public void shareNews(String newsurl) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
            i.putExtra(Intent.EXTRA_TEXT, newsurl);
            startActivity(Intent.createChooser(i, "Share URL"));

            /* Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        share.putExtra(Intent.EXTRA_TEXT, "<source url>");

        startActivity(Intent.createChooser(share, "Share text to..."));*/

        }

        @JavascriptInterface
        public void newxtab(String A) {
            // TODO Auto-generated method stub
            if (A.equals(null)) {
            }

            String l = "";
            //  String file = "samplepicture";
        /*Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);*/

            Uri uriUrl = Uri.parse(A);
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(launchBrowser);

            //displayData(file);
        }


        @JavascriptInterface
        public String tutup() {
            String k = "";
            //finish();
            //showToast("closebrowser");

            return k;

        }


    }


    @SuppressLint("NewApi")
    private void validateCameraPermission() {
        String[] permissions = {android.Manifest.permission.CAMERA};

        if (context.checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT);
            scanQR();
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    public boolean scanQR() {

        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();

        return false;

    }


    public void AlertDialogLogout() {
        final CharSequence[] items = {"Yes", "No"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                this);
        builder.setTitle("Are you sure want to exit?");
        builder.setItems(items, new

                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (item == 0) {

                            // menuGesture();

                            // log_out();
                            Intent intent = getIntent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();

                        } else {
                            // do nothing
                        }
                    }
                });

        android.app.AlertDialog alerts = builder.create();
        alerts.show();
    }

    public String natgetGPSCoor() {
        String l = "";
        // TODO Auto-generated method stub
        checkGPSStatus();


        l = LongLatitude();

        return l;
    }

    public void setRingtone() {
        String ringtoneuri = Environment.getExternalStorageDirectory().getAbsolutePath() + "/media/ringtone";
        File file1 = new File(ringtoneuri);
        file1.mkdirs();
        File newSoundFile = new File(ringtoneuri, "myringtone.mp3");


        Uri mUri = Uri.parse("android.resource://com.smartlab/raw/ring.mp3");


        ContentResolver mCr = this.getContentResolver();
        AssetFileDescriptor soundFile;
        try {
            soundFile = mCr.openAssetFileDescriptor(mUri, "r");
        } catch (FileNotFoundException e) {
            soundFile = null;
        }

        try {
            byte[] readData = new byte[1024];
            FileInputStream fis = soundFile.createInputStream();
            FileOutputStream fos = new FileOutputStream(newSoundFile);
            int i = fis.read(readData);

            while (i != -1) {
                fos.write(readData, 0, i);
                i = fis.read(readData);
            }

            fos.close();
        } catch (IOException io) {
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, newSoundFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, "my ringtone");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.MediaColumns.SIZE, newSoundFile.length());
        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(newSoundFile.getAbsolutePath());
        Uri newUri = mCr.insert(uri, values);
        try {
            Uri rUri = RingtoneManager.getValidRingtoneUri(this);
            if (rUri != null)

            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE, newUri);
            Toast.makeText(this, "New Rigntone set", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {

        }

    }

    public void callRing(int ring){

        String ringtoneuri = Environment.getExternalStorageDirectory().getAbsolutePath() + "/media/ringtone";
        File file1 = new File(ringtoneuri);
        file1.mkdirs();
        File newSoundFile = new File(android.os.Environment.getExternalStorageDirectory(), "/media/ringtone/ring.mp3");

        Uri path = Uri.parse("android.resource://"+ "com.smartlab.slidingmenu/" + R.raw.iphone);
        MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.iphone);

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                Log.i("MyApp","Silent mode");

                am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                mediaPlayer.setVolume(0,0);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Log.i("MyApp","Vibrate mode");

                am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                mediaPlayer.setVolume(0,0);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                Log.i("MyApp","Normal mode");

                am.setStreamVolume(AudioManager.STREAM_MUSIC, 90, 0);
                mediaPlayer.setVolume(90,90);
                break;
        }


        RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE,path);
        //   Uri defaultRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        Ringtone defaultRingtone = RingtoneManager.getRingtone(getApplicationContext() ,path);

        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 1000};
        if(ring == 1){
            //defaultRingtone.play();
            mediaPlayer.start();
            if (Build.VERSION.SDK_INT >= 26) {
                //v.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE);
                v.vibrate(pattern,0);
            } else {
                v.vibrate(350);
            }
        }
        else{
            //defaultRingtone.stop();
            mediaPlayer.stop();
            v.cancel();
        }

        showToast("Ring..Ring");
    }






    private String LongLatitude() {
        GpsTracker gt = new GpsTracker(getApplicationContext());
        Location l = gt.getLocation();
        double lat;
        double lon;

        String fullAddr = "";
        String latlon = "";
        String detLoc = "";

        if (l == null) {

            //getNetworkGPS();
            Toast.makeText(getApplicationContext(), "GPS unable to get Value", Toast.LENGTH_SHORT).show();

        } else {
            lat = l.getLatitude();
            lon = l.getLongitude();

            latlon = String.valueOf(lat) + "," + String.valueOf(lon);

            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(lat, lon, 1);


                fullAddr = addresses.get(0).getAddressLine(0);

                /*StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                        sb.append(address.getAddressLine(i)).append(" ");
                    sb.append(address.getLocality()).append(" ");
                    sb.append(address.getPostalCode()).append(" ");
                    sb.append(address.getCountryName());
                    txtLocation2.append(sb);
                }*/
            } catch (IOException e) {
//                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Canont get Address!", Toast.LENGTH_SHORT).show();
            }
        }

        detLoc = latlon + "|" + fullAddr;

        return detLoc;
    }


    public void AlertDialogR() {
        final CharSequence[] items = {"Yes", "No"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                this);
        builder.setTitle("Are you sure want to exit?");
        builder.setItems(items, new

                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (item == 0) {

                            Intent intent = getIntent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            finish();

                        } else {
                            // do nothing
                        }
                    }
                });

        android.app.AlertDialog alerts = builder.create();
        alerts.show();
    }

    public void menuGesture() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public void AlertDialogPicUniversal(final String B, final String GP,String universeURl) {
        CharSequence[] items = null;
        String title = null;

        title = "Select photo";

        items = new CharSequence[2];
        items[0] = "Album";
        items[1] = "Take photo";
        //items[2] = "File Manager";

        android.app.AlertDialog.Builder builder = new
                android.app.AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(items, new

                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int item) {

                        if (item == 0) {
                            Intent intent = new Intent();
                            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            reqid = B;
                            gpse = GP;

                            /*intent.putExtra("crop", "true");
                            intent.putExtra("aspectX", 1);
                            intent.putExtra("aspectY", 1);
                            intent.putExtra("outputX", 96);
                            intent.putExtra("outputY", 96);
                            intent.putExtra("return-data", true);*/
                            startActivityForResult(intent, SELECT_PHOTO);

                        } else if (item == 1) {
                            //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            //startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

                            //strict mode to enable
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());

                            //----------------------------------------------------------------------------
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            String currDT = sdf.format(new Date());

                            Intent CamImageIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                            File cameraFolder;

                            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                                //cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(),"workplace/camera");
                                cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(), "DCIM/Camera");
                            else
                                // cameraFolder = getCacheDir();
                                cameraFolder = getFilesDir();

                            if (!cameraFolder.exists())
                                cameraFolder.mkdirs();

                            //  File photo = new File(cameraFolder,"workplace/camera/imgNews_" + currDT.toString() + ".jpg");
                            File photo = new File(cameraFolder, "imgNews_" + currDT.toString() + ".jpg");
                            CamImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                            targetURI = Uri.fromFile(photo);
                            reqid = B;
                            gpse = GP;
                            startActivityForResult(CamImageIntent, CAMERA_PIC_REQUEST);
                            //-----------------------------------------------------------------------------
                        }
                    }
                });

        android.app.AlertDialog alerts = builder.create();
        alerts.show();
    }

    public void AlertDialogPic(final String B, final String GP) {
        CharSequence[] items = null;
        String title = null;

        title = "Select photo";

        items = new CharSequence[2];
        items[0] = "Album";
        items[1] = "Take photo";
        //items[2] = "File Manager";

        android.app.AlertDialog.Builder builder = new
                android.app.AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setItems(items, new

                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int item) {

                        if (item == 0) {
                            Intent intent = new Intent();
                            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            reqid = B;
                            gpse = GP;

                            /*intent.putExtra("crop", "true");
                            intent.putExtra("aspectX", 1);
                            intent.putExtra("aspectY", 1);
                            intent.putExtra("outputX", 96);
                            intent.putExtra("outputY", 96);
                            intent.putExtra("return-data", true);*/
                            startActivityForResult(intent, SELECT_PHOTO);

                        } else if (item == 1) {
                            //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            //startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

                            //strict mode to enable
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());

                            //----------------------------------------------------------------------------
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            String currDT = sdf.format(new Date());

                            Intent CamImageIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                            File cameraFolder;

                            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                                //cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(),"workplace/camera");
                                cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(), "DCIM/Camera");
                            else
                                // cameraFolder = getCacheDir();
                                cameraFolder = getFilesDir();


                            if (!cameraFolder.exists())
                                cameraFolder.mkdirs();

                            //  File photo = new File(cameraFolder,"workplace/camera/imgNews_" + currDT.toString() + ".jpg");
                            File photo = new File(cameraFolder, "imgNews_" + currDT.toString() + ".jpg");
                            CamImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                            targetURI = Uri.fromFile(photo);
                            reqid = B;
                            gpse = GP;
                            startActivityForResult(CamImageIntent, CAMERA_PIC_REQUEST);
                            //-----------------------------------------------------------------------------
                        }
                    }
                });

        android.app.AlertDialog alerts = builder.create();
        alerts.show();
    }

    private Uri mImageCaptureUri;

    private void showFileChooser() {

        Intent intentPDF = new Intent(Intent.ACTION_GET_CONTENT);
        intentPDF.setType("application/pdf");
        intentPDF.addCategory(Intent.CATEGORY_OPENABLE);

        Intent intentTxt = new Intent(Intent.ACTION_GET_CONTENT);
        intentTxt.setType("text/plain");
        intentTxt.addCategory(Intent.CATEGORY_OPENABLE);

        Intent intentXls = new Intent(Intent.ACTION_GET_CONTENT);
        intentXls.setType("application/x-excel");
        intentXls.addCategory(Intent.CATEGORY_OPENABLE);

        PackageManager packageManager = getPackageManager();

        List activitiesPDF = packageManager.queryIntentActivities(intentPDF,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafePDF = activitiesPDF.size() > 0;

        List activitiesTxt = packageManager.queryIntentActivities(intentTxt,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafeTxt = activitiesTxt.size() > 0;

        List activitiesXls = packageManager.queryIntentActivities(intentXls,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafeXls = activitiesXls.size() > 0;

        if (!isIntentSafePDF || !isIntentSafeTxt || !isIntentSafeXls) {
            // Potentially direct the user to the Market with a Dialog
        }

    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (bUploadFromChrome == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                    super.onActivityResult(requestCode, resultCode, intent);
                    return;
                }
                Uri[] results = null;
                // Check that the response is a good one
                if (resultCode == Activity.RESULT_OK) {
                    if (intent == null) {
                        // If there is not data, then we may have taken a photo
                        if (mCameraPhotoPath != null) {
                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }

                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;

            } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                    super.onActivityResult(requestCode, resultCode, intent);
                    return;
                }

                if (requestCode == FILECHOOSER_RESULTCODE) {

                    if (null == this.mUploadMessage) {
                        return;
                    }

                    Uri result = null;

                    try {
                        if (resultCode != RESULT_OK) {

                            result = null;

                        } else {

                            result = intent == null ? mCapturedImageURI : intent.getData();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "activity :" + e,
                                Toast.LENGTH_LONG).show();
                    }

                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                }
            }

            return;

        } else {

            try {
                if (resultCode != RESULT_CANCELED) {
                    if (requestCode == CAMERA_PIC_REQUEST) {
                        Bitmap bitmap = null;
                        String path = "";

                        path = getRealPathFromURI(targetURI); //from Gallery

                        if (path == null)

                            path = targetURI.getPath(); //from File Manager

                        if (path != null) {
                            imgPath = path;

                            BitmapFactory.Options opt = new BitmapFactory.Options();
                            opt.inSampleSize = 6;
                            bitmap = BitmapFactory.decodeFile(path, opt);

                        }

                        /* Check whether to rotate image */
                        ExifInterface exif = new ExifInterface(path);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        int rotateValue;

                        if (orientation == 3)
                            rotateValue = 180;
                        else if (orientation == 6)
                            rotateValue = 90;
                        else if (orientation == 8)
                            rotateValue = 270;
                        else
                            rotateValue = 0;

                        if (rotateValue != 0) {
                            Matrix m = new Matrix();
                            m.postRotate(rotateValue);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                        }

                        AlertDialogUpdate(path, reqid, gpse);

                    } else if (requestCode == SELECT_PHOTO) {

                        Bitmap bitmap = null;
                        String path = "";

                        mImageCaptureUri = intent.getData();

                        path = getRealPathFromURI(mImageCaptureUri); //from Gallery

                        AlertDialogUpdate(path, reqid, gpse);

                    } else if (requestCode == SELECT_DOC) {
                        Bitmap bitmap = null;
                        String path = "";

                        mImageCaptureUri = intent.getData();

                        path = getRealPathFromURI(mImageCaptureUri); //from Gallery

                        if (path == null)
                            path = mImageCaptureUri.getPath(); //from File Manager

                        if (path != null) {
                            imgPath = path;

                            BitmapFactory.Options opt = new BitmapFactory.Options();
                            opt.inSampleSize = 6;
                            bitmap = BitmapFactory.decodeFile(path, opt);
                        }

                        /* Check whether to rotate image */
                        ExifInterface exif = new ExifInterface(path);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        int rotateValue;

                        if (orientation == 3)
                            rotateValue = 180;
                        else if (orientation == 6)
                            rotateValue = 90;
                        else if (orientation == 8)
                            rotateValue = 270;
                        else
                            rotateValue = 0;

                        if (rotateValue != 0) {
                            Matrix m = new Matrix();

                            m.postRotate(rotateValue);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                        }
                        AlertDialogUpdate(path, reqid, gpse);
                    }
                }
            } catch (Exception e) {
                Log.e("Error get image", e.getMessage().toString());
                Toast.makeText(
                        getApplicationContext(),
                        "Exception ~ onActivityResult() :\n" + e.getClass().getName() + " \n" + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressWarnings("deprecation")
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    public void AlertDialogUpdateFile(String Path, final String rId, final String sCoor) {

        final String newPath = Path;
        final CharSequence[] items = {"Yes", "No"};
        android.app.AlertDialog.Builder builder = new
                android.app.AlertDialog.Builder(this);
        builder.setTitle("Are you sure to upload this?");
        builder.setItems(items, new

                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int item) {

                        if (item == 0) {
                            //update to server
                            //showToast(newPath);
                            //HttpTryPut(newPath);
                            uploadtoserverFile(primary, "Update", newPath, rId, sCoor);
                        }
                    }
                });

        android.app.AlertDialog alerts = builder.create();
        alerts.show();
    }

    public void AlertDialogUpdate(String Path, final String rId, final String sgps) {

        final String newPath = Path;
        final CharSequence[] items = {"Yes", "No"};
        android.app.AlertDialog.Builder builder = new
                android.app.AlertDialog.Builder(this);
        builder.setTitle("Are you sure to upload this?");
        builder.setItems(items, new

                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int item) {
                        if (item == 0) {
                            //update to server

                            //  uploadtoserver(primary,"Update",newPath,rId);
                            load_progressbar(newPath, rId, sgps);
                        }
                    }
                });

        android.app.AlertDialog alerts = builder.create();
        alerts.show();
    }

    public boolean load_progressbar(final String st, final String rqi, final String g) {

        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(MainActivity.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please wait..it may take sometimes."); // set message in progressbar dialog
        //horizontal progress bar type of progress bar
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0); //set min value of progress bar
        progressBar.setMax(100); // set max value of progress bar
        progressBar.show(); // display progress bar
        //reset progress bar status
        progressBarStatus = 0;
        //reset progress
        progress = 0;
        new Thread(new Runnable() {
            public void run() {
                while (progressBarStatus < 100) {
                    // process some tasks
                    progressBarStatus = doSomeTasks();

                    // your computer is too fast, sleep 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Update the progress bar
                    progressBarHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                            if (progressBarStatus == 30) {
                                uploadtoserver(primary, "Update", st, rqi, g);
                            }
                        }
                    });
                }

                // Progress completed ?!?!,
                if (progressBarStatus >= 100) {
                    // sleep 2000 milliseconds, so that you can see the 100%
                    try {

                        Thread.sleep(2000);

                        //showToast("Data Berjaya Di MuatNaik");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // close the progress bar dialog
                    progressBar.dismiss();

                }
            }
        }).start();

        return true;
    }

    public int doSomeTasks() {

        int prog = 0;
        while (progress <= 1000000) {
            progress++;
            if (progress == 100000) {

                return 10;
            } else if (progress == 200000) {
                return 20;
            } else if (progress == 300000) {
                return 30;
            }
        }

        return 100;
    }


    public void uploadtoserver(int primary, String button, String fPath, String sReqId, String gpsee) {
        try {

            String dothis;
            String url = "";
            String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
            String imgPath = fPath;

            String url3 = config[3];
            String domain = url3.replace("workplace", "");


            if (isUniversalUpload == true){
                url = domain + uurl.substring(1);
                //showToast(url + " " + isUniversalUpload );
                Log.i("Processed url :",url);
            }
            else
                url = domain + "/workplace/dashboard/uploadnews.wp";



            String Title = "";
            String Info = "";
            sect = "";
            String SynCA = "";

            Info = Info.replace("\t", "&emsp;");
            Info = Info.replace("\n", "<br>");

            byte[] pic1 = null;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            if (imgPath != null) {
                Bitmap bitmap = null;

                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inSampleSize = 2;
                bitmap = BitmapFactory.decodeFile(imgPath, opt);

                /* Check whether to rotate image */
                ExifInterface exif = new ExifInterface(imgPath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                int rotateValue;

                if (orientation == 3)
                    rotateValue = 180;
                else if (orientation == 6)
                    rotateValue = 90;
                else if (orientation == 8)
                    rotateValue = 270;
                else
                    rotateValue = 0;
                /* ---------------------------- */

                if (rotateValue != 0) {
                    Matrix m = new Matrix();

                    m.postRotate(rotateValue);
                    Bitmap dispBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

                    bitmap = dispBMP;
                }

                if (bitmap.getHeight() > bitmap.getWidth()) {
                    /*if (bitmap.getHeight() > 3264)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, stream);
                    else if (bitmap.getHeight() > 2048)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                    else if (bitmap.getHeight() > 1600)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, stream);
                    else if (bitmap.getHeight() > 1280)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                    else if (bitmap.getHeight() > 1024)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                    else if (bitmap.getHeight() > 800)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                    else if (bitmap.getHeight() > 640)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 55, stream);
                    else if (bitmap.getHeight() > 480)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    else if (bitmap.getHeight() > 320)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    else
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);*/
                } else {
                    /*if (bitmap.getWidth() > 3264)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, stream);
                    else if (bitmap.getWidth() > 2048)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                    else if (bitmap.getWidth() > 1600)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, stream);
                    else if (bitmap.getWidth() > 1280)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                    else if (bitmap.getWidth() > 1024)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                    else if (bitmap.getWidth() > 800)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                    else if (bitmap.getWidth() > 640)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 55, stream);
                    else if (bitmap.getWidth() > 480)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    else if (bitmap.getWidth() > 320)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    else
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);*/
                }

                /*if (bitmap.getWidth() > 3264){
                bitmap.compress(Bitmap.CompressFormat.JPEG,10,stream);
              }*/

                bitmap.compress(Bitmap.CompressFormat.JPEG, 15, stream);

                pic1 = stream.toByteArray();

                stream.flush();
                stream.close();
            }

            //byte[] pic1 = dba.GetBlob(primary, 5);

            if (button == "Upload")
                dothis = "insert";
            else
                dothis = "update";

            //upload data to server
            //showToast("pic " + pic1);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("username", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("password", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("imgurl", sReqId));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("Ntype", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("Title", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("Info", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("image1", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("SynCA", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("dothis", dothis));

            Log.i("uploadtoserver", "before post");

            post(url, nameValuePairs, pic1, primary, sReqId, gpsee);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void uploadimgurl(String imgurl) {

        String dothis;
        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");


        String url3 = config[3];
        String domain = url3.replace("workplace", "");

        String url = domain + "/workplace/dashboard/uploadnews.wp";

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add((NameValuePair) new BasicNameValuePair("imgurl", imgurl));

        Log.i("uploadtoserver", "before post");

        postImgUrl(url, nameValuePairs, primary);

    }


    public void uploadtoserverFile(int primary, String button, String fPath, String sReqId, String coor) {
        try {
            String dothis;

            //String[] result = dba.getResultArray(dbHelper, "*", "_ID='"+primary+"'");
            String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
            // String NewsID = dba.getResult(dbHelper, "NewsID", "_ID ='"+primary+"'");
            String FilePath = fPath;

		 /*if (NewsID == null)
			 NewsID = "";*/

            String usr = "";
            String pass = "";
            //String url = config[3] + getResources().getString(R.string.upload_news);
            String url = "http://mediacity.voffice.my/helpdesk/mobile/uploadnews.wp";
            String Title = "";
            String Info = "";
            sect = "";
            String SynCA = "";

            Info = Info.replace("\t", "&emsp;");
            Info = Info.replace("\n", "<br>");

            File file = new File(FilePath);
            FileInputStream fileInputStream;
            byte[] data = null;
            byte[] finalData = null;

            fileInputStream = new FileInputStream(FilePath);
            data = new byte[(int) file.length()];
            finalData = new byte[(int) file.length()];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            fileInputStream.read(data);
            stream.write(data);
            finalData = stream.toByteArray();

            stream.flush();
            stream.close();

            showToast("" + finalData);

            //byte[] pic1 = dba.GetBlob(primary, 5);

            if (button == "Upload")
                dothis = "insert";
            else
                dothis = "update";

            //upload data to server
            //showToast("pic " + pic1);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("username", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("password", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("NewsID", sReqId));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("Ntype", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("Title", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("Info", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("image1", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("SynCA", ""));
            nameValuePairs.add((NameValuePair) new BasicNameValuePair("dothis", dothis));

            Log.i("uploadtoserver", "before post");

            post(url, nameValuePairs, finalData, primary, sReqId, coor);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void post(String url, List<NameValuePair> nameValuePairs, byte[] pic1, int primary, String reqid, String gpps) {

        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);
        ByteArrayBody image1 = null;

        if (pic1 != null) {
            image1 = new ByteArrayBody(pic1, "imgNews");

        }

        try {

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (int index = 0; index < nameValuePairs.size(); index++) {
                if (image1 != null && nameValuePairs.get(index).getName().equalsIgnoreCase("image1")) {
                    entity.addPart(nameValuePairs.get(index).getName(), image1);
                    Log.i(nameValuePairs.get(index).getName(), "image1");
                } else if (nameValuePairs.get(index).getValue() != null) {
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                    Log.i(nameValuePairs.get(index).getName(), nameValuePairs.get(index).getValue());
                }
            }

            httpPost.setEntity(entity);
            Log.i("getRequestLine()", httpPost.getRequestLine().toString());

            HttpResponse response = httpClient.execute(httpPost, localContext);

            HttpEntity resEntity = response.getEntity();

            Log.e("getStatusLine()", response.getStatusLine().toString());

            if (resEntity != null) {
                String res = EntityUtils.toString(resEntity);

                Log.e("post : res", res);

                xmlParse(res, primary, reqid, gpps);

            }
            if (resEntity != null) {
                resEntity.consumeContent();
            }

            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("post() ~ Exception", e.getClass().getName() + " : " + e.getMessage());
            Toast.makeText(
                    getApplicationContext(),
                    "Exception ~ post() :\n" + e.getClass().getName() + " \n" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void postImgUrl(String url, List<NameValuePair> nameValuePairs, int primary) {

        StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        try {

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (int index = 0; index < nameValuePairs.size(); index++) {

                if (nameValuePairs.get(index).getValue() != null) {
                    entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                    Log.i(nameValuePairs.get(index).getName(), nameValuePairs.get(index).getValue());
                }
            }

            httpPost.setEntity(entity);
            Log.i("getRequestLine()", httpPost.getRequestLine().toString());

            HttpResponse response = httpClient.execute(httpPost, localContext);

            HttpEntity resEntity = response.getEntity();

            Log.e("getStatusLine()", response.getStatusLine().toString());

            if (resEntity != null) {
                String res = EntityUtils.toString(resEntity);

                Log.e("post : res", res);

                // xmlParse(res,primary);

            }
            if (resEntity != null) {
                resEntity.consumeContent();
            }

            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("post() ~ Exception", e.getClass().getName() + " : " + e.getMessage());
            Toast.makeText(
                    getApplicationContext(),
                    "Exception ~ post() :\n" + e.getClass().getName() + " \n" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void xmlParse(String result, int primary, String reqid, String sGPS) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();



            xpp.setInput(new StringReader(result));

            int eventType = xpp.getEventType();

            String dat = "";
            String info = "";
            String id = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.i("Start document", "start");
                } else if (eventType == XmlPullParser.START_TAG) {
                    Log.i("Start tag ", xpp.getName());
                    if (xpp.getName().equals("MSG")) {
                        id = xpp.getName();
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.i("End tag ", xpp.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    if (id.equals("MSG")) {
                        info = "Error updating data, " + xpp.getText();
                    } else
                        dat = xpp.getText();
                }

                eventType = xpp.next();
            }
            Log.i("End document", "end");
            if (dat != "" && dat != null) {
                if (heldesk_f == 1) {
                    displayWebView3();
                } else {

                    if (isUniversalUpload == false){
                        displayWebView2(dat + "&newstext=" + reqid + "&GPSE=" + sGPS);
                    }
                    else
                        displayWebView2(dat + "&sid=" + reqid);


                    Log.i("displayweb ", dat + "&sid=" + reqid);
                }
                // showToast(dat + "&" +reqid);
                //showToast(dat);
                // uploadimgurl(dat);
            } else
                Toast.makeText(this, "Error updating data for NewsID '" + dat + "' : \n" + info, Toast.LENGTH_SHORT).show();
            Log.e("data for NewsID '" + dat + "'.", info);
        } catch (Exception e1) {
            Log.e("xmlParse() ~ Exception", e1.getClass().getName() + " : " + e1.getMessage(), e1);

            Toast.makeText(
                    getApplicationContext(),
                    "Exception ~ xmlParse() : \n" + e1.getClass().getName() + " \n" + e1.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }

    public void displayWebView3() {

        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");

        final String offlineMessageHtml = "<div style='width:100%; height:80%; text-align:center;'><div style='margin-top:100px;'><font size='4pt' color='red'><b>Network not available or connection timed-out</b></font></div></div>";
        final String connectingMessageHtml = "<div style='width:100%; height:80%; text-align:center;'><div style='margin-top:100px;'><font size='4pt' color='white'><b>Connecting...</b></font></div></div>";

        String usr = config[1];
        String server = config[3];

        String url = server;
        String domain; //= server.replace("http://","");

        domain = server.replace("/workplace", "");

        // retrieve UI elements
        webView = (WebView) findViewById(R.id.webview1);

        isConnected = checkInternetConnection(this);

        if (isConnected == true) {
            //webView.loadUrl(url + "?android=yes&memberid=" + usr);

            /*if (Build.VERSION.SDK_INT >= 11){
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);*/

            //webView.setWebViewClient(new myWebClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(false);
            webView.getSettings().setAllowFileAccess(true);
            webView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");

            //    webView.setWebViewClient(new WebViewClient());
            webView.setWebViewClient(new WebViewClient() {
                //webView.setWebChromeClient(new WebChromeClient(){
                public void onProgressChanged(WebView view, int progress) {

                }
            });


          //showToast(domain + "/helpdesk/m/helpdesk_viewdetail.asp?memberid=" + usr + "&requestid=" + reqid);
            String testurl;
            // testurl = "http://ge.smartlab.com/helpdesk/m/helpdesk_viewdetail.asp?memberid=syukri&requestid=9527";
            webView.loadUrl(domain + "/helpdesk/m/helpdesk_viewdetail.asp?memberid=" + usr + "&requestid=" + reqid);
            //webView.loadUrl(testurl);
        } else

        {
            noInternet();

        }

    }

    public void displayWebView2(String url3) {

        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");

        final String offlineMessageHtml = "<div style='width:100%; height:80%; text-align:center;'><div style='margin-top:100px;'><font size='4pt' color='red'><b>Network not available or connection timed-out</b></font></div></div>";
        final String connectingMessageHtml = "<div style='width:100%; height:80%; text-align:center;'><div style='margin-top:100px;'><font size='4pt' color='white'><b>Connecting...</b></font></div></div>";

        String usr = config[1];
        String server = config[3];

        String url = server;
        String domain = server.replace("http://", "");

        domain = domain.replace("/workplace", "");

        // retrieve UI elements
        webView = (WebView) findViewById(R.id.webview1);

        isConnected = checkInternetConnection(this);

        if (isConnected == true) {
            //webView.loadUrl(url + "?android=yes&memberid=" + usr);

            /*if (Build.VERSION.SDK_INT >= 11){
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);*/

            //webView.setWebViewClient(new myWebClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(false);
            webView.getSettings().setAllowFileAccess(true);
            webView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");


            //    webView.setWebViewClient(new WebViewClient());
            webView.setWebViewClient(new WebViewClient() {
                //webView.setWebChromeClient(new WebChromeClient(){
                public void onProgressChanged(WebView view, int progress) {

                }
            });

            //  webView.loadUrl(url +"/mobile/helpdesk_viewdetail.asp?memberid=" + usr + "&requestid=" + reqid );
            webView.loadUrl(url3 + "&android=yes&memberid=" + usr);
         showToast(url3);

        } else

        {
            noInternet();

        }

    }

    public void showToast(String strMsg) {
        Toast.makeText(this, strMsg, Toast.LENGTH_LONG).show();
    }

    public boolean checkInternetConnection(Context context) {

        ConnectivityManager con_manager = (ConnectivityManager)

                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else

        {
            return false;
        }

    }

    private void noInternet() {
        WebView myWebView = (WebView) findViewById(R.id.webview1);
        String summary =
                "<html><head>\n" +
                        "    <meta charset=\"utf-8\" />\n" +
                        "    <title>No connection to the internet</title>\n" +
                        "    <style>\n" +
                        "      html,body { margin:0; padding:0; }\n" +
                        "      html {\n" +
                        "        background: #191919 -webkit-linear-gradient(top, #000 0%, #191919 100%) no-repeat;\n" +
                        "        background: #191919 linear-gradient(to bottom, #000 0%, #191919 100%) no-repeat;\n" +
                        "      }\n" +
                        "      body {\n" +
                        "        font-family: sans-serif;\n" +
                        "        color: #FFF;\n" +
                        "        text-align: center;\n" +
                        "        font-size: 50%;\n" +
                        "      }\n" +
                        "      h1, h2 { font-weight: normal; }\n" +
                        "      h1 { margin: 0 auto; padding: 0.15em; font-size: 10em; text-shadow: 0 2px 2px #000; }\n" +
                        "      h2 { margin-bottom: 2em; }\n" +
                        "    </style>\n" +
                        "  </head>\n" +
                        "  <body>\n" +
                        "    <h1>⚠</h1>\n" +
                        "    <h2>No connection to the internet</h2>\n" +
                        "    <p>This Display has a connection to your network but no connection to the internet.</p>\n" +
                        "    <p class=\"desc\">The connection to the outside world is needed for updates and keeping the time.</p>\n" +
                        "  </body></html>";

        myWebView.loadData(summary, "text/html", null);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        WebView myWebView = (WebView) findViewById(R.id.webview1);

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                myWebView.setWebChromeClient(new WebChromeClient() {
                    public void onProgressChanged(WebView view, int progress) {
                        //progressBar.setProgress(progress);
                        //if (progress == 100)
                        //progressBar.setVisibility(progressBar.GONE);
                    }
                });
                if (myWebView.canGoBack()) {
                    myWebView.goBack();
                } else {
                    //finish();
                    AlertDialogR();
                }
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void log_out() {
        //  callConfig();
        this.finish();
    }

    public boolean callConfig() {
        Boolean del = false;

        dbConfigHelper = new MySQLiteHelper(this);

        del = dbConfigHelper.deleteAllRows();

        //Intent intent = new Intent(this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra("sfromMain","");
        //startActivity(intent);
        this.finish();
        checkConfigDB();
        checkFPAuth();

        return del;
    }

    public boolean validatepassword(String pwd) {

        String usr = "";
        String sPwd = "";
        boolean bRetBol = false;

        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");

        //String usr = config[1];
        sPwd = config[2];

        if (sPwd.equals(pwd)) {
            bRetBol = true;
        }

        return bRetBol;
    }

    public boolean checkFPAuth() {
        boolean bRetBol = false;
        int result = 0;

        try {
            int iCount = dba.countSettingDB(dbConfigHelper);

            if (iCount > 0) {
                result = dba.getSettingValue(dbConfigHelper, "active",
                        "_ID='1'");

                if (result == 1) {
                    bRetBol = true;
                }
            }
        } catch (Exception e) {
            showToast("Please delete and reinstall application");
            e.printStackTrace();
        }


        return bRetBol;
    }

    public void callGeoFence() {
        Intent intent = new Intent(this, geoFenceActivity.class);
        startActivity(intent);
    }

    //------------ upload using chrome client
    public class ChromeClient extends WebChromeClient {

        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e("ErrorCreatingFile", "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

            return true;

        }

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard

            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");

            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }

            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");

            mCapturedImageURI = Uri.fromFile(file);

            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");

            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[]{captureIntent});

            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);


        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {

            openFileChooser(uploadMsg, acceptType);
        }

    }

    public void secondFragment(String surl) {

        /*
        *   fragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
            .replace(R.id.frameContainer, new secondFragment(), "secondFragmentTag").addToBackStack(null)
            .commit();
        * */

       /* WebViewFragmentii fragment = new WebViewFragmentii();
        Bundle args = new Bundle();

        args.putString("sURL",surl);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.contain_main, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();*/
        Intent intent = new Intent(this, WebViewFragmentii.class);
        startActivity(intent);
    }



    //---------- protected class idle timer
    Handler myHandler = new Handler();
    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            //task to do if user is inactive
          //  showToast("renew session after 1 minutes on inactivity.");
            renew_Session();
            Log.i("User inactivity", "sending token for session renewal..");
            Log.i("User inactivity", "Updating sessionID..");
        }
    };

            @Override
            public void onUserInteraction() {
                super.onUserInteraction();
                myHandler.removeCallbacks(myRunnable);
               // myHandler.postDelayed(myRunnable,1 * 60 * 1000 );
                myHandler.postDelayed(myRunnable,60 * 60 * 1000);
            }

    //---------- end of protected class idle timer


    public void AdminLocation()
    {
        Intent intent = new Intent(this, LocationManagement.class);
        startActivity(intent);
        this.finish();
    }

    public void UserLocation()
    {
        Intent intent = new Intent(this, InitLocationManagement.class);
        startActivity(intent);
        this.finish();
    }

    public int checkLocationCreator(String sUid, String Svr) {

        int response = 0;

        String strCurrentLine = "";
        String token = "";
        String urlAddress = "";
        String chksum;

        String encUid;

        try {

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);


                urlAddress = Svr +"/websvc/CheckLocCreator.wp";

                Log.i("uriCheckLoc", urlAddress);

                URL url = new URL(urlAddress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                chksum = DES.SHA1(sUid);
                encUid = encryption(sUid);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("JWT", encUid);
                jsonParam.put("CS", chksum);

                Log.i("JWT request Loc stat : ", jsonParam.toString());
                //showToast(jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                String temp = "";

                BufferedReader br = null;
                if (conn.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((strCurrentLine = br.readLine()) != null) {
                        temp = strCurrentLine;
                    }

                    JSONObject jObject = new JSONObject(temp);

                    String aJsonStatus = jObject.getString("STATUS");

                    if (aJsonStatus.contains("1")) {
                        response = 1;
                        Log.i("bol", temp);
                    }
                    else if(aJsonStatus.contains("0")){
                        response = 0;
                    }
                    else{
                        response = 2;
                        reqToken();
                    }
                }

                Log.i("JWT Loc status response", temp);
                conn.disconnect();
            }

        } catch (Exception e) {
            String error = "";
            for (StackTraceElement elem : e.getStackTrace()) {
                error += elem.toString();
            }
            Log.e("Probs", error);

        }

        return response;
    }



    public void reqToken(){

        String strCurrentLine = "";
        boolean bRetBol = false;
        String sTOken = "";
        //sever info
        String Svr = "";
        String domain = "";

        String chksum;
        String rawData = "";
        String encData = "";
        String deviceID = "";

        try {

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                final String server = dba.getConfigValue(dbConfigHelper, "server",
                        "active='1'");
                final String uid = dba.getConfigValue(dbConfigHelper, "username",
                        "active='1'");

                final String pwd = dba.getConfigValue(dbConfigHelper, "password",
                        "active='1'");

                Svr = server + "/websvc/LocReg.wp";

                URL url = new URL(Svr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

               // imei = getIMEIDeviceId(getApplicationContext());

                imei = UUID.randomUUID().toString();
                String substrDeviceID;
                if (imei.length() > 7){
                    substrDeviceID =imei.substring(0,7);
                }
                else
                    substrDeviceID =imei;
                deviceID = substrDeviceID;

                if (deviceID == null || deviceID.length() == 0) {
                    deviceID = "0000-0000";
                }

                rawData = uid + "||" + pwd + "||"+ deviceID + "||LOC_DISABLED_" + uid + "||LOCKEY_DISABLED||Android";
                encData = encryption(rawData);
                chksum = DES.SHA1(rawData);

                JSONObject jsonParam = new JSONObject();

                jsonParam.put("JWT", encData);
                jsonParam.put("CS", chksum);

                Log.i("getTOK raw : ", rawData);
                Log.i("getTOK : ", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());
                os.flush();
                os.close();

                Log.i("getTOK STATUS", String.valueOf(conn.getResponseCode()));
                String temp = "";

                BufferedReader br = null;
                if (conn.getResponseCode() == 200) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((strCurrentLine = br.readLine()) != null) {
                        temp = strCurrentLine;
                    }
                }

                sTOken = "";
                Log.i("getTOK RESPONSE", temp);
                conn.disconnect();
                JSONObject jObject = new JSONObject(temp);
                String aJsonString = jObject.getString("JWT");
                String aJsonStatus = jObject.getString("STATUS");
                String decData = Decryption(aJsonString);

                Log.i("getTOK Decypt", aJsonString);
                Log.i("getTOK RAw", decData);

                if (aJsonStatus.equalsIgnoreCase("SUCCESS")) {
                    bRetBol = true;
                    sTOken = aJsonString;
                    dba.UpdateLocationManagement(dbConfigHelper,1,true,"LOCDISABLE_"+ uid,"LOCKEY_DISABLED",sTOken);
                } else
                    showToast("Invalid location key");
            }

        } catch (Exception e) {
            String error = "";
            for (StackTraceElement elem : e.getStackTrace()) {
                error += elem.toString();
            }
            Log.e("getTOK Probs", error);

        }
    }

    private void hideMenu(String mode){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(mode=="GUEST MODE") {
            //to hide menu
            Menu menu = navigationView.getMenu();
            Menu menu2 = navigationView.getMenu();
            Menu menu4 = navigationView.getMenu();

            MenuItem target = menu.findItem(R.id.workplace);
            MenuItem target3 = menu2.findItem(R.id.nav_gallery); //web portal
            MenuItem target4 = menu4.findItem(R.id.shortcut); //analytic
            MenuItem target5 = menu.findItem(R.id.fm);
            MenuItem target6 = menu2.findItem(R.id.Analytics); //web portal
            MenuItem target7 = menu4.findItem(R.id.config); //analytic
            MenuItem target8 = menu.findItem(R.id.attendance);
            MenuItem target9 = menu2.findItem(R.id.scanqr); //web portal


            target3.setVisible(false);
            target.setVisible(false);
            target4.setVisible(false);
            target5.setVisible(false);
            target6.setVisible(false);
            target7.setVisible(false);
            target8.setVisible(false);
            target9.setVisible(false);
        }
    }

    public static String getIMEIDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= 29)
        {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }
            assert mTelephony != null;
            if (mTelephony.getDeviceId() != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    deviceId = mTelephony.getImei();
                }else {
                    deviceId = mTelephony.getDeviceId();
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        Log.d("deviceId", deviceId);
        return deviceId;
    }

    public  void showtime(){

        TextView time = (TextView)findViewById(R.id.time);

        long ltime = System.currentTimeMillis();

        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss a");
        String datetime = sdf1.format(ltime);

        time.setText(datetime);

    }


    private void InvokeChat(){
        nativePermissionCheck();

        String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
        String usr = config[1];
        String server = config[3];

        String domain = "";
        String urltemp;
        heldesk_f = 0;
        WebView myWebView = (WebView) findViewById(R.id.webview1);
        myWebView.getSettings().setJavaScriptEnabled(true);
        String fbtoken = FirebaseInstanceId.getInstance().getToken();
        domain = server.replace("/workplace", "");
        myWebView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
        urltemp = "https://chat.voffice.my/app_chat/signon.php?uname=" + usr + "&email=" + usr + "@smartlab.com&coid=ge.smartlab.com&system=workplace&dp=&android_token=" + fbtoken;
        myWebView.loadUrl(urltemp);
        Log.i("chaturl",urltemp);
        webUrl =urltemp;
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progressSpinner1);
        progressBar1.setMax(100);
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar1.setVisibility(View.INVISIBLE);

                } else {
                    progressBar1.setVisibility(View.VISIBLE);

                    progressBar1.setProgress(newProgress);
                }
            }
        });

        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                myRequest = request;
                String[] resources = request.getResources();
                switch (resources[0]){
                    case PermissionRequest.RESOURCE_AUDIO_CAPTURE:
                        askForPermission(request.getOrigin().toString(), Manifest.permission.RECORD_AUDIO, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                        break;
                    case PermissionRequest.RESOURCE_VIDEO_CAPTURE:
                        askForPermission(request.getOrigin().toString(), Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_RECORD_VIDEO);
                        break;
                }
            }
        });
    }




}


