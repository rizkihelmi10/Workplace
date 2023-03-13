package com.smartlab.wpslidingmenu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

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

import static android.app.Activity.RESULT_CANCELED;

public class WebViewFragment2 extends AppCompatActivity {

    private static final int CAMERA_PIC_REQUEST = 1337;
    String sCOMPID ="";
    String sContractID ="";
    String sAssetID = "";
    String sInfo = "";

    final int SELECT_PHOTO = 1;
    ProgressDialog progressBar;

    WebView webView;
    String url;

    String reqid;
    String gpse;
    String imgPath;
    Uri targetURI;

    int primary;
    private int progressBarStatus = 0; // setting progress bar to zero
    private Handler progressBarHandler = new Handler(); // object for handlemyWebClientr class
    private long progress = 0;
    dbActivities dba;
    private Uri mImageCaptureUri;
    MySQLiteHelper dbConfigHelper;
    LayoutInflater inflater;
    ViewGroup container;
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);

            setContentView(R.layout.fragment_web_view);

            String sURL = "";

            ProgressBar progressBar;
            dba = new dbActivities();
            dbConfigHelper = new MySQLiteHelper(this);

            //Bundle bundle = this.getArguments();
            //Bundle bundle = new Bundle();
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String data = bundle.getString("AssetInfo");
                Log.i("AssetInfo", data);

                sURL = bundle.getString("sURL");
                Log.i("sURLwebview", sURL);
                String[] separated = data.split("\\|");

                sCOMPID = separated[2].trim().replace("*", "");

                sContractID = separated[3].trim().replace("*", "");
                sAssetID = separated[4].trim().replace("*", "");
                sInfo = separated[0].trim().replace("*", "");

               showToast(sInfo);
            }

            url = sURL + "?ok=1&svalue=" + sCOMPID + "&svalue2=" + sContractID + "&svalue3=" + sAssetID;
            Log.i("queryString", url);

            progressBar = findViewById(R.id.progressBar);
            progressBar.setMax(100);

            webView = findViewById(R.id.webview);
            webView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.setWebViewClient(new WebViewClient());

            webView.loadUrl(url);
            progressBar.setProgress(0);

            final ProgressBar finalProgressBar = progressBar;
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    if (newProgress == 100)
                        finalProgressBar.setVisibility(View.INVISIBLE);
                    else
                        finalProgressBar.setVisibility(View.VISIBLE);
                    finalProgressBar.setProgress(newProgress);
                }
            });
        }catch (Exception e){
            Log.e("error WebViewFragment", e.getMessage());

        }
    }

    public void showToast(String strMsg)
    {
        Toast.makeText(this, strMsg, Toast.LENGTH_LONG).show();
    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        Cursor cursor       = this.managedQuery(contentUri, proj, null, null,null);

        if (cursor == null) return null;
        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        try{
            if (resultCode != RESULT_CANCELED){
                if (requestCode == SELECT_PHOTO){
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


                    if (rotateValue != 0)
                    {
                        Matrix m = new Matrix();

                        m.postRotate(rotateValue);
                        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
                    }

                    AlertDialogUpdate(path,reqid,gpse);

                }
                else if (requestCode == CAMERA_PIC_REQUEST)
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

                    if (rotateValue != 0)
                    {
                        Matrix m = new Matrix();

                        m.postRotate(rotateValue);
                        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
                    }

                    AlertDialogUpdate(path,reqid,gpse);

                }
            }
        }
        catch(Exception e)
        {
            Log.e("Error get image",e.getMessage().toString());
        }
    }
    public boolean load_progressbar(final String st,final String rqi,final String g){

        // prepare for a progress bar dialog
        progressBar = new ProgressDialog(this);
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
                            if (progressBarStatus== 30){
                                uploadtoserver(primary, "Update", st, rqi,g);
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
    public void AlertDialogUpdate(String Path,final String rId,final String sgps)
    {

        final String newPath = Path;
        final CharSequence[] items = {"Yes", "No"};
        android.app.AlertDialog.Builder builder = new
                android.app.AlertDialog.Builder(this);
        builder.setTitle("Are you sure to upload this?");
        builder.setItems(items, new

                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int item) {

                        if(item==0)
                        {
                            load_progressbar(newPath,rId,sgps);


                        }
                    }
                });

        android.app.AlertDialog alerts = builder.create();
        alerts.show();
    }

    class MyJavascriptInterface extends AppCompatActivity  {

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
            intent.setAction(Intent.ACTION_VIEW);
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
        public void choosePhoto(String A, String F) {
           // showToast(A + " " + F);
            //AlertDialogPic(A,F);

            String[] permissions={android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //if (ContextCompat.checkSelfPermission(this, permissions[0])== PackageManager.PERMISSION_GRANTED) {
               // Log.e("The TAG", "permission storage granted");
                AlertDialogPic(A,F);
            //}else{
              //  ActivityCompat.requestPermissions(this, permissions ,1);
            //}
            }


    }
    public void scanQR(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("");
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();

    }

    public void AlertDialogPic(final String B,final String GP)
    {
try{
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
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, SELECT_PHOTO);

                            reqid = B;
                            gpse = GP;
                        }
                        else if (item==1)
                        {

                            //strict mode to enable
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());

                            //----------------------------------------------------------------------------
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            String currDT = sdf.format(new Date());

                            Intent CamImageIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                            File cameraFolder;

                            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
                                cameraFolder = new File(android.os.Environment.getExternalStorageDirectory(),"DCIM/Camera");
                            else
                                cameraFolder =  context.getFilesDir();

                            if (!cameraFolder.exists())
                                cameraFolder.mkdirs();

                            //  File photo = new File(cameraFolder,"workplace/camera/imgNews_" + currDT.toString() + ".jpg");
                            File photo = new File(cameraFolder,"imgNews_" + currDT.toString() + ".jpg");
                            CamImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                            targetURI = Uri.fromFile(photo);
                            reqid = B;
                            gpse = GP;
                            startActivityForResult(CamImageIntent, CAMERA_PIC_REQUEST);

                        }
                    }


                });

        android.app.AlertDialog alerts = builder.create();
        alerts.show();


    }   catch(Exception e){
     Log.e("alertDialogError", e.getMessage());

}
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

    public void uploadtoserver(int primary,String button,String fPath,String sReqId,String gpsee)
    {
        try
        {

            String dothis;
            String[] config = dba.getResultConfig(dbConfigHelper, "*", "active='1'");
            String imgPath = fPath;


           String url3 = config[3];
           // String url3 = "http://urbancubes.voffice.my/workplace";
            Log.i("url3",url3);

            String domain = url3.replace("workplace","");

           // String url = domain + "/ezfm/hd_frontdeskprocess.asp?src=apps&contractid=KON1-21&custid=C01&asid=CSSB01";
            String url = domain + "ezfm/hd_frontdeskdetailv2_process.asp";
            Log.i("url",url);

            // String url = domain + "http://urbancubes.voffice.my/ezfm/hd_frontdeskdetailv2.asp?contractid=59&custid=94&asid=19312"
            String Title = "";
            String Info = "";
            String SynCA = "";

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

                bitmap.compress(Bitmap.CompressFormat.JPEG,15,stream);

                pic1 = stream.toByteArray();

                stream.flush();
                stream.close();
            }

            //byte[] pic1 = dba.GetBlob(primary, 5);

            if (button=="Upload")
                dothis = "insert";
            else
                dothis = "update";

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

            Log.i("uploadtoserver","before post");

            post(url, nameValuePairs,pic1,primary,sReqId,gpsee);

        }
        //catch (IOException e)
        catch (Exception e)
        {
            // TODO Auto-generated catch block
           // e.printStackTrace();
            Log.i("uploadtoservererror", e.getMessage());
        }
    }
    public void post(String url, List<NameValuePair> nameValuePairs,byte[] pic1,int primary,String reqid,String gpps) {

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

            Log.e("getStatusLine()",response.getStatusLine().toString());

            if (resEntity != null) {
                String res =  EntityUtils.toString(resEntity);

                Log.e("post : res",res);

                xmlParse(res,primary,reqid,gpps);

            }
            if (resEntity != null) {
                resEntity.consumeContent();
            }

            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
            //e.printStackTrace();
            Log.i("post() ~ Exception",e.getClass().getName() + " : " + e.getMessage());
        }
    }
    public void xmlParse(String result,int primary,String reqid,String sGPS)
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
               // displayWeb(dat + "?contractid="+ sContractID +"&custid="+ sCOMPID +"&asid="+sAssetID + "&imgurl="+sGPS);
                displayWeb(dat + "&mobileid="+ reqid);

               //displayWeb("http://urbancubes.voffice.my/ezfm/hd_frontdeskdetailv2.asp?contractid="+ sContractID +"&custid="+ sCOMPID +"&asid="+sAssetID);
                Log.i("data for url '" + dat + "'.", info);
        }
        else
            Log.e("data for NewsID '" + dat + "'.", info);
        }
        catch (Exception e1)
        {

            Log.e("xmlParse() ~ Exception", e1.getClass().getName() + " : " + e1.getMessage(), e1);

        }
    }

    public void displayWeb(String url){
        this.secondFragment(url);
    }

    public void secondFragment(String surl) {

        ProgressBar progressBar;

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        url = surl;
        Log.i("queryStringreport", url);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);

        webView = findViewById(R.id.webview);
        webView.addJavascriptInterface(new MyJavascriptInterface(this), "Android");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl(url);
        progressBar.setProgress(0);

        final ProgressBar finalProgressBar = progressBar;
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100)
                    finalProgressBar.setVisibility(View.INVISIBLE);
                else
                    finalProgressBar.setVisibility(View.VISIBLE);
                finalProgressBar.setProgress(newProgress);
            }
        });

    }

}

