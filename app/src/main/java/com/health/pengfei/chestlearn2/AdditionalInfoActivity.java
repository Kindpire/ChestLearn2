package com.health.pengfei.chestlearn2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.provider.Settings.Secure;
import android.widget.Toast;

import com.bumptech.glide.Glide;


public class AdditionalInfoActivity extends AppCompatActivity {
    public static final String EVALUATE_DIALOG = "evaluate_dialog";
    public static final int REQUEST_EVALUATE = 0X110;
    public static final String ARGUMENT = "argument";
    public static final String RESPONSE = "response";

    private SharedPreferences sp;
    private ProgressDialog progressDialog;
    boolean check_two = false;
//    private TextView textview_patient_last_name;
    private String patientlastname;
    private ImageView second_XRay_Image,third_XRay_Image;
    private String main_XRay_Image_uri, second_XRay_Image_uri,third_XRay_Image_uri;
    private Button second_XRay_button,third_XRay_button, submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);

        second_XRay_Image=(ImageView) findViewById(R.id.nurse_imageTB2);
        third_XRay_Image=(ImageView) findViewById(R.id.nurse_imageTB3);

        sp= getSharedPreferences("doctor.conf", Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);

        second_XRay_button = (Button) findViewById(R.id.buttonTBPhoto2);
        third_XRay_button = (Button) findViewById(R.id.buttonTBPhoto3);
        submitButton = (Button) findViewById(R.id.button_Nurse_submit);

        TextView textview_patient_last_name = (TextView) findViewById(R.id.tv_patientlastname);

        Intent intent = getIntent();
        String X_Img_url = intent.getStringExtra("x_img_uri");
        patientlastname = intent.getStringExtra("patientlastname");
        main_XRay_Image_uri = X_Img_url;
        String mainTppFileName = null;

        textview_patient_last_name.setText(patientlastname);

        if (X_Img_url != null) {
            Uri imageUri = Uri.parse(X_Img_url);
            File file = new File(imageUri.getPath());
            mainTppFileName = file.getName();
        }

        Log.e("HashMapTest", X_Img_url);
        Log.e("HashMapTest", mainTppFileName);
        String  clinicName= sp.getString("clinicName","");
        String  nurseName=  sp.getString("nurseName","");
        final String  recipients=sp.getString("recipients","");
        String  doc1=sp.getString("doc1","");
        String  doc2=sp.getString("doc2","");
        String  doc3=sp.getString("doc3","");
        String uploadDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        HashMap<String, RequestBody> map = new HashMap<String, RequestBody>();
        map.put("id", toRequestBody("1"));
        map.put("patientLastName", toRequestBody(patientlastname));
        map.put("uploadDeviceID", toRequestBody(uploadDeviceID));
        map.put("uploadPersonType", toRequestBody("n"));
        map.put("uploadNurseName", toRequestBody(nurseName));
        map.put("clinicName", toRequestBody(clinicName));
        map.put("doc1", toRequestBody(doc1));
        map.put("doc2", toRequestBody(doc2));
        map.put("doc3", toRequestBody(doc3));
        map.put("mainTppFileName", toRequestBody(mainTppFileName));

        second_XRay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdditionalInfoActivity.this, TakePhoto.class);

                startActivityForResult(i,2);
            }
        });
        third_XRay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdditionalInfoActivity.this, TakePhoto.class);

                startActivityForResult(i,3);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(second_XRay_Image_uri==null && third_XRay_Image_uri==null){
                    //open a dialog box to "yes" continue to send or "no" to cancel the submit request.
                    AlertDialog.Builder builder=new AlertDialog.Builder(AdditionalInfoActivity.this);
                    builder.setTitle("Physical exam and lab result are empty")
                            .setMessage("Upload X-ray image?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadPics();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                } else if(second_XRay_Image_uri==null && third_XRay_Image_uri!=null){
                    //open a dialog box to "yes" continue to send or "no" to cancel the submit request.
                    AlertDialog.Builder builder=new AlertDialog.Builder(AdditionalInfoActivity.this);
                    builder.setTitle("Physical exam result is empty")
                            .setMessage("Upload lab result and X-ray image?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadPics();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                } else if (second_XRay_Image_uri!=null && third_XRay_Image_uri==null){
                    //open a dialog box to "yes" continue to send or "no" to cancel the submit request.
                    AlertDialog.Builder builder=new AlertDialog.Builder(AdditionalInfoActivity.this);

                    builder.setTitle("Lab result is empty")
                            .setMessage("Upload exam result and X-ray image?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadPics();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                } else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(AdditionalInfoActivity.this);
                    builder.setTitle("Confirmation")
                            .setMessage("Upload the additional info with X-ray image?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    uploadPics();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
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
        String uploadDeviceID = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);

        String mainTppFileName = null;
        check_two = false;

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        System.out.println("Ready to upload");

        if (main_XRay_Image_uri != null) {
            Uri imageUri = Uri.parse(main_XRay_Image_uri);
            File file = new File(imageUri.getPath());
            mainTppFileName = file.getName();
            RequestBody uploadedfile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("uploadedfile[]", file.getName(), uploadedfile);
        }

        if (second_XRay_Image_uri != null) {
            Uri imageUri = Uri.parse(second_XRay_Image_uri);
            File file = new File(imageUri.getPath());
            RequestBody uploadedfile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("uploadedfile[]", file.getName(), uploadedfile);

        }

        if (third_XRay_Image_uri != null) {
            Uri imageUri = Uri.parse(third_XRay_Image_uri);
            File file = new File(imageUri.getPath());
            RequestBody uploadedfile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("uploadedfile[]", file.getName(), uploadedfile);
        }

//        if (mainTppFileName == null) {
//            Toast.makeText(this.getApplicationContext(), "Main X ray must be at first place", Toast.LENGTH_SHORT).show();
//            return;
//        }
        progressDialog.setMessage("Uploading");
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
        map.put("mainTppFileName", toRequestBody(mainTppFileName));

        List<MultipartBody.Part> parts = builder.build().parts();
        ApiUtil.uploadFile(parts, map).enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse != null) {
                    //error
                    if (serverResponse.getError()) {
                        Toast.makeText(AdditionalInfoActivity.this.getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdditionalInfoActivity.this.getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        second_XRay_Image.setImageResource(R.drawable.image_border);
                        third_XRay_Image.setImageResource(R.drawable.image_border);
                    }

                } else {
                    assert serverResponse != null;
                    Log.v("Response", serverResponse.toString());
                }
                progressDialog.dismiss();
                AlertDialog.Builder builder=new AlertDialog.Builder(AdditionalInfoActivity.this);
                builder.setTitle("Result")
                        .setMessage("Success Upload.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("additionalinfo", "success");
                                setResult(9, resultIntent);
                                finish();
                            }
                        })
                        .show();
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
        if (requestCode == REQUEST_EVALUATE)
        {
            String evaluate = data.getStringExtra(NoticeDialogFragment.RESPONSE_EVALUATE);
            System.out.println(evaluate);
            if (evaluate == "YES"){
                System.out.println("Yes, upload.");
                uploadPics();
            }else if (evaluate == "CANCEL"){
                System.out.println("Give up.");
            }
            Toast.makeText(this, evaluate, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra(RESPONSE, evaluate);
            this.setResult(AdditionalInfoActivity.RESULT_OK, intent);
        }
        if(resultCode != RESULT_CANCELED) {
            if (requestCode == 2) {
                System.out.println("From second" + data.getStringExtra("uri"));
                String mCurrentPhotoPath=data.getStringExtra("uri");
                if(mCurrentPhotoPath!=null) {
                    second_XRay_Image_uri = mCurrentPhotoPath;
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    File file = new File(imageUri.getPath());
                    Glide.with(this).load(new File(imageUri.getPath())).into(second_XRay_Image);

                }
            } else if (requestCode == 3) {
                System.out.println("From third" + data.getStringExtra("uri"));
                String mCurrentPhotoPath=data.getStringExtra("uri");
                if(mCurrentPhotoPath!=null) {
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    third_XRay_Image_uri = mCurrentPhotoPath;
                    File file = new File(imageUri.getPath());
                    Glide.with(this).load(new File(imageUri.getPath())).into(third_XRay_Image);

                }
            }
        }
    }
}
