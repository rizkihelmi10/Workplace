package com.smartlab.wpslidingmenu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.core.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
public class AddNews extends Activity {
	private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int SELECT_FILE = 1;

	private GoogleMap mMap;
	LocationManager locationManager;
	private static final int REQUEST_LOCATION_PERMISSION = 1;
	Marker marker;
	LocationListener locationListener;
	
	Uri targetURI;
	
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
        
        setContentView(R.layout.addnews);
        
        dba = new dbActivities();
        dbHelper = new MySQLiteHelper(this);
        
       // sect = getIntent().getExtras().getString("sect");
        TextView txtSectVal = (TextView)findViewById(R.id.tvSectVal);
        txtSectVal.setText(sect);

		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
        
        getCurrentDomain();
        
        addListenerOnButtonNews();
        
       // adjustResolution();

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this,
				android.Manifest.permission.ACCESS_FINE_LOCATION)
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

                getLocation();
			}
		});


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
    	/*else if (r==("map"))
    	{
	    	Intent intent = new Intent(this, Map.class); 
	    	intent.putExtra("GPS", GPS);
	        startActivity(intent);
    	}*/
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
					 intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

					if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
						cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(),"mschool/camera");
					else
						cameraFolder = getCacheDir();
					
					if (!cameraFolder.exists())
						cameraFolder.mkdirs();
					
					File photo = new File(Environment.getExternalStorageDirectory(),"mschool/camera/imgNews_" + currDT.toString() + ".jpg");
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


}
