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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;


public class AdditionalInfoActivity extends AppCompatActivity {
    public static final String EVALUATE_DIALOG = "evaluate_dialog";
    public static final int REQUEST_EVALUATE = 0X110;
    public static final String ARGUMENT = "argument";
    public static final String RESPONSE = "response";

    private SharedPreferences sp;
    private SharedPreferences.Editor sp_editor;
    private ProgressDialog progressDialog;
    boolean check_two = false;
//    private TextView textview_patient_last_name;
    private String patientlastname;
    private ImageView second_XRay_Image,third_XRay_Image;
    private String main_XRay_Image_uri, second_XRay_Image_uri,third_XRay_Image_uri;
    private Button second_XRay_button,third_XRay_button, submitButton;

    Account patient = new Account("", false, false, false);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_info);

        second_XRay_Image=(ImageView) findViewById(R.id.nurse_imageTB2);
        third_XRay_Image=(ImageView) findViewById(R.id.nurse_imageTB3);

        sp= getSharedPreferences("doctor.conf", Context.MODE_PRIVATE);
        sp_editor=sp.edit();
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
        patient = (Account)intent.getSerializableExtra("account");
        textview_patient_last_name.setText(patientlastname);

        if (X_Img_url != null) {
            Uri imageUri = Uri.parse(X_Img_url);
            File file = new File(imageUri.getPath());
            mainTppFileName = file.getName();
        }

        Log.e("HashMapTest", X_Img_url);
        Log.e("HashMapTest", mainTppFileName);

        second_XRay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdditionalInfoActivity.this, TakePhoto.class);

                startActivityForResult(i,2);
                patient.setSecondPic(true);
            }
        });
        third_XRay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdditionalInfoActivity.this, TakePhoto.class);

                startActivityForResult(i,3);
                patient.setThirdPic(true);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(second_XRay_Image_uri==null && third_XRay_Image_uri==null){
                    //open a dialog box to "yes" continue to send or "no" to cancel the submit request.
                    AlertDialog.Builder builder=new AlertDialog.Builder(AdditionalInfoActivity.this);
                    builder.setTitle(R.string.exam_lab_result_empty)
                            .setMessage(R.string.upload_xray_img)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    AlertDialog.Builder builder2=new AlertDialog.Builder(AdditionalInfoActivity.this);
                                    LayoutInflater inflater2 = (LayoutInflater) AdditionalInfoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
                                    final View dialog_treatment = inflater2.inflate(R.layout.dialog_treatment, null, false);

                                    final RadioGroup msg_RadioGroup = (RadioGroup) dialog_treatment
                                            .findViewById(R.id.treatmentRadioButton);
                                    msg_RadioGroup.check(R.id.NoRadioButton);

                                    final EditText msg_EditText = (EditText) dialog_treatment
                                            .findViewById(R.id.months_EditText);
                                    msg_EditText.setText("0");
                                    builder2.setView(dialog_treatment)
                                            .setTitle("Treatment")
                                            .setCancelable(false)
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    /*
                                                     * Getting the value of selected RadioButton.
                                                     */
                                                    // get selected radio button from radioGroup
                                                    int selectedId = msg_RadioGroup
                                                            .getCheckedRadioButtonId();

                                                    // find the radiobutton by returned id
                                                    RadioButton selectedRadioButton = (RadioButton) dialog_treatment
                                                            .findViewById(selectedId);

                                                    sp_editor.putString("undertreatment",selectedRadioButton.getText().toString());
                                                    sp_editor.putString("treatment_duration",msg_EditText.getText().toString());
                                                    sp_editor.apply();
                                                    uploadPics();
//                                                    Toast.makeText(AdditionalInfoActivity.this.getApplicationContext(), "XDDD", Toast.LENGTH_SHORT).show();

//                                                dialog.cancel();
                                                }

                                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
//                                            return;
                                            dialog.cancel();
                                        }
                                    }).show();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                } else if(second_XRay_Image_uri==null && third_XRay_Image_uri!=null){
                    //open a dialog box to "yes" continue to send or "no" to cancel the submit request.
                    AlertDialog.Builder builder=new AlertDialog.Builder(AdditionalInfoActivity.this);
                    builder.setTitle(R.string.exam_result_empty)
                            .setCancelable(false)
                            .setMessage(
                                    R.string.upload_lab_xray_img)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    AlertDialog.Builder builder2=new AlertDialog.Builder(AdditionalInfoActivity.this);
                                    LayoutInflater inflater2 = (LayoutInflater) AdditionalInfoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
                                    final View dialog_treatment = inflater2.inflate(R.layout.dialog_treatment, null, false);

                                    final RadioGroup msg_RadioGroup = (RadioGroup) dialog_treatment
                                            .findViewById(R.id.treatmentRadioButton);
                                    msg_RadioGroup.check(R.id.NoRadioButton);

                                    final EditText msg_EditText = (EditText) dialog_treatment
                                            .findViewById(R.id.months_EditText);
                                    msg_EditText.setText("0");
                                    builder2.setView(dialog_treatment)
                                            .setTitle("Treatment")
                                            .setCancelable(false)
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    /*
                                                     * Getting the value of selected RadioButton.
                                                     */
                                                    // get selected radio button from radioGroup
                                                    int selectedId = msg_RadioGroup
                                                            .getCheckedRadioButtonId();

                                                    // find the radiobutton by returned id
                                                    RadioButton selectedRadioButton = (RadioButton) dialog_treatment
                                                            .findViewById(selectedId);

                                                    sp_editor.putString("undertreatment",selectedRadioButton.getText().toString());
                                                    sp_editor.putString("treatment_duration",msg_EditText.getText().toString());
                                                    sp_editor.apply();
                                                    uploadPics();
//                                                    Toast.makeText(AdditionalInfoActivity.this.getApplicationContext(), "XDDD", Toast.LENGTH_SHORT).show();

//                                                dialog.cancel();
                                                }

                                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
//                                            return;
                                            dialog.cancel();
                                        }
                                    }).show();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                } else if (second_XRay_Image_uri!=null && third_XRay_Image_uri==null){
                    //open a dialog box to "yes" continue to send or "no" to cancel the submit request.
                    AlertDialog.Builder builder=new AlertDialog.Builder(AdditionalInfoActivity.this);

                    builder.setTitle(
                            R.string.lab_result_empty)
                            .setMessage(R.string.upload_exam_xray_img)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    AlertDialog.Builder builder2=new AlertDialog.Builder(AdditionalInfoActivity.this);
                                    LayoutInflater inflater2 = (LayoutInflater) AdditionalInfoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
                                    final View dialog_treatment = inflater2.inflate(R.layout.dialog_treatment, null, false);

                                    final RadioGroup msg_RadioGroup = (RadioGroup) dialog_treatment
                                            .findViewById(R.id.treatmentRadioButton);
                                    msg_RadioGroup.check(R.id.NoRadioButton);

                                    final EditText msg_EditText = (EditText) dialog_treatment
                                            .findViewById(R.id.months_EditText);
                                    msg_EditText.setText("0");
                                    builder2.setView(dialog_treatment)
                                            .setTitle("Treatment")
                                            .setCancelable(false)
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    /*
                                                     * Getting the value of selected RadioButton.
                                                     */
                                                    // get selected radio button from radioGroup
                                                    int selectedId = msg_RadioGroup
                                                            .getCheckedRadioButtonId();

                                                    // find the radiobutton by returned id
                                                    RadioButton selectedRadioButton = (RadioButton) dialog_treatment
                                                            .findViewById(selectedId);

                                                    sp_editor.putString("undertreatment",selectedRadioButton.getText().toString());
                                                    sp_editor.putString("treatment_duration",msg_EditText.getText().toString());
                                                    sp_editor.apply();
                                                    uploadPics();
                                                }

                                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    }).show();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();
                } else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(AdditionalInfoActivity.this);
                    builder.setTitle(R.string.Confirmation)
                            .setMessage(
                                    R.string.readtoproceed)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    AlertDialog.Builder builder2=new AlertDialog.Builder(AdditionalInfoActivity.this);
                                    LayoutInflater inflater2 = (LayoutInflater) AdditionalInfoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
                                    final View dialog_treatment = inflater2.inflate(R.layout.dialog_treatment, null, false);

                                    final RadioGroup msg_RadioGroup = (RadioGroup) dialog_treatment
                                            .findViewById(R.id.treatmentRadioButton);
                                    msg_RadioGroup.check(R.id.NoRadioButton);

                                    final EditText msg_EditText = (EditText) dialog_treatment
                                            .findViewById(R.id.months_EditText);
                                    msg_EditText.setText("0");
                                    builder2.setView(dialog_treatment)
                                            .setTitle("Treatment")
                                            .setCancelable(false)
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    /*
                                                     * Getting the value of selected RadioButton.
                                                     */
                                                    // get selected radio button from radioGroup
                                                    int selectedId = msg_RadioGroup
                                                            .getCheckedRadioButtonId();

                                                    // find the radiobutton by returned id
                                                    RadioButton selectedRadioButton = (RadioButton) dialog_treatment
                                                            .findViewById(selectedId);

                                                    sp_editor.putString("undertreatment",selectedRadioButton.getText().toString());
                                                    sp_editor.putString("treatment_duration",msg_EditText.getText().toString());
                                                    sp_editor.apply();
                                                    uploadPics();
                                                }

                                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
//                                            return;
                                            dialog.cancel();
                                        }
                                    }).show();
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
        String  doc4=sp.getString("doc4","");
        String patientLName= patientlastname;
        String uploadDeviceID = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
        String undertreatment = sp.getString("undertreatment","");
        String treatment_duration = sp.getString("treatment_duration","");
        String mainTppFileName = null;
        String add1TppFileName = null;
        String add2TppFileName = null;

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
            add1TppFileName = file.getName();
            RequestBody uploadedfile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("uploadedfile[]", file.getName(), uploadedfile);

        }

        if (third_XRay_Image_uri != null) {
            Uri imageUri = Uri.parse(third_XRay_Image_uri);
            File file = new File(imageUri.getPath());
            add2TppFileName = file.getName();
            RequestBody uploadedfile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("uploadedfile[]", file.getName(), uploadedfile);
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
        map.put("doc4", toRequestBody(doc4));
        map.put("undertreatment", toRequestBody(undertreatment));
        map.put("treatment_duration",toRequestBody(treatment_duration));

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
                        Toast.makeText(AdditionalInfoActivity.this.getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AdditionalInfoActivity.this.getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        second_XRay_Image.setImageResource(R.drawable.image_border);
                        second_XRay_Image_uri = null;
                        third_XRay_Image.setImageResource(R.drawable.image_border);
                        third_XRay_Image_uri = null;
                        AlertDialog.Builder builder=new AlertDialog.Builder(AdditionalInfoActivity.this);
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
