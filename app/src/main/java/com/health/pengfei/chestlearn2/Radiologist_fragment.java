package com.health.pengfei.chestlearn2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Radiologist_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Radiologist_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Radiologist_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView radio_image_view;
    private Button radio_take_image_button,radio_submit_button;
    private String radio_image_uri;
    private EditText radio_note_textField,patientLastName;

    private SharedPreferences sp;

    private ProgressDialog progressDialog;



    private OnFragmentInteractionListener mListener;

    public Radiologist_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Radiologist_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Radiologist_fragment newInstance(String param1, String param2) {
        Radiologist_fragment fragment = new Radiologist_fragment();
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

        View radioFragmentView = inflater.inflate(R.layout.radiologist_fragment, container, false);
        progressDialog = new ProgressDialog(radioFragmentView.getContext());
        sp= getActivity().getSharedPreferences("doctor.conf", Context.MODE_PRIVATE);
        radio_image_view=(ImageView)radioFragmentView.findViewById(R.id.radio_image);
        radio_take_image_button=(Button)radioFragmentView.findViewById(R.id.radioTakePhoto);
        radio_take_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),TakePhoto.class);

                startActivityForResult(i,1);
            }
        });
        patientLastName=(EditText)radioFragmentView.findViewById(R.id.patientLastName_Radio);
        radio_note_textField=(EditText)radioFragmentView.findViewById(R.id.raidoNoteTextfield);
        radio_submit_button=(Button)radioFragmentView.findViewById(R.id.radioSubmitButton);
        radio_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String  clinicName= sp.getString("clinicName","");
                String  nurseName=  sp.getString("nurseName","");
                String  recipients=sp.getString("recipients","");
                String  doc1=sp.getString("doc1","");
                String  doc2=sp.getString("doc2","");
                String  doc3=sp.getString("doc3","");
                String patientLName=patientLastName.getText().toString();
                String uploadDeviceID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                String mainTppFileName = null;


                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);

                if(radio_image_uri!=null) {
                    Uri imageUri = Uri.parse(radio_image_uri);
                    File file = new File(imageUri.getPath());
                    mainTppFileName = file.getName();
                    RequestBody uploadedfile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    builder.addFormDataPart("uploadedfile[]", file.getName(), uploadedfile);

                }
                if(mainTppFileName == null)
                {
                    Toast.makeText(getActivity().getApplicationContext(),"Please Select Image For Upload", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.setMessage(getActivity().getApplicationContext().getText(R.string.waitamoment));
                progressDialog.show();
                HashMap<String, RequestBody> map = new HashMap<String, RequestBody>();
                map.put("id",toRequestBody("1"));
                map.put("patientLastName",toRequestBody(patientLName));
                map.put("uploadDeviceID",toRequestBody(uploadDeviceID));
                map.put("uploadPersonType",toRequestBody("r"));
                map.put("uploadNurseName",toRequestBody(nurseName));
                map.put("clinicName",toRequestBody(clinicName));
                //map.put("recipientName",toRequestBody(recipients));
                map.put("doc1",toRequestBody(doc1));
                map.put("doc2",toRequestBody(doc2));
                map.put("doc3",toRequestBody(doc3));
                map.put("mainTppFileName",toRequestBody(mainTppFileName));
                map.put("doctorNotes",toRequestBody(radio_note_textField.getText().toString()));
                List<MultipartBody.Part> parts = builder.build().parts();
                ApiUtil.uploadFile(parts,map).enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        ServerResponse serverResponse = response.body();
                        if (serverResponse != null) {
                            if (serverResponse.getError()) {
                                Toast.makeText(getActivity().getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }else {
                                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                                builder.setTitle(R.string.result)
                                        .setMessage(R.string.successfulupload)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .show();
                                radio_image_view.setImageResource(R.drawable.image_border);
                                radio_note_textField.setText(" ");
                                patientLastName.setText("");
                                Toast.makeText(getActivity().getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            assert  serverResponse != null;
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
        });
        return radioFragmentView;
    }
    public RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == 1) {
                // make use of "data" = profit
                System.out.println("From main" + data.getStringExtra("uri"));
                String mCurrentPhotoPath = data.getStringExtra("uri");
                if(mCurrentPhotoPath!=null) {
                    radio_image_uri = mCurrentPhotoPath;
                    Uri imageUri = Uri.parse(mCurrentPhotoPath);
                    File file = new File(imageUri.getPath());


                    Glide.with(this).load(new File(imageUri.getPath())).into(radio_image_view);

                }
            }
        }
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
