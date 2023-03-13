package com.smartlab.wpslidingmenu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.Spinner;


@SuppressLint("CutPasteId")
public class AddNewswithdb extends Activity {
	private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int SELECT_FILE = 1;

	private GoogleMap mMap;
	LocationManager locationManager;
	private static final int REQUEST_LOCATION_PERMISSION = 1;
	Marker marker;
	LocationListener locationListener;

	Uri targetURI;
	String button;

	String GPS;
	String sect;
	boolean isSelected;

	MySQLiteHelper dbHelper;
    SQLiteDatabase db;
    dbActivities dba;
    int primary;
    byte[] byteArray;
    String domain;
    String imgPath;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.addnewsndb);

        dba = new dbActivities();
        dbHelper = new MySQLiteHelper(this);

		primary = dba.checkDB(dbHelper);

       // sect = getIntent().getExtras().getString("sect");
        TextView txtSectVal = (TextView)findViewById(R.id.tvSectVal);
        txtSectVal.setText(sect);

		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());

        getCurrentDomain();

        addListenerOnButtonNews();

		generateButton();

       // adjustResolution();

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]
							{Manifest.permission.ACCESS_FINE_LOCATION},
					REQUEST_LOCATION_PERMISSION);
		}


    }


    private void adjustResolution()
    {
    	int txtSize = 0;
    	int TxtPdg = 0;
    	int LayHigh = 0;
    	int LayPdg = 0;
    	int txtLayPdg = 0;
    	int layTitleHigh = 0;
    	int picHnW = 0;

    	// Set value based on resolution
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

		if (dm.heightPixels <= 800)
		{
			txtSize = 15;
			TxtPdg = 5;
			LayHigh = 60;
			LayPdg = 0;
			txtLayPdg = 14;
			layTitleHigh = 30;
			picHnW = 300;
		}
		else if (dm.heightPixels > 800 && dm.heightPixels <= 1024)
		{
			txtSize = 16;
			TxtPdg = 5;
			LayHigh = 62;
			LayPdg = 0;
			txtLayPdg = 14;
			layTitleHigh = 31;
			picHnW = 350;
		}
		else if (dm.heightPixels > 1024 && dm.heightPixels <= 1280)
		{
			txtSize = 17;
			TxtPdg = 7;
			LayHigh = 62;
			LayPdg = 0;
			txtLayPdg = 15;
			layTitleHigh = 32;
			picHnW = 450;
		}
		else if (dm.heightPixels > 1280 && dm.heightPixels <= 1366)
		{
			txtSize = 18;
			TxtPdg = 7;
			LayHigh = 64;
			LayPdg = 0;
			txtLayPdg = 15;
			layTitleHigh = 33;
			picHnW = 550;
		}
		else if (dm.heightPixels > 1366 && dm.heightPixels <= 1600)
		{
			txtSize = 19;
			TxtPdg = 9;
			LayHigh = 66;
			LayPdg = 0;
			txtLayPdg = 16;
			layTitleHigh = 34;
			picHnW = 600;
		}
		else
		{
			//txtSize = 20;
			txtSize = 20;
			TxtPdg = 9;
			LayHigh = 66;
			LayPdg = 0;
			txtLayPdg = 16;
			layTitleHigh = 35;
			picHnW = 600;
		}

		//set text size
		TextView tvPicture = (TextView)findViewById(R.id.tvPicture);
		TextView tvSect = (TextView)findViewById(R.id.tvSect);
		TextView tvHead = (TextView)findViewById(R.id.tvHead);
		TextView tvDescp = (TextView)findViewById(R.id.tvDescp);
		ImageView imvNewsPic = (ImageView)findViewById(R.id.imvNewsPic);

	    //layTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , layTitleHigh));

	    //LinearLayout.LayoutParams layoutParamsTitle = (LinearLayout.LayoutParams)tvTitle.getLayoutParams();
	    //layoutParamsTitle.setMargins(TxtPdg, 0, 0, 0);

	    tvPicture.setTextSize(txtSize);
	    LinearLayout.LayoutParams layoutParamsPicture = (LinearLayout.LayoutParams)tvPicture.getLayoutParams();
	    layoutParamsPicture.setMargins(TxtPdg, 0, 0, 0);

	    tvSect.setTextSize(txtSize);
	    LinearLayout.LayoutParams layoutParamsSect = (LinearLayout.LayoutParams)tvSect.getLayoutParams();
	    layoutParamsSect.setMargins(TxtPdg, 0, 0, 0);

	    imvNewsPic.setLayoutParams(new LinearLayout.LayoutParams(picHnW , picHnW));
	    //imvNewsPic.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT));

	    tvHead.setTextSize(txtSize);
	    LinearLayout.LayoutParams layoutParamsHead = (LinearLayout.LayoutParams)tvHead.getLayoutParams();
	    layoutParamsHead.setMargins(TxtPdg, txtSize, 0, 0);

	    tvDescp.setTextSize(txtSize);
	    LinearLayout.LayoutParams layoutParamsDescp = (LinearLayout.LayoutParams)tvDescp.getLayoutParams();
	    layoutParamsDescp.setMargins(TxtPdg, 0, 0, 0);

    }

    private void getCurrentDomain()
    {
    	final String server = dba.getConfigValue(dbHelper,"server","active='1'");
    	final String uid = dba.getConfigValue(dbHelper,"username","active='1'");
    	//final TextView lblDomain = (TextView) findViewById(R.id.lblDomain);
    	final TextView lblTitle = (TextView) findViewById(R.id.tvShortCutTitle);
    	final TextView txtNewsTitle = (TextView) findViewById(R.id.edTxtHead);

    	domain = server.replace("http://","");
    	domain = domain.replace("/workplace", "");

    	//lblDomain.setText(domain);
    	lblTitle.setText(lblTitle.getText() + "   -  " + domain + "   ( " + uid + " )");
    	txtNewsTitle.setText("From " + uid + "'s android");
    }

    public void addListenerOnButtonNews()
    {
    	final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

       	final Button btnGPS = (Button) findViewById(R.id.btnGPS);
       	final TextView txtLoc = (TextView) findViewById(R.id.tvGPSLoc);

       	ImageView NewsPic = (ImageView) findViewById(R.id.imvNewsPic);
	   	Button Save = (Button) findViewById(R.id.btnSave);
	   	Button Cancel = (Button) findViewById(R.id.btnCancel);

	   	NewsPic.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				AlertDialogPic();
			}
		});

	   	Cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});

	   	Save.setOnClickListener(new OnClickListener(){
    		public void onClick(View arg0) {

				//save to internal db
    			AlertDialogMain();
    			//SavetoDb();
			}
    	});

    	btnGPS.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				checkGPSStatus();
                getLocation();
			}
		});
	}

	private void checkGPSStatus() {
		LocationManager locationManager = null;
		boolean gps_enabled = false;
		boolean network_enabled = false;
		if ( locationManager == null ) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		try {
			gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex){}
		try {
			network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex){}
		if ( !gps_enabled && !network_enabled ){
			AlertDialog.Builder dialog = new AlertDialog.Builder(AddNewswithdb.this);
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

	public void getLocation(){

		final String[] logLat = {""};
		final TextView txtLoc = (TextView) findViewById(R.id.tvGPSLoc);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]
							{Manifest.permission.ACCESS_FINE_LOCATION},
					REQUEST_LOCATION_PERMISSION);
		}
		locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				double latitude = location.getLatitude();
				double longitude = location.getLongitude();


// if (latitude != 0) {
// Button auth = (Button) findViewById(R.id.btnAuthorized);
//
// auth.setText("Authorized Location");
// auth.setBackgroundColor(getResources().getColor(R.color.colorGreen));

                if (longitude != 0){
                    txtLoc.setVisibility(View.VISIBLE);
                }
// }


				logLat[0] = "(" + String.valueOf(latitude) + "," + String.valueOf(longitude) + ")";

//get the location name from latitude and longitude
				Geocoder geocoder = new Geocoder(getApplicationContext());
				try {
					List<Address> addresses =
							geocoder.getFromLocation(latitude, longitude, 1);
					String result = addresses.get(0).getLocality() + ":";
					result += addresses.get(0).getCountryName();
					LatLng latLng = new LatLng(latitude, longitude);

					StringBuilder sb = new StringBuilder();
					if (addresses.size() > 0) {
						Address address = addresses.get(0);
						for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
							sb.append(address.getAddressLine(i)).append(" ");
						sb.append(address.getLocality()).append(" ");
						sb.append(address.getPostalCode()).append(" ");
						sb.append(address.getCountryName());

					}
					txtLoc.setText(sb + "" +  logLat[0]);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {

			}

			@Override
			public void onProviderDisabled(String provider) {

			}
		};
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}


    public void address()
    {
    	int space = GPS.indexOf(",");
		double latitude=Double.parseDouble(GPS.substring(0,space));
		double longitude=Double.parseDouble(GPS.substring(space+1));

		/*
		DecimalFormat df = new DecimalFormat("0.0000");
		df.setRoundingMode(RoundingMode.HALF_UP);

		latitude = Double.parseDouble(df.format(latitude));
		longitude = Double.parseDouble(df.format(longitude));
		*/

		//EditText txtInfo = (EditText) findViewById(R.id.edMultiDescp);
		TextView txtLoc = (TextView) findViewById(R.id.tvGPSLoc);

		//String sCountry;
		//String sGPS;
		String sStreet;
		//String sPostal;
		String sState;
		String sArea;
		String sLocation;

    	//sCountry="";
    	sStreet="";
    	//sPostal="";
    	sState="";
    	sArea="";
    	sLocation = "";

		Geocoder gCoder = new Geocoder(this);
		List<Address> addresses = null;
		try {
			addresses = gCoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (addresses != null && addresses.size() > 0) {
		    //Toast.makeText(this, "country: " + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
		    Log.d("Address","country: " + addresses.get(0).toString());

		    //sCountry=addresses.get(0).getCountryName();
		    sState=addresses.get(0).getAdminArea();

		    //sLocation = sCountry.toUpperCase();

		    //if (sLocation != null && sLocation.length() > 0)
		    //{
			//    if (sState != null && sState.length() > 0)
			//    	sLocation = sState + ", " + sLocation;
		    //}
		    //else
		    //{
			    if (sState != null && sState.length() > 0)
			    	sLocation = sState.toUpperCase();
		    //}
		}

		if (addresses != null && addresses.size() > 0) {
			sArea=addresses.get(0).getLocality();

		    if (sLocation != null && sLocation.length() > 0)
		    {
			    if (sArea != null && sArea.length() > 0)
			    	sLocation = sArea + ", " + sLocation;
		    }
		    else
		    {
			    if (sArea != null && sArea.length() > 0)
			    	sLocation = sArea;
		    }
		}

		if (addresses != null && addresses.size() > 0) {
			sStreet=addresses.get(0).getFeatureName();
			//sPostal=addresses.get(0).getPostalCode();

		    if (sLocation != null && sLocation.length() > 0)
		    {
			    if (sStreet != null && sStreet.length() > 0)
			    	sLocation = sStreet + ", " + sLocation;
		    }
		    else
		    {
			    if (sStreet != null && sStreet.length() > 0)
			    	sLocation = sStreet;
		    }
		}

		//txtInfo.setText(txtInfo.getText().toString() + "\n\nLocation: \n" + latitude + ", " + longitude + "\n" + sStreet + ", " + sArea + ", " + sState + ", " + sCountry);

		Button btnGPS = (Button) findViewById(R.id.btnGPS);

		if (sLocation != null && sLocation.length() > 0)
		{
			sLocation = "\nLocation:\t\t" + sLocation + ".";
			txtLoc.setText("Coordinate:\t" + latitude + ", " + longitude + sLocation + "\n");
			txtLoc.setVisibility(View.VISIBLE);
			btnGPS.setText(getResources().getString(R.string.btn_label_hidelocation));
		}
		else
		{
			txtLoc.setText("");
			txtLoc.setVisibility(View.GONE);
			btnGPS.setText(getResources().getString(R.string.btn_label_showlocation));
		}
		//txtInfo.setText(txtInfo.getText().toString() + "\n\nCoordinate:\t" + latitude + ", " + longitude + sLocation);

    }

    public void AlertDialogGPS()
    {
    	final CharSequence[] items = {"OK"};
    			android.app.AlertDialog.Builder builder = new
    			android.app.AlertDialog.Builder(this);
    			builder.setTitle("Failed while get GPS location");
    			builder.setItems(items, new

    			DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog,
	    			int item) {

	    				if(item==0)
	    				{
	    					//
	    				}
	    				else if(item==1)
	    				{
	    					//
	    				}
	    			}
    			});

    			android.app.AlertDialog alerts = builder.create();
    			alerts.show();
    }

    public void AlertDialogMain()
    {
    	final CharSequence[] items = {"Yes", "No","Cancel"};
    			android.app.AlertDialog.Builder builder = new
    			android.app.AlertDialog.Builder(this);
    			builder.setTitle("Save this news?");
    			builder.setItems(items, new

    			DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog,
	    			int item) {

	    				if(item==0)
	    				{
	    					Log.i("AlertDialogMain", "SavetoDb");
	    					SavetoDb();
	    				}
	    				else if(item==1)
	    				{
	    					callclass("main");
	    				}
	    			}
    			});

    			android.app.AlertDialog alerts = builder.create();
    			alerts.show();
    }

    public void callclass(String r)
    {
    	if (r=="list")
    	{
    		Intent intent = new Intent(this, dbList.class);
            startActivity(intent);
            finish();
    	}
    	else if (r=="main")
    	{
	    	Intent intent = new Intent(this, MainActivity.class);
	        startActivity(intent);
	        finish();
    	}
    	else if (r=="view")
    	{
	    	Intent intent = new Intent(this, ViewInserted.class);
	    	intent.putExtra("sect", sect);
	        startActivity(intent);
	        finish();
    	}
    	else if (r==("load"))
    	{
    		finish();
	    	Intent intent = new Intent(this, AddNewswithdb.class);
	    	intent.putExtra("GPS", GPS);
	        startActivity(intent);
    	}
    }

    public void AlertDialogPic()
    {
    	CharSequence[] items = null;
    	String title = null;

    	title = "Select photo";

		items = new CharSequence[2];
		items[0] = "Album";
		items[1] = "Take photo";

		android.app.AlertDialog.Builder builder = new
		android.app.AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setItems(items, new

		DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,
			int item) {

				if(item==0)
				{
					 Intent intent = new Intent();
					 intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					 startActivityForResult(intent, SELECT_FILE);
				}
				else if (item==1)
				{
					//Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					//startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

					//----------------------------------------------------------------------------
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
					String currDT = sdf.format(new Date());

					Intent CamImageIntent = new Intent("android.media.action.IMAGE_CAPTURE");
					File cameraFolder;

					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
						cameraFolder = new File(Environment.getExternalStorageDirectory(),"DCIM/Camera");
					else
						cameraFolder = getCacheDir();
					
					if (!cameraFolder.exists())
						cameraFolder.mkdirs();
					
					File photo = new File(Environment.getExternalStorageDirectory(),"imgNews_" + currDT.toString() + ".jpg");
					CamImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
					targetURI = Uri.fromFile(photo);
					
					startActivityForResult(CamImageIntent, CAMERA_PIC_REQUEST);
					//-----------------------------------------------------------------------------
				}
				else
					callclass("main");
			}
		});
	
		android.app.AlertDialog alerts = builder.create();
		alerts.show();
    }
    
    private Uri mImageCaptureUri;
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
    	try{
        	
			if (resultCode != RESULT_CANCELED)
			{
		        if (requestCode == CAMERA_PIC_REQUEST) 
		        {  	        	
		        	Bitmap bitmap   = null;
		        	String path     = "";
		        	
		            path = getRealPathFromURI(targetURI); //from Gallery
		 
		            if (path == null)
		                path = targetURI.getPath(); //from File Manager
		 
		            if (path != null)
		            {
		            	imgPath = path;
		            	
		            	BitmapFactory.Options opt = new BitmapFactory.Options();
		            	opt.inSampleSize = 6;
		                bitmap  = BitmapFactory.decodeFile(path,opt);
		                
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
	            	/* ---------------------------- */
	            	
	                //populate image
		            ImageView myImage = (ImageView) findViewById(R.id.imvNewsPic);
		        	
		            //ByteArrayOutputStream stream = new ByteArrayOutputStream();
		            
		        	if (rotateValue != 0)
		        	{
			            Matrix m = new Matrix();
			        	
			        	m.postRotate(rotateValue);
			        	bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
		        	}
		        	
		            myImage.setImageBitmap(bitmap);
		            myImage.setVisibility(View.VISIBLE);
		            	            
		        }  
		        else if (requestCode == SELECT_FILE) 
		        {

		        	Bitmap bitmap   = null;
		        	String path     = "";
		        	
	        		mImageCaptureUri = intent.getData();
		            		            
		            path = getRealPathFromURI(mImageCaptureUri); //from Gallery

					if (path == null)
						path = mImageCaptureUri.getPath(); //from File Manager

					if (path != null)
					{
						imgPath = path;

						BitmapFactory.Options opt = new BitmapFactory.Options();
						opt.inSampleSize = 6;
						bitmap  = BitmapFactory.decodeFile(path,opt);
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
					/* ---------------------------- */

					//populate image
					ImageView myImage = (ImageView) findViewById(R.id.imvNewsPic);

					if (rotateValue != 0)
					{
						Matrix m = new Matrix();

						m.postRotate(rotateValue);
						bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
					}

					myImage.setImageBitmap(bitmap);
					myImage.setVisibility(View.VISIBLE);
		            		 

		           
		        }	// end else if (requestCode == SELECT_FILE) 
		            
        	}	// end if (resultCode != RESULT_CANCELED)
    	}
    	catch(Exception e)
    	{
    		Log.e("Error get image",e.getMessage().toString());
    		Toast.makeText(
                    getApplicationContext(),
                    "Exception ~ onActivityResult() :\n" + e.getClass().getName() + " \n" + e.getMessage(),
                    Toast.LENGTH_LONG).show();
    	}
    	
    }
    
    @SuppressWarnings("deprecation")
	public String getRealPathFromURI(Uri contentUri) {
        String[] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = managedQuery( contentUri, proj, null, null,null);
 
        if (cursor == null) return null;
 
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
 
        cursor.moveToFirst();
 
        return cursor.getString(column_index);
    }
    
    public void SavetoDb(){
    	
    	Timer_Delay(2000);
    	   	
    	primary = dba.checkDB(dbHelper);
    	
    	EditText txtHead = (EditText) findViewById(R.id.edTxtHead);
    	EditText txtInfo = (EditText) findViewById(R.id.edMultiDescp);
    	TextView txtLoc = (TextView) findViewById(R.id.tvGPSLoc);
    	
    	//String memberid = getResources().getString(R.string.uid);
    	//String Author = getResources().getString(R.string.uname);
    	//String AuthorMail = getResources().getString(R.string.email);
    	String NType = sect;
    	String Title = null;
    	String Info = null;
    	String Location = null;
    	
    	boolean pass1 = false;
    	boolean pass2 = false;
    	
    	if (txtHead.getText().toString().length() == 0)
    	{
    		Toast.makeText(this, "Please enter News Title.", Toast.LENGTH_SHORT).show();
    		txtHead.setFocusable(true);
    	}
    	else
    	{
    		Title = txtHead.getText().toString();
    		pass1 = true;
    	}
    	
/*   	
    	if (txtInfo.getText().toString().length() == 0)
    	{
    		Toast.makeText(this, "Please enter News Description.", Toast.LENGTH_SHORT).show();
    		txtInfo.setFocusable(true);
    	}
    	else
    	{  */  		
    		Info = txtInfo.getText().toString();
    		
    		if ((txtLoc.getText().toString().length() > 0) && (txtLoc.getText().toString() != "Small Text"))
    			Info = Info + "\n\n" + txtLoc.getText().toString();
    		
    		pass2 = true;
/*    	}
*/
    	if (pass1 == true && pass2 == true)
    	{
    		Log.i("SavetoDb", "SaveDB(dbHelper, "+primary+", "+Title+", "+Info+", "+NType+", byteArray, "+Location+", "+domain+", "+imgPath+")");
	    	//dba.SaveDB(dbHelper, primary+1, Title, Info, NType, byteArray, Location, domain, imgPath);
	    	dba.SaveDB(dbHelper, primary+1, Title, Info, NType, null, Location, domain, imgPath);

	    	Log.i("SavetoDb", "1");
	    	
	    	Toast.makeText(this, "News saved successfully.", Toast.LENGTH_SHORT).show();
	    	
	    	Log.i("SavetoDb", "2");

			callclass("list");
			callclass("load");
			
			Log.i("SavetoDb", "3");
    	}
    	
    	//AlertDialogMain();
    	
    }
    
    public void Timer_Delay(int millisec)
    {    	
    	TimerTask tskSaveToDb;
    	Timer tmr = new Timer();
    	
    	tskSaveToDb = new TimerTask() {
        public void run() {

        	delay();
        }};
        
        tmr.schedule(tskSaveToDb, millisec);
    }
    
    public void delay()
    {
    	for (int i=0; i<100; i++)
        {
        	for (int j=0; j<100; j++)
            {
            	i = i + 0;
            	j = j + 1;
            }
        }
    }

    //---------

	public void AlertDialogR(final int i)
	{
		final CharSequence[] items = {"Yes", "No"};
		android.app.AlertDialog.Builder builder = new
				android.app.AlertDialog.Builder(this);
		builder.setTitle("Remove this news?");
		builder.setItems(items, new

				DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
										int item) {

						if(item==0)
						{
							//delete from database
							dba.deletebyId(i);
							callclass();
						}
						else
						{
							//do nothing
						}
					}
				});

		android.app.AlertDialog alerts = builder.create();
		alerts.show();
	}

	public void callclass()
	{
		Intent intent = new Intent(this, AddNewswithdb.class);
		startActivity(intent);
		finish();
	}


	public void generateButton()
	{
		/*
		----------------------------------------------------
		1. get the screen size
		2. create the header
		3. generate button (Based on the icon name inside the database)
		4. create OnClickListener for each button
		5. when button clicked go to the register page
		----------------------------------------------------
		*/

		LinearLayout lin = (LinearLayout)findViewById(R.id.tvList);
		//1.
		int txtSize = 0;
		int TxtPdg = 0;
		int LayHigh = 0;
		int LayPdg = 0;
		int imgSize = 0;

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		if (dm.heightPixels <= 800)
		{
			txtSize = 13;
			TxtPdg = 15;
			LayHigh = 150;
			LayPdg = 10;
			imgSize = 150;
		}
		else if (dm.heightPixels <= 1024 && dm.heightPixels > 800)
		{
			txtSize = 13;
			TxtPdg = 20;
			LayHigh = 250;
			LayPdg = 10;
			imgSize = 250;
		}
		else if (dm.heightPixels <= 1280 && dm.heightPixels > 1024)
		{
			txtSize = 13;
			TxtPdg = 20;
			LayHigh = 250;
			LayPdg = 10;
			imgSize = 250;

		}
		else if (dm.heightPixels >= 2009)
		{
			txtSize = 14;
			TxtPdg = 25;
			LayHigh = 500;
			LayPdg = 20;
			imgSize = 500;
		}

		//Toast.makeText(this,"Pixel : " + dm.heightPixels,Toast.LENGTH_SHORT).show();

		//2.
		/*
		int resId2 = getResources().getIdentifier("head", "drawable", getPackageName());

		TextView txt1 = new TextView(this);
		txt1.setText(getResources().getText(R.string.label_newslist));
		txt1.setGravity(Gravity.CENTER_VERTICAL);
		txt1.setTextSize(20);
		txt1.setPadding(TxtPdg, 0, 0, 0);
		txt1.setTextColor(Color.WHITE);
		*/

		int addId = getResources().getIdentifier("add", "drawable", getPackageName());
		ImageView add = new ImageView(this);
		add.setBackgroundResource(addId);

		LinearLayout rl = (LinearLayout) findViewById(R.id.linearLayoutDB);
		//LinearLayout r3 = (LinearLayout) findViewById(R.id.linearLayout2);
		/*
		LinearLayout  rl2 = new LinearLayout (this);
		rl2.setOrientation(LinearLayout.HORIZONTAL);
		rl2.setBackgroundResource(resId2);
		rl2.setPadding(0, 0, 0, 10);

		rl2.addView(txt1,LayoutParams.WRAP_CONTENT,90);*/
		//r3.addView(rl2,LayoutParams.FILL_PARENT,90);

		//3.
		int iDBCount = 0;
		DecimalFormat df = new DecimalFormat("0.0");
		df.setRoundingMode(RoundingMode.FLOOR);

		for (int i=primary;i>0;i--)
		{
			final int j = i;
			//byte[] pic1 = dba.GetBlob(i, 5);
			Bitmap bitmap = null;
			button = "Upload";
			String strSize = "";

			String sDom = dba.getResult(dbHelper, "domain", "_ID ='"+i+"'");

			if (domain.equals(sDom) == true)
			{
				iDBCount = iDBCount + 1;

				String id = dba.getResult(dbHelper, "_ID", "_ID ='"+i+"'");
				String NewsID = dba.getResult(dbHelper, "NewsID", "_ID ='"+i+"'");
				String path = dba.getResult(dbHelper, "imgLoc", "_ID='"+i+"'");
				String strNewsID;

				if (NewsID != null)
				{
					button = "Update";
				}
				else
				{
					NewsID = "";
				}

				if (id!= null)
				{
					if (path != null)	//if (pic1 != null)
					{
						//bitmap = BitmapFactory.decodeByteArray(pic1 , 0, pic1.length);

						try
						{
							double iSize;
							//ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							BitmapFactory.Options opt = new BitmapFactory.Options();

							/* -- Check whether to rotate image -- */
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
							/* ----------------------------------- */

							opt.inSampleSize = 2;
							bitmap  = BitmapFactory.decodeFile(path,opt);

							if (rotateValue != 0)
							{
								Matrix m = new Matrix();

								m.postRotate(rotateValue);
								bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
							}

							/* ================== Read Bitmap Size, after compress ================== */
							if (bitmap.getHeight() > bitmap.getWidth())
							{
								if (bitmap.getHeight() > 3264)
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
									bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
							}
							else
							{
								if (bitmap.getWidth() > 3264)
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
									bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
							}

							iSize = stream.size();
							iSize = iSize / 1024;

							if (iSize > 1024)
							{
								iSize = iSize / 1024;
								strSize = strSize + "** Compressed Image Size : " + df.format(iSize) + " MB\n\n";
							}
							else
								strSize = strSize + "** Compressed Image Size : " + df.format(iSize) + " KB\n\n";


							//pic1 = stream.toByteArray();
							stream.flush();
							stream.close();

							opt.inSampleSize = 4;
							bitmap  = BitmapFactory.decodeFile(path,opt);

							if (rotateValue != 0)
							{
								Matrix n = new Matrix();

								n.postRotate(rotateValue);
								bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),n,true);
							}

							/* ================================================================================= */

						}
						catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					final String iconTxt = dba.getResult(dbHelper, "Title", "_ID='"+(i)+"'");

					String[] result = dba.getResultArray(dbHelper, "inserted data", "_ID = '"+(i)+"' ");

					String txtsect = "";
					String txtTittle = "";
					String txtDesc = "";


					txtsect = result[4];
					txtTittle = result[2];
					txtDesc = result[3];


					id = " ("+id+")";

					if (NewsID != "")
						strNewsID = "(" + NewsID + ")\n";
					else
						strNewsID = "";

					ImageView icon = new ImageView(this);
					icon.setImageBitmap(bitmap);

					TextView label = new TextView(this);
					//label.setText(strSize + id + "\n" + iconTxt);
					//label.setText(strSize + strNewsID + iconTxt);
					label.setText("Desc : "+ iconTxt + "\n" + txtDesc);
					//label.setGravity(Gravity.CENTER_VERTICAL);
					label.setGravity(Gravity.TOP);
					label.setTextSize(txtSize);
					label.setPadding(TxtPdg, 0, 0, 0);
					label.setTextColor(Color.BLACK);
					label.setLines(5);
					label.setMovementMethod(new ScrollingMovementMethod());
					label.setId(1);

					int lineId = getResources().getIdentifier("cback", "drawable", getPackageName());
					ImageView line = new ImageView(this);
					line.setBackgroundResource(lineId);

					LinearLayout LayIcon = new LinearLayout(this);
					LayIcon.setOrientation(LinearLayout.HORIZONTAL);

					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayHigh);
					layoutParams.setMargins(LayPdg, LayPdg, LayPdg, LayPdg);

					LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					layoutParams2.setMargins(LayPdg, LayPdg, LayPdg, LayPdg);

					RelativeLayout LayButton = new RelativeLayout(this);

					RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayHigh);
					layoutParams3.setMargins(LayPdg, LayPdg, LayPdg, LayPdg);
					layoutParams3.addRule(RelativeLayout.RIGHT_OF,label.getId());

					Button upload = new Button(this);

					upload.setBackgroundColor(Color.parseColor("#34495e"));
					upload.setTextColor(Color.WHITE);
					upload.setText(button);
					upload.setId(2);

					/*if (button.equals("Upload"))
					{
						upload.setHeight(30);
						upload.setWidth(130);
						upload.setBackgroundResource(R.drawable.btn_blue);
					}*/
					//else
					//	upload.setBackgroundResource(R.drawable.btn_green);

					Button remove = new Button(this);

					remove.setBackgroundColor(Color.parseColor("#e74c3c"));
					remove.setTextColor(Color.WHITE);
					remove.setText("Remove");
					remove.setId(3);



					if (dm.heightPixels >= 2009)
					{
						RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(300, 200);
						layoutParams4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						layoutParams4.addRule(RelativeLayout.BELOW,upload.getId());

						RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(300,200);
						layoutParams5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

						RelativeLayout.LayoutParams layoutParams6 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
						layoutParams6.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
						layoutParams6.addRule(RelativeLayout.CENTER_VERTICAL);
						layoutParams6.addRule(RelativeLayout.LEFT_OF,upload.getId());

						LayButton.addView(label,layoutParams6);
						LayButton.addView(remove,layoutParams4);
						LayButton.addView(upload,layoutParams5);
						LayIcon.addView(icon,imgSize,imgSize);
						//LayIcon.addView();
						LayIcon.addView(LayButton,layoutParams3);

						rl.addView(LayIcon,layoutParams);
						rl.addView(line,layoutParams2);

					}
					else
					{

						RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(200, 70);
						layoutParams4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						layoutParams4.addRule(RelativeLayout.BELOW,upload.getId());
						RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(200, 70);
						layoutParams5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

						RelativeLayout.LayoutParams layoutParams6 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
						layoutParams6.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
						layoutParams6.addRule(RelativeLayout.CENTER_VERTICAL);
						layoutParams6.addRule(RelativeLayout.LEFT_OF,upload.getId());

						LayButton.addView(label,layoutParams6);
						LayButton.addView(remove,layoutParams4);
						LayButton.addView(upload,layoutParams5);
						LayIcon.addView(icon,imgSize,imgSize);
						//LayIcon.addView();
						LayIcon.addView(LayButton,layoutParams3);

						rl.addView(LayIcon,layoutParams);
						rl.addView(line,layoutParams2);

					}
					//layoutParams5.addRule(RelativeLayout.LEFT_OF,remove.getId());


					//4.
					final String b = button;
					final String newsid = NewsID;
					final Intent intent = new Intent(this, ViewInserted.class);
					upload.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							//upload to server
							if (newsid.length() != 0)
							{
								intent.putExtra("primary", j);
								startActivity(intent);
								finish();
							} else{
								AlertDialogU(j,b);}
						}
					});

					final Intent intent1 = new Intent(this, ViewTwo.class);
					label.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							intent1.putExtra("primary", j);
							startActivity(intent1);
							finish();
						}
					});
					LayIcon.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							intent1.putExtra("primary", j);
							startActivity(intent1);
						}
					});
					/*
					final Intent intent11 = new Intent(this, Viewpic.class);
					LayIcon.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							intent11.putExtra("primary", j);
							startActivity(intent11);
						}
					});
					*/

					remove.setOnClickListener(new OnClickListener() {
						public void onClick(View arg0) {
							//pop up alert confirmation to delete
							AlertDialogR(j);
							primary=dba.checkDB(dbHelper);
							//if yes, remove from internal database
						}
					});

				}
			}

		}

		if (iDBCount == 0)
			lin.setVisibility(View.INVISIBLE);
			//Toast.makeText(this,"No record available.",Toast.LENGTH_SHORT).show();
	}

	public void AlertDialogU(final int i,final String button)
	{
		final CharSequence[] items = {"Yes", "No"};
		android.app.AlertDialog.Builder builder = new
				android.app.AlertDialog.Builder(this);
		builder.setTitle("Upload this news to server?");
		builder.setItems(items, new

				DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
										int item) {

						if(item==0)
						{
							uploadtoserver(i, button);
							//xmlParse(null);
						}
						else
						{
							//do nothing
						}
					}
				});

		android.app.AlertDialog alerts = builder.create();
		alerts.show();
	}

	public void uploadtoserver(int primary,String button)
	{
		try
		{
			String dothis;

			String[] result = dba.getResultArray(dbHelper, "*", "_ID='"+primary+"'");
			String[] config = dba.getResultConfig(dbHelper, "*", "active='1'");
			String NewsID = dba.getResult(dbHelper, "NewsID", "_ID ='"+primary+"'");
			String imgPath = dba.getResult(dbHelper, "imgLoc", "_ID='"+primary+"'");

			if (NewsID == null)
				NewsID = "";

			String usr = config[1];
			String pass = config[2];
			String url = config[3] + getResources().getString(R.string.upload_news) ;
			String Title = result[2].toString().replace("'", "''");
			String Info = result[3].toString().replace("'", "''");
			sect = result[4];
			String SynCA = result[6];

			Info = Info.replace("\t", "&emsp;");
			Info = Info.replace("\n", "<br>");

			byte[] pic1 = null;
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			if (imgPath != null)
			{
				Bitmap bitmap = null;

				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inSampleSize = 2;
				bitmap  = BitmapFactory.decodeFile(imgPath,opt);

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

				if (rotateValue != 0)
				{
					Matrix m = new Matrix();

					m.postRotate(rotateValue);
					bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
				}

				if (bitmap.getHeight() > bitmap.getWidth())
				{
					if (bitmap.getHeight() > 3264)
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
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				}
				else
				{
					if (bitmap.getWidth() > 3264)
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
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				}

				pic1 = stream.toByteArray();
				stream.flush();
				stream.close();
			}

			//pic1 = dba.GetBlob(primary, 5);

			if (button=="Upload")
				dothis = "insert";
			else
				dothis = "update";

			//upload data to server
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add((NameValuePair) new BasicNameValuePair("username", usr));
			nameValuePairs.add((NameValuePair) new BasicNameValuePair("password", pass));
			nameValuePairs.add((NameValuePair) new BasicNameValuePair("NewsID", NewsID));
			nameValuePairs.add((NameValuePair) new BasicNameValuePair("Ntype", "management news"));
			nameValuePairs.add((NameValuePair) new BasicNameValuePair("Title", Title));
			nameValuePairs.add((NameValuePair) new BasicNameValuePair("Info", Info));
			nameValuePairs.add((NameValuePair) new BasicNameValuePair("image1", ""));
			nameValuePairs.add((NameValuePair) new BasicNameValuePair("SynCA", usr));
			nameValuePairs.add((NameValuePair) new BasicNameValuePair("dothis", dothis));

			Log.i("uploadtoserver","before post");

			Log.i("uploadtoserver - imgPath"," - " + imgPath.toString());
			showToast(pass);


			post(url, nameValuePairs,pic1,primary);

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void showToast(String strMsg)
	{
		Toast.makeText(this, strMsg, Toast.LENGTH_LONG).show();
	}

	public void post(String url, List<NameValuePair> nameValuePairs, byte[] pic1, int primary) {

		StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
		StrictMode.setThreadPolicy(tp);

		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(url);
		ByteArrayBody image1 = null;

		if (pic1 != null)
		{
			image1 = new ByteArrayBody(pic1, "imgNews");
		}

		try {

			MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			for(int index=0; index < nameValuePairs.size(); index++) {
				if(image1 != null && nameValuePairs.get(index).getName().equalsIgnoreCase("image1")) {
					entity.addPart(nameValuePairs.get(index).getName(), image1);
					Log.i(nameValuePairs.get(index).getName(), "image1");
				}
				else if(nameValuePairs.get(index).getValue() != null){
					entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
					Log.i(nameValuePairs.get(index).getName(), nameValuePairs.get(index).getValue());
				}
			}

			httpPost.setEntity(entity);
			Log.i("getRequestLine()" , httpPost.getRequestLine().toString());

			HttpResponse response = httpClient.execute(httpPost, localContext);

			HttpEntity resEntity = response.getEntity();

			Log.i("getStatusLine()",response.getStatusLine().toString());

			if (resEntity != null) {
				String res =  EntityUtils.toString(resEntity);
				//String newRes = res.replace("<meta http-equiv=Content-Type content=text/html; charset=iso-8859-1>\n", "");

				Log.i("post : res",res);
				//Log.i("post : newRes",newRes);

				//Toast.makeText(this,"res : \n" + res,Toast.LENGTH_LONG).show();
				//Toast.makeText(this,"newRes : \n" + newRes,Toast.LENGTH_LONG).show();

				xmlParse(res,primary);

			}
			if (resEntity != null) {
				resEntity.consumeContent();
			}

			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			//Toast.makeText(this, e.getMessage().toString() + " upload error. \nPlease check your internet connection.", Toast.LENGTH_LONG).show();
			Log.e("post() ~ Exception",e.getClass().getName() + " : " + e.getMessage());
	            /*Toast.makeText(
	                    getApplicationContext(),
	                    "post() ~ Exception \n" + e.getClass().getName() + " \n" + e.getMessage(),
	                    Toast.LENGTH_LONG).show();*/
			Toast.makeText(getApplicationContext(), "Failure connecting to '" + e.getMessage() + "'. \nPlease check your internet connection.", Toast.LENGTH_LONG).show();
		}
	}

	public void xmlParse(String result, int primary)
	{
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();

			xpp.setInput( new StringReader(result) );

			int eventType = xpp.getEventType();

			//String data[] = new String [10] ;
			String dat = "";
			String info = "";
			String id = "";
			while (eventType != XmlPullParser.END_DOCUMENT)
			{

				if(eventType == XmlPullParser.START_DOCUMENT)
				{
					Log.i("Start document", "start");
				}
				else if(eventType == XmlPullParser.START_TAG)
				{
					Log.i("Start tag ",xpp.getName());
					if (xpp.getName().equals("MSG"))
					{
						id = xpp.getName();
					}
				}
				else if(eventType == XmlPullParser.END_TAG)
				{
					Log.i("End tag ",xpp.getName());
				}
				else if(eventType == XmlPullParser.TEXT)
				{
					if (id.equals("MSG"))
					{
						info = "Error inserting data, "+xpp.getText();
					}
					else
						dat = xpp.getText();
				}

				eventType = xpp.next();
			}
			Log.i("End document", "end");
			if ( dat != "" && dat != null)
			{
				//update db insert property id
				dba.updateDB(dat, primary);
				Toast.makeText(this, "Insert successful. NewsID = " + dat, Toast.LENGTH_LONG).show();
				callclass();
			}
			else
				Toast.makeText(this, "Error inserting data: \n" + info, Toast.LENGTH_LONG).show();
			Log.e("xmlParse() ~ Error inserting data", info);
		}
		catch (Exception e1)
		{
			Log.e("xmlParse() ~ Exception", e1.getClass().getName() + " : " + e1.getMessage(), e1);

			Toast.makeText(
					getApplicationContext(),
					"xmlParse() ~ Exception \n" + e1.getClass().getName() + " \n" + e1.getMessage(),
					Toast.LENGTH_LONG).show();
		}

	}



}
