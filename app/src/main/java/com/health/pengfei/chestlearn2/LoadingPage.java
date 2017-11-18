package com.health.pengfei.chestlearn2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;

public class LoadingPage extends AppCompatActivity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading_page);



            Intent i = new Intent(LoadingPage.this, MainActivity.class);
            startActivity(i);
            finish();

    }
}
