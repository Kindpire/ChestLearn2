package com.health.pengfei.chestlearn2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.provider.Settings.Secure;

public class Late_info extends AppCompatActivity {
    private SharedPreferences sp;
    private ProgressDialog progressDialog;
    //    private TextView textview_patient_last_name;
    private String patientlastname;
    private ImageView Img_Xray,Img_add1, Img_add2;
    private String Uri_Xray, Uri_add1,Uri_add2;
    private Button Btn_Xray,Btn_add1, Btn_add2, Btn_submit;
    private boolean Status_First_Pic, Status_Second_Pic, Status_Third_Pic;
    Account patient = new Account("", false, false, false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resubmit_late_info);

        //TODO change xml
        Img_Xray =(ImageView) findViewById(R.id.nurse_imageXray);
        Img_add1=(ImageView) findViewById(R.id.nurse_imageTB2);
        Img_add2=(ImageView) findViewById(R.id.nurse_imageTB3);

        Status_First_Pic = false;
        Status_Second_Pic = false;
        Status_Third_Pic = false;

        sp= getSharedPreferences("doctor.conf", Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);

        //TODO change xml
        Btn_Xray = (Button) findViewById(R.id.buttonTBPhoto1);
        Btn_add1 = (Button) findViewById(R.id.buttonTBPhoto2);
        Btn_add2 = (Button) findViewById(R.id.buttonTBPhoto3);
        Btn_submit = (Button) findViewById(R.id.button_Nurse_submit);

        TextView textview_patient_last_name = (TextView) findViewById(R.id.tv_patientlastname);
        Intent intent = getIntent();

        patientlastname = intent.getStringExtra("patientlastname");

        ArrayList<Account> results = new ArrayList<Account>();
        AccountRecord account = new AccountRecord();
        try {
            results = account.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Account result:results) {
            if(result.getPatientName().equals(patientlastname)) {
                Log.d("MyApp","Patient exist!");
                patient.setPatientName(patientlastname);
                patient.setFirstPic(result.isFirstPic());
                patient.setSecondPic(result.isSecondPic());
                patient.setThirdPic(result.isThirdPic());
            }
        }
//        Log.d("MyApp",String.valueOf(Status_First_Pic) + String.valueOf(Status_Second_Pic) + String.valueOf(Status_Third_Pic));
        String mainTppFileName = null;

        textview_patient_last_name.setText(patientlastname);

        Btn_Xray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Late_info.this, TakePhoto.class);
                startActivityForResult(i,2);
                patient.setFirstPic(true);
            }
        });
        Btn_add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Late_info.this, TakePhoto.class);
                startActivityForResult(i,3);
                patient.setSecondPic(true);
            }
        });
        Btn_add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Late_info.this, TakePhoto.class);
                startActivityForResult(i,4);
                patient.setThirdPic(true);
            }
        });
        Btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Uri_Xray==null && Uri_add1==null && Uri_add2==null){
                    //open a dialog box to "yes" continue to send or "no" to cancel the submit request.
                    AlertDialog.Builder builder=new AlertDialog.Builder(Late_info.this);
                    builder.setTitle(R.string.nothing_to_upload)
                            .setMessage(R.string.upload_nothing)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                } else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(Late_info.this);
                    builder.setTitle(R.string.Confirmation)
                            .setMessage(
                                    R.string.readtoproceed)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadPics();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                }
            }
        });
    }
    public RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }
    public void uploadPics(){
        String  clinicName= sp.getString("clinicName","");
        String  nurseName=  sp.getString("nurseName","");
        final String  recipients=sp.getString("recipients","");
        String  doc1=sp.getString("doc1","");
        String  doc2=sp.getString("doc2","");
        String  doc3=sp.getString("doc3","");
        String patientLName= patientlastname;
        String uploadDeviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        //TODO anybetter way to do it?
        String mainTppFileName = null;
        String add1TppFileName = null;
        String add2TppFileName = null;

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        System.out.println("Ready to upload");

        if (Uri_Xray != null) {
            Uri imageUri = Uri.parse(Uri_Xray);
            File file = new File(imageUri.getPath());
            mainTppFileName = file.getName();
            RequestBody uploadedfile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("uploadedfile[]", file.getName(), uploadedfile);
            Log.d("MyApp", "Uri_Xray: "+file.getName());
        }

        if (Uri_add1 != null) {
            Uri imageUri = Uri.parse(Uri_add1);
            File file = new File(imageUri.getPath());
            add1TppFileName = file.getName();
            RequestBody uploadedfile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("uploadedfile[]", file.getName(), uploadedfile);
            Log.d("MyApp", "Uri_add1: "+file.getName());
        }

        if (Uri_add2 != null) {
            Uri imageUri = Uri.parse(Uri_add2);
            File file = new File(imageUri.getPath());
            add1TppFileName = file.getName();
            RequestBody uploadedfile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("uploadedfile[]", file.getName(), uploadedfile);
            Log.d("MyApp", "Uri_add2: "+file.getName());
        }


        progressDialog.setMessage(this.getApplicationContext().getText(R.string.waitamoment));
        progressDialog.show();

        HashMap<String, RequestBody> map = new HashMap<String, RequestBody>();
        map.put("id", toRequestBody("1"));
        map.put("patientLastName", toRequestBody(patientLName));
        map.put("uploadDeviceID", toRequestBody(uploadDeviceID));
        map.put("uploadPersonType", toRequestBody("n"));
        map.put("uploadNurseName", toRequestBody(nurseName));
        map.put("clinicName", toRequestBody(clinicName));
        // map.put("recipientName",toRequestBody(recipients));
        map.put("doc1", toRequestBody(doc1));
        map.put("doc2", toRequestBody(doc2));
        map.put("doc3", toRequestBody(doc3));
        if(mainTppFileName!=null){
            map.put("mainTppFileName", toRequestBody(mainTppFileName));
        }
        if(add1TppFileName!=null){
            map.put("add1TppFileName", toRequestBody(add1TppFileName));
        }
        if(add2TppFileName!=null){
            map.put("add2TppFileName", toRequestBody(add2TppFileName));
        }

        List<MultipartBody.Part> parts = builder.build().parts();

        ApiUtil.uploadFile(parts, map).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    //error
                    if (serverResponse.getError()) {
                        Toast.makeText(Late_info.this.getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        ArrayList<Account> results = new ArrayList<Account>();
                        AccountRecord account = new AccountRecord();
                        File file = account.createFile();
                        try {
                            results = account.readFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        results = account.updateAccount(results, patient);

                        try {
                            account.saveAccounts(results);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(Late_info.this.getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        Img_Xray.setImageResource(R.drawable.image_border);
                        Uri_Xray = null;
                        Img_add1.setImageResource(R.drawable.image_border);
                        Uri_add1 = null;
                        Img_add2.setImageResource(R.drawable.image_border);
                        Uri_add2 = null;
                        AlertDialog.Builder builder=new AlertDialog.Builder(Late_info.this);
                        builder.setTitle(R.string.result)
                                .setMessage(R.string.successfulupload)
                                .setCancelable(false)
                                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("additionalinfo", "success");
                                        setResult(9, resultIntent);
                                        finish();
                                    }
                                })
                                .show();
                    }

                } else {
                    assert serverResponse != null;
                    Log.v("Response", serverResponse.toString());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.v("OnFailure", t.toString());
            }

        });
    }
    //get data back from child activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyApp", "onActivityResult: " + requestCode);
        Log.d("MyApp", "resultCode:  " + resultCode);
        if(resultCode != RESULT_CANCELED) {
            if (requestCode == 2) {
                Log.d("MyApp", "From Xray" + data.getStringExtra("uri"));
                String mCurrentPhotoPath=data.getStringExtra("uri");
                if(mCurrentPhotoPath!=null) {
                    Uri_Xray = mCurrentPhotoPath;
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    File file = new File(imageUri.getPath());
                    Glide.with(this).load(new File(imageUri.getPath())).into(Img_Xray);
                }
            } else if (requestCode == 3) {
                System.out.println("From third" + data.getStringExtra("uri"));
                String mCurrentPhotoPath=data.getStringExtra("uri");
                if(mCurrentPhotoPath!=null) {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    Uri_add1 = mCurrentPhotoPath;
                    File file = new File(imageUri.getPath());
                    Glide.with(this).load(new File(imageUri.getPath())).into(Img_add1);

                }
            } else if (requestCode == 4) {
                System.out.println("From third" + data.getStringExtra("uri"));
                String mCurrentPhotoPath=data.getStringExtra("uri");
                if(mCurrentPhotoPath!=null) {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    Uri_add2 = mCurrentPhotoPath;
                    File file = new File(imageUri.getPath());
                    Glide.with(this).load(new File(imageUri.getPath())).into(Img_add2);
                }
            }
        }
    }
}
