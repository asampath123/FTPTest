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

import com.android.internal.http.multipart.MultipartEntity;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpProgressMonitor;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.io.File;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

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

    /*public void buttonUploadClick(View view) throws IOException{

                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

                HttpPost httppost = new HttpPost("http://localhost:9000/upload");
                File file = new File("C:\\Users\\joao\\Pictures\\bla.jpg");

                org.apache.http.entity.mime.MultipartEntity mpEntity = new org.apache.http.entity.mime.MultipartEntity();
                ContentBody cbFile = new FileBody(file, "image/jpeg");
                mpEntity.addPart("userfile", cbFile);


                httppost.setEntity(mpEntity);
                System.out.println("executing request " + httppost.getRequestLine());
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();

                System.out.println(response.getStatusLine());
                if (resEntity != null) {
                    System.out.println(EntityUtils.toString(resEntity));
                }
                if (resEntity != null) {
                    resEntity.consumeContent();
                }

                httpclient.getConnectionManager().shutdown();
            }*/





    public void buttonUploadClick(View view) {
        System.out.println("buttonUploadClick");
        final ProgressDialog pd = ProgressDialog.show(MainActivity.this, "Please wait", "Uploading Picture ...", true);
        new Thread() {

            @Override
            public void run() {
                System.out.println("filePath"+filePath);
                File file = new File(filePath);
                Session session = null;
                Channel channel = null;
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                boolean conStatus = false;
                SftpProgressMonitor mSpm = null;
                String ftp_server_username="asampath";
                String ftp_server_password="my name is abhi";
                System.out.println("ftp_server_username set");
                try {
                    JSch ssh = new JSch();
                    session = ssh.getSession(ftp_server_username, "silo.soic.indiana.edu", 22);
                    session.setPassword(ftp_server_password);
                    session.setConfig(config);
                    session.connect();
                    conStatus = session.isConnected();
                    System.out.println("Session is "+conStatus);
                    channel = session.openChannel("sftp");
                    channel.connect();
                    //client.setFileType(FTP.BINARY_FILE_TYPE);
                    System.out.println("connection established");
                    ChannelSftp sftp = (ChannelSftp) channel;
                    sftp.cd("/u/asampath/");
                    fis = new FileInputStream(filePath);
                    //sftp.put(filePath, "/u/asampath/");
                    //sftp.put(fis,sftp.pwd());

                    sftp.put(fis,file.getName());
                    //sftp.put(filePath,sftp.pwd());
                    //FTPClient client = new FTPClient();
                    //System.out.println("FTPClient");
                    //client.enterLocalPassiveMode();
                    //client.connect(InetAddress.getByName("silo.soic.indiana.edu"),22);
                    //System.out.println("client.connect");
                    //client.login(ftp_server_username, ftp_server_password); //this is the login credentials of your ftpserver. Ensure to use valid username and password otherwise it throws exception
                    //System.out.println("client.login");

                    //fis = new FileInputStream(file);
                    // Upload file to the ftp server
                    //boolean result = client.storeFile("testfile",fis);
                    //if(result){
                        // Toast.makeText(this,"file sent",Toast.LENGTH_LONG).show();
                        System.out.println("File sent");
                    //}
                    //this is actual file uploading on FtpServer in specified directory/folder
                    //client.logout();
                    //client.disconnect();   //after file upload, don't forget to disconnect from FtpServer.
                    fis.close();
                    file.delete();
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            }


        }.start();
        pd.dismiss();
    }
}
