package com.health.pengfei.chestlearn2;

import android.Manifest;
import android.app.Activity;
//import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//v4 and app's Fragment are different, v4 support 1.6 minimum, app for 3.0
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.provider.Settings.Secure;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NurseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NurseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NurseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ARGUMENT = "argument";
    public static final String RESPONSE = "response";
    public static final String EVALUATE_DIALOG = "evaluate_dialog";
    public static final int REQUEST_EVALUATE = 0X110;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView main_XRay_Image;
    private String main_XRay_Image_uri;
    boolean checkallthree;
    private Button main_XRay_button, submitButton, edit_Button, additional_Button;
    private EditText patientLastName;
    private EditText patient_LastName_textEdit;

    private SharedPreferences sp;

    private ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    Account patient = new Account("",false,false,false );

    public NurseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment nurse_fargment.
     */
    // TODO: Rename and change types and number of parameters
    public static NurseFragment newInstance(String param1, String param2) {
        NurseFragment fragment = new NurseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View nurseFragmentView = inflater.inflate(R.layout.nurse_fargment, container, false);
        patientLastName=(EditText)nurseFragmentView.findViewById(R.id.patientLastName);
        main_XRay_Image=(ImageView) nurseFragmentView.findViewById(R.id.nurse_imageTB);

        sp= getActivity().getSharedPreferences("doctor.conf", Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(nurseFragmentView.getContext());

        main_XRay_button=(Button) nurseFragmentView.findViewById(R.id.buttonTBPhoto);
        submitButton =(Button) nurseFragmentView.findViewById(R.id.button_Nurse_submit);
        additional_Button =(Button) nurseFragmentView.findViewById(R.id.button_additional_info);
        edit_Button=(Button)nurseFragmentView.findViewById(R.id.button_edit);


        main_XRay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),TakePhoto.class);

                startActivityForResult(i,1);
                patient.setFirstPic(true);
            }
        });
        edit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientLastName.setEnabled(true);
            }
        });
        patientLastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))){
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

                    builder.setTitle("")
                            .setMessage(R.string.is_patient_last_name_correct)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    patientLastName.setEnabled(false);
                                    patient.setPatientName(patientLastName.getText().toString());
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    patientLastName.setEnabled(true );
                                    patientLastName.requestFocus();
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(patientLastName, InputMethodManager.SHOW_IMPLICIT);
                                }
                            })
                            .show();
                    return true;
                }
                else{
                    return false;
                }
            }
        });
        additional_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String  clinicName= sp.getString("clinicName","");
                String  nurseName=  sp.getString("nurseName","");
                final String  recipients=sp.getString("recipients","");
                String  doc1=sp.getString("doc1","");
                String  doc2=sp.getString("doc2","");
                String  doc3=sp.getString("doc3","");
                String patientLName=patientLastName.getText().toString();
                String uploadDeviceID = Secure.getString(getContext().getContentResolver(),Secure.ANDROID_ID);

                String mainTppFileName = null;
                checkallthree = false;

                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);

                System.out.println("Ready to upload");
                if (main_XRay_Image_uri != null) {
                    Uri imageUri = Uri.parse(main_XRay_Image_uri);
                    File file = new File(imageUri.getPath());
                    mainTppFileName = file.getName();
                }

                if (mainTppFileName == null) {
//                    Toast.makeText(getActivity().getApplicationContext(), "Main X ray must be at first place", Toast.LENGTH_SHORT).show();
                    //TODO open a dialog box to "yes" continue to send or "no" to cancel the submit request.
                    AlertDialog.Builder alertbuilder=new AlertDialog.Builder(getActivity());
                    alertbuilder.setTitle(R.string.noxrayimg)
                            .setMessage(R.string.plztakexrayimgfirst)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                    return;
                }
                Log.v("Patient", patientLName);
                System.out.println("PNAME");
                System.out.println(patientLName);
                if (patientLName.length()==0) {
//                    Toast.makeText(getActivity().getApplicationContext(), "Main X ray must be at first place", Toast.LENGTH_SHORT).show();
                    //TODO open a dialog box to "yes" continue to send or "no" to cancel the submit request.
                    AlertDialog.Builder alertbuilder=new AlertDialog.Builder(getActivity());
                    alertbuilder.setTitle(R.string.noxrayimg)
                            .setMessage(R.string.plztakexrayimgfirst)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                    return;
                }
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

                Intent i = new Intent(getActivity(),AdditionalInfoActivity.class);
                i.putExtra("patientlastname", patientLName);
                i.putExtra("x_img_uri", main_XRay_Image_uri);
                i.putExtra("account", patient);
                startActivityForResult(i,9);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.xrayimg)
                        .setMessage(R.string.do_you_wanna_upload)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                uploadPics();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .show();
            }
        });
        return nurseFragmentView;
    }

    public void uploadPics(){
        String  clinicName= sp.getString("clinicName","");
        String  nurseName=  sp.getString("nurseName","");
        final String  recipients=sp.getString("recipients","");
        String  doc1=sp.getString("doc1","");
        String  doc2=sp.getString("doc2","");
        String  doc3=sp.getString("doc3","");
        String patientLName=patientLastName.getText().toString();
        String uploadDeviceID = Secure.getString(getContext().getContentResolver(),Secure.ANDROID_ID);

        String mainTppFileName = null;
        checkallthree = false;

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

        if (mainTppFileName == null) {
            //TODO open a dialog box to "yes" continue to send or "no" to cancel the submit request.
            AlertDialog.Builder alertbuilder=new AlertDialog.Builder(getActivity());
            alertbuilder.setTitle(R.string.noxrayimg)
                    .setMessage(R.string.plztakexrayimgfirst)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            return;
        }
        if (patientLName.length()==0) {
//                    Toast.makeText(getActivity().getApplicationContext(), "Main X ray must be at first place", Toast.LENGTH_SHORT).show();
            //TODO open a dialog box to "yes" continue to send or "no" to cancel the submit request.
            AlertDialog.Builder alertbuilder=new AlertDialog.Builder(getActivity());
            alertbuilder.setTitle(R.string.noxrayimg)
                    .setMessage(R.string.plztakexrayimgfirst)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            return;
        }

        progressDialog.setMessage(getActivity().getApplicationContext().getText(R.string.waitamoment));
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
                        Toast.makeText(getActivity().getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        //TODO: read record file, check if current account is in file, update account records, save to record file
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

//                        Toast.makeText(getActivity().getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.result)
                                .setMessage(R.string.successfulupload)
                                .setCancelable(false)
                                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                        main_XRay_Image.setImageResource(R.drawable.image_border);
                        main_XRay_Image_uri=null;
                        patientLastName.setText("");
                        patientLastName.setEnabled(true);
                    }

                } else {
                    assert serverResponse != null;
                    Log.e("Response", serverResponse.toString());
                }
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.v("OnFailure", t.toString());
            }
        });
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
            Toast.makeText(getActivity(), evaluate, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra(RESPONSE, evaluate);
            getActivity().setResult(Activity.RESULT_OK, intent);
        }
        if(resultCode != RESULT_CANCELED) {
            if (requestCode == 1) {
                // make use of "data" = profit
                System.out.println("From main" + data.getStringExtra("uri"));
                String mCurrentPhotoPath=data.getStringExtra("uri");
                if(mCurrentPhotoPath!=null) {
                    main_XRay_Image_uri = mCurrentPhotoPath;
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    File file = new File(imageUri.getPath());
                    Glide.with(this).load(new File(imageUri.getPath())).into(main_XRay_Image);

                }
            }
            if (requestCode == 9) {
                // make use of "data" = profit
                System.out.println("From SUB!!!!!!!!!!!!!!");
                main_XRay_Image.setImageResource(R.drawable.image_border);
                main_XRay_Image_uri=null;
                patientLastName.setText("");
                patientLastName.setEnabled(true);
            }

        }
    }
    public RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
