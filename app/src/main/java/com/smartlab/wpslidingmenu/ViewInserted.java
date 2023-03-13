package com.smartlab.wpslidingmenu;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import java.io.IOException;
//import java.text.DecimalFormat;
//import android.content.Context;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.LocationManager;
//import android.provider.Browser;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.MotionEvent;
//import android.view.View.OnTouchListener;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.CheckBox;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.Spinner;

//import android.widget.AdapterView.OnItemSelectedListener;
public class ViewInserted extends Activity {

	MySQLiteHelper dbHelper; 
    SQLiteDatabase db;
    dbActivities dba;

    byte[] byteArray;
    String GPS;
    String sect = null;
    int primary;
    int g=0;
    private static final int CAMERA_PIC_REQUEST = 1337;
    private static final int SELECT_FILE = 1;
    
    Uri targetURI;
    
    int size = 0;
    String domain;
    String imgPath;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.view);
        
        dba = new dbActivities();
        dbHelper = new MySQLiteHelper(this);

        getCurrentDomain();
        viewInsertedData();
        addListenerOnButton();
    }
    
    private void getCurrentDomain()
    {    	    	    	
    	final String server = dba.getConfigValue(dbHelper,"server","active='1'");
    	final String uid = dba.getConfigValue(dbHelper,"username","active='1'");
    	final TextView tvEditNews = (TextView) findViewById(R.id.tvEditNews);
    	
    	domain = server.replace("http://","");		
    	domain = domain.replace("/workplace", "");
		
    	tvEditNews.setText(tvEditNews.getText() + "   -  " + domain + "   ( " + uid + " )");
    }

    public void addListenerOnButton() {
    	
    	//final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	Button save = (Button) findViewById(R.id.btnSave);
    	Button update = (Button) findViewById(R.id.btnUpdate);
    	Button Cancel = (Button) findViewById(R.id.btnCancel);
    	//final Button btnGPS = (Button) findViewById(R.id.btnGPS);
    	//Button btnMap = (Button) findViewById(R.id.btnMap);
    	
    	Cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				callclass("main");
			}
		});
    	/*
    	btnGPS.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//Get The GPS location
    			
    			GPSLocation gps = new GPSLocation();
    			GPS = gps.getlocation(locationManager);
    			if (GPS != null)
    			{
    				View a = findViewById(R.id.btnMap);
    				a.setVisibility(View.VISIBLE);
    			}
				address();
			}
		});
    	btnMap.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//Get The GPS location
				callclass("map");
			}
		});
    	*/
    	ImageView img2 = (ImageView) findViewById(R.id.imvNews2);
    	img2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				AlertDialog();								
			}
		});
    	save.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//update news
				AlertDialogU(primary);
			}
		});
    	update.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//upload to server
				AlertDialogUpdate();
			}
		});
    	
    	/*
    	final EditText edSize = (EditText) findViewById(R.id.edSize);
    	final EditText edSizeM = (EditText) findViewById(R.id.edSizeM);
    	edSize.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				size = 1;
				edSize.addTextChangedListener(new TextWatcher(){
					
    	            public void afterTextChanged(Editable s) {
    	            	if (size == 1)
    					{
	    	            	if (!edSize.getText().toString().equals(null)&&!edSize.getText().toString().equals(""))
	    	            	{
	    	            		DecimalFormat twoDForm = new DecimalFormat("#.00");
	    	            		double saiz = Double.parseDouble(edSize.getText().toString())*0.09290304;
	    	            		edSizeM.setText(Double.valueOf(twoDForm.format(saiz))+"");
	    	            	}
	    	            	else
	    	            		edSizeM.setText("");
    	            }}
    	            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
    	            public void onTextChanged(CharSequence s, int start, int before, int count){}
    	        }); 
				return false;
			}
    	});
    	edSizeM.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View arg0, MotionEvent arg1) {
				size = 2;
		    	edSizeM.addTextChangedListener(new TextWatcher(){
		            public void afterTextChanged(Editable s) {
		            	if (size == 2)
    					{
			            	if (!edSizeM.getText().toString().equals(null)&&!edSizeM.getText().toString().equals(""))
			            	{
			            		DecimalFormat twoDForm = new DecimalFormat("#.00");
			            		double saiz = Double.parseDouble(edSizeM.getText().toString())*10.7639104167097;
			            		
			            		edSize.setText(Double.valueOf(twoDForm.format(saiz))+"");
			            	}
			            	else
			            		edSize.setText("");
		            }}
		            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		            public void onTextChanged(CharSequence s, int start, int before, int count){}
		        });
				return false;
			}
    	});  
    	
    	*/
    }
    public void AlertDialog()
    {
    	final CharSequence[] items = {"Album", "Take photo"};
    			android.app.AlertDialog.Builder builder = new
    			android.app.AlertDialog.Builder(this);
    			builder.setTitle("Select photo");
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
	    						cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(),"WorkplaceClient/camera");
	    					else
	    						cameraFolder = getCacheDir();
	    					
	    					if (!cameraFolder.exists())
	    						cameraFolder.mkdirs();
	    					
	    					File photo = new File(Environment.getExternalStorageDirectory(),"WorkplaceClient/camera/imgNews_" + currDT.toString() + ".jpg");
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
    public void AlertDialogUpdate()
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
	    					 //update to server
	    					uploadtoserver(primary,"Update");
	    				}
	    			}
    			});
    			
    			android.app.AlertDialog alerts = builder.create();
    			alerts.show();
    }
    private Uri mImageCaptureUri;
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {    	
    	try
    	{
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
    	            ImageView myImage = (ImageView) findViewById(R.id.imvNews2);
    	            
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
    	        	ImageView myImage = (ImageView) findViewById(R.id.imvNews2);
    	        	
    	        	if (rotateValue != 0)
    	        	{
    		            Matrix m = new Matrix();
        	        	
    		        	m.postRotate(rotateValue);        	        	
    		        	bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
    	        	}
    	        	
    	        	myImage.setImageBitmap(bitmap);    	        	
    	            myImage.setVisibility(View.VISIBLE);
    		       
    	        } // end else if (requestCode == SELECT_FILE) 
    			
    		}	// end if (resultCode != RESULT_CANCELED)  		
	        
    	}
    	catch(Exception e)
    	{
    		Log.e("Error get image",e.getMessage().toString());
    		Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
    	}    	
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = managedQuery( contentUri, proj, null, null,null);
 
        if (cursor == null) return null;
 
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
 
        cursor.moveToFirst();
 
        return cursor.getString(column_index);
    }
    public void AlertDialogU(final int i)
    {
    	final CharSequence[] items = {"Yes", "No"};
    			android.app.AlertDialog.Builder builder = new
    			android.app.AlertDialog.Builder(this);
    			builder.setTitle("Update this news?");
    			builder.setItems(items, new
    					
    			DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog,
	    			int item) {
	    				
	    				if(item==0)
	    				{
	    					//update internal database
	    					updatetodb(i);
	    					//if (updatetodb(i) == true)
	    					//	callclass("view");
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
    public boolean updatetodb(int primary)
	 {
    	boolean bRetVal = false;
    	
    	TextView txtSect = (TextView) findViewById(R.id.tvSectVal);
    	EditText txtTitle = (EditText) findViewById(R.id.edTxtHead);
    	EditText txtDescp = (EditText) findViewById(R.id.edMultiDescp);
    	GPS = null;
    	
    	//update
    	if(dba.updateDB(dbHelper, primary, txtTitle.getText().toString(), txtDescp.getText().toString(), txtSect.getText().toString(), null, GPS, domain, imgPath))
		{
			View a = findViewById(R.id.btnUpdate);
			View b = findViewById(R.id.btnSave);
			
			b.setVisibility(View.INVISIBLE);
			a.setVisibility(View.VISIBLE);
			
			Toast.makeText(this, "Update news successfull", Toast.LENGTH_SHORT).show();
			
			bRetVal = true;
		}
    	else
    		Toast.makeText(this, "Error updating data", Toast.LENGTH_SHORT).show();
		     //post(url, nameValuePair,pic1,pic2,pic3,pic4,primary);
    	
    	return bRetVal;
	 }
    public void viewInsertedData()
    {    	
    	primary = getIntent().getExtras().getInt("primary");
    	
    	TextView txtSect = (TextView) findViewById(R.id.tvSectVal);
    	EditText txtTitle = (EditText) findViewById(R.id.edTxtHead);
    	EditText txtDescp = (EditText) findViewById(R.id.edMultiDescp);
    	
    	String[] result = dba.getResultArray(dbHelper, "inserted data", "_ID = '"+primary+"' ");
    	
    	txtSect.setText(result[4]);
    	sect = result[6];
    	txtTitle.setText(result[2]);
    	txtDescp.setText(result[3]);
    	
    	/*byte[] pic1 = dba.GetBlob(primary, 5);
    	
    	if (pic1 != null)
		 {
    		View a = findViewById(R.id.imvNews2);
			a.setVisibility(View.VISIBLE);
    		ImageView iv = (ImageView) findViewById(R.id.imvNews2);
			Bitmap bitmap = BitmapFactory.decodeByteArray(pic1 , 0, pic1.length);
			iv.setImageBitmap(bitmap);
			 
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
	        byteArray = stream.toByteArray();
	        g=1;
		 }
		 */
    	
    	imgPath = dba.getResult(dbHelper, "imgLoc", "_ID='"+primary+"'");
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	ImageView iv = (ImageView) findViewById(R.id.imvNews2);
    		
    	if (imgPath != null)
		 {
			 try 
				{			
				 	Bitmap bitmap = null;
				 
	            	BitmapFactory.Options opt = new BitmapFactory.Options();
	            	opt.inSampleSize = 4;
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
	            	
	            	/*
	            	if (bitmap.getHeight() > bitmap.getWidth())
		        	{	
	            		if (bitmap.getHeight() > 1600)
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
	            		if (bitmap.getHeight() > 1280)
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 35, stream);
	            		else if (bitmap.getHeight() > 1024)
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
		            	else if (bitmap.getHeight() > 800)
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
		            	else if (bitmap.getHeight() > 640)
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
		            	else
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);		            		
		        	}
	            	else
	            	{		            	
	            		if (bitmap.getWidth() > 1600)
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
	            		if (bitmap.getWidth() > 1280)
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 35, stream);
	            		else if (bitmap.getWidth() > 1024)
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
		            	else if (bitmap.getWidth() > 800)
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
		            	else if (bitmap.getWidth() > 640)
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream);
		            	else
		            		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);	
	            	}
	            	
	            	
	            	byteArray = stream.toByteArray();
	    	        g=1;
	    	        
		            stream.flush();
		            stream.close();
		            */
		            
		            iv.setImageBitmap(bitmap);
		            iv.setVisibility(View.VISIBLE);
	            									
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
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
			 String url = config[3] + getResources().getString(R.string.upload_news);;
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
		        	Bitmap dispBMP = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
		        	
		        	bitmap = dispBMP;
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
			 			 
			 //byte[] pic1 = dba.GetBlob(primary, 5);

			 if (button=="Upload")
				 dothis = "insert";
			 else
				 dothis = "update";
			 
			 //upload data to server
			 
			 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			 nameValuePairs.add((NameValuePair) new BasicNameValuePair("username", usr));
			 nameValuePairs.add((NameValuePair) new BasicNameValuePair("password", pass));
			 nameValuePairs.add((NameValuePair) new BasicNameValuePair("NewsID", NewsID));
			 nameValuePairs.add((NameValuePair) new BasicNameValuePair("Ntype", sect));
			 nameValuePairs.add((NameValuePair) new BasicNameValuePair("Title", Title));
			 nameValuePairs.add((NameValuePair) new BasicNameValuePair("Info", Info));
			 nameValuePairs.add((NameValuePair) new BasicNameValuePair("image1", ""));
			 nameValuePairs.add((NameValuePair) new BasicNameValuePair("SynCA", SynCA));
			 nameValuePairs.add((NameValuePair) new BasicNameValuePair("dothis", dothis));
		     
		     Log.i("uploadtoserver","before post");
		     
		     post(url, nameValuePairs,pic1,primary);
				
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 
	 public void post(String url, List<NameValuePair> nameValuePairs, byte[] pic1, int primary) {

			ThreadPolicy tp = ThreadPolicy.LAX;
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
			    	
			    	Log.i("post : res",res);

			    	xmlParse(res,primary);
		    		
			    }
			    if (resEntity != null) {
			      resEntity.consumeContent();
			    }

			    httpClient.getConnectionManager().shutdown();
	        } catch (Exception e) {
	            e.printStackTrace();
	            Log.e("post() ~ Exception",e.getClass().getName() + " : " + e.getMessage());
	            Toast.makeText(
	                    getApplicationContext(),
	                    "Exception ~ post() :\n" + e.getClass().getName() + " \n" + e.getMessage(),
	                    Toast.LENGTH_LONG).show();
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
			        		info = "Error updating data, "+xpp.getText();
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
		        	Toast.makeText(this, "Update successful for NewsID '" + dat + "'.", Toast.LENGTH_SHORT).show();
		        	callclass("view");
		        }
		        else
		        	Toast.makeText(this, "Error updating data for NewsID '" + dat + "' : \n" + info, Toast.LENGTH_SHORT).show();
		        	Log.e("xmlParse() ~ Error updating data for NewsID '" + dat + "'.", info);
		    	}
		    	catch (Exception e1)
		        {
		        	Log.e("xmlParse() ~ Exception", e1.getClass().getName() + " : " + e1.getMessage(), e1);

		        	Toast.makeText(
		                    getApplicationContext(),
		                    "Exception ~ xmlParse() : \n" + e1.getClass().getName() + " \n" + e1.getMessage(),
		                    Toast.LENGTH_LONG).show();
		        } 

	 }
	     
	 public void callclass(String r)
    {
    	if (r.equals("cat"))
    	{
	    	Intent intent = new Intent(this, Categories.class);
	        startActivity(intent);
	        finish();
    	}
    	else if (r.equals("view"))
    	{
	    	Intent intent = new Intent(this, ViewTwo.class);
	    	intent.putExtra("primary", primary);
	        startActivity(intent);
	        finish();
    	}else if (r.equals("main"))
    	{
	    	Intent intent = new Intent(this, dbList.class);
	        startActivity(intent);
	        finish();
    	}/*else if (r.equals("map"))
    	{
	    	Intent intent = new Intent(this, Map.class); 
	    	intent.putExtra("GPS", GPS);
	        startActivity(intent);
    	}*/
    }
}

