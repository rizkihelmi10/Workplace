package com.smartlab.wpslidingmenu;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoriesXML extends Activity {
	
	MySQLiteHelper dbHelper; 
    SQLiteDatabase db;
    dbActivities dba;
    
    String uid;
		
	
	// XML node keys
	static final String KEY_ITEM = "category"; // parent node
	static final String KEY_ID = "catid";
	static final String KEY_NAME = "catname";
	static final String KEY_COST = "catid";
	static final String KEY_DESC = "catname";
	
    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] flags = new int[]{
        R.drawable.news,  
    };
    // Array of strings storing list names 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	ThreadPolicy tp = ThreadPolicy.LAX;
		StrictMode.setThreadPolicy(tp);
    	
        super.onCreate(savedInstanceState);
        
        dba = new dbActivities();
	    dbHelper = new MySQLiteHelper(this);
        
        uid = getIntent().getExtras().getString("uid");
        
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.listcat);
		 // Each row in the list stores country name, currency and flag
        //List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
        
        ArrayList<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
    	final String server = dba.getConfigValue(dbHelper,"server","active='1'");
        final String URL = server+"/news-category.wp?memberid=" + uid;
        
       // showToast(URL);
        
        //xml parser
        XMLParser parser = new XMLParser();
		String xml = parser.getXmlFromUrl(URL); // getting XML
		Document doc = parser.getDomElement(xml); // getting DOM element
		
	
	        		
		NodeList nl = doc.getElementsByTagName(KEY_ITEM);
		// adding new hashmap for client list
		for (int i = 0; i < nl.getLength(); i++) {			
			// creating new HashMap
			HashMap<String, String> map = new HashMap<String, String>();
			Element e = (Element) nl.item(i);
			map.put(KEY_NAME, parser.getValue(e, KEY_NAME));
			map.put(KEY_COST, parser.getValue(e,KEY_COST));
			map.put(KEY_ID, parser.getValue(e,KEY_ID));
			//map.put("client_image", Integer.toString(flags[0]) ); gmbaq
			
			aList.add(map);				
		}
        
        // Keys used in Hashmap
       ///String[] from = { "client_image","catname","catid"};
		String[] from = {"catname","catid"};
       
        // Ids of views in listview_layout
        ///int[] to = { R.id.client_image,R.id.client_name,R.id.custid};
       int[] to = {R.id.client_name,R.id.custid};
 
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.grid_singlexml, from, to);
 
        // Getting a reference to gridview of MainActivity
        ///GridView gridView = (GridView) findViewById(R.id.gridview);this is for grid view
       
        ListView gridView = (ListView) findViewById(R.id.gridview);//this is for list
        
        // Setting an adapter containing images to the gridview
        gridView.setAdapter(adapter);
        
        gridView.setOnItemClickListener(new OnItemClickListener() {
        //gridView.setOnItemClickListener(new OnItemClickListener() {
        			@Override
        			public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
        				// getting values from selected ListItem
        				//String name = ((TextView) view.findViewById(R.id.client_image)).getText().toString();
        				///ImageView name = (ImageView) view.findViewById(R.id.client_image);
        				String custid = ((TextView) view.findViewById(R.id.custid)).getText().toString();
        				String lblname = ((TextView) view.findViewById(R.id.client_name)).getText().toString();
        				//String description = ((TextView) view.findViewById(R.id.desciption)).getText().toString();
        				
        				// Starting new intent		        
        				//Intent in = new Intent(getApplicationContext(), frontdesk.class);
        				//in.putExtra(KEY_NAME, name);
        				//in.putExtra(KEY_COST, cost);
        				//in.putExtra(KEY_DESC, description);

        				//startActivity(in);
        				callclass2(custid,lblname);
        				
        				/*name.setOnClickListener(new OnClickListener() {
        					public void onClick(View arg0) {
        						callclass("FrontDesk","", arg0);
        					}
        				});*/
        			}
        		});
    }
          
    public void callclass2(String custid, String lblname)
    {
    	
    	Log.i("callclass", "execute : " + custid + " - " + lblname);
	
    	Intent intent = new Intent(this, AddNews.class);
    	intent.putExtra("custid", custid); 
    	intent.putExtra("secth;l	", lblname);    		
    	startActivity(intent);
    }
    
    public void showToast(String strMsg)
    {
    	Toast.makeText(this, strMsg, Toast.LENGTH_SHORT).show();
    }
    
  
}

