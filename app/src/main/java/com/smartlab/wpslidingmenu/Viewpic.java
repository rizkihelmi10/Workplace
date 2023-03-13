package com.smartlab.wpslidingmenu;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;

public class Viewpic extends Activity {
	
	MySQLiteHelper dbHelper; 
    SQLiteDatabase db;
    dbActivities dba;
	String cat;
	int primary;
	String button;

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        setContentView(R.layout.picture);
	        
	        Log.i("Viewpic","Start onCreate");
	        
	        dba = new dbActivities();
	        dbHelper = new MySQLiteHelper(this);
	        
	        generatePicture();
	    }
	 
	 public void generatePicture()
	 {
		 DisplayMetrics dm = new DisplayMetrics();
	     getWindowManager().getDefaultDisplay().getMetrics(dm);
		 
		primary = getIntent().getExtras().getInt("primary");
		String imgPath = dba.getResult(dbHelper, "imgLoc", "_ID='"+primary+"'");
		
		if (imgPath != null)
		 {
			try 
			{			
				ImageView iv = (ImageView) findViewById(R.id.img1);
			 	Bitmap bitmap = null;
			 
            	BitmapFactory.Options opt = new BitmapFactory.Options();
            	opt.inSampleSize = 2;
                bitmap  = BitmapFactory.decodeFile(imgPath,opt);
                
            	if (bitmap.getHeight() > bitmap.getWidth())
            	{
            		if (bitmap.getHeight() < 800)
            		{
            			opt.inSampleSize = 1;
                        bitmap  = BitmapFactory.decodeFile(imgPath,opt);
            		}
            	}
            	else
            	{
            		if (bitmap.getWidth() < 800)
            		{
            			opt.inSampleSize = 1;
                        bitmap  = BitmapFactory.decodeFile(imgPath,opt);
            		}
            	}

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
				iv.setImageBitmap(bitmap);
				iv.setMaxHeight(dm.heightPixels);
				iv.setMaxWidth(dm.widthPixels);
				iv.setMinimumWidth(dm.widthPixels);
				iv.setMinimumHeight(dm.heightPixels);
				iv.setVisibility(View.VISIBLE);
            									
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		
		/*byte[] pic1 = dba.GetBlob(primary, 5,dbHelper);
    	
		
    	if (pic1 != null)
		 {
    		View a = findViewById(R.id.img1);
			a.setVisibility(View.VISIBLE);
    		ImageView iv = (ImageView) findViewById(R.id.img1);
			 Bitmap bitmap = BitmapFactory.decodeByteArray(pic1 , 0, pic1.length);
			 iv.setImageBitmap(bitmap);
			 iv.setMaxHeight(dm.heightPixels);
			 iv.setMaxWidth(dm.widthPixels);
			 iv.setMinimumWidth(dm.widthPixels);
			 iv.setMinimumHeight(dm.heightPixels);
	         //byteArray1 = stream.toByteArray();
		 }
    	*/
	 
	 }
}
