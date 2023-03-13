package com.smartlab.wpslidingmenu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Categories extends Activity {

	MySQLiteHelper dbHelper; 
    //SQLiteDatabase db;
    dbActivities dba;
	//String cat;
	//int primary;
    String domain;
    
    private ProgressDialog progressBar;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	GridView grid;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                               WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.categories);
        
        dba = new dbActivities();
        dbHelper = new MySQLiteHelper(this);
        
        Log.i("Categories - Start","On Create Page");
        
        getCurrentDomain();
        generateSectBtn();
    }
    
    private void getCurrentDomain()
    {    	    	    	
    	final String server = dba.getConfigValue(dbHelper,"server","active='1'");
    	final String uid = dba.getConfigValue(dbHelper,"username","active='1'");
    	final TextView lblDomain = (TextView) findViewById(R.id.lblDomain);
    	
    	domain = server.replace("http://","");		
    	domain = domain.replace("/workplace", "");
		
    	lblDomain.setText(domain + "   ( " + uid + " )");
    }
    
    public void generateSectBtn()
    {   	
    	/*--------------------------------------------------
    	1. get the screen size
    	2. set the header
    	3. generate button (Based on the item name in section.xml)
    	4. create OnClickListener for each button
    	5. when button clicked go to the AddNews page
    	----------------------------------------------------*/
    	
    	//1-----------------------------------------------------    	
    	int txtSize = 0;
		int TxtPdg = 0;
		int LayHigh = 0;
		int LayPdg = 0;
		 
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
		 
		if (dm.heightPixels <= 800)
		{
			txtSize = 20;
			TxtPdg = 5;
			LayHigh = 60;
			LayPdg = 10;
		}
		else if (dm.heightPixels > 800 && dm.heightPixels <= 1024)
		{
			txtSize = 22;
			TxtPdg = 6;
			LayHigh = 70;
			LayPdg = 8;
		}
		else if (dm.heightPixels > 1024 && dm.heightPixels <= 1280)
		{
			txtSize = 25;
			TxtPdg = 7;
			LayHigh = 75;
			LayPdg = 7;
		}
		else if (dm.heightPixels > 1280 && dm.heightPixels <= 1366)
		{
			txtSize = 27;
			TxtPdg = 8;
			LayHigh = 80;
			LayPdg = 5;
		}
		else if (dm.heightPixels > 1366 && dm.heightPixels <= 1600)
		{
			txtSize = 28;
			TxtPdg = 9;
			LayHigh = 85;
			LayPdg = 3;
		}
		else
		{
			/*txtSize = 26;
			TxtPdg = 10;
			LayHigh = 90;
			LayPdg = 5;*/
			txtSize = 20;
			TxtPdg = 10;
			LayHigh = 90;
			LayPdg = 5;
		}
		
		
		//2----------------------------------------------------------------------------
		//int resId2 = getResources().getIdentifier("head", "drawable", getPackageName());
		
//		TextView txt1 = (TextView)findViewById(R.id.tvTitle);
//		txt1.setGravity(Gravity.CENTER_VERTICAL);
//		txt1.setTextSize(txtSize);
		//txt1.setPadding(0, 15, 0, 0);
		
		LinearLayout rl = (LinearLayout) findViewById(R.id.linearLayout1);
		
		LinearLayout rl2 = (LinearLayout) findViewById(R.id.layTitle);
		rl2.setOrientation(LinearLayout.HORIZONTAL);
		//rl2.setBackgroundResource(resId2);
		//rl2.setPadding(0, 0, 5, 0);
		
		//3---------------------------------------------------------------------------------
		final String[] arrSect;
		
		if (domain.equals("smartlab.com.my") || domain.equals("workplace.com.my"))
			arrSect = getResources().getStringArray(R.array.smartlab_section_array);
		else if (domain.equals("workplace.chuliafm.com") || domain.equals("uat.chuliafm.com"))
			arrSect = getResources().getStringArray(R.array.chulia_section_array);
		else
			arrSect = getResources().getStringArray(R.array.section_array);
		
		
		int maxBound = arrSect.length;
		
		for (int i=0;i<maxBound;i++)
		{
			final String iconTxt = arrSect[i];
			
			//Log.i("generateSectBtn","arrSect[" + i + "] = " + iconTxt);
			
			TextView label = new TextView(this);
			label.setText(iconTxt);
			label.setGravity(Gravity.CENTER_VERTICAL);
			label.setTextSize(txtSize);
			label.setPadding(TxtPdg, 0, 0, 0);
			label.setTextColor(Color.WHITE);
			
			int lineId = getResources().getIdentifier("cback", "drawable", getPackageName());
			ImageView line = new ImageView(this);
			line.setBackgroundResource(lineId);
			
			LinearLayout LayIcon = new LinearLayout(this);
			LayIcon.setOrientation(LinearLayout.HORIZONTAL);
			
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayHigh);
			layoutParams.setMargins(LayPdg, LayPdg, LayPdg, LayPdg);
			
			LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams2.setMargins(LayPdg, LayPdg, LayPdg, LayPdg);
			
			LayIcon.addView(label,LayoutParams.WRAP_CONTENT,LayHigh);
			rl.addView(LayIcon,layoutParams);
			rl.addView(line,layoutParams2);
			
			//4----------------------------------------------------------------------------------------
			
			LayIcon.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					//5--------------------------------------------------------------------------------
					callclass(iconTxt, arg0);
				}
			});
		}
    }

    public void callclass(String sect, View vw)
    {
    	final String sSect = sect;
    	
    	callclassExecute(sSect);
    	
    	//===================================================================
		// prepare for a progress bar dialog
    	/*
		progressBar = new ProgressDialog(vw.getContext());
		progressBar.setMessage("Please wait ");
		progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressBar.show();

		
		new Thread(new Runnable() {
			public void run() {
				try 
				{
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}

				callclassExecute(sSect);
				
				// close the progress bar dialog
				progressBar.dismiss();
			}
		}).start();
		*/
		//===================================================================
    }
    
    public void callclassExecute(String sect)
    {
    	Intent intent = new Intent(this, AddNews.class);
    	intent.putExtra("sect", sect);
        startActivity(intent);
        finish();
    }

}