package com.smartlab.wpslidingmenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.widget.Spinner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

//import org.apache.http.entity.mime.content.ByteArrayBody;
//import android.net.Uri;
//import android.widget.ImageView;


//import com.google.firebase.iid.FirebaseInstanceId;


public class OpenCameravtwo extends Activity {

    Uri targetURI;

    private Context context;

    public static final int CAMERA_REQUEST = 9999;

    String sfromMain;

    MySQLiteHelper dbConfigHelper;
    SQLiteDatabase db;
    dbActivities dba;
    int primary;
    String sHttp;
    Spinner spinHttp;

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    public OpenCameravtwo(Context context) {
        this.context = context;

    }

   public void openCam(){
       StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
       StrictMode.setVmPolicy(builder.build());

       //----------------------------------------------------------------------------
       SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
       String currDT = sdf.format(new Date());

       Intent CamImageIntent = new Intent("android.media.action.IMAGE_CAPTURE");
       File cameraFolder;

       if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
           cameraFolder = new File(Environment.getExternalStorageDirectory(),"mschool/camera");
       else
           cameraFolder = getCacheDir();


       if (!cameraFolder.exists())
           cameraFolder.mkdirs();

       File photo = new File(Environment.getExternalStorageDirectory(),"mschool/camera/imgNews_" + currDT.toString() + ".jpg");
       CamImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
       targetURI = Uri.fromFile(photo);

       //startActivityForResult(CamImageIntent, CAMERA_REQUEST);

       ((frmTakePhoto) context).startActivityForResult(CamImageIntent, CAMERA_REQUEST);
   }

}
