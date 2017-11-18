package com.health.pengfei.chestlearn2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TakePhoto extends AppCompatActivity {

    private Button confirmButton, takePhoto;
    private ImageView image_preview;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final int REQUEST_TAKE_PHOTO = 1;
    static int TAKE_PIC =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        confirmButton=(Button)findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent data= new Intent();
                data.putExtra("uri",mCurrentPhotoPath);
                setResult(RESULT_OK,data);
                galleryAddPic();
                finish();
            }
        });

        takePhoto=(Button)findViewById(R.id.button_takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();

            }
        });
        image_preview=(ImageView)findViewById(R.id.imageView_preview);

        //start camera when activity start
        startCamera();

    }
@Override
public void onDestroy() {

    super.onDestroy();


}


    public void startCamera() {
        try {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        0);
            }
            else
            dispatchTakePictureIntent();


        } catch (IOException e) {
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                startCamera();
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Show the thumbnail on ImageView
            if (mCurrentPhotoPath != null) {
                Uri imageUri = Uri.parse(mCurrentPhotoPath);
                File file = new File(imageUri.getPath());

                Glide.with(this).load(new File(imageUri.getPath())).into(image_preview);



                // ScanFile so it will be appeared on Gallery
                galleryAddPic();
            }
        }
        else
            mCurrentPhotoPath=null;
    }


    private void galleryAddPic() {
        if (mCurrentPhotoPath != null) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() throws IOException {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(" IOException",ex.toString());
                return;
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = FileProvider.getUriForFile(TakePhoto.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


}
