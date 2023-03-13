package com.smartlab.wpslidingmenu;

//import java.io.ByteArrayOutputStream;
//import java.text.DecimalFormat;

//import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

//import android.content.Context;
//import android.content.DialogInterface;
//import android.database.Cursor;
//import android.location.LocationManager;
//import android.net.Uri;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.Toast;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.RadioGroup;
//import android.widget.Spinner;

public class ViewTwo extends Activity {
	//private static final int CAMERA_PIC_REQUEST = 1337;
	//private static final int SELECT_FILE = 1;
	

	MySQLiteHelper dbHelper; 
    SQLiteDatabase db;
    dbActivities dba;
	
	int click = 0;
    byte[] byteArray;

    String GPS;
    int primary;
    int g=0;
    String sect = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.viewtwo);
        
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
    	final TextView tvViewNews = (TextView) findViewById(R.id.tvViewNews);
    	
    	String domain = server.replace("http://","");
    	domain = domain.replace("/workplace", "");
		
    	tvViewNews.setText(tvViewNews.getText() + "   -  " + domain + "   ( " + uid + " )");
    }
    
    public void viewInsertedData()
    {
    	primary = getIntent().getExtras().getInt("primary");
    	
    	TextView txtSect = (TextView) findViewById(R.id.edSect);
    	TextView txtTitle = (TextView) findViewById(R.id.edTitle);
    	TextView txtDescp = (TextView) findViewById(R.id.edDescp);
    	
    	String[] result = dba.getResultArray(dbHelper, "inserted data", "_ID = '"+primary+"' ");
    	
    	txtSect.setText(result[4]);
    	sect = result[4];
    	txtTitle.setText(result[2]);
    	txtDescp.setText(result[3]);

    	/*
    	byte[] pic1 = dba.GetBlob(primary, 5);
    	
    	if (pic1 != null)
		 {
    		View a = findViewById(R.id.imvNewsPic2);
			a.setVisibility(View.VISIBLE);
    		ImageView iv = (ImageView) findViewById(R.id.imvNewsPic2);
			 Bitmap bitmap = BitmapFactory.decodeByteArray(pic1 , 0, pic1.length);
			 iv.setImageBitmap(bitmap);
		 }
    	*/
    	
    	String imgPath = dba.getResult(dbHelper, "imgLoc", "_ID='"+primary+"'");
    	ImageView iv = (ImageView) findViewById(R.id.imvNewsPic2);
    		
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

    public void addListenerOnButton() {
    	
    	ImageView iv = (ImageView) findViewById(R.id.imvNewsPic2);
    	Button edit = (Button) findViewById(R.id.btnEdit);
    	Button Cancel = (Button) findViewById(R.id.btnCancel);
    	//Button btnMap = (Button) findViewById(R.id.btnMap);
    	
    	Cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				callclass("main");
			}
		});
    	/*btnMap.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//Get The GPS location
				callclass("map");
			}
		});*/
    	
    	final Intent intent11 = new Intent(this, Viewpic.class);
    	iv.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				intent11.putExtra("primary", primary);
				startActivity(intent11);
			}
		});
		
    	final Intent intent = new Intent(this, ViewInserted.class);
    	edit.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				intent.putExtra("primary", primary);
				startActivity(intent);
		        finish();
			}
		});
    }

    public void callclass(String r)
    {
    	if (r.equals("cat"))
    	{
	    	Intent intent = new Intent(this, Categories.class);
	        startActivity(intent);
	        finish();
    	}
    	else if (r.equals("main"))
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

    /*
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
					 startActivityForResult(intent, 1);
				}
				else if (item==1)
				{
					Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST); 
				}
			}
		});
		android.app.AlertDialog alerts = builder.create();
		alerts.show();
    }
    
    private Uri mImageCaptureUri;

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	
    	try{
        if (requestCode == CAMERA_PIC_REQUEST) {  

        	Bitmap thumbnail= (Bitmap) intent.getExtras().get("data");
        	
     		ImageView myImage = (ImageView) findViewById(R.id.imvNewsPic2);
            myImage.setImageBitmap(thumbnail);
            
            View a = findViewById(R.id.imvNewsPic2);
			a.setVisibility(View.VISIBLE);
            
    		ByteArrayOutputStream stream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            
            byteArray = stream.toByteArray();
        	
        }  
        if (requestCode == SELECT_FILE) {
        	
        	Bitmap bitmap   = null;
        	String path     = "";
        	
            mImageCaptureUri = intent.getData();
            path = getRealPathFromURI(mImageCaptureUri); //from Gallery
 
            if (path == null)
                path = mImageCaptureUri.getPath(); //from File Manager
 
            if (path != null)
            {
            	BitmapFactory.Options opt = new BitmapFactory.Options();
            	opt.inSampleSize = 6;
                bitmap  = BitmapFactory.decodeFile(path,opt);
            }
           
        	ImageView myImage = (ImageView) findViewById(R.id.imvNewsPic2);
            myImage.setImageBitmap(bitmap);
            
            View a = findViewById(R.id.imvNewsPic2);
			a.setVisibility(View.VISIBLE);

			
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byteArray = stream.toByteArray();
            
        }
    	}catch(Exception e)
    	{
    		Log.e("Error get image",e.getMessage().toString());
    		Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
    	}
    	
    }
    

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = managedQuery( contentUri, proj, null, null,null);
 
        if (cursor == null) return null;
 
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
 
        cursor.moveToFirst();
 
        return cursor.getString(column_index);
    }
    */
}
