package com.example.abhi.ftptest;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout photos_layout;
    private static Bitmap scaledphoto = null;
    private String filePath = null;
    private Uri u = null;
    private Boolean picTaken = false;

    FileInputStream fis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                100);

    }

    public void buttonImageClick(View view) {

        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //File photo = new File(Environment. pictureName + ".jpg");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            filePath = image.getAbsolutePath();

            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
              //      Uri.fromFile(image));
            startActivityForResult(takePictureIntent,100);
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {

            //this is needed if you want to show pic in image view
            //setPic();
            Toast.makeText(this,"Pic stored!",Toast.LENGTH_LONG).show();
        }
    }


    public void buttonUploadClick(View view) {
        System.out.println("buttonUploadClick");
        final ProgressDialog pd = ProgressDialog.show(MainActivity.this, "Please wait", "Uploading Picture ...", true);
        new Thread() {

            @Override
            public void run() {

                File file = new File(filePath);
                String ftp_server_username="asampath";
                String ftp_server_password="my name is abhi";
                try {

                    FTPClient client = new FTPClient();
                    client.connect("silo.soic.indiana.edu");
                    client.login(ftp_server_username, ftp_server_password); //this is the login credentials of your ftpserver. Ensure to use valid username and password otherwise it throws exception

                    try {

                        client.changeWorkingDirectory("/u/asampath"); //I want to upload picture in MyPictures directory/folder. you can use your own.
                    } catch (Exception e) {
                        //client.createDirectory("MyPictures");
                        //client.changeDirectory("MyPictures");
                    }
                    fis = new FileInputStream(file);
                    // Upload file to the ftp server
                    boolean result = client.storeFile("testfile",fis);
                    if(result){
                        // Toast.makeText(this,"file sent",Toast.LENGTH_LONG).show();
                        System.out.println("File sent");
                    }
                    //this is actual file uploading on FtpServer in specified directory/folder
                    client.disconnect();   //after file upload, don't forget to disconnect from FtpServer.
                    file.delete();
                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }


        }.start();
        pd.dismiss();
    }
}
