package com.health.pengfei.chestlearn2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;


public class History extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get history data from file
        ArrayList history = new ArrayList();
        AccountRecord account = new AccountRecord();
        File file = account.createFile();
        try {
            history = account.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create new hashmap for history display
        ArrayList<HashMap<String, Object>> data = getData(history);
        MyAdapter adapter = new MyAdapter(this, data);
        setListAdapter(adapter);
    }

    private ArrayList<HashMap<String, Object>> getData(ArrayList<Account> results){
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList();
        for (Account result:results) {
            HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
            tempHashMap.put("image", R.drawable.buckethead);
            tempHashMap.put("name", result.getPatientName());
            tempHashMap.put("first picture", result.isFirstPic());
            tempHashMap.put("second picture", result.isSecondPic());
            tempHashMap.put("third picture", result.isThirdPic());
            arrayList.add(tempHashMap);
        }
        return arrayList;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

    }



}
