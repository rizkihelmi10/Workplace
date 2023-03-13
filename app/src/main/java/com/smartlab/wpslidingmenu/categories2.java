package com.smartlab.wpslidingmenu;


import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import android.view.ViewGroup;

public class categories2 extends Activity {

	MySQLiteHelper dbHelper; 
    SQLiteDatabase db;
    dbActivities dba;
	String cat;
	int primary;
    String domain;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.categoriestwo);
        
        dba = new dbActivities();
        dbHelper = new MySQLiteHelper(this);
        
        
        Log.e("Start","On Category Page");
       
         
        //2. generate button
        getCurrentDomain();
        generateButton();
    }
    
    private void getCurrentDomain()
    {    	    	    	
    	final String server = dba.getConfigValue(dbHelper,"server","active='1'");
    	final String uid = dba.getConfigValue(dbHelper,"username","active='1'");
    	final TextView lblDomain = (TextView) findViewById(R.id.lblDomain);
    	
    	domain = server.replace("http://","");		
    	domain = domain.replace("/workplace", "");
		
    	//lblDomain.setText(domain + "   ( " + uid + " )");
    }
    
    public void generateButton()
    {
    	/*--------------------------------------------------
    	1. get the screen size
    	2. create the header
    	3. generate button (Based on the icon name inside the db)
    	4. create OnClickListener for each button
    	5. when button clicked go to the register page
    	----------------------------------------------------*/
    	
    	//1-----------------------------------------------------
    	
    	int txtSize = 0;
		int TxtPdg = 0;
		int LayHigh = 0;
		int LayPdg = 0;
		int imgSize = 0;
		@SuppressWarnings("unused")
		int txtlsize = 0;
		 
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
		 
		if (dm.heightPixels <= 800)
		{
			txtSize = 20;
			TxtPdg = 15;
			LayHigh = 90;
			LayPdg = 5;
			imgSize = 90;
			txtlsize = 90;
		}
		else if (dm.heightPixels <= 1024 && dm.heightPixels > 800)
		{
			txtSize = 20;
			TxtPdg = 20;
			LayHigh = 90;
			LayPdg = 5;
			imgSize = 70;
			txtlsize = 90;
		}
		else if (dm.heightPixels <= 1280 && dm.heightPixels > 1024)
		{
			txtSize = 20;
			TxtPdg = 30;
			LayHigh = 90;
			LayPdg = 10;
			imgSize = 90;
			txtlsize = 90;
		}
		
		//2----------------------------------------------------------------------------    
       
		int resId2 = getResources().getIdentifier("head", "drawable", getPackageName());
		
		TextView txt1 = new TextView(this);
		txt1.setText("Categories");
		txt1.setGravity(Gravity.CENTER_VERTICAL);
		txt1.setTextSize(txtSize);
		txt1.setPadding(TxtPdg, 0, 0, 0);
		txt1.setTextColor(Color.WHITE);
		
		int addId = getResources().getIdentifier("add", "drawable", getPackageName());
		ImageView add = new ImageView(this);
		add.setBackgroundResource(addId);
		
		LinearLayout rl = (LinearLayout) findViewById(R.id.linearLayout1);
		
		LinearLayout rl2 = new LinearLayout(this);
		rl2.setOrientation(LinearLayout.HORIZONTAL);
		rl2.setBackgroundResource(resId2);
		rl2.setPadding(0, 0, 0, 10);
		
		rl2.addView(txt1,LayoutParams.WRAP_CONTENT,LayHigh);
		rl.addView(rl2,LayoutParams.MATCH_PARENT,LayHigh);
		
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
			//String iconImg = dba.getResult(dbHelper, "Title", "_ID='"+(i+1)+"' AND Category='Label'");
			final String iconTxt = arrSect[i];
			
			int iconId = getResources().getIdentifier("news", "drawable", getPackageName());
			ImageView icon = new ImageView(this);
			icon.setBackgroundResource(iconId);
			
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
			
			LayIcon.addView(icon,imgSize,imgSize);
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
    }
    public void callclassExecute(String sect)
    {
    	Intent intent = new Intent(this, AddNews.class);
    	intent.putExtra("sect", sect);
        startActivity(intent);
        finish();
    }


}