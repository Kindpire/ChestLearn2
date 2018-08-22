package com.health.pengfei.chestlearn2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class doctor_setting extends AppCompatActivity  {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private EditText clinicName, nurseName, nurseEmail,doc1,doc2,doc3,doc4;
    private Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_setting);
        sp= getSharedPreferences("doctor.conf", Context.MODE_PRIVATE);
        editor=sp.edit();

        clinicName=(EditText)findViewById(R.id.clinicText);
        nurseName=(EditText)findViewById(R.id.nurseText);
        nurseEmail =(EditText)findViewById(R.id.nurseEmail);

        doc1=(EditText)findViewById(R.id.doc1Text);
        doc2=(EditText)findViewById(R.id.doc2Text);
        doc3=(EditText)findViewById(R.id.doc3Text);
        doc4=(EditText)findViewById(R.id.doc4Text);
        clinicName.setText(sp.getString("clinicName",""));
        nurseName.setText(sp.getString("nurseName",""));
        nurseEmail.setText(sp.getString("nurseEmail",""));

        doc1.setText(sp.getString("doc1",""));
        doc2.setText(sp.getString("doc2",""));
        doc3.setText(sp.getString("doc3",""));
        doc4.setText(sp.getString("doc4",""));

        saveButton=(Button)findViewById(R.id.saveSettingButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("clinicName",clinicName.getText().toString());
                editor.putString("nurseName",nurseName.getText().toString());
                editor.putString("nurseEmail",nurseEmail.getText().toString());
                editor.putString("doc1",doc1.getText().toString());
                editor.putString("doc2",doc2.getText().toString());
                editor.putString("doc3",doc3.getText().toString());
                editor.putString("doc4",doc4.getText().toString());
                editor.apply();
                finish();
            }
        });
    }
}
